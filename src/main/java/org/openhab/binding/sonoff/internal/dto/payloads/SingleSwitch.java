
package org.openhab.binding.sonoff.internal.dto.payloads;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleSwitch implements Serializable {

    @SerializedName("switch")
    @Expose
    private String _switch;
    @SerializedName("voltage")
    @Expose
    private String voltage;
    @SerializedName("current")
    @Expose
    private String current;
    @SerializedName("power")
    @Expose
    private String power;
    @SerializedName("uiActive")
    @Expose
    private Integer uiActive;
    @SerializedName("sledOnline")
    @Expose
    private String sledOnline;
    @SerializedName("startup")
    @Expose
    private String startup;

    private final static long serialVersionUID = 1205249120703729170L;

    public String getSwitch() {
        return _switch;
    }

    public void setSwitch(String _switch) {
        this._switch = _switch;
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

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Integer getUiActive() {
        return uiActive;
    }

    public void setUiActive(Integer uiActive) {
        this.uiActive = uiActive;
    }

    public String getSledOnline() {
        return sledOnline;
    }

    public void setSledOnline(String sledOnline) {
        this.sledOnline = sledOnline;
    }

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }
}
