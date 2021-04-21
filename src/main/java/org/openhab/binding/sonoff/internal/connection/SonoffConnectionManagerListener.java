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

import com.google.gson.JsonObject;

/**
 * The {@link SonoffConnectionManagerImpl} informs the account about connection events
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public interface SonoffConnectionManagerListener extends ServiceListener {

    /**
     * Provides overall connection information
     *
     */
    void isConnected(Boolean lanConnected, Boolean cloudConnected);

    /**
     * Responses coming from the LAN Connection
     *
     */
    void lanResponse(String message);

    /**
     * Responses coming from the Websocket Connection
     *
     */
    void websocketMessage(String message);

    /**
     * Responses coming from the Api Connection
     *
     */
    void apiMessage(JsonObject thingResponse);

    /**
     * Provides the ApiKey for use in message generation
     *
     */
    void setApiKey(String apiKey);
}
