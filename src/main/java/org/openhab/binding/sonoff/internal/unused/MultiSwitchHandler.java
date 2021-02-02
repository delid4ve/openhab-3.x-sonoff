/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.sonoff.internal.unused;

import static org.eclipse.smarthome.core.library.unit.SmartHomeUnits.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.sonoff.internal.Utils;
import org.openhab.binding.sonoff.internal.config.DeviceConfig;
import org.openhab.binding.sonoff.internal.dto.api.Device;
import org.openhab.binding.sonoff.internal.dto.api.Params;
import org.openhab.binding.sonoff.internal.dto.payloads.MultiSwitch;
import org.openhab.binding.sonoff.internal.dto.payloads.UiActive;
import org.openhab.binding.sonoff.internal.handler.AccountHandler;
import org.openhab.binding.sonoff.internal.listeners.DeviceStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The {@link lightwaverfBindingConstants} class defines common constants, which
 * are used across the whole binding.
 *
 * @author David Murton - Initial contribution
 */

@NonNullByDefault
public class MultiSwitchHandler extends BaseThingHandler implements DeviceStateListener {

    private final Logger logger = LoggerFactory.getLogger(MultiSwitchHandler.class);
    private @Nullable AccountHandler account;
    private @Nullable DeviceConfig config;
    private @Nullable ScheduledFuture<?> wsTask;
    private final Gson gson;
    private String deviceKey = "";
    private String ipaddress = "";
    private Map<Integer, String> channels = new HashMap<>();

    public MultiSwitchHandler(Thing thing, Gson gson) {
        super(thing);
        this.gson = gson;
    }

