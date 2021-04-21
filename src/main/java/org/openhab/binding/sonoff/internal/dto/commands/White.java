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
public class White extends AbstractCommand<White> implements Serializable {

    @SerializedName("switch")
    @Expose
    private String switch0;
    @SerializedName("white")
    @Expose
    private WhiteObject white = new WhiteObject();

    public String getSwitch() {
        return switch0;
    }

    public void setSwitch(String switch0) {
        this.switch0 = switch0;
    }

    public WhiteObject getWhite() {
        return white;
    }

    public void setWhite(WhiteObject white) {
        this.white = white;
    }

    private static final long serialVersionUID = 1205249120703729170L;

    public class WhiteObject implements Serializable {
        @SerializedName("br")
        @Expose
        private Integer br;
        @SerializedName("ct")
        @Expose
        private Integer ct;

        private static final long serialVersionUID = 1205249120703729170L;

        public Integer getBrightness() {
            return br;
        }

        public void setBrightness(Integer br) {
            this.br = br;
        }

        public Integer getColorTemperature() {
            return ct;
        }

        public void setColorTemperature(Integer ct) {
            this.ct = ct;
        }
    }

    @Override
    public White getCommand() {
        return this;
    }
}
