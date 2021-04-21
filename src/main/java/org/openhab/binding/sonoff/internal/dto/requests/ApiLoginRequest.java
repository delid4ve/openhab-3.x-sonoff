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

import org.openhab.binding.sonoff.internal.dto.commands.AbstractCommand;

/**
 * @author David Murton - Initial contribution
 */
public class ApiLoginRequest extends AbstractCommand<ApiLoginRequest> {

    private final String email;
    private final String password;
    private final String countryCode;

    public ApiLoginRequest(String email, String password, String countryCode) {
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public ApiLoginRequest getCommand() {
        return this;
    }
}
