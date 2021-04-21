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
import org.openhab.binding.sonoff.internal.dto.commands.AbstractCommand;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class ApiStatusChange extends AbstractCommand<ApiStatusChange> implements Serializable {

    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("version")
    @Expose
    private Integer version = SonoffCommandMessageUtilities.VERSION;
    @SerializedName("appid")
    @Expose
    private String appid = SonoffCommandMessageUtilities.APPID;
    @SerializedName("ts")
    @Expose
    private Long ts = SonoffCommandMessageUtilities.getTs();
    @SerializedName("params")
    @Expose
    private String params;
    private static final long serialVersionUID = 2958983062170484126L;

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Long getTs() {
        return this.ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public ApiStatusChange getCommand() {
        return this;
    }
}
