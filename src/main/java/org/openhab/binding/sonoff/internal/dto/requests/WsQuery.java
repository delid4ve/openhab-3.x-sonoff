/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.sonoff.internal.dto.requests;

import java.io.Serializable;

import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessageUtilities;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class WsQuery implements Serializable {

    @SerializedName("action")
    @Expose
    private String action = "query";
    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("apikey")
    @Expose
    private String apikey;
    @SerializedName("userAgent")
    @Expose
    private String userAgent = "app";
    @SerializedName("params")
    @Expose
    private JsonObject params;

    public JsonObject getParams() {
        return this.params;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }

    @SerializedName("tempRec")
    @Expose
    private String tempRec;
    @SerializedName("sequence")
    @Expose
    private Long sequence = SonoffCommandMessageUtilities.getSequence();

    private static final long serialVersionUID = -1947653206187395468L;

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getSequence() {
        return this.sequence;
    }
}
