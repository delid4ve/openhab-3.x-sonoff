
package org.openhab.binding.sonoff.internal.dto.payloads;

import java.io.Serializable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WsUpdate implements Serializable {

    @SerializedName("action")
    @Expose
    private String action = "update";
    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("apikey")
    @Expose
    private String apikey;
    @SerializedName("userAgent")
    @Expose
    private String userAgent = "app";
    @SerializedName("params")
    @Expose
    private JsonObject params;
    private String offlineTime;
    @SerializedName("selfApikey")
    @Expose
    private String selfApikey;
    private String at;
    private Long ts;
    private String sequence;
    private int version = 8;
    private String nonce;

    public WsUpdate(String at, String apikey, String selfApikey, String deviceid, String nonce, JsonObject params,
            long sequence) {
        this.at = at;
        this.apikey = apikey;
        this.nonce = nonce;
        this.deviceid = deviceid;
        this.selfApikey = selfApikey;
        this.params = params;
        this.ts = sequence / 1000;
        this.sequence = sequence + "";
    }

    public String getAt() {
        return this.at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public Long getTs() {
        return this.ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getSequence() {
        return Long.parseLong(sequence);
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    private final static long serialVersionUID = -1947653206187395468L;

    public String getAction() {
        return action;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public JsonObject getParams() {
        return params;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }

    public String getOfflineTime() {
        return this.offlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getSelfApikey() {
        return this.selfApikey;
    }

    public void setSelfApikey(String selfApikey) {
        this.selfApikey = selfApikey;
    }
}
