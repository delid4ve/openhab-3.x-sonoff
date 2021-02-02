
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Devices implements Serializable {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("devicelist")
    @Expose
    private List<Device> devicelist = new ArrayList<Device>();
    private final static long serialVersionUID = 8145552471787779538L;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public List<Device> getDevicelist() {
        return devicelist;
    }

    public void setDevicelist(List<Device> devicelist) {
        this.devicelist = devicelist;
    }
}
