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
import org.openhab.core.types.*;

/**
 * The {@link SonoffZigbeeDevice2026Handler} is responsible for updates and handling commands to/from Zigbee Devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffZigbeeDevice2026Handler extends SonoffBaseZigbeeHandler {

    public SonoffZigbeeDevice2026Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void updateDevice(SonoffDeviceState newDevice) {
        // Motion
        updateState("motion", newDevice.getParameters().getMotion());
        updateState("battery", newDevice.getParameters().getBattery());
        updateState("trigTime", newDevice.getParameters().getTrigTime());
        // Connections
        this.cloud = newDevice.getCloud();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();
    }

    @Override
    public void startTasks() {
    }

    @Override
    public void cancelTasks() {
    }
}
