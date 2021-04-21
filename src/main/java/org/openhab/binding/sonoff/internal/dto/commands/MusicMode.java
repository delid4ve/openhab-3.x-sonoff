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
public class MusicMode extends AbstractCommand<MusicMode> implements Serializable {

    @SerializedName("switch")
    @Expose
    private String switch0;
    @SerializedName("bright")
    @Expose
    private Integer bright;
    @SerializedName("mode")
    @Expose
    private Integer mode;
    @SerializedName("colorR")
    @Expose
    private Integer colorR;
    @SerializedName("colorG")
    @Expose
    private Integer colorG;
    @SerializedName("colorB")
    @Expose
    private Integer colorB;
    @SerializedName("bright")
    @Expose

    private static final long serialVersionUID = 1205249120703729170L;

    public String getSwitch() {
        return switch0;
    }

    public void setSwitch(String switch0) {
        this.switch0 = switch0;
    }

    public Integer getColorR() {
        return this.colorR;
    }

    public void setColorR(Integer colorR) {
        this.colorR = colorR;
    }

    public Integer getColorG() {
        return this.colorG;
    }

    public void setColorG(Integer colorG) {
        this.colorG = colorG;
    }

    public Integer getColorB() {
        return this.colorB;
    }

    public void setColorB(Integer colorB) {
        this.colorB = colorB;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getBrightness() {
        return bright;
    }

    public void setBrightness(Integer bright) {
        this.bright = bright;
    }

    @Override
    public MusicMode getCommand() {
        return this;
    }
}
