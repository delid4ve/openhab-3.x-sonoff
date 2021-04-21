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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.dto.commands.SLed;
import org.openhab.binding.sonoff.internal.dto.commands.ZLed;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

/**
 * The {@link SonoffZigbeeBridgeHandler} is responsible for Zigbee Bridge Devices and manages the connections to child
 * devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffZigbeeBridgeHandler extends SonoffBaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(SonoffZigbeeBridgeHandler.class);

    public SonoffZigbeeBridgeHandler(Bridge thing) {
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
                case "zled":
                    ZLed zled = new ZLed();
                    zled.setZled(command.toString().toLowerCase());
                    message = new SonoffCommandMessage("zled", getDeviceid(), false, zled);
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
        // Other
        updateState("rssi", newDevice.getParameters().getRssi());
        updateState("sled", newDevice.getParameters().getNetworkLED());
        updateState("zled", newDevice.getParameters().getZigbeeLED());
        // Connections
        this.cloud = newDevice.getCloud();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();
    }

    // required for discovery
    public JsonArray getSubDevices() {
        JsonArray subDevices = new JsonArray();
        SonoffAccountHandler account = this.account;
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
