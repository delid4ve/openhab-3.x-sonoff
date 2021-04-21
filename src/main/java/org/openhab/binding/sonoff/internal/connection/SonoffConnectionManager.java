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
package org.openhab.binding.sonoff.internal.connection;

import javax.jmdns.ServiceEvent;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * The {@link SonoffConnectionManager} Manages the external connections
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffConnectionManager
        implements SonoffApiConnectionListener, SonoffWebSocketConnectionListener, SonoffLanConnectionListener {

    private final Logger logger = LoggerFactory.getLogger(SonoffConnectionManager.class);
    private final SonoffConnectionManagerListener listener;
    private final SonoffLanConnection lan;
    private final SonoffApiConnection api;
    private final SonoffWebSocketConnection webSocket;

    private Boolean webSocketConnected = false;
    private Boolean webSocketLoggedIn = false;
    private Boolean lanConnected = false;
    private String mode = "";

    public SonoffConnectionManager(WebSocketClient webSocketClient, HttpClient httpClient,
            SonoffConnectionManagerListener listener) {
        this.listener = listener;
        this.api = new SonoffApiConnection(this, httpClient);
        this.lan = new SonoffLanConnection(this, httpClient);
        this.webSocket = new SonoffWebSocketConnection(this, webSocketClient);
    }

    public void start(String email, String password, String mode) {
        this.mode = mode;
        api.setEmail(email);
        api.setPassword(password);
        if (!mode.equals("cloud")) {
            lan.start();
        }
        if (!mode.equals("local")) {
            api.login();
        }
    }

    public void stop() {
        this.lanConnected = lan.stop();
        webSocket.stop();
        webSocketConnected = false;
        webSocketLoggedIn = false;
        lanConnected = false;
        mode = "";
    }

    public void checkConnection() {
        logger.debug("Connection Check Running for {} mode ", mode);

        // Start the LAN connection if its not connected
        if (!mode.equals("cloud") && !lanConnected) {
            lan.start();
        }

        // Start the websocket connection if its not connected
        if (!mode.equals("local") && !webSocketLoggedIn) {
            api.login();
        }
    }

    public void refreshTokens() {
        if (!mode.equals("local") && webSocketLoggedIn) {
            api.login();
        }
    }

    @Override
    public void lanConnected(Boolean connected) {
        this.lanConnected = connected;
        logger.debug("Lan {}", connected ? "connected" : "disconnected");
        listener.isConnected(this.lanConnected, this.webSocketLoggedIn);
    }

    @Override
    public void apiConnected(Boolean connected, String apiKey, String at) {
        logger.debug("Api {}", connected ? "connected" : "disconnected");
        webSocket.setAt(api.getAt());
        webSocket.setApiKey(api.getApiKey());
        listener.setApiKey(apiKey);

        // discovery uses the api only if in local mode
        if (this.mode.equals("local")) {
            return;
        }

        if (connected) {
            if (!webSocketConnected) {
                String url = api.getWebsocketServer();
                if (url != "") {
                    webSocket.setUrl(url);
                    webSocket.start();
                }
            } else {
                webSocket.login();
            }
        } else {
            webSocket.stop();
        }
    }

    @Override
    public void websocketConnected(Boolean connected) {
        this.webSocketConnected = connected;
        logger.debug("Websocket {}", connected ? "connected" : "disconnected");
        if (connected) {
            webSocket.login();
        } else {
            this.webSocketLoggedIn = false;
            listener.isConnected(this.lanConnected, this.webSocketLoggedIn);
        }
    }

    @Override
    public void webSocketLoggedIn(Boolean loggedIn) {
        this.webSocketLoggedIn = loggedIn;
        listener.isConnected(this.lanConnected, this.webSocketLoggedIn);
    }

    public void sendPing() {
        if (webSocketLoggedIn) {
            webSocket.sendPing();
        }
    }

    public void getStates() {
        api.getDevices();
    }

    /**
     * Allows the discovery service to use the current api connection
     * 
     */
    public SonoffApiConnection getApi() {
        return this.api;
    }

    /**
     * Allows the discovery service to check what mode we are in
     * 
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * Allows devices to add a sub service to the LAN connection
     * in order to initially rsesolve the state
     */
    public void addSubService(String deviceid) {
        if (lanConnected) {
            lan.addSubService(deviceid);
        } else {
            logger.error("Unable to add lan service for device {} as the connection is offline", deviceid);
        }
    }

    /**
     * Allows devices to remove a sub service to the LAN connection
     * 
     */
    public void removeSubService(String deviceid) {
        if (lanConnected) {
            lan.removeSubService(deviceid);
        } else {
            logger.error("Unable to remove lan service for device {} as the connection is offline", deviceid);
        }
    }

    /**
     * Send LAN messages coming back from the message provider
     *
     */
    public void sendLanMessage(String url, String payload) {
        lan.sendMessage(url, payload);
    }

    /**
     * Send Api messages coming back from the message provider
     *
     */
    public void sendApiMessage(String deviceid) {
        if (!webSocketLoggedIn) {
            logger.debug("Unable to request cloud update as the connection is offline");
        } else {
            if (deviceid != "") {
                api.getDevice(deviceid);
            } else {
                api.getDevices();
            }
        }
    }

    /**
     * Send Websocket messages coming back from the message provider
     *
     */
    public void sendWebsocketMessage(String params) {
        webSocket.sendMessage(params);
    }

    @Override
    public void serviceAdded(@Nullable ServiceEvent event) {
        listener.serviceAdded(event);
    }

    @Override
    public void serviceRemoved(@Nullable ServiceEvent event) {
        listener.serviceRemoved(event);
    }

    @Override
    public void serviceResolved(@Nullable ServiceEvent event) {
        listener.serviceResolved(event);
    }

    @Override
    public void lanResponse(String message) {
        listener.lanResponse(message);
    }

    @Override
    public void websocketMessage(String message) {
        listener.websocketMessage(message);
    }

    @Override
    public void apiMessage(JsonObject thingResponse) {
        listener.apiMessage(thingResponse);
    }
}
