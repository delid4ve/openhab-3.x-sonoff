/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.sonoff.internal.listeners;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sonoff.internal.dto.api.Device;

/**
 * Informs about updated sensor states
 *
 * @author David Graeff - Initial contribution
 */
@NonNullByDefault
public interface WebSocketConnectionListener {
    /**
     * A sensor config was updated.
     *
     * @param channelUID device channel type
     * @param state new state of the channel
     */
    void webSocketConnectionOpen(Boolean connected);

    void webSocketMessage(Device device);
}
