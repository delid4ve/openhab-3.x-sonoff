package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlertMode implements Serializable {

    @SerializedName("enabled")
    @Expose
    private Integer enabled;
    private final static long serialVersionUID = 3129283417541224909L;

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
