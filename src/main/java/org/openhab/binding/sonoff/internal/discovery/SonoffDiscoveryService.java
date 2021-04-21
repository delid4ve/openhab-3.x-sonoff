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
package org.openhab.binding.sonoff.internal.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.SonoffBindingConstants;
import org.openhab.binding.sonoff.internal.SonoffCacheProvider;
import org.openhab.binding.sonoff.internal.connection.SonoffApiConnection;
import org.openhab.binding.sonoff.internal.connection.SonoffConnectionManager;
import org.openhab.binding.sonoff.internal.handler.*;
import org.openhab.binding.sonoff.internal.handler.SonoffRfBridgeHandler;
import org.openhab.binding.sonoff.internal.handler.SonoffZigbeeBridgeHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffDiscoveryService} Allows Discovery of Ewelink devices
 *
 * @author David Murton - Initial contribution
 */

@Component(service = SonoffDiscoveryService.class, immediate = true, configurationPid = "discovery.sonoff")

@NonNullByDefault
public class SonoffDiscoveryService extends AbstractDiscoveryService implements ThingHandlerService, DiscoveryService {
    // , DiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(SonoffDiscoveryService.class);
    private static final int DISCOVER_TIMEOUT_SECONDS = 10;
    private @Nullable SonoffAccountHandler account;
    private @Nullable ScheduledFuture<?> scanTask;
    private final Gson gson;

    public SonoffDiscoveryService() {
        super(SonoffBindingConstants.DISCOVERABLE_THING_TYPE_UIDS, DISCOVER_TIMEOUT_SECONDS, false);
        this.gson = new Gson();
    }

    @Override
    protected void activate(@Nullable Map<String, Object> configProperties) {
    }

    @Override
    public void deactivate() {
    }

