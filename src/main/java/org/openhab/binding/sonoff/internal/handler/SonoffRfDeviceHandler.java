/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.sonoff.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.config.DeviceConfig;
import org.openhab.binding.sonoff.internal.dto.commands.RFChannel;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RFDeviceHandler} allows the handling of commands and updates to RF Remotes
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffRfDeviceHandler extends BaseThingHandler implements SonoffRfDeviceListener {

    private final Logger logger = LoggerFactory.getLogger(SonoffRfDeviceHandler.class);
    private @Nullable SonoffRfBridgeHandler rfBridge;

    private String deviceid = "";

    public SonoffRfDeviceHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initialising device: {}", this.thing.getUID());
        deviceid = this.getConfigAs(DeviceConfig.class).deviceid;
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
            return;
        } else {
            rfBridge = (SonoffRfBridgeHandler) bridge.getHandler();
            addListener();
        }
        if (bridge.getStatus() == ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        }
    }

    private void addListener() {
        SonoffRfBridgeHandler rfBridge = this.rfBridge;
        if (rfBridge != null) {
            if (this.getThing().getThingTypeUID().getId().equals("rfsensor")) {
                rfBridge.addListener(deviceid, this);
            } else {
                Integer buttons = Integer.parseInt(this.getThing().getThingTypeUID().getId().substring(8));
                for (int i = 0; i < buttons; i++) {
                    String number = (Integer.parseInt(deviceid) + i) + "";
                    rfBridge.addListener(number, this);
                }
            }
        }
    }

    private void removeListener() {
        SonoffRfBridgeHandler rfBridge = this.rfBridge;
        if (rfBridge != null) {
            if (this.getThing().getThingTypeUID().getId().equals("rfsensor")) {
                rfBridge.removeListener(deviceid);
            } else {
                Integer buttons = Integer.parseInt(this.getThing().getThingTypeUID().getId().substring(8));
                for (int i = 0; i < buttons; i++) {
                    String number = (Integer.parseInt(deviceid) + i) + "";
                    rfBridge.removeListener(number);
                }
            }
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged {} for thing {}", bridgeStatusInfo, getThing().getUID());
        if (rfBridge != null) {
            if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        }
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        removeListener();
        this.rfBridge = null;
        super.dispose();
    }

    private void queueMessage(String endpoint, RFChannel rfChannel) {
        SonoffRfBridgeHandler rfBridge = this.rfBridge;
        if (rfBridge != null) {
            SonoffCommandMessage message = new SonoffCommandMessage(endpoint, rfBridge.getDeviceid(), true, rfChannel);
            rfBridge.queueMessage(message);
        } else {
            logger.error("Couldnt send RF Command for device {} as the bridge returned null", deviceid);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        } else {
            if (command.toString().toLowerCase().equals("on") && channelUID.getId().contains("button")) {
                RFChannel rfChannel = new RFChannel();
                Integer button = Integer.parseInt(channelUID.getId().substring(6));
                Integer no = Integer.parseInt(deviceid) + button;
                rfChannel.setCmd("transmit");
                rfChannel.setRfChannel(no);
                String endpoint = "transmit";
                queueMessage(endpoint, rfChannel);
                Channel channel = this.thing.getChannel("rf" + button + "Internal");
                if (channel != null) {
                    updateState(channel.getUID(), new DateTimeType(System.currentTimeMillis() + ""));
                }
                updateState(channelUID, OnOffType.OFF);
            }
        }
    }

    @Override
    public void rfTriggered(Integer chl, DateTimeType date) {
        String channelName = "rf" + (chl - Integer.parseInt(deviceid)) + "External";
        updateState(channelName, date);
    }

    @Override
    public void rfCode(Integer chl, String rfVal) {
        Map<String, String> properties = editProperties();
        properties.put("RF Code For Channel " + (chl - Integer.parseInt(deviceid)), rfVal);
        updateProperties(properties);
    }
}
