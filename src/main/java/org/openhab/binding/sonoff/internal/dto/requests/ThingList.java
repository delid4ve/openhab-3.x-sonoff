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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class ThingList implements Serializable {

    @SerializedName("thingList")
    @Expose
    private List<Things> things = new ArrayList<Things>();
    private static final long serialVersionUID = 1205249120703729170L;

    public List<Things> getThings() {
        return things;
    }

    public void setThings(List<Things> things) {
        this.things = things;
    }
}
