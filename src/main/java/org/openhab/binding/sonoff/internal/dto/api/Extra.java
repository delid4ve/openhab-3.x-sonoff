
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Extra implements Serializable {

    @SerializedName("extra")
    @Expose
    private Extra_ extra;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("ipCountry")
    @Expose
    private String ipCountry;
    @SerializedName("additionalProperties")
    @Expose
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 517431720449540552L;

    public String getIpCountry() {
        return ipCountry;
    }

    public void setIpCountry(String ipCountry) {
        this.ipCountry = ipCountry;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Extra_ getExtra() {
        return extra;
    }

    public void setExtra(Extra_ extra) {
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
