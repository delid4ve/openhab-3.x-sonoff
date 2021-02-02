
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings implements Serializable {

    @SerializedName("opsNotify")
    @Expose
    private Integer opsNotify;
    @SerializedName("opsHistory")
    @Expose
    private Integer opsHistory;
    @SerializedName("alarmNotify")
    @Expose
    private Integer alarmNotify;
    @SerializedName("wxAlarmNotify")
    @Expose
    private Integer wxAlarmNotify;
    @SerializedName("wxOpsNotify")
    @Expose
    private Integer wxOpsNotify;
    @SerializedName("wxDoorbellNotify")
    @Expose
    private Integer wxDoorbellNotify;
    @SerializedName("appDoorbellNotify")
    @Expose
    private Integer appDoorbellNotify;
    @SerializedName("doorOnNotify")
    @Expose
    private Object doorOnNotify;
    @SerializedName("doorOffNotify")
    @Expose
    private Object doorOffNotify;
    @SerializedName("wxDoorOffNotify")
    @Expose
    private Object wxDoorOffNotify;
    @SerializedName("wxDoorOnNotify")
    @Expose
    private Object wxDoorOnNotify;
    private final static long serialVersionUID = 5307814168481405394L;

    public Integer getOpsNotify() {
        return opsNotify;
    }

    public void setOpsNotify(Integer opsNotify) {
        this.opsNotify = opsNotify;
    }

    public Integer getOpsHistory() {
        return opsHistory;
    }

    public void setOpsHistory(Integer opsHistory) {
        this.opsHistory = opsHistory;
    }

    public Integer getAlarmNotify() {
        return alarmNotify;
    }

    public void setAlarmNotify(Integer alarmNotify) {
        this.alarmNotify = alarmNotify;
    }

    public Integer getWxAlarmNotify() {
        return wxAlarmNotify;
    }

    public void setWxAlarmNotify(Integer wxAlarmNotify) {
        this.wxAlarmNotify = wxAlarmNotify;
    }

    public Integer getWxOpsNotify() {
        return wxOpsNotify;
    }

    public void setWxOpsNotify(Integer wxOpsNotify) {
        this.wxOpsNotify = wxOpsNotify;
    }

    public Integer getWxDoorbellNotify() {
        return wxDoorbellNotify;
    }

    public void setWxDoorbellNotify(Integer wxDoorbellNotify) {
        this.wxDoorbellNotify = wxDoorbellNotify;
    }

    public Integer getAppDoorbellNotify() {
        return appDoorbellNotify;
    }

    public void setAppDoorbellNotify(Integer appDoorbellNotify) {
        this.appDoorbellNotify = appDoorbellNotify;
    }

    public Object getDoorOnNotify() {
        return doorOnNotify;
    }

    public void setDoorOnNotify(Object doorOnNotify) {
        this.doorOnNotify = doorOnNotify;
    }

    public Object getDoorOffNotify() {
        return doorOffNotify;
    }

    public void setDoorOffNotify(Object doorOffNotify) {
        this.doorOffNotify = doorOffNotify;
    }

    public Object getWxDoorOffNotify() {
        return wxDoorOffNotify;
    }

    public void setWxDoorOffNotify(Object wxDoorOffNotify) {
        this.wxDoorOffNotify = wxDoorOffNotify;
    }

    public Object getWxDoorOnNotify() {
        return wxDoorOnNotify;
    }

    public void setWxDoorOnNotify(Object wxDoorOnNotify) {
        this.wxDoorOnNotify = wxDoorOnNotify;
    }
}
