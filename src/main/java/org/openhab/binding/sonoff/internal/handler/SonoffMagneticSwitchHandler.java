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
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;

/**
 * The {@link SonoffMagneticSwitchHandler} allows the handling of commands and updates to Magnetic Switch Type Devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffMagneticSwitchHandler extends SonoffBaseDeviceHandler {

    public SonoffMagneticSwitchHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void updateDevice(SonoffDeviceState newDevice) {
        // Switches
        updateState("switch", newDevice.getParameters().getSwitch0());
        // Other
        updateState("rssi", newDevice.getParameters().getRssi());
        updateState("battery", newDevice.getParameters().getBattery());
        // Action Times
        updateState("lastUpdate", newDevice.getParameters().getLastUpdate());
        updateState("actionTime", newDevice.getParameters().getActionTime());
        // Connections
        // Connections
        this.cloud = newDevice.getCloud();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();
    }

    @Override
    public synchronized void updateStatus() {
        ThingStatus status = ThingStatus.ONLINE;
        String detail = null;
        SonoffAccountHandler account = this.account;
        if (account != null) {
            String mode = account.getMode();
            // Boolean cloudConnected = account.getCloudConnected();

            if (mode.equals("local")) {
                status = ThingStatus.OFFLINE;
                detail = "Local Mode Not supported by device";
            }
        }

        if (detail != null) {
            updateStatus(status, ThingStatusDetail.COMMUNICATION_ERROR, detail);
        } else {
            updateStatus(status);
        }
    }

    @Override
    public void startTasks() {
    }

    @Override
    public void cancelTasks() {
    }
}
