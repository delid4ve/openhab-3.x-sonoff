package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Configure implements Serializable {

    @SerializedName("startup")
    @Expose
    private String startup;
    @SerializedName("outlet")
    @Expose
    private Integer outlet;
    private final static long serialVersionUID = 3543593025836134522L;

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public Integer getOutlet() {
        return outlet;
    }

    public void setOutlet(Integer outlet) {
        this.outlet = outlet;
    }
}
