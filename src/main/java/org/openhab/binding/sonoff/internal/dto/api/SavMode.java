package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavMode implements Serializable {

    @SerializedName("enabled")
    @Expose
    private Integer enabled;
    @SerializedName("dTime")
    @Expose
    private Integer dTime;
    @SerializedName("tUnit")
    @Expose
    private String tUnit;
    @SerializedName("outlets")
    @Expose
    private List<Integer> outlets = null;
    private final static long serialVersionUID = -5911904924758281536L;

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getDTime() {
        return dTime;
    }

    public void setDTime(Integer dTime) {
        this.dTime = dTime;
    }

    public String getTUnit() {
        return tUnit;
    }

    public void setTUnit(String tUnit) {
        this.tUnit = tUnit;
    }

    public List<Integer> getOutlets() {
        return outlets;
    }

    public void setOutlets(List<Integer> outlets) {
        this.outlets = outlets;
    }
}
