
package org.openhab.binding.sonoff.internal.dto.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindInfos {

    private List<String> alexa = null;
    private List<String> gaction = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<String> getAlexa() {
        return alexa;
    }

    public void setAlexa(List<String> alexa) {
        this.alexa = alexa;
    }

    public List<String> getGaction() {
        return gaction;
    }

    public void setGaction(List<String> gaction) {
        this.gaction = gaction;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
