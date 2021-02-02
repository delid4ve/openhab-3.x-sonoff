
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Room implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    private final static long serialVersionUID = 2640008994771127017L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
