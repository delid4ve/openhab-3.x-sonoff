
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WsMessage implements Serializable {

    @SerializedName("apikey")
    @Expose
    private String apikey;
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("sequence")
    @Expose
    private String sequence;
    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("switch")
    @Expose
    private String _switch;
    @SerializedName("power")
    @Expose
    private String power;
    @SerializedName("voltage")
    @Expose
    private String voltage;
    @SerializedName("current")
    @Expose
    private String current;
    @SerializedName("rssi")
    @Expose
    private Integer rssi;
    @SerializedName("offlineTime")
    @Expose
    private String offlineTime;
    @SerializedName("uiActive")
    @Expose
    private String uiActive;

    @SerializedName("action")
    @Expose
    private String action;

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @SerializedName("deviceid")
    @Expose
    private String deviceid;

    public String getDeviceid() {
        return this.deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @SerializedName("userAgent")
    @Expose
    private String userAgent;

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @SerializedName("d_seq")
    @Expose
    private Long d_seq;

    public Long getD_seq() {
        return this.d_seq;
    }

    public void setD_seq(Long d_seq) {
        this.d_seq = d_seq;
    }

    @SerializedName("params")
    @Expose
    private Params params;

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @SerializedName("from")
    @Expose
    private String from;

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @SerializedName("seq")
    @Expose
    private String seq;

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    private final static long serialVersionUID = 9111270141241205415L;

    public Integer getError() {
        return this.error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getSequence() {
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String get_switch() {
        return this._switch;
    }

    public void set_switch(String _switch) {
        this._switch = _switch;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public Integer getRssi() {
        return this.rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public String getOfflineTime() {
        return this.offlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getUiActive() {
        return this.uiActive;
    }

    public void setUiActive(String uiActive) {
        this.uiActive = uiActive;
    }
}
