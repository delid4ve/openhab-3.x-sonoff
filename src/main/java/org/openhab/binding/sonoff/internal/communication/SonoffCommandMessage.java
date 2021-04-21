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
package org.openhab.binding.sonoff.internal.communication;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.dto.commands.AbstractCommand;

/**
 * The {@link SonoffCommandMessage} creates a new message to be sent to Ewelink devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffCommandMessage {

    private final String command;
    private final @Nullable AbstractCommand<?> params;
    private String deviceid = "";
    private Long sequence = 0L;
    private Boolean lanSupported = false;

    // Api device message
    public SonoffCommandMessage(String deviceid) {
        this.command = "device";
        this.deviceid = deviceid;
        this.params = null;
    }

    // Api devices message
    public SonoffCommandMessage() {
        this.command = "devices";
        this.params = null;
    }

    // Device Message Type
    public SonoffCommandMessage(String command, String deviceid, Boolean lanSupported, AbstractCommand<?> params) {
        this.command = command;
        this.deviceid = deviceid;
        this.lanSupported = lanSupported;
        this.params = params;
    }

    // WebSocket Login Message Type
    public SonoffCommandMessage(String command, AbstractCommand<?> params) {
        this.command = command;
        this.params = params;
    }

    public String getCommand() {
        return this.command;
    }

    public String getDeviceid() {
        return this.deviceid;
    }

    public Long getSequence() {
        return this.sequence;
    }

    public void setSequence() {
        this.sequence = SonoffCommandMessageUtilities.getSequence();
    }

    public Boolean getLanSupported() {
        return this.lanSupported;
    }

    public @Nullable AbstractCommand<?> getParams() {
        return this.params;
    }
}
