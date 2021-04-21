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
package org.openhab.binding.sonoff.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link AccountConfig} class defines the configuration for an Account Thing
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class AccountConfig {

    public String email = "";
    public String password = "";
    public String accessmode = "";

    @Override
    public String toString() {
        return "[email=" + email + ", password=" + getPasswordForPrinting() + ", accessmode=" + accessmode + "]";
    }

    private String getPasswordForPrinting() {
        return password.isEmpty() ? "<empty>" : "*********";
    }
}
