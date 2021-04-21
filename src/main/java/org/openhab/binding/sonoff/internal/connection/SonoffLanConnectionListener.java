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
package org.openhab.binding.sonoff.internal.connection;

import javax.jmdns.ServiceListener;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SonoffMessageListener} passes all received messages from connections to be converted
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public interface SonoffLanConnectionListener extends ServiceListener {

    void lanResponse(String message);

    void lanConnected(Boolean connected);
}
