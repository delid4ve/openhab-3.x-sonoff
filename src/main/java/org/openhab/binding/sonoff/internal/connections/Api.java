package org.openhab.binding.sonoff.internal.connections;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.sonoff.internal.Constants;
import org.openhab.binding.sonoff.internal.Utils;
import org.openhab.binding.sonoff.internal.config.AccountConfig;
import org.openhab.binding.sonoff.internal.dto.api.ApiLoginResponse;
import org.openhab.binding.sonoff.internal.dto.api.Devices;
import org.openhab.binding.sonoff.internal.dto.api.WsServerResponse;
import org.openhab.binding.sonoff.internal.dto.payloads.ApiLoginRequest;
import org.openhab.binding.sonoff.internal.dto.payloads.ApiStatusChange;
import org.openhab.binding.sonoff.internal.dto.payloads.WsServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Api {

    private final Logger logger = LoggerFactory.getLogger(Api.class);
    private final Gson gson;
    private final HttpClient httpClient;
    private final AccountConfig config;
    private final String baseUrl;
    private String apiKey = "";
    private String at = "";

    private Boolean connected = false;

    public Api(AccountConfig config, HttpClientFactory httpClientFactory, Gson gson) {
        this.gson = gson;
        this.config = config;
        this.httpClient = httpClientFactory.createHttpClient("sonoffApi");
        this.baseUrl = "https://" + config.region + "-api.coolkit.cc:8080/";
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAt() {
        return at;
    }

    public void start() {
        try {
            httpClient.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            httpClient.stop();
            httpClient.destroy();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Boolean login() {
        long ts = new Date().getTime();
        ApiLoginResponse loginResponse = new ApiLoginResponse();
        String url = baseUrl + "api/user/login";
        ApiLoginRequest loginRequest = new ApiLoginRequest();
        loginRequest.setAppid(Constants.appid);
        loginRequest.setEmail(config.email);
        loginRequest.setPassword(config.password);
        loginRequest.setTs(ts + "");
        loginRequest.setVersion("8");
        loginRequest.setNonce(Utils.getNonce());
        logger.debug("Login url:{}", url);
        logger.debug("Login Request:{}", loginRequest.toString());
        logger.debug("Login Request url:{}", url);
        try {
            ContentResponse response = httpClient.newRequest(url).header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Sign " + getAuthMac(gson.toJson(loginRequest))).method("POST")
                    .content(new StringContentProvider(gson.toJson(loginRequest)), "application/json").send();
            loginResponse = gson.fromJson(response.getContentAsString(), ApiLoginResponse.class);
            connected = (loginResponse.getError() > 0) ? false : true;
            logger.debug("Login Response Raw:{}", response.getContentAsString());
            at = loginResponse.getAt();
            apiKey = loginResponse.getUser().getApikey();
            return connected;
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | InterruptedException
                | TimeoutException | ExecutionException e) {
            logger.debug("Api Couldnt log in:{}", e);
            return connected;
        }
    }

    public WsServerResponse getWsServer() {
        String url = baseUrl + "dispatch/app";
        WsServerRequest request = new WsServerRequest(Constants.appid, Utils.getNonce());
        try {
            ContentResponse response = httpClient.newRequest(url).header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8").header("Authorization", "Bearer " + at)
                    .method("POST").content(new StringContentProvider(gson.toJson(request)), "application/json").send();
            logger.debug("Get WebSocket Server Response Raw:{}", response.getContentAsString());
            return gson.fromJson(response.getContentAsString(), WsServerResponse.class);
        } catch (Exception e) {
            logger.debug("Error Fetching Websocket Server:{}", e);
            return null;
        }
    }

    public void setStatus(String data, String deviceid, String deviceKey) {
        String url = baseUrl + "api/user/device/status";
        long ts = new Date().getTime();
        JsonObject payload = new JsonParser().parse(data).getAsJsonObject();
        ApiStatusChange request = new ApiStatusChange(deviceid, Constants.appid, ts, payload);
        try {
            ContentResponse response = httpClient.newRequest(url).header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8").header("Authorization", "Bearer " + at)
                    .method("POST").content(new StringContentProvider(gson.toJson(request)), "application/json").send();
            logger.debug("Get Api Server Response Raw:{}", response.getContentAsString());
            // return gson.fromJson(response.getContentAsString(), WsServerResponse.class);
        } catch (Exception e) {
            logger.debug("Error Sending Device Status:{}", e);
        }
    }

    public Devices discover() {
        String url = baseUrl + "api/user/device?lang=en&appid=" + Constants.appid + "&ts=" + new Date().getTime()
                + "&version=8&getTags=1";
        ContentResponse response;
        try {
            response = httpClient.newRequest(url).header("accept", "text/html").header("connection", "Keep-Alive")
                    .header("Authorization", "Bearer " + at).method("GET").send();
            logger.info("Discovery response:{}", response.getContentAsString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.warn("Api Couldnt discover devices:{}", e);
            return null;
        }
        return gson.fromJson(response.getContentAsString(), Devices.class);
    }

    public void sendLocalUpdate(String data, String command, String deviceid, String ipaddress, String deviceKey,
            String seq) {
        logger.debug("Unencrypted payload: {}", data);
        logger.debug("Sonoff - sendUpdate DeviceId: {}", deviceid);
        JsonObject payload = Utils.encrypt(data, deviceKey, deviceid, seq);
        String url = "http://" + ipaddress + ":8081/zeroconf/" + command;
        logger.debug("Updating url: {}", url);
        logger.debug("with unencrypted payload:{}", data);
        logger.debug("with encrypted payload:{}", payload);
        try {
            ContentResponse response = httpClient.newRequest(url).method("POST").header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8")
                    .content(new StringContentProvider(gson.toJson(payload)), "application/json")
                    .timeout(5, TimeUnit.SECONDS).send();
            logger.debug("Lan Response:{}", response.getContentAsString());
        } catch (Exception e) {
            logger.warn("Sonoff - Failed to send update:{}", e);
        }
    }

    public static String getAuthMac(String data)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = null;
        byte[] byteKey = Constants.appSecret.getBytes("UTF-8");
        final String HMAC_SHA256 = "HmacSHA256";
        sha256_HMAC = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
        sha256_HMAC.init(keySpec);
        byte[] mac_data = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(mac_data);
    }
}
