
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Do implements Serializable {

    @SerializedName("switch")
    @Expose
    private String _switch;
    private final static long serialVersionUID = -5614884611405430479L;

    public String getSwitch() {
        return _switch;
    }

    public void setSwitch(String _switch) {
        this._switch = _switch;
    }
}
