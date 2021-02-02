package org.openhab.binding.sonoff.internal.dto.payloads;

import java.util.Date;

public class WsServerRequest {

    private String appid;
    private String nonce;
    private long ts;
    private int version = 8;
    private String accept = "ws";

    public WsServerRequest(String appid, String nonce) {
        this.appid = appid;
        this.nonce = nonce;
        ts = new Date().getTime() / 1000;
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public long getTs() {
        return this.ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAccept() {
        return this.accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
