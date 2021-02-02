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
package org.openhab.binding.sonoff.internal;

import static org.openhab.binding.sonoff.internal.Constants.*;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.eclipse.smarthome.io.net.http.WebSocketFactory;
import org.openhab.binding.sonoff.internal.handler.AccountHandler;
import org.openhab.binding.sonoff.internal.handler.SwitchHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;

/**
 * The {@link sonoffHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author David Murton - Initial contribution
 */
// @NonNullByDefault
@Component(configurationPid = "binding.sonoff", service = ThingHandlerFactory.class)

public class SonoffHandlerFactory extends BaseThingHandlerFactory {
    private final WebSocketFactory webSocketFactory;
    private final HttpClientFactory httpClientFactory;
    private final Gson gson = new Gson();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPE_UIDS.contains(thingTypeUID);
    }

    @Activate
    public SonoffHandlerFactory(final @Reference WebSocketFactory webSocketFactory,
            final @Reference HttpClientFactory httpClientFactory) {
        this.webSocketFactory = webSocketFactory;
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        ThingHandler handler = null;
        switch (thingTypeUID.toString()) {
            case "THING_TYPE_ACCOUNT":
                handler = new AccountHandler((Bridge) thing, webSocketFactory, httpClientFactory, gson);
                break;
            case "THING_TYPE_1":
            case "THING_TYPE_2":
            case "THING_TYPE_3":
            case "THING_TYPE_4":
            case "THING_TYPE_5":
            case "THING_TYPE_6":
            case "THING_TYPE_7":
            case "THING_TYPE_8":
            case "THING_TYPE_9":
            case "THING_TYPE_15":
            case "THING_TYPE_32":
            case "THING_TYPE_77":
                handler = new SwitchHandler(thing, gson);
                break;
        }
        return handler;
    }
}
