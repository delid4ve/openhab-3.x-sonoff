
package org.openhab.binding.sonoff.internal.dto.api;

import java.util.HashMap;
import java.util.Map;

public class AppInfo {

    private String os;
    private String appVersion;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "AppInfo{" + "os='" + os + '\'' + ", appVersion='" + appVersion + '\'' + ", additionalProperties="
                + additionalProperties + '}';
    }
}
