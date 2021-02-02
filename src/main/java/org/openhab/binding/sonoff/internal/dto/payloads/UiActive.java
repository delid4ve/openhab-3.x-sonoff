
package org.openhab.binding.sonoff.internal.dto.payloads;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UiActive implements Serializable {

    @SerializedName("uiActive")
    @Expose
    private Integer uiActive;
    private final static long serialVersionUID = 1205249120703729170L;

    public Integer getUiActive() {
        return uiActive;
    }

    public void setUiActive(Integer uiActive) {
        this.uiActive = uiActive;
    }
}
