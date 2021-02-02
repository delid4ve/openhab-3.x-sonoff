
package org.openhab.binding.sonoff.internal.dto.payloads;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultiSwitch implements Serializable {

    @SerializedName("switches")
    @Expose
    private List<Switch> switches = new ArrayList<Switch>();
    private final static long serialVersionUID = 1205249120703729170L;

    public List<Switch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }

    public class Switch implements Serializable {

        @SerializedName("switch")
        @Expose
        private String _switch;
        @SerializedName("outlet")
        @Expose
        private Integer outlet;
        private final static long serialVersionUID = 5376512912986765998L;

        public String getSwitch() {
            return _switch;
        }

        public void setSwitch(String _switch) {
            this._switch = _switch;
        }

        public Integer getOutlet() {
            return outlet;
        }

        public void setOutlet(Integer outlet) {
            this.outlet = outlet;
        }
    }
}
