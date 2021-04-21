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

package org.openhab.binding.sonoff.internal.dto.commands;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class SingleSwitch extends AbstractCommand<SingleSwitch> implements Serializable {

    @SerializedName("switch")
    @Expose
    private String switch0;
    @SerializedName("voltage")
    @Expose
    private String voltage;
    @SerializedName("current")
    @Expose
    private String current;
    @SerializedName("power")
    @Expose
    private String power;
    @SerializedName("uiActive")
    @Expose
    private Integer uiActive;
    @SerializedName("sledOnline")
    @Expose
    private String sledOnline;
    @SerializedName("startup")
    @Expose
    private String startup;

    private static final long serialVersionUID = 1205249120703729170L;

    public String getSwitch() {
        return switch0;
    }

    public void setSwitch(String switch0) {
        this.switch0 = switch0;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Integer getUiActive() {
        return uiActive;
    }

    public void setUiActive(Integer uiActive) {
        this.uiActive = uiActive;
    }

    public String getSledOnline() {
        return sledOnline;
    }

    public void setSledOnline(String sledOnline) {
        this.sledOnline = sledOnline;
    }

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    @Override
    public SingleSwitch getCommand() {
        return this;
    }
}
