package org.openhab.binding.sonoff.internal.connections;

import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.smarthome.io.net.http.WebSocketFactory;
import org.openhab.binding.sonoff.internal.Constants;
import org.openhab.binding.sonoff.internal.Utils;
import org.openhab.binding.sonoff.internal.dto.api.Device;
import org.openhab.binding.sonoff.internal.dto.api.WsMessage;
import org.openhab.binding.sonoff.internal.dto.api.WsServerResponse;
import org.openhab.binding.sonoff.internal.dto.payloads.WsLoginRequest;
import org.openhab.binding.sonoff.internal.dto.payloads.WsUpdate;
import org.openhab.binding.sonoff.internal.listeners.WebSocketConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebSocket
public class Websocket {

    private final Logger logger = LoggerFactory.getLogger(Websocket.class);
    private final WebSocketClient webSocketClient;
    private final Api api;
    private final Gson gson;
    private Session session;
    private final WebSocketConnectionListener connectionListener;
    private long lastSequence;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pingTask;
    private Boolean connected = false;

    public Websocket(WebSocketFactory webSocketFactory, Gson gson, Api api,
            WebSocketConnectionListener connectionListener) {
        this.webSocketClient = webSocketFactory.createWebSocketClient("SonoffWebSocket");
        this.webSocketClient.setMaxIdleTimeout(86400);
        this.gson = gson;
        this.api = api;
        this.connectionListener = connectionListener;
    }

    public synchronized void start() {
        try {
            webSocketClient.start();
            WsServerResponse response = api.getWsServer();
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            URI uri = new URI("wss://" + response.getDomain() + ":" + response.getPort() + "/api/ws");
            webSocketClient.setAsyncWriteTimeout(10000);
            webSocketClient.setMaxTextMessageBufferSize(10000);
            webSocketClient.connect(this, uri, request);
            Runnable wsKeepAlive = () -> {
                if (connected) {
                    sendMessage("ping");
                    logger.debug("Ping Sent");
                }
            };
            pingTask = scheduler.scheduleWithFixedDelay(wsKeepAlive, 30, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
    }

    public void stop() {
        try {
            if (pingTask != null) {
                pingTask.cancel(true);
                pingTask = null;
            }
            webSocketClient.stop();
        } catch (Exception e) {
            logger.debug("Error while closing connection", e);
        }
        webSocketClient.destroy();
    }

    public void login() {
        WsLoginRequest loginRequest = new WsLoginRequest(api.getAt(), api.getApiKey(), Constants.appid,
                Utils.getNonce());
        // sentMessages.put(loginRequest.getSequence(), "login");
        logger.debug("Sonoff - login json: {}", gson.toJson(loginRequest));
        queueMessage(loginRequest.getSequence(), gson.toJson(loginRequest));
    }

    private void sendMessage(String message) {
        logger.debug("Message sent: {}", message);
        this.session.getRemote().sendStringByFuture(message);
    }

    public void sendChange(String data, String deviceid, String deviceKey) {
        JsonObject payload = new JsonParser().parse(data).getAsJsonObject();
        WsUpdate device = new WsUpdate(api.getAt(), api.getApiKey(), deviceKey, deviceid, Utils.getNonce(), payload,
                Utils.getSequence());
        queueMessage(device.getSequence(), gson.toJson(device));
    }

    private synchronized void queueMessage(Long sequence, String message) {
        sendMessage(message);
        if (lastSequence - sequence < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session wssession) {
        session = wssession;
        logger.debug("Sonoff - WebSocket Socket {} successfully connected to {}", this,
                session.getRemoteAddress().getAddress());
        login();
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        logger.debug("Websocket Message recieved: {}", message);
        if (message.contains("pong")) {
            logger.debug("Websocket Pong Response");
        } else {
            WsMessage wsMessage = gson.fromJson(message, WsMessage.class);
            if (wsMessage.getError() == null) {
                Device device = gson.fromJson(message, Device.class);
                if (device != null) {
                    connectionListener.webSocketMessage(device);
                }
            } else {
                connected = (wsMessage.getError() > 0) ? false : true;
                logger.debug("Websocket Connected: {}", connected);
                connectionListener.webSocketConnectionOpen(connected);
            }
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        connected = false;
        connectionListener.webSocketConnectionOpen(connected);
        logger.debug("Websocket Connection Closed, Status Code: {}, Reason: {}", statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        onClose(0, cause.getMessage());
    }
}
