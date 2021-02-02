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
package org.openhab.binding.sonoff.internal.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerService;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.eclipse.smarthome.io.net.http.WebSocketFactory;
import org.openhab.binding.sonoff.internal.config.AccountConfig;
import org.openhab.binding.sonoff.internal.connections.Api;
import org.openhab.binding.sonoff.internal.connections.Lan;
import org.openhab.binding.sonoff.internal.connections.Websocket;
import org.openhab.binding.sonoff.internal.dto.api.Device;
import org.openhab.binding.sonoff.internal.dto.api.Devices;
import org.openhab.binding.sonoff.internal.listeners.DeviceStateListener;
import org.openhab.binding.sonoff.internal.listeners.MDnsListener;
import org.openhab.binding.sonoff.internal.listeners.WebSocketConnectionListener;
import org.openhab.binding.sonoff.internal.sonoffDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The {@link sonoffHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class AccountHandler extends BaseBridgeHandler implements WebSocketConnectionListener {

    private final Logger logger = LoggerFactory.getLogger(AccountHandler.class);

    private final WebSocketFactory webSocketFactory;
    private final HttpClientFactory httpClientFactory;
    private @Nullable Websocket ws;
    private @Nullable Lan lan;
    private MDnsListener mdnsListener = new MDnsListener(this);
    private @Nullable Api api;
    private @Nullable AccountConfig config;
    private @Nullable ScheduledFuture<?> tokenTask;
    private @Nullable ScheduledFuture<?> udpTask;
    private @Nullable ScheduledFuture<?> connectionTask;
    private final Gson gson;
    private Boolean lanOnline = false;
    private Boolean wsOnline = false;
    private String mode = "";
    private final Map<String, DeviceStateListener> deviceStateListener = new HashMap<>();
    final Map<String, Device> deviceState = new HashMap<>();
    // private @Nullable ReceiverThread udp;

    public AccountHandler(Bridge thing, WebSocketFactory webSocketFactory, HttpClientFactory httpClientFactory,
            Gson gson) {
        super(thing);
        this.webSocketFactory = webSocketFactory;
        this.httpClientFactory = httpClientFactory;
        this.gson = gson;
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(sonoffDiscoveryService.class);
    }

    @Override
    public void thingUpdated(Thing thing) {
        dispose();
        this.thing = thing;
        initialize();
    }

    @Override
    public void initialize() {
        // udp = new ReceiverThread();
        config = this.getConfigAs(AccountConfig.class);
        mode = config.accessmode.toString();
        api = new Api(config, httpClientFactory, gson);
        logger.debug("Sonoff - Starting Api: {}");
        api.start();
        api.login();
        logger.debug("Sonoff - Starting Discovery: {}");
        Devices devices = api.discover();
        for (int i = 0; i < devices.getDevicelist().size(); i++) {
            deviceState.put(devices.getDevicelist().get(i).getDeviceid(), devices.getDevicelist().get(i));
        }

        if ((mode.equals("mixed") || mode.equals("local"))) {
            logger.debug("Sonoff - Starting LAN Connection");
            try {
                lan = new Lan(config.ipaddress);
                lan.start();
                lan.addListener(this.thing.getUID().toString(), mdnsListener);
                lanConnectionOpen(true);
            } catch (IOException e1) {
                lanConnectionOpen(false);
            }
        }

        if ((mode.equals("mixed") || mode.equals("cloud"))) {
            ws = new Websocket(webSocketFactory, gson, api, this);
        }

        Runnable getToken = () -> {
            if ((mode.equals("mixed") || mode.equals("cloud"))) {
                try {
                    api.login();
                } catch (Exception e) {
                    logger.warn("Sonoff - Exception Logging in. Cause: {}", e.getMessage());
                }
            }
        };
        tokenTask = scheduler.scheduleWithFixedDelay(getToken, 6, 6, TimeUnit.HOURS);

        Runnable connect = () -> {
            logger.debug("Sonoff - Connection check Running");
            if ((mode.equals("mixed") || mode.equals("cloud")) && !wsOnline) {
                logger.debug("Sonoff - Starting Cloud Connection");
                ws.start();
            }
        };
        connectionTask = scheduler.scheduleWithFixedDelay(connect, 10, 60, TimeUnit.SECONDS);
        // scheduler.schedule(udp, 5, TimeUnit.MILLISECONDS);
    }

    @Override
    public void dispose() {
        logger.debug("Sonoff - Running dispose()");
        if (tokenTask != null) {
            tokenTask.cancel(true);
            tokenTask = null;
        }
        if (connectionTask != null) {
            connectionTask.cancel(true);
            connectionTask = null;
        }
        if (ws != null) {
            ws.stop();
            ws = null;
        }
        wsOnline = false;
        if (lan != null) {
            try {
                lan.stop();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            lan = null;
        }
        lanOnline = false;
        if (api != null) {
            api.stop();
            api = null;
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public @Nullable Websocket getWebsocket() {
        return ws;
    }

    public @Nullable Api getApi() {
        return api;
    }

    public @Nullable Lan getLan() {
        return lan;
    }

    public @Nullable Device getDevice(String deviceid) {
        return deviceState.get(deviceid);
    }

    public void registerStateListener(String deviceId, DeviceStateListener listener) {
        deviceStateListener.put(deviceId, listener);
        logger.debug("Sonoff - Listener Added for deviceId: {}", deviceId);
    }

    public void unregisterStateListener(String deviceId) {
        deviceStateListener.remove(deviceId);
    }

    public @Nullable AccountConfig getAccountConfig() {
        return config;
    }

    public Boolean lanOnline() {
        return lanOnline;
    }

    public Boolean wsOnline() {
        return wsOnline;
    }

    @Override
    public void webSocketConnectionOpen(Boolean connected) {
        this.wsOnline = connected;
        logger.debug("Sonoff - Websocket Connected: {}", connected);
        if (mode.equals("mixed") && lanOnline) {
            updateStatus(ThingStatus.ONLINE);
        } else if (mode.equals("cloud")) {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    public void lanConnectionOpen(Boolean connected) {
        this.lanOnline = connected;
        logger.debug("Sonoff - Lan Connected: {}", connected);
        if (mode.equals("mixed") && wsOnline) {
            updateStatus(ThingStatus.ONLINE);
        } else if (mode.equals("local")) {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    @Override
    public void webSocketMessage(Device device) {
        DeviceStateListener listener = deviceStateListener.get(device.getDeviceid());
        listener.cloudUpdate(device);
    }

    public void resolved(ServiceInfo serviceInfo) {
        JsonObject jsonObject = new JsonObject();
        Enumeration<String> info = serviceInfo.getPropertyNames();
        while (info.hasMoreElements()) {
            String name = info.nextElement().toString();
            String value = serviceInfo.getPropertyString(name);
            jsonObject.addProperty(name, value);
        }
        String seq = jsonObject.get("seq").getAsString();
        String deviceid = jsonObject.get("id").getAsString();
        String ipaddress = serviceInfo.getInet4Addresses()[0].getHostAddress();
        DeviceStateListener listener = deviceStateListener.get(deviceid);
        deviceState.get(deviceid).setLocalAddress(ipaddress);
        deviceState.get(deviceid).setSequence(seq);
        listener.lanUpdate(jsonObject, ipaddress, seq);
    }
}
