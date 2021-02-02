
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Switch implements Serializable {

    @SerializedName("switch")
    @Expose
    private String _switch;
    @SerializedName("outlet")
    @Expose
    private Integer outlet;
    private final static long serialVersionUID = 5376512912986765998L;

    public String getSwitch() {
        return _switch;
    }

    public void setSwitch(String _switch) {
        this._switch = _switch;
    }

    public Integer getOutlet() {
        return outlet;
    }

    public void setOutlet(Integer outlet) {
        this.outlet = outlet;
    }
}
