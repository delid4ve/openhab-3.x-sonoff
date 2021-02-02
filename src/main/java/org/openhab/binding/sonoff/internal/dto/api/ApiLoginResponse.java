
package org.openhab.binding.sonoff.internal.dto.api;

import java.util.HashMap;
import java.util.Map;

public class ApiLoginResponse {

    private String at;
    private String rt;
    private User user;
    private String region;
    private int error;
    private String msg;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "LoginResponse{" + "at='" + at + '\'' + ", rt='" + rt + '\'' + ", user=" + user + ", region='" + region
                + '\'' + ", error=" + error + ", msg='" + msg + '\'' + ", additionalProperties=" + additionalProperties
                + '}';
    }
}
