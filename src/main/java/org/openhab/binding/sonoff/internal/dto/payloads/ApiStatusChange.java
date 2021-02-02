
package org.openhab.binding.sonoff.internal.dto.payloads;

import java.io.Serializable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiStatusChange implements Serializable {

    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("version")
    @Expose
    private int version = 8;
    @SerializedName("appid")
    @Expose
    private String appid = "appid";
    @SerializedName("ts")
    @Expose
    private Long ts;
    @SerializedName("params")
    @Expose
    private JsonObject params;
    private final static long serialVersionUID = 2958983062170484126L;

    public ApiStatusChange(String deviceid, String appid, Long ts, JsonObject params) {
        this.deviceid = deviceid;
        this.appid = appid;
        this.ts = ts;
        this.params = params;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getVersion() {
        return this.version;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Long getTs() {
        return this.ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public JsonObject getParams() {
        return params;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }
}
