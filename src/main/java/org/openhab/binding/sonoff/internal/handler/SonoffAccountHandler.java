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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jmdns.ServiceEvent;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.sonoff.internal.SonoffCacheProvider;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.communication.SonoffCommunicationManager;
import org.openhab.binding.sonoff.internal.communication.SonoffCommunicationManagerListener;
import org.openhab.binding.sonoff.internal.config.AccountConfig;
import org.openhab.binding.sonoff.internal.connection.SonoffConnectionManager;
import org.openhab.binding.sonoff.internal.connection.SonoffConnectionManagerListener;
import org.openhab.binding.sonoff.internal.discovery.SonoffDiscoveryService;
import org.openhab.binding.sonoff.internal.dto.commands.UiActive;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffAccountHandler} is responsible for the main Ewelink Account and
 * manages the connections to devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffAccountHandler extends BaseBridgeHandler
        implements SonoffConnectionManagerListener, SonoffCommunicationManagerListener {

    private final Logger logger = LoggerFactory.getLogger(SonoffAccountHandler.class);
    private final Gson gson;
    private final SonoffCommunicationManager commandManager;
    private final SonoffConnectionManager connectionManager;

    private @Nullable ScheduledFuture<?> tokenTask;
    private @Nullable ScheduledFuture<?> connectionTask;
    private @Nullable ScheduledFuture<?> activateTask;
    private @Nullable ScheduledFuture<?> queueTask;

    private Boolean lanConnected = false;
    private Boolean cloudConnected = false;
    private String mode = "";

    private final Map<String, SonoffDeviceState> deviceStates = new HashMap<String, SonoffDeviceState>();
    private final Map<String, SonoffDeviceListener> deviceListeners = new HashMap<String, SonoffDeviceListener>();
    private final Map<String, String> ipaddresses = new HashMap<String, String>();

    public SonoffAccountHandler(Bridge thing, WebSocketClient webSocketClient, HttpClient httpClient) {
        super(thing);
        this.gson = new Gson();
        this.commandManager = new SonoffCommunicationManager(this, gson);
        this.connectionManager = new SonoffConnectionManager(webSocketClient, httpClient, this);
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(SonoffDiscoveryService.class);
    }

    @Override
    public void initialize() {
        logger.debug("Initialising Sonoff Account: {}", this.thing.getUID());

        AccountConfig config = this.getConfigAs(AccountConfig.class);
        this.mode = config.accessmode;
        logger.info("Sonoff mode set to: {}", config.accessmode);

        commandManager.start(config.accessmode);
        restoreStates();
        queueTask = scheduler.scheduleWithFixedDelay(commandManager, 0, 100, TimeUnit.MILLISECONDS);

        connectionManager.start(config.appId, config.appSecret, config.email, config.password, config.accessmode);
        createTasks();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void dispose() {
        logger.debug("Sonoff - Running dispose()");
        final ScheduledFuture<?> activateTask = this.activateTask;
        if (activateTask != null) {
            activateTask.cancel(true);
            this.activateTask = null;
        }
        final ScheduledFuture<?> tokenTask = this.tokenTask;
        if (tokenTask != null) {
            tokenTask.cancel(true);
            this.tokenTask = null;
        }
        final ScheduledFuture<?> connectionTask = this.connectionTask;
        if (connectionTask != null) {
            connectionTask.cancel(true);
            this.connectionTask = null;
        }
        final ScheduledFuture<?> queueTask = this.queueTask;
        if (queueTask != null) {
            commandManager.stopRunning();
            queueTask.cancel(true);
            this.queueTask = null;
        }
        commandManager.stop();
        connectionManager.stop();
    }

    /**
     * Creates scheduled tasks for the account handler
     *
     */
    private void createTasks() {
        // Task to refresh our login credentials
        Runnable getToken = () -> {
            logger.info("Updating Sonoff Access Tokens");
            connectionManager.refreshTokens();
        };
        tokenTask = scheduler.scheduleWithFixedDelay(getToken, 12, 12, TimeUnit.HOURS);

        // Task to check we are still connected
        Runnable connection = () -> {
            logger.debug("Running connection check task");
            connectionManager.checkConnection();
        };
        connectionTask = scheduler.scheduleWithFixedDelay(connection, 60, 60, TimeUnit.SECONDS);

        Runnable activate = () -> {
            logger.debug("Running Activation task");
            connectionManager.sendPing();
            // Check all devices to see if their online status has changed
            // queueMessage(new SonoffCommandMessage());
            // For each device that supports streaming data send activation
            if (!mode.equals("local")) {
                for (Map.Entry<String, SonoffDeviceState> entry : this.deviceStates.entrySet()) {
                    // Get the device status so we can check whether its back online
                    // queueMessage(new SonoffCommandMessage(entry.getKey()));
                    // if online send streaming data activation for certain devices
                    Integer uiid = entry.getValue().getUiid();
                    if (uiid.equals(5) || uiid.equals(32) || uiid.equals(15)) {
                        if (entry.getValue().getCloud()) {
                            UiActive uiActive = new UiActive();
                            uiActive.setUiActive(60);
                            SonoffCommandMessage message = new SonoffCommandMessage("uiActive",
                                    entry.getValue().getDeviceid(), false, uiActive);
                            queueMessage(message);
                        }
                    }
                }
            }
        };
        activateTask = scheduler.scheduleWithFixedDelay(activate, 20, 60, TimeUnit.SECONDS);
    }

    // Updates the account status
    private synchronized void updateStatus() {
        ThingStatus status = ThingStatus.ONLINE;
        String detail = null;
        if ((mode.equals("local") && !lanConnected) || (mode.equals("cloud") && !cloudConnected)
                || (mode.equals("mixed") && (!lanConnected || !cloudConnected))) {
            status = ThingStatus.OFFLINE;
            commandManager.stopRunning();
        }

        if (mode.equals("mixed") && lanConnected && !cloudConnected) {
            detail = "Cloud Offline";
        } else if (mode.equals("mixed") && !lanConnected && cloudConnected) {
            detail = "LAN Offline";
        }

        if (detail != null) {
            commandManager.startRunning();
            updateStatus(ThingStatus.ONLINE, ThingStatusDetail.COMMUNICATION_ERROR, detail);

        } else {
            commandManager.startRunning();
            updateStatus(status);
        }
    }

    @Override
    public void isConnected(Boolean lanConnected, Boolean cloudConnected) {
        this.lanConnected = lanConnected;
        this.cloudConnected = cloudConnected;
        commandManager.isConnected(lanConnected, cloudConnected);
        updateStatus();
    }

    /**
     * Send LAN messages coming back from the message provider
     *
     */
    @Override
    public void sendLanMessage(String url, String payload) {
        connectionManager.sendLanMessage(url, payload);
    }

    /**
     * Send Api messages coming back from the message provider
     *
     */
    @Override
    public void sendApiMessage(String deviceid) {
        connectionManager.sendApiMessage(deviceid);
    }

    /**
     * Send Websocket messages coming back from the message provider
     *
     */
    @Override
    public void sendWebsocketMessage(String params) {
        connectionManager.sendWebsocketMessage(params);
    }

    /**
     * Allows devices to add a device listener to the messaging service
     *
     */
    public void addDeviceListener(String deviceid, SonoffDeviceListener listener) {
        deviceListeners.put(deviceid, listener);
    }

    /**
     * Allows devices to remove a device listener from the messaging service
     *
     */
    public void removeDeviceListener(String deviceid) {
        deviceListeners.remove(deviceid);
    }

    public void addLanService(String deviceid) {
        connectionManager.addSubService(deviceid);
    }

    public void removeLanService(String deviceid) {
        connectionManager.removeSubService(deviceid);
    }

    /**
     * Allows the discovery service to use the current connections
     * 
     */
    public SonoffConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    /**
     * Allows devices to add a message to the queue ready to send
     *
     */
    public void queueMessage(SonoffCommandMessage message) {
        commandManager.queueMessage(message);
    }

    /**
     * Allows devices to get the current mode
     *
     */
    public String getMode() {
        return this.mode;
    }

    private void restoreStates() {
        SonoffCacheProvider cacheProvider = new SonoffCacheProvider(gson);
        Map<String, SonoffDeviceState> deviceStates = cacheProvider.getStates();
        for (Map.Entry<String, SonoffDeviceState> entry : deviceStates.entrySet()) {
            if (ipaddresses.containsKey(entry.getKey())) {
                entry.getValue().setIpAddress(new StringType(ipaddresses.get(entry.getKey())));
                entry.getValue().setLocal(true);
            }
            this.deviceStates.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    /**
     * To add a new state (Used for discovery)
     *
     */
    public void addState(String deviceid) {
        SonoffCacheProvider cacheProvider = new SonoffCacheProvider(gson);
        SonoffDeviceState state = cacheProvider.getState(deviceid);
        if (state != null) {
            if (ipaddresses.containsKey(deviceid)) {
                state.setIpAddress(new StringType(ipaddresses.get(deviceid)));
                state.setLocal(true);
            }
            this.deviceStates.putIfAbsent(deviceid, state);
        }
    }

    /**
     * Allows devices to retrieve the current state
     *
     */
    @Override
    public @Nullable SonoffDeviceState getState(String deviceid) {
        if (this.deviceStates.containsKey(deviceid)) {
            return this.deviceStates.get(deviceid);
        } else {
            return null;
        }
    }

    @Override
    public @Nullable SonoffDeviceListener getListener(String deviceid) {
        if (this.deviceListeners.containsKey(deviceid)) {
            return this.deviceListeners.get(deviceid);
        } else {
            return null;
        }
    }

    @Override
    public void serviceAdded(@Nullable ServiceEvent event) {
        commandManager.serviceAdded(event);
    }

    @Override
    public void serviceRemoved(@Nullable ServiceEvent event) {
        commandManager.serviceRemoved(event);
    }

    @Override
    public void serviceResolved(@Nullable ServiceEvent event) {
        commandManager.serviceResolved(event);
        String localAddress = event.getInfo().getInet4Addresses()[0].getHostAddress();
        if (!localAddress.equals("null")) {
            String deviceid = event.getInfo().getPropertyString("id");
            ipaddresses.put(deviceid, localAddress);
            logger.debug("Added IP Address {} for device {}", deviceid, localAddress);
        }
    }

    @Override
    public void lanResponse(String message) {
        commandManager.lanResponse(message);
    }

    @Override
    public void websocketMessage(String message) {
        commandManager.websocketMessage(message);
    }

    @Override
    public void apiMessage(JsonObject thingResponse) {
        commandManager.apiMessage(thingResponse);
    }

    @Override
    public void setApiKey(String apiKey) {
        commandManager.setApiKey(apiKey);
    }
}
