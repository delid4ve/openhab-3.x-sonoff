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
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.config.DeviceConfig;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SonoffBaseZigbeeHandler} allows the handling of commands and updates to Devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public abstract class SonoffBaseZigbeeHandler extends SonoffBaseDeviceHandler {

    private final Logger logger = LoggerFactory.getLogger(SonoffBaseZigbeeHandler.class);

    protected @Nullable SonoffZigbeeBridgeHandler zigbeeBridge;

    public SonoffBaseZigbeeHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initialising device: {}", this.thing.getUID());
        DeviceConfig config = this.getConfigAs(DeviceConfig.class);
        this.deviceid = config.deviceid;
        Bridge bridge = getBridge();
        if (bridge != null) {
            SonoffZigbeeBridgeHandler zigbeeBridge = (SonoffZigbeeBridgeHandler) bridge.getHandler();
            if (zigbeeBridge != null) {
                this.zigbeeBridge = zigbeeBridge;
                SonoffAccountHandler account = zigbeeBridge.account;
                if (account != null) {
                    this.account = account;
                    // Initialize the state of the device
                    SonoffDeviceState state = account.getState(this.deviceid);
                    if (state != null) {
                        if (account.getMode().equals("local")) {
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                    "Local Mode Not supported by device");
                            return;
                        }

                        setProperties(state.getProperties());
                        account.addDeviceListener(this.deviceid, this);
                        checkBridge();
                    } else {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                                "This device has not been initilized, please run discovery");
                        return;
                    }
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                            "Bridge Not set on Zigbee Bridge");
                    return;
                }
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
                return;
            }
        }
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        SonoffAccountHandler account = this.account;
        if (account != null) {
            account.removeDeviceListener(this.deviceid);
        }
        cancelTasks();
        this.cloud = false;
        this.local = false;
        this.account = null;
        this.zigbeeBridge = null;
        super.dispose();
    }

    public abstract void handleCommand(ChannelUID channelUID, Command command);

    public abstract void updateDevice(SonoffDeviceState newDevice);
}