    @Override
    public void initialize() {
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
        } else {
            initializeBridge(bridge.getHandler(), bridge.getStatus());
            config = this.getConfigAs(DeviceConfig.class);
            setProperties();
            account.registerStateListener(config.deviceId, this);
            Runnable activateWs = () -> {
                if (account.wsOnline()) {
                    UiActive params = new UiActive();
                    params.setUiActive(60);
                    sendUpdate(gson.toJson(params), "uiActive", "100");
                }
            };
            wsTask = scheduler.scheduleWithFixedDelay(activateWs, 10, 60, TimeUnit.SECONDS);
        }
        if (account.getThing().getStatus() == ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    @Override
    public void thingUpdated(Thing thing) {
        dispose();
        this.thing = thing;
        initialize();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (config.deviceId.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "ID not set");
            return;
        }
        if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            account.unregisterStateListener(config.deviceId);
            return;
        }
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            account.registerStateListener(config.deviceId, this);
            updateStatus(ThingStatus.ONLINE);
            return;
        }
    }

    private void initializeBridge(@Nullable ThingHandler thingHandler, ThingStatus bridgeStatus) {
        logger.debug("initializeBridge {} for thing {}", bridgeStatus, getThing().getUID());
        if (thingHandler != null && bridgeStatus != null) {
            account = (AccountHandler) thingHandler;
            if (bridgeStatus != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        if (wsTask != null) {
            wsTask.cancel(true);
            wsTask = null;
        }
        account.unregisterStateListener(config.deviceId);
        account = null;
    }

    private void setProperties() {
        Device device = account.getDevice(config.deviceId);
        for (int i = 0; i < device.getParams().getSwitches().size(); i++) {
            Integer outlet = device.getParams().getSwitches().get(i).getOutlet();
            String _switch = device.getParams().getSwitches().get(i).getSwitch();
            channels.put(outlet, _switch);
        }
        deviceKey = device.getDevicekey();
        ipaddress = device.getLocalAddress();
        Map<String, String> properties = editProperties();
        properties.put("Name", device.getName());
        properties.put("Brand", device.getBrandName());
        properties.put("Model", device.getProductModel());
        properties.put("FW Version", device.getParams().getFwVersion());
        properties.put("Device ID", device.getDeviceid());
        properties.put("Device Key", device.getDevicekey());
        properties.put("Connected To SSID", device.getParams().getSsid());
        properties.put("UUID", device.getUiid().toString());
        updateProperties(properties);
    }

    private void sendUpdate(String data, String command, String seq) {
        if (!command.contains("switch") && account.getAccountConfig().accessmode.equals("local")) {
            logger.warn("Sonoff - Cannot send command {}, Not supported by LAN", command.toString());
        } else if (account.lanOnline() && !ipaddress.equals("") && command.contains("switch")) {
            account.getApi().sendLocalUpdate(data, command, config.deviceId, ipaddress, deviceKey, seq);
        } else if (account.wsOnline()) {
            account.getWebsocket().sendChange(data, config.deviceId, deviceKey);
        } else {
            logger.info("Cannot send command, all connections are offline");
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        } else {
            MultiSwitch params = new MultiSwitch();
            MultiSwitch.Switch _switch = params.new Switch();
            if (channelUID.getId().equals("switch0")) {
                _switch.setOutlet(0);
                _switch.setSwitch(command.toString().toLowerCase());
            } else if (channelUID.getId().equals("switch1")) {
                _switch.setOutlet(1);
                _switch.setSwitch(command.toString().toLowerCase());
            } else if (channelUID.getId().equals("switch2")) {
                _switch.setOutlet(2);
                _switch.setSwitch(command.toString().toLowerCase());
            } else if (channelUID.getId().equals("switch3")) {
                _switch.setOutlet(3);
                _switch.setSwitch(command.toString().toLowerCase());
            }
            params.getSwitches().add(_switch);
            String data = gson.toJson(params);
            String endpoint = "switches";
            sendUpdate(data, endpoint, "100");

        }
    }

    private synchronized void updateState(Device device) {
        Integer rssi = device.getParams().getRssi();
        String offlineTime = device.getOfflineTime();
        String switch0 = device.getParams().getSwitches().get(0).getSwitch();
        String switch1 = device.getParams().getSwitches().get(1).getSwitch();
        String switch2 = device.getParams().getSwitches().get(2).getSwitch();
        String switch3 = device.getParams().getSwitches().get(3).getSwitch();
        Boolean channel0 = this.thing.getChannel("switch0") == null ? false : true;
        Boolean channel1 = this.thing.getChannel("switch1") == null ? false : true;
        Boolean channel2 = this.thing.getChannel("switch2") == null ? false : true;
        Boolean channel3 = this.thing.getChannel("switch3") == null ? false : true;

        if (rssi != null) {
            updateState(this.thing.getChannel("rssi").getUID(), new QuantityType<>(rssi, (DECIBEL_MILLIWATTS)));
        }
        if (offlineTime != null) {
            updateState(this.thing.getChannel("offlineTime").getUID(), new StringType(offlineTime));
        }
        if (switch0 != null && channel0 == true) {
            updateState(this.thing.getChannel("switch0").getUID(), switch0.equals("on") ? OnOffType.ON : OnOffType.OFF);
        }
        if (switch1 != null && channel1 == true) {
            updateState(this.thing.getChannel("switch1").getUID(), switch1.equals("on") ? OnOffType.ON : OnOffType.OFF);
        }
        if (switch2 != null && channel2 == true) {
            updateState(this.thing.getChannel("switch2").getUID(), switch2.equals("on") ? OnOffType.ON : OnOffType.OFF);
        }
        if (switch3 != null && channel3 == true) {
            updateState(this.thing.getChannel("switch3").getUID(), switch3.equals("on") ? OnOffType.ON : OnOffType.OFF);
        }

        // for (int i = 0; i < device.getParams().getSwitches().size(); i++) {
        // String _switch = device.getParams().getSwitches().get(i).getSwitch();
        // Integer outlet = device.getParams().getSwitches().get(i).getOutlet();
        // if (_switch != null) {
        // channels.put(outlet, _switch);
        // updateState(this.thing.getChannel("switch" + outlet).getUID(),
        // _switch.equals("on") ? OnOffType.ON : OnOffType.OFF);
        // }
        // }
    }

    @Override
    public void cloudUpdate(Device device) {
        logger.debug("Message Received for {} from websocket", config.deviceId);
        updateState(device);
    }

    @Override
    public void lanUpdate(JsonObject jsonObject, String ipaddress, String sequence) {
        logger.debug("Sonoff - Lan Encrypted Message:{}", gson.toJson(jsonObject));
        String message = Utils.decrypt(jsonObject, deviceKey);
        logger.debug("Sonoff - Lan Decrypted Message:{}", message);
        this.ipaddress = ipaddress;
        Device device = new Device();
        device.setParams(gson.fromJson(message, Params.class));
        updateState(device);
    }
}