    @Override
    protected void startScan() {
        logger.debug("Start Scan");
        final ScheduledFuture<?> scanTask = this.scanTask;
        if (scanTask != null) {
            scanTask.cancel(true);
        }
        this.scanTask = scheduler.schedule(() -> {
            try {
                discover();
            } catch (Exception e) {
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void stopScan() {
        logger.debug("Stop Scan");
        super.stopScan();
        final ScheduledFuture<?> scanTask = this.scanTask;
        if (scanTask != null) {
            scanTask.cancel(true);
            this.scanTask = null;
        }
    }

    // Used for discovery
    public List<JsonObject> createCache(List<Thing> things) {
        SonoffCacheProvider cacheProvider = new SonoffCacheProvider();
        List<JsonObject> devices = new ArrayList<JsonObject>();

        final SonoffAccountHandler account = this.account;
        if (account != null) {
            SonoffConnectionManager connectionManager = account.getConnectionManager();
            final String mode = connectionManager.getMode();
            final SonoffApiConnection api = account.getConnectionManager().getApi();
            // If we are in local mode connect to the api
            if (mode.equals("local")) {
                api.login();
            }
            String response = "";
            try {
                response = api.createCache();
            } catch (Exception e) {
                logger.debug("Creating the device cache threw an error {}", e.getMessage());
            }
            JsonObject main = gson.fromJson(response, JsonObject.class);
            if (main != null) {
                JsonObject data = main.get("data").getAsJsonObject();
                JsonArray thingList = data.get("thingList").getAsJsonArray();
                for (int i = 0; i < thingList.size(); i++) {
                    // Items (type 1)
                    JsonElement type = thingList.get(i).getAsJsonObject().get("itemType");
                    if (type != null) {
                        if (type.getAsInt() == 1) {
                            JsonObject device = thingList.get(i).getAsJsonObject().getAsJsonObject("itemData");
                            String deviceid = device.get("deviceid").getAsString();
                            logger.debug("Processing device {}", deviceid);
                            if (!cacheProvider.checkFile(deviceid)) {
                                cacheProvider.newFile(deviceid, gson.toJson(device));
                                account.addState(deviceid);
                                logger.debug("Cache file and state created for device {} as it was missing", deviceid);

                                for (int m = 0; m < things.size(); m++) {
                                    String config = things.get(m).getConfiguration().get("deviceid").toString();
                                    if (config.equals(deviceid)) {
                                        logger.info("Re-Initializing {} as a thing was already present", deviceid);
                                        Thing thing = things.get(m);
                                        thing.getHandler().thingUpdated(thing);
                                    }
                                }

                            }
                            devices.add(device);
                        }
                    }
                }
            }
        }
        return devices;
    }

    private void discover() {
        logger.debug("Sonoff - Start Discovery");
        // Get the master Bridge and create the cache
        final SonoffAccountHandler account = this.account;
        if (account != null) {
            new ArrayList<JsonObject>();
            ThingUID bridgeUID = account.getThing().getUID();
            int i = 0;

            // Get a list of child things so we can add sub devices and reinitialise if required
            List<Thing> things = account.getThing().getThings();

            List<JsonObject> devices = createCache(things);

            // Create Top Level Devices
            for (i = 0; i < devices.size(); i++) {
                JsonObject device = devices.get(i);
                String deviceid = device.get("deviceid").getAsString();
                Integer uiid = device.get("extra").getAsJsonObject().get("uiid").getAsInt();
                JsonObject params = device.getAsJsonObject("params");

                logger.info("Discovered device {}", deviceid);
                ThingTypeUID thingTypeUid = SonoffBindingConstants.createMap().get(uiid);
                if (thingTypeUid != null) {
                    ThingUID deviceThing = new ThingUID(thingTypeUid, account.getThing().getUID(), deviceid);
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("deviceid", deviceid);
                    properties.put("Name", device.get("name").getAsString());
                    properties.put("Brand", device.get("brandName").getAsString());
                    properties.put("Model", device.get("productModel").getAsString());
                    if (params.get("fwVersion") != null) {
                        properties.put("FW Version", params.get("fwVersion").getAsString());
                    }
                    properties.put("Device ID", deviceid);
                    properties.put("Device Key", device.get("devicekey").getAsString());
                    properties.put("UIID", uiid);
                    properties.put("API Key", device.get("apikey").getAsString());
                    if (params.get("ssid") != null) {
                        properties.put("Connected To SSID", params.get("ssid").getAsString());
                    }
                    String label = device.get("name").getAsString();
                    thingDiscovered(
                            DiscoveryResultBuilder.create(deviceThing).withLabel(label).withProperties(properties)
                                    .withRepresentationProperty("deviceid").withBridge(bridgeUID).build());
                } else {
                    Boolean subDevice = false;
                    subDevice = SonoffBindingConstants.createZigbeeMap().get(uiid) != null ? true : subDevice;
                    subDevice = SonoffBindingConstants.createSensorMap().get(uiid) != null ? true : subDevice;
                    if (!subDevice) {
                        logger.error(
                                "Unable to add {} as its not supported, please forward the cache file to the developer",
                                deviceid);
                    }
                }
            }

            // Create Child Devices
            int j = 0;
            for (i = 0; i < things.size(); i++) {
                String uiid = things.get(i).getThingTypeUID().getId();
                switch (uiid) {
                    // RF Devices
                    case "28":
                        SonoffRfBridgeHandler rfBridge = (SonoffRfBridgeHandler) account.getThing().getThings().get(i)
                                .getHandler();
                        if (rfBridge != null) {
                            JsonArray subDevices = rfBridge.getSubDevices();
                            logger.debug("Found {} rf device/s", subDevices.size());
                            for (j = 0; j < subDevices.size(); j++) {
                                JsonObject subDevice = subDevices.get(j).getAsJsonObject();
                                Integer type = Integer.parseInt(subDevice.get("remote_type").getAsString());
                                ThingTypeUID thingTypeUid = SonoffBindingConstants.createSensorMap().get(type);
                                if (thingTypeUid != null) {
                                    ThingUID rfThing = new ThingUID(thingTypeUid, rfBridge.getThing().getUID(), j + "");
                                    Map<String, Object> properties = new HashMap<>();
                                    properties.clear();
                                    properties.put("deviceid", j + "");
                                    properties.put("Name", subDevice.get("name").getAsString());
                                    String rfLabel = subDevice.get("name").getAsString();
                                    thingDiscovered(DiscoveryResultBuilder.create(rfThing).withLabel(rfLabel)
                                            .withProperties(properties).withRepresentationProperty("deviceid")
                                            .withBridge(rfBridge.getThing().getUID()).build());
                                }
                            }
                        }
                        break;
                    // Zigbee Devices
                    case "66":
                        SonoffZigbeeBridgeHandler zigbeeBridge = (SonoffZigbeeBridgeHandler) account.getThing()
                                .getThings().get(i).getHandler();
                        if (zigbeeBridge != null) {
                            JsonArray subDevices = zigbeeBridge.getSubDevices();
                            logger.debug("Found {} zigbee device/s", subDevices.size());
                            for (j = 0; j < subDevices.size(); j++) {
                                JsonObject subDevice = subDevices.get(j).getAsJsonObject();
                                String subDeviceid = subDevice.get("deviceid").getAsString();
                                logger.debug("Discovering zigbee device {}", subDeviceid);
                                Integer subDeviceuiid = subDevice.get("uiid").getAsInt();
                                // Lookup our device in the main list
                                for (int k = 0; k < devices.size(); k++) {
                                    if (devices.get(k).getAsJsonObject().get("deviceid").getAsString()
                                            .equals(subDeviceid)) {
                                        subDevice = devices.get(k);
                                        JsonObject subParams = subDevice.get("params").getAsJsonObject();
                                        ThingTypeUID thingTypeUid = SonoffBindingConstants.createZigbeeMap()
                                                .get(subDeviceuiid);
                                        if (thingTypeUid != null) {
                                            ThingUID zigbeeThing = new ThingUID(thingTypeUid,
                                                    zigbeeBridge.getThing().getUID(), subDeviceid);
                                            Map<String, Object> properties = new HashMap<>();
                                            properties.clear();
                                            properties.put("deviceid", subDeviceid);
                                            properties.put("Name", subDevice.get("name").getAsString());
                                            properties.put("Brand", subDevice.get("brandName").getAsString());
                                            properties.put("Model", subDevice.get("productModel").getAsString());
                                            if (subParams.get("fwVersion") != null) {
                                                properties.put("FW Version", subParams.get("fwVersion").getAsString());
                                            }
                                            properties.put("Device Key", subDevice.get("devicekey").getAsString());
                                            properties.put("UIID", subDeviceuiid);
                                            properties.put("API Key", subDevice.get("apikey").getAsString());
                                            String label = subDevice.get("name").getAsString();
                                            thingDiscovered(DiscoveryResultBuilder.create(zigbeeThing).withLabel(label)
                                                    .withProperties(properties).withRepresentationProperty("deviceid")
                                                    .withBridge(zigbeeBridge.getThing().getUID()).build());
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof SonoffAccountHandler) {
            account = (SonoffAccountHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return account;
    }
}
