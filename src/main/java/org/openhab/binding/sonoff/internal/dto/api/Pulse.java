
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pulse implements Serializable {

    @SerializedName("pulse")
    @Expose
    private String pulse;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("outlet")
    @Expose
    private Integer outlet;
    private final static long serialVersionUID = 6723293372924782978L;

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getOutlet() {
        return outlet;
    }

    public void setOutlet(Integer outlet) {
        this.outlet = outlet;
    }
}
