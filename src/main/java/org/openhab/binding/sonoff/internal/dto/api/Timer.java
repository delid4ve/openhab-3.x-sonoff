
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timer implements Serializable {

    @SerializedName("mId")
    @Expose
    private String mId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("at")
    @Expose
    private String at;
    @SerializedName("coolkit_timer_type")
    @Expose
    private String coolkitTimerType;
    @SerializedName("enabled")
    @Expose
    private Integer enabled;
    @SerializedName("do")
    @Expose
    private Do _do;
    private final static long serialVersionUID = -2530429442608113695L;

    public String getMId() {
        return mId;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getCoolkitTimerType() {
        return coolkitTimerType;
    }

    public void setCoolkitTimerType(String coolkitTimerType) {
        this.coolkitTimerType = coolkitTimerType;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Do getDo() {
        return _do;
    }

    public void setDo(Do _do) {
        this._do = _do;
    }
}
