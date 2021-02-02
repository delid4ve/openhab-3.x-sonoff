package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Effective implements Serializable {

    @SerializedName("outlet")
    @Expose
    private Integer outlet;
    @SerializedName("enabled")
    @Expose
    private Integer enabled;
    @SerializedName("days")
    @Expose
    private List<Integer> days = null;
    @SerializedName("effIndex")
    @Expose
    private List<EffIndex> effIndex = null;
    private final static long serialVersionUID = -698034682007150654L;

    public Integer getOutlet() {
        return outlet;
    }

    public void setOutlet(Integer outlet) {
        this.outlet = outlet;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public List<EffIndex> getEffIndex() {
        return effIndex;
    }

    public void setEffIndex(List<EffIndex> effIndex) {
        this.effIndex = effIndex;
    }
}
