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
package org.openhab.binding.sonoff.internal.communication;

import java.util.Enumeration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.connection.SonoffConnectionManagerListener;
import org.openhab.binding.sonoff.internal.dto.requests.WebsocketRequest;
import org.openhab.binding.sonoff.internal.dto.responses.LanResponse;
import org.openhab.binding.sonoff.internal.handler.SonoffDeviceListener;
import org.openhab.binding.sonoff.internal.handler.SonoffDeviceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffCommunicationManager} provides a sequential queue for outgoing messages
 * accross connections and allows for retrying when messages are not delivered
 * correctly
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffCommunicationManager implements Runnable, SonoffConnectionManagerListener {

    private final Logger logger = LoggerFactory.getLogger(SonoffCommunicationManager.class);
    // Queue Utilities
    // Map of message retry attempts
    private final ConcurrentMap<Long, CountDownLatch> latchMap = new ConcurrentHashMap<Long, CountDownLatch>();
    // Queue of messages to send
    private final BlockingDeque<SonoffCommandMessage> queue = new LinkedBlockingDeque<SonoffCommandMessage>();
    // Map of Integers so we can count retry attempts.
    private final ConcurrentMap<Long, Integer> retryCountMap = new ConcurrentHashMap<Long, Integer>();
    // Map of our message types so we can process them correctly
    private final ConcurrentMap<Long, String> messageTypes = new ConcurrentHashMap<Long, String>();
    // Timeout
    private final int timeoutForOkMessagesMs = 1000;
    // Boolean to indicate if we are running
    private boolean running;

    private final Gson gson;
    private final SonoffCommunicationManagerListener listener;

    private String mode = "";
    private String apiKey = "";

    private Boolean lanConnected = false;
    private Boolean cloudConnected = false;

    public SonoffCommunicationManager(SonoffCommunicationManagerListener listener, Gson gson) {
        this.gson = gson;
        this.listener = listener;
    }

    public synchronized void start(String mode) {
        this.mode = mode;
        startRunning();
    }

    public synchronized void stop() {
        stopRunning();
        queue.clear();
        retryCountMap.clear();
        latchMap.clear();
        messageTypes.clear();
    }

    public void startRunning() {
        this.running = true;
    }

    public void stopRunning() {
        this.running = false;
    }

    @Override
    public void run() {
        logger.debug("Message queue is running");
        try {
            // Get the first message in the queue
            final @Nullable SonoffCommandMessage message = queue.take();
            logger.debug("{} messages remaining in queue", queue.size());
            
            if (message.getSequence().equals(0L)) {
                message.setSequence();
            }
            if (message.getCommand().equals("devices") || message.getCommand().equals("device")) {
                sendMessage(message);
            } else {
                // Add our message type so we can identify it correctly when we get the response
                messageTypes.put(message.getSequence(), message.getCommand());
                CountDownLatch latch = new CountDownLatch(1);
                latchMap.putIfAbsent(message.getSequence(), latch);
                retryCountMap.putIfAbsent(message.getSequence(), Integer.valueOf(1));
                sendMessage(message);
                boolean unlatched = latch.await(timeoutForOkMessagesMs, TimeUnit.MILLISECONDS);
                latchMap.remove(message.getSequence());
                Integer newRetryCount = 0;
                if (!unlatched) {
                    Integer sendCount = retryCountMap.get(message.getSequence());
                    if (sendCount != null) {
                        if (sendCount.intValue() >= 3) {
                            logger.warn("Unable to send transaction {}, command was {}, after {} retry attempts",
                                    message.getSequence(), message.getCommand(), 3);
                            return;
                        }
                        newRetryCount = Integer.valueOf(sendCount.intValue() + 1);
                    }
                    if (!running) {
                        logger.error("Not retrying transactionId {} as we are stopping", message.getSequence());
                        return;

                    }
                    logger.warn(
                            "Ok message not received for transaction: {}, command was {}, retrying again. Retry count {}",
                            message.getSequence(), message.getCommand(), newRetryCount);
                    retryCountMap.put(message.getSequence(), newRetryCount);
                    queue.addFirst(message);
                }
            }
        } catch (InterruptedException e) {
            if (running) {
                logger.error("Error Running queue:{}", e.getMessage());
            }
        }
    }

    // Add the messsage to the queue
    public void queueMessage(SonoffCommandMessage message) {
        try {
            if (running) {
                message.setSequence();
                queue.put(message);
                logger.debug("Added a message to the queue, {} messages in queue", queue.size());
            } else {
                logger.info("Message not added to queue as we are shutting down");
            }
        } catch (InterruptedException e) {
            logger.error("Error adding command to queue: {}", e.getMessage());
        }
    }

    private void okMessage(Long sequence) {
        CountDownLatch latch = latchMap.get(sequence);
        if (latch != null) {
            latch.countDown();
        }
    }

    /**
     * Forward messages to the appropriate connection
     *
     */
    public void sendMessage(SonoffCommandMessage message) {
        // Send Api Device requests
        if (message.getCommand().equals("device") || message.getCommand().equals("devices")) {
            listener.sendApiMessage(message.getDeviceid());
            return;
        }

        // Dont send commands if not supported by local mode
        if (!message.getLanSupported() && mode.equals("local")) {
            logger.warn("Cannot send command {} for device {}, Not supported by local mode", message.getCommand(),
                    message.getDeviceid());
            return;
        }

        // If local supported see if we can send it
        String ipaddress = "";
        String deviceKey = "";
        String url = "";
        if (message.getLanSupported() && lanConnected) {
            SonoffDeviceState state = listener.getState(message.getDeviceid());
            if (state != null) {
                deviceKey = state.getDeviceKey();
                ipaddress = state.getIpAddress().toString();
                url = "http://" + ipaddress + ":8081/zeroconf/" + message.getCommand();
            }

            // Send LAN Message
            if (!ipaddress.equals("")) {
                logger.debug("Sending message via LAN");
                listener.sendLanMessage(url, new SonoffCommandMessageEncryptionUtilities().encrypt(
                        gson.toJson(message.getParams()), deviceKey, message.getDeviceid(), message.getSequence()));
            }
            return;
        }

        // Send Websocket Message
        if (cloudConnected) {
            logger.debug("Sending message via Cloud");
            String params = gson.toJson(message.getParams().getCommand());
            WebsocketRequest request = new WebsocketRequest(message.getSequence(), apiKey, message.getDeviceid(),
                    gson.fromJson(params, JsonObject.class));
            listener.sendWebsocketMessage(gson.toJson(request));
            return;
        }

        // Log if we cant send
        logger.error("Cannot send command {}, all connections are offline for deviceid {}", message.getCommand(),
                message.getDeviceid());
    }

    /**
     * Processes and forwards incoming states to the appropriate device handler
     *
     */
    private synchronized void processState(JsonObject device, Boolean encrypted) {
        String deviceid = device.get("deviceid").getAsString();
        SonoffDeviceState state = listener.getState(deviceid);
        if (state == null) {
            logger.error("The device {} doesnt exist, unable to set state", deviceid);
            return;
        }

        // LAN messages need to be decrypted first
        if (encrypted) {
            logger.trace("Decrypting LAN message for {}", deviceid);
            String ipAddress = device.get("localAddress") != null ? device.get("localAddress").getAsString() : "";
            JsonObject params = gson.fromJson(
                    new SonoffCommandMessageEncryptionUtilities().decrypt(device, state.getDeviceKey()),
                    JsonObject.class);
            device.add("params", params);
            if (ipAddress != "") {
                device.addProperty("ipaddress", ipAddress);
            }
            logger.trace("LAN message for {} is {}", deviceid, gson.toJson(device));
        }

        state.updateState(device);
        logger.debug("Updated state for {}, with data {}", deviceid, gson.toJson(device));
        SonoffDeviceListener deviceListener = listener.getListener(deviceid);
        if (deviceListener != null) {
            deviceListener.updateDevice(state);
            logger.trace("Forwarded state to device {}", deviceid);
        } else {
            logger.debug("Unable to forward state for {} as no listener present", deviceid);
        }
    }

    @Override
    public void websocketMessage(String message) {
        String messageType = "";
        String messageAction = "";
        String messageErrorCode = "";
        String messageError = "";

        JsonObject response = gson.fromJson(message, JsonObject.class);
        if (response != null) {
            JsonElement action = response.get("action"); // .getAsString();
            JsonElement seq = response.get("sequence"); // .getAsString();
            JsonElement errorCode = response.get("error"); // .getAsInt();
            if (response.get("reason") != null) {
                messageError = response.get("reason") != null ? response.get("reason").getAsString() : "No reason";
            }
            if (seq != null) {
                Long sequence = Long.parseLong(seq.getAsString());
                latchMap.get(sequence);
                String type = messageTypes.get(sequence);
                messageType = type != null ? type : messageType;
                messageTypes.remove(sequence);
                okMessage(sequence);
            }
            if (action != null) {
                messageAction = action.getAsString();
            }
            if (errorCode != null) {
                messageErrorCode = errorCode.getAsString();
            }

            // Process any state messages
            if (messageAction.equals("update") || messageAction.equals("sysmsg")) {
                JsonObject device = gson.fromJson(response, JsonObject.class);
                if (device != null) {
                    processState(device, false);
                }
                return;
            }

            // Process Other messages
            if (seq != null) {

                // Process streaming data activation
                if (messageType.equals("uiActive")) {
                    if (!messageErrorCode.equals("0")) {
                        JsonElement deviceid = response.get("deviceid");
                        String id = deviceid != null ? deviceid.getAsString() : "unknown";
                        logger.trace("Streaming Data Activation Error {} - {} ,For Device:{}", messageErrorCode,
                                messageError, id);
                    }

                    return;
                }

                // Consumption message
                if (messageType.equals("consumption")) {
                    JsonObject device = gson.fromJson(response, JsonObject.class);
                    if (device != null) {
                        JsonObject params = device.getAsJsonObject("config");
                        device.add("params", params);
                        processState(device, false);
                    }
                    return;
                }

                // All other message (may need this for new device payloads)
                logger.trace("Websocket processed {} type message with payload {}", messageType, message);
            }
        } else {
            logger.error("Websocket message didnt have any content");
        }
    }

    @Override
    public void apiMessage(JsonObject thingResponse) {
        JsonObject data = thingResponse.get("data").getAsJsonObject();
        JsonArray thingList = data.get("thingList").getAsJsonArray();
        for (int i = 0; i < thingList.size(); i++) {
            JsonObject thing = thingList.get(i).getAsJsonObject();
            JsonObject device = thing.get("itemData").getAsJsonObject();
            processState(device, false);
        }
    }

    @Override
    public void lanResponse(String message) {
        LanResponse response = gson.fromJson(message, LanResponse.class);
        if (response != null) {
            okMessage(Long.parseLong(response.getSequence()));
            messageTypes.remove(Long.parseLong(response.getSequence()));
        } else {
            logger.error("LAN response returned null for message: {}", message);
        }
    }

    @Override
    public void serviceAdded(@Nullable ServiceEvent event) {
        if (event != null) {
            logger.trace("Sonoff - LAN Service added:{}", event.getInfo());
        }
    }

    @Override
    public void serviceRemoved(@Nullable ServiceEvent event) {
        if (event != null) {
            logger.debug("Sonoff - LAN Service removed:{}", event.getInfo());
        }
    }

    @Override
    public void serviceResolved(@Nullable ServiceEvent event) {
        if (event != null) {
            ServiceInfo eventInfo = event.getInfo();
            String localAddress = eventInfo.getInet4Addresses()[0].getHostAddress();
            logger.trace("Lan event received from {} with payload {}", localAddress, eventInfo);
            JsonObject device = new JsonObject();
            Enumeration<String> info = eventInfo.getPropertyNames();
            while (info.hasMoreElements()) {
                final @Nullable String name = info.nextElement();
                String value = eventInfo.getPropertyString(name);
                device.addProperty(name.equals("id") ? "deviceid" : name, value);
            }
            device.addProperty("localAddress", localAddress.equals("null") ? "" : localAddress);
            processState(device, true);
        }
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void isConnected(Boolean lanConnected, Boolean cloudConnected) {
        this.lanConnected = lanConnected;
        this.cloudConnected = cloudConnected;
    }
}
