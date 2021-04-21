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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessageEncryptionUtilities;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessageUtilities;
import org.openhab.binding.sonoff.internal.dto.requests.ApiLoginRequest;
import org.openhab.binding.sonoff.internal.dto.requests.GeneralRequest;
import org.openhab.binding.sonoff.internal.dto.requests.ThingList;
import org.openhab.binding.sonoff.internal.dto.requests.Things;
import org.openhab.binding.sonoff.internal.dto.responses.WsServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffApiConnection} class is the Http Api Connection to the Ewelink Servers and uses the shared
 * httpClient
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffApiConnection {

    private final Logger logger = LoggerFactory.getLogger(SonoffApiConnection.class);
    private final Gson gson;
    private final HttpClient httpClient;
    private final SonoffApiConnectionListener listener;

    private String apiKey = "";
    private String at = "";
    private String countryCode = "";
    private String region = "";
    private String baseUrl = "";
    private String dispUrl = "";
    private String email = "";
    private String password = "";
    private String webSocketServer = "";

    public SonoffApiConnection(SonoffApiConnectionListener listener, HttpClient httpClient) {
        this.gson = new Gson();
        this.httpClient = httpClient;
        this.httpClient.setResponseBufferSize(86400);
        this.listener = listener;
        this.countryCode = "+1";
        this.region = "us";
        this.dispUrl = "https://us-dispa.coolkit.cc/dispatch/app";
        this.baseUrl = "https://us-apia.coolkit.cc";
    }

    public void setCountryCode(String countryCode) {
        logger.debug("Api CountryCode changed to:{}", countryCode);
        this.countryCode = countryCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getAt() {
        return this.at;
    }

    public void login() {
        Boolean regionOk = updateLogin();
        if (regionOk) {
            return;
        } else {
            updateLogin();
        }
    }

    private Boolean updateLogin() {
        String url = this.baseUrl + "/v2/user/login";
        ApiLoginRequest request = new ApiLoginRequest(this.email, this.password, this.countryCode);
        logger.debug("Api Login Request:{}", gson.toJson(request));
        try {
            ContentResponse contentResponse = httpClient.newRequest(url).header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("X-CK-Appid", SonoffCommandMessageUtilities.APPID)
                    .header("X-CK-Nonce", SonoffCommandMessageUtilities.getNonce())
                    .header("Authorization",
                            "Sign " + new SonoffCommandMessageEncryptionUtilities().getAuthMac(gson.toJson(request)))
                    .method("POST").content(new StringContentProvider(gson.toJson(request)), "application/json").send();
            if (contentResponse != null) {
                logger.debug("Api Login Response:{}", contentResponse.getContentAsString());
                JsonObject loginResponse = gson.fromJson(contentResponse.getContentAsString(), JsonObject.class);
                if (loginResponse != null) {
                    JsonElement error = loginResponse.get("error");
                    // If the region isnt correct set the new regions and re-login
                    if (error != null) {
                        if (error.getAsInt() == 10004) {
                            this.region = loginResponse.getAsJsonObject("data").get("region").getAsString();
                            this.dispUrl = "https://" + this.region + "-dispa.coolkit.cc/dispatch/app";
                            this.baseUrl = "https://" + this.region + "-apia.coolkit.cc";
                            return false;
                            // if we get any other error then disconnnect
                        } else if (error.getAsInt() > 0) {
                            listener.apiConnected(false, "", "");
                            return true;
                            // otherwise process the login request
                        } else {
                            // set the tokens
                            this.at = loginResponse.getAsJsonObject("data").get("at").getAsString();
                            this.apiKey = loginResponse.getAsJsonObject("data").getAsJsonObject("user").get("apikey")
                                    .getAsString();
                            listener.apiConnected(true, this.apiKey, this.at);
                            return true;
                        }
                    }
                }
                listener.apiConnected(false, "", "");
                return true;
            } else {
                logger.error("Api Login Response returned empty");
                listener.apiConnected(false, "", "");
                return true;
            }
        } catch (Exception e) {
            logger.trace("Api Login Exception:{}, {}", e.getMessage(), e.getStackTrace());
            listener.apiConnected(false, "", "");
            return true;
        }
    }

    public String getWebsocketServer() {
        logger.debug("Attempt to get websocket server");
        GeneralRequest request = new GeneralRequest();
        request.setAccept("ws");
        // WsServerResponse response = new WsServerResponse();
        logger.debug("Websocket URL Request:{}", gson.toJson(request));
        try {
            ContentResponse contentResponse = httpClient.newRequest(this.dispUrl).header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8").header("Authorization", "Bearer " + this.at)
                    .method("POST").content(new StringContentProvider(gson.toJson(request)), "application/json").send();
            logger.debug("Websocket URL Response:{}", contentResponse.getContentAsString());
            WsServerResponse response = gson.fromJson(contentResponse.getContentAsString(), WsServerResponse.class);
            if (response != null) {
                if ((!response.getError().equals(0)) || response.getError() == null) {
                    listener.apiConnected(false, "", "");
                    return "";
                } else {
                    this.webSocketServer = "wss://" + response.getDomain() + ":" + response.getPort() + "/api/ws";
                    return this.webSocketServer;
                }
            } else {
                logger.error("Api Websocket Server Response returned empty");
                return "";
            }
        } catch (Exception e) {
            listener.apiConnected(false, "", "");
            logger.trace("Api Exception:{}", e.getMessage());
            return "";
        }
    }

    public String createCache() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        GeneralRequest request = new GeneralRequest();
        logger.debug("Api Cache Request:{}", gson.toJson(request));
        String url = this.baseUrl + "/v2/device/thing";
        ContentResponse contentResponse = httpClient.newRequest(url).header("Authorization", "Bearer " + this.at)
                .header("Content-Type", "application/json").header("X-CK-Appid", SonoffCommandMessageUtilities.APPID)
                .header("X-CK-Nonce", SonoffCommandMessageUtilities.getNonce()).method("GET").send();
        logger.debug("Api Cache response:{}", contentResponse.getContentAsString());
        return contentResponse.getContentAsString();
    }

    public String getDeviceCache(String deviceid) throws InterruptedException, TimeoutException, ExecutionException {
        ThingList request = new ThingList();
        Things thing = new Things();
        thing.setItemType(1);
        thing.setId(deviceid);
        request.getThings().add(thing);
        String url = this.baseUrl + "/v2/device/thing";
        logger.debug("Api Get Device Request for id:{}", deviceid);
        ContentResponse response = httpClient.newRequest(url).header("Content-Type", "application/json")
                .header("X-CK-Appid", SonoffCommandMessageUtilities.APPID)
                .header("X-CK-Nonce", SonoffCommandMessageUtilities.getNonce())
                .header("Authorization", "Bearer " + this.at)
                .content(new StringContentProvider(gson.toJson(request)), "application/json").method("POST").send();
        return response.getContentAsString();
    }

    public void getDevices() {
        GeneralRequest request = new GeneralRequest();
        logger.debug("Api Devices Request:{}", gson.toJson(request));
        String url = this.baseUrl + "/v2/device/thing";
        httpClient.newRequest(url).header("Authorization", "Bearer " + this.at)
                .header("Content-Type", "application/json").header("X-CK-Appid", SonoffCommandMessageUtilities.APPID)
                .header("X-CK-Nonce", SonoffCommandMessageUtilities.getNonce()).method("GET")
                .send(new BufferingResponseListener(8 * 1024 * 1024) {
                    @Override
                    public void onComplete(@Nullable Result result) {
                        if (!result.isFailed()) {
                            byte[] responseContent = getContent();
                            String response = new String(responseContent, StandardCharsets.UTF_8);
                            logger.debug("Api Devices response:{}", response);
                            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
                            if (responseObject != null) {
                                Integer error = responseObject.get("error").getAsInt();
                                if (!error.equals(0)) {
                                    processError(error, responseObject.get("msg").getAsString());
                                } else {
                                    listener.apiMessage(responseObject);
                                }
                            }
                        }
                    }
                });
    }

    public void getDevice(String deviceid) {
        ThingList request = new ThingList();
        Things thing = new Things();
        thing.setItemType(1);
        thing.setId(deviceid);
        request.getThings().add(thing);
        String url = this.baseUrl + "/v2/device/thing";
        logger.debug("Api Get Device Request for id:{}", deviceid);
        httpClient.newRequest(url).header("Content-Type", "application/json")
                .header("X-CK-Appid", SonoffCommandMessageUtilities.APPID)
                .header("X-CK-Nonce", SonoffCommandMessageUtilities.getNonce())
                .header("Authorization", "Bearer " + this.at)
                .content(new StringContentProvider(gson.toJson(request)), "application/json").method("POST")
                .send(new Response.Listener.Adapter() {
                    @Override
                    public void onContent(@Nullable Response contentResponse, @Nullable ByteBuffer buffer) {
                        String response = StandardCharsets.UTF_8.decode(buffer).toString();
                        logger.debug("Api Device request for {} returned {}", deviceid, response);
                        // ThingResponse response = gson.fromJson(string, ThingResponse.class);
                        JsonObject responseObject = gson.fromJson(response, JsonObject.class);
                        if (responseObject != null) {
                            Integer error = responseObject.get("error").getAsInt();
                            if (!error.equals(0)) {
                                processError(error, responseObject.get("msg").getAsString());
                            } else {
                                listener.apiMessage(responseObject);
                            }
                        }
                    }
                });
    }

    private void processError(Integer code, String message) {
        String logError = "";
        switch (code) {
            case 401:
                logError = "You cannot use more than 1 instance of Ewelink at one time, please check the app or another binding is not logged into your account";
                break;
            case 0:
                logError = "This was a serious exception and should be reported to the binding developer";
                break;
            default:
                logError = "";
        }
        logger.error("Api threw an error code {} with message {}. Additional User information: {}", code, message,
                logError);
        listener.apiConnected(false, "", "");
    }
}
