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

/**
 * @author David Murton - Initial contribution
 */
public class GeneralRequest {

    private String appid = SonoffCommandMessageUtilities.APPID;
    private String nonce = SonoffCommandMessageUtilities.getNonce();
    private Long ts = SonoffCommandMessageUtilities.getTs();
    private int version = SonoffCommandMessageUtilities.VERSION;
    private String accept;

    public String getAppid() {
        return this.appid;
    }

    public String getNonce() {
        return this.nonce;
    }

    public Long getTs() {
        return this.ts;
    }

    public int getVersion() {
        return this.version;
    }

    public String getAccept() {
        return this.accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
