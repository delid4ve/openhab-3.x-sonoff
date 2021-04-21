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

import java.net.URI;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.sonoff.internal.dto.requests.WebsocketRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffWebSocketConnection} class is the websocket Connection to the Ewelink API to
 * enable streaming data and uses the shared websocketClient
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
@WebSocket
public class SonoffWebSocketConnection {

    private final Logger logger = LoggerFactory.getLogger(SonoffWebSocketConnection.class);
    private final WebSocketClient websocketClient;
    private final SonoffWebSocketConnectionListener listener;
    private final Gson gson;
    private String url = "";
    private String apiKey = "";
    private String at = "";

    private @Nullable Session session;

    public SonoffWebSocketConnection(SonoffWebSocketConnectionListener listener, WebSocketClient websocketClient) {
        this.websocketClient = websocketClient;
        this.listener = listener;
        this.gson = new Gson();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public void login() {
        WebsocketRequest request = new WebsocketRequest(this.apiKey, this.at);
        sendMessage(gson.toJson(request));
    }

    public void start() {
        if (!url.equals("")) {
            try {
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                URI uri = new URI(url);
                websocketClient.connect(this, uri, request);
            } catch (Exception e) {
                logger.error("Websocket Login Exception:{}, {}", e.getMessage(), e.getStackTrace());
                listener.websocketConnected(false);
            }
        } else {
            logger.error("Unable to start websocket as the server address is not set");
            listener.websocketConnected(false);
        }
    }

    public void sendPing() {
        sendMessage("ping");
    }

    public void stop() {
        logger.debug("Stopping websocket client");
        final Session session = this.session;
        if (session != null) {
            session.close();
        }
    }

    public void sendMessage(String message) {
        logger.debug("Websocket Sending Message:{}", message);
        Session session = this.session;
        if (session != null) {
            session.getRemote().sendStringByFuture(message);
        } else {
            logger.error("WebSocket couldn't send the message {} as the session was null", message);
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        logger.debug("WebSocket Socket successfully connected to {}", session.getRemoteAddress().getAddress());
        listener.websocketConnected(true);
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        if (message.contains("pong")) {
            logger.debug("Pong Response received");
            return;
        }

        if (message.contains("hbInterval")) {
            logger.debug("Login Response Received: {}", message);
            JsonObject object = gson.fromJson(message, JsonObject.class);
            if (object.get("error").getAsInt() == 0) {
                listener.webSocketLoggedIn(true);
            } else {
                listener.websocketConnected(false);
            }
            return;
        } else {
            logger.debug("WebSocket message received {}", message);
            listener.websocketMessage(message);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (statusCode == 1000) {
            logger.debug("Websocket Sucessfully closed");
        } else {
            logger.error("Websocket Closed, Status Code: {}, Reason:{}", statusCode, reason);
        }
        listener.websocketConnected(false);
        this.session = null;
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        String reason = cause.getMessage();
        if (reason != null) {
            onClose(0, reason);
        } else {
            onClose(0, "");
        }
    }
}
