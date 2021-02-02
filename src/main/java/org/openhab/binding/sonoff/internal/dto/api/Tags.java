
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tags implements Serializable {

    @SerializedName("m_c395_djmu")
    @Expose
    private String mC395Djmu;
    private final static long serialVersionUID = 5437403083294828237L;

    public String getMC395Djmu() {
        return mC395Djmu;
    }

    public void setMC395Djmu(String mC395Djmu) {
        this.mC395Djmu = mC395Djmu;
    }
}
