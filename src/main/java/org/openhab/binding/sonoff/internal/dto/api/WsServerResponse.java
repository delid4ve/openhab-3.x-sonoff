
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WsServerResponse implements Serializable {

    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("IP")
    @Expose
    private String iP;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("error")
    @Expose
    private Integer error;
    private final static long serialVersionUID = -5318327189575236342L;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIP() {
        return iP;
    }

    public void setIP(String iP) {
        this.iP = iP;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}
