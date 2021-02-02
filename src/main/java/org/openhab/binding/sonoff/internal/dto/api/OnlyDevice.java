
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OnlyDevice implements Serializable {

    @SerializedName("ota")
    @Expose
    private String ota;
    private final static long serialVersionUID = 3674980726452527678L;

    public String getOta() {
        return ota;
    }

    public void setOta(String ota) {
        this.ota = ota;
    }
}
