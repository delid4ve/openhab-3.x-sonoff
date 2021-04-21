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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class MultiSwitch extends AbstractCommand<MultiSwitch> implements Serializable {

    @SerializedName("switches")
    @Expose
    private List<Switch> switches = new ArrayList<Switch>();
    private static final long serialVersionUID = 1205249120703729170L;

    public List<Switch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }

    public class Switch implements Serializable {

        @SerializedName("switch")
        @Expose
        private String switch0;
        @SerializedName("outlet")
        @Expose
        private Integer outlet;
        private static final long serialVersionUID = 5376512912986765998L;

        public String getSwitch() {
            return switch0;
        }

        public void setSwitch(String switch0) {
            this.switch0 = switch0;
        }

        public Integer getOutlet() {
            return outlet;
        }

        public void setOutlet(Integer outlet) {
            this.outlet = outlet;
        }
    }

    @Override
    public MultiSwitch getCommand() {
        return this;
    }
}
