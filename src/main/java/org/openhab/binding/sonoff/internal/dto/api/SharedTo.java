package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharedTo implements Serializable {

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("permit")
    @Expose
    private Integer permit;
    @SerializedName("note")
    @Expose
    private String note;
    private final static long serialVersionUID = 7001856722877788902L;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getPermit() {
        return permit;
    }

    public void setPermit(Integer permit) {
        this.permit = permit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
