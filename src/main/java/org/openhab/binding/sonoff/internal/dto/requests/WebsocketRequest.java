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

import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessageUtilities;

import com.google.gson.JsonObject;

/**
 * @author David Murton - Initial contribution
 */
public class WebsocketRequest {
    private final String action;
    // = "userOnline";
    private final String at;
    private final String apikey;
    private final String appid;
    private final String nonce;
    private final Long ts;
    private final String userAgent = "app";
    private final Long sequence;
    private final Integer version;
    private final JsonObject params;
    private final String deviceid;
    private final String tempRec;

    // This for logging in
    public WebsocketRequest(String appId, String apikey, String at) {
        this.action = "userOnline";
        this.apikey = apikey;
        // this.sequence = sequence;
        this.at = at;
        this.deviceid = null;
        this.params = null;
        this.tempRec = null;
        this.appid = appId;
        this.nonce = SonoffCommandMessageUtilities.getNonce();
        this.ts = SonoffCommandMessageUtilities.getTs() / 1000;
        this.version = SonoffCommandMessageUtilities.VERSION;
        this.sequence = SonoffCommandMessageUtilities.getSequence();
    }

    // This is for updates
    public WebsocketRequest(Long sequence, String apikey, String deviceid, JsonObject params) {
        this.action = "update";
        this.apikey = apikey;
        this.sequence = sequence;
        this.at = null;
        this.deviceid = deviceid;
        this.params = params;
        this.tempRec = "";
        this.appid = null;
        this.nonce = null;
        this.ts = null;
        this.version = null;
    }

    public String getAt() {
        return at;
    }

    public String getApikey() {
        return apikey;
    }

    public String getAction() {
        return action;
    }

    public String getAppid() {
        return appid;
    }

    public String getNonce() {
        return nonce;
    }

    public Long getTs() {
        return ts;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Long getSequence() {
        return sequence;
    }

    public int getVersion() {
        return version;
    }

    public JsonObject getParams() {
        return this.params;
    }

    public String getDeviceid() {
        return this.deviceid;
    }

    public String getTempRec() {
        return this.tempRec;
    }
}
