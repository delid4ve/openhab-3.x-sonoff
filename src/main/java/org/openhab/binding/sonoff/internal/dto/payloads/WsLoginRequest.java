package org.openhab.binding.sonoff.internal.dto.payloads;

import java.util.Date;

public class WsLoginRequest {
    private String action = "userOnline";
    private String at;
    private String apikey;
    private String appid;
    private String nonce;
    private Long ts;
    private String userAgent = "app";
    private Long sequence;
    private int version = 8;

    public WsLoginRequest(String at, String apikey, String appid, String nonce) {
        this.at = at;
        this.apikey = apikey;
        this.appid = appid;
        this.nonce = nonce;
        ts = new Date().getTime() / 1000;
        sequence = new Date().getTime();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "WssLogin{" + "action='" + action + '\'' + ", at='" + at + '\'' + ", apikey='" + apikey + '\''
                + ", appid='" + appid + '\'' + ", nonce='" + nonce + '\'' + ", ts=" + ts + ", userAgent='" + userAgent
                + '\'' + ", sequence=" + sequence + ", version=" + version + '}';
    }
}
