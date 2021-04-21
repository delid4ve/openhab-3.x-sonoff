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
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.SonoffBindingConstants;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.config.DeviceConfig;
import org.openhab.binding.sonoff.internal.dto.commands.SLed;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HandlerBase} allows the handling of commands and updates to Devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public abstract class SonoffBaseBridgeHandler extends BaseBridgeHandler implements SonoffDeviceListener {

    private final Logger logger = LoggerFactory.getLogger(SonoffBaseBridgeHandler.class);

    protected String deviceid = "";
    protected Boolean cloud = false;
    protected Boolean local = false;
    protected Boolean isLocalIn = false;
    protected Boolean isLocalOut = false;

    protected @Nullable SonoffAccountHandler account;
    protected final Map<String, SonoffRfDeviceListener> rfListeners = new HashMap<>();

    public SonoffBaseBridgeHandler(Bridge thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initialising device: {}", this.thing.getUID());
        DeviceConfig config = this.getConfigAs(DeviceConfig.class);
        this.deviceid = config.deviceid;
        Bridge bridge = getBridge();
        if (bridge != null) {
            this.account = (SonoffAccountHandler) bridge.getHandler();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Bridge Not set");
            return;
        }

        SonoffAccountHandler account = this.account;
        if (account != null) {
            // Initialize the state of the device
            SonoffDeviceState state = account.getState(this.deviceid);
            if (state == null) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "This device has not been initilized, please run discovery");
                return;
            } else {
                // Check whether we are a local only device
                if (SonoffBindingConstants.LAN_IN.contains(state.getUiid())) {
                    isLocalIn = true;
                }
                if (SonoffBindingConstants.LAN_OUT.contains(state.getUiid())) {
                    this.isLocalOut = true;
                }

                if (account.getMode().equals("local") && !isLocalIn) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "Local Mode Not supported by device");
                    return;
                }

                setProperties(state.getProperties());
                account.addDeviceListener(this.deviceid, this);
                // Get initial connection statuses
                checkBridge();
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
        super.dispose();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged {} for thing {}", bridgeStatusInfo, getThing().getUID());
        SonoffAccountHandler account = this.account;
        if (account != null) {
            if (bridgeStatusInfo.getStatus().equals(ThingStatus.ONLINE)) {
                if (isLocalIn) {
                    account.addLanService(deviceid);
                    // account.requestLanUpdate(deviceid);
                }
                account.queueMessage(new SonoffCommandMessage(deviceid));
                startTasks();
                updateStatus();
            } else {
                if (isLocalIn) {
                    account.removeLanService(deviceid);
                }
                cancelTasks();
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Bridge Offline");
            }
        }
    }

    private void checkBridge() {
        Bridge bridge = this.getBridge();
        if (bridge != null) {
            bridgeStatusChanged(bridge.getStatusInfo());
        }
    }

    protected void setProperties(Map<String, String> properties) {
        updateProperties(properties);
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

    public void queueMessage(SonoffCommandMessage message) {
        logger.debug("Sonoff - Command Payload:{}", message.getParams());
        SonoffAccountHandler account = this.account;
        if (account != null) {
            account.queueMessage(message);
        } else {
            logger.debug("Couldn't send Command {} with parameters {} as the bridge returned null",
                    message.getCommand(), message.getParams());
        }
    }

    public synchronized void updateStatus() {
        ThingStatus status = ThingStatus.ONLINE;
        String detail = null;
        SonoffAccountHandler account = this.account;
        if (account != null) {
            String mode = account.getMode();

            if (mode.equals("local")) {
                if (!isLocalIn) {
                    status = ThingStatus.OFFLINE;
                    detail = "Local Mode Not supported by device";
                } else {
                    if (!local) {
                        status = ThingStatus.OFFLINE;
                    }
                }
            }

            if (mode.equals("cloud")) {
                if (!cloud) {
                    status = ThingStatus.OFFLINE;
                }
            }

            if (mode.equals("mixed")) {
                if (!isLocalIn && !cloud) {
                    status = ThingStatus.OFFLINE;
                } else if (!isLocalIn && cloud) {
                    status = ThingStatus.ONLINE;
                } else {
                    if (!cloud && local) {
                        detail = "Cloud Offline";
                    }
                    if (!local && cloud) {
                        detail = "LAN Offline";
                    }
                    if (!local && !cloud) {
                        status = ThingStatus.OFFLINE;
                    }
                }
            }
        }

        if (detail != null) {
            updateStatus(status, ThingStatusDetail.COMMUNICATION_ERROR, detail);
        } else {
            updateStatus(status);
        }
    }

    public abstract void startTasks();

    public abstract void cancelTasks();

    public abstract void updateDevice(SonoffDeviceState newDevice);

    public String getDeviceid() {
        return this.deviceid;
    }
}
