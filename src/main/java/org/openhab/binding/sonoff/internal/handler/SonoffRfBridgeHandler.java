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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.dto.commands.SLed;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

/**
 * The {@link SonoffRfBridgeHandler} is responsible for RF Bridge Devices and manages the connections to child devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffRfBridgeHandler extends SonoffBaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(SonoffRfBridgeHandler.class);

    public SonoffRfBridgeHandler(Bridge thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        SonoffCommandMessage message = null;
        if (command instanceof RefreshType) {
            return;
        } else {
            switch (channelUID.getId()) {
                case "sled":
                    SLed sled = new SLed();
                    sled.setSledOnline(command.toString().toLowerCase());
                    message = new SonoffCommandMessage("sledOnline", getDeviceid(), false, sled);
                    break;
            }
            if (message != null) {
                queueMessage(message);
            } else {
                logger.debug("Unable to send command as was null for device {}", getDeviceid());
            }
        }
    }

    @Override
    public void updateDevice(SonoffDeviceState newDevice) {
        logger.debug("Updating rf bridge");
        // Other
        updateState("rssi", newDevice.getParameters().getRssi());
        updateState("sled", newDevice.getParameters().getNetworkLED());
        updateState("ipaddress", newDevice.getIpAddress());
        // Connections
        this.cloud = newDevice.getCloud();
        this.local = newDevice.getLocal();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateState("localOnline", this.local ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();

        // handle new updates to rf External triggers
        Map<Integer, DateTimeType> channels = new HashMap<Integer, DateTimeType>();
        channels.put(0, newDevice.getParameters().getRf0());
        channels.put(1, newDevice.getParameters().getRf1());
        channels.put(2, newDevice.getParameters().getRf2());
        channels.put(3, newDevice.getParameters().getRf3());
        channels.put(4, newDevice.getParameters().getRf4());
        channels.put(5, newDevice.getParameters().getRf5());
        channels.put(6, newDevice.getParameters().getRf6());
        channels.put(7, newDevice.getParameters().getRf7());
        channels.put(8, newDevice.getParameters().getRf8());
        channels.put(9, newDevice.getParameters().getRf9());
        channels.put(10, newDevice.getParameters().getRf10());
        channels.put(11, newDevice.getParameters().getRf11());
        channels.put(12, newDevice.getParameters().getRf12());
        channels.put(13, newDevice.getParameters().getRf13());
        channels.put(14, newDevice.getParameters().getRf14());
        channels.put(15, newDevice.getParameters().getRf15());

        for (Map.Entry<Integer, DateTimeType> entry : channels.entrySet()) {
            Integer key = entry.getKey();
            SonoffRfDeviceListener listener = rfListeners.get(key + "");
            if (listener != null) {
                listener.rfTriggered(key, entry.getValue());
            }
        }

        for (int i = 0; i < newDevice.getParameters().getRfCodeList().size(); i++) {
            Integer rfChl = newDevice.getParameters().getRfCodeList().get(i).getAsJsonObject().get("rfChl").getAsInt();
            String rfVal = newDevice.getParameters().getRfCodeList().get(i).getAsJsonObject().get("rfVal")
                    .getAsString();
            SonoffRfDeviceListener rflistener = rfListeners.get(rfChl.toString());
            if (rflistener != null) {
                rflistener.rfCode(rfChl, rfVal);
            }
        }
    }

    public void addListener(String deviceid, SonoffRfDeviceListener listener) {
        rfListeners.putIfAbsent(deviceid, listener);
    }

    public void removeListener(String deviceid) {
        if (rfListeners.containsKey(deviceid)) {
            rfListeners.remove(deviceid);
        }
    }

    // required for discovery
    public JsonArray getSubDevices() {
        SonoffAccountHandler account = this.account;
        JsonArray subDevices = new JsonArray();
        if (account != null) {
            SonoffDeviceState state = account.getState(this.deviceid);
            if (state != null) {
                subDevices = state.getSubDevices();
            }
        }
        return subDevices;
    }

    @Override
    public void startTasks() {
    }

    @Override
    public void cancelTasks() {
    }
}
