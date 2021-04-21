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
package org.openhab.binding.sonoff.internal;

import static org.openhab.binding.sonoff.internal.SonoffBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.sonoff.internal.handler.*;
import org.openhab.binding.sonoff.internal.handler.SonoffAccountHandler;
import org.openhab.binding.sonoff.internal.handler.SonoffRfBridgeHandler;
import org.openhab.binding.sonoff.internal.handler.SonoffRfDeviceHandler;
import org.openhab.binding.sonoff.internal.handler.SonoffZigbeeBridgeHandler;
import org.openhab.binding.sonoff.internal.handler.SonoffZigbeeDevice1770Handler;
import org.openhab.binding.sonoff.internal.handler.SonoffZigbeeDevice2026Handler;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.io.net.http.WebSocketFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link sonoffHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.sonoff", service = ThingHandlerFactory.class)

public class SonoffHandlerFactory extends BaseThingHandlerFactory {
    private final WebSocketClient websocketClient;
    private final HttpClient httpClient;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPE_UIDS.contains(thingTypeUID);
    }

    @Activate
    public SonoffHandlerFactory(final @Reference WebSocketFactory webSocketFactory,
            final @Reference HttpClientFactory httpClientFactory) {
        this.websocketClient = webSocketFactory.getCommonWebSocketClient();
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        String id = thing.getThingTypeUID().getId();
        switch (id) {
            case "account":
                return new SonoffAccountHandler((Bridge) thing, websocketClient, httpClient);
            case "1":
            case "6":
            case "14":
            case "27":
            case "81":
            case "107":
                return new SonoffSwitchSingleHandler(thing);
            case "2":
            case "3":
            case "4":
            case "7":
            case "8":
            case "9":
            case "29":
            case "30":
            case "31":
            case "77":
            case "78":
            case "82":
            case "83":
            case "84":
            case "126":
                return new SonoffSwitchMultiHandler(thing);
            case "5":
                return new SonoffSwitchPOWHandler(thing);
            case "15":
                return new SonoffSwitchTHHandler(thing);
            case "24":
                return new SonoffGSMSocketHandler(thing);
            case "28":
                return new SonoffRfBridgeHandler((Bridge) thing);
            case "32":
                return new SonoffSwitchPOWR2Handler(thing);
            case "59":
                return new SonoffRGBStripHandler(thing);
            case "66":
                return new SonoffZigbeeBridgeHandler((Bridge) thing);
            case "102":
                return new SonoffMagneticSwitchHandler(thing);
            case "104":
                return new SonoffRGBCCTHandler(thing);
            case "1770":
                return new SonoffZigbeeDevice1770Handler(thing);
            case "2026":
                return new SonoffZigbeeDevice2026Handler(thing);
            case "rfremote1":
            case "rfremote2":
            case "rfremote3":
            case "rfremote4":
            case "rfsensor":
                return new SonoffRfDeviceHandler(thing);

            default:
                return null;
        }
    }
}
