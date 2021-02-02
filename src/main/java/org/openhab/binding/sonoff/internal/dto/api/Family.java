
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Family implements Serializable {

    @SerializedName("room")
    @Expose
    private Room room;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("index")
    @Expose
    private Integer index;
    private final static long serialVersionUID = -7637393362726388467L;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
