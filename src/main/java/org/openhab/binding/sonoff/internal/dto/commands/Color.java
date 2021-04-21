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
public class Color extends AbstractCommand<Color> implements Serializable {

    @SerializedName("switch")
    @Expose
    private String switch0;
    @SerializedName("color")
    @Expose
    private ColorObject color = new ColorObject();

    public String getSwitch() {
        return switch0;
    }

    public void setSwitch(String switch0) {
        this.switch0 = switch0;
    }

    public ColorObject getColor() {
        return color;
    }

    public void setColor(ColorObject color) {
        this.color = color;
    }

    private static final long serialVersionUID = 5376512912986765998L;

    public class ColorObject implements Serializable {
        @SerializedName("r")
        @Expose
        private Integer r;
        @SerializedName("g")
        @Expose
        private Integer g;
        @SerializedName("b")
        @Expose
        private Integer b;
        @SerializedName("br")
        @Expose
        private Integer br;

        private static final long serialVersionUID = 5376512912986765998L;

        public Integer getColorR() {
            return this.r;
        }

        public void setColorR(Integer r) {
            this.r = r;
        }

        public Integer getColorG() {
            return this.g;
        }

        public void setColorG(Integer g) {
            this.g = g;
        }

        public Integer getColorB() {
            return this.b;
        }

        public void setColorB(Integer b) {
            this.b = b;
        }

        public Integer getBrightness() {
            return br;
        }

        public void setBrightness(Integer br) {
            this.br = br;
        }
    }

    @Override
    public Color getCommand() {
        return this;
    }
}
