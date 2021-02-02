package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config implements Serializable {

    @SerializedName("hb")
    @Expose
    private Integer hb;
    @SerializedName("hbInterval")
    @Expose
    private Integer hbInterval;
    private final static long serialVersionUID = -2605312819381631092L;

    public Integer getHb() {
        return hb;
    }

    public void setHb(Integer hb) {
        this.hb = hb;
    }

    public Integer getHbInterval() {
        return hbInterval;
    }

    public void setHbInterval(Integer hbInterval) {
        this.hbInterval = hbInterval;
    }
}
