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
package org.openhab.binding.sonoff.internal.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * The {@link SonoffDeviceState} contains the base state of a device
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffDeviceState {
    private final Logger logger = LoggerFactory.getLogger(SonoffAccountHandler.class);

    // Main Parameters
    private final String deviceKey;
    private final Integer uiid;
    private final String deviceid;
    // Properties
    private final String name;
    private final String brand;
    private final String model;
    private final String fw;
    // Connection
    private StringType ipAddress = new StringType("");
    private JsonArray subDevices = new JsonArray();
    private Boolean local = false;
    private Boolean cloud = false;
    // Parameters
    private final SonoffDeviceStateParameters parameters;

    public SonoffDeviceState(JsonObject device) {
        this.deviceid = device.get("deviceid").getAsString();
        this.uiid = device.getAsJsonObject("extra").get("uiid").getAsInt();
        this.deviceKey = device.get("devicekey").getAsString();
        this.name = device.get("name").getAsString();
        this.brand = device.get("brandName").getAsString();
        this.model = device.get("productModel").getAsString();
        JsonElement firmware = device.getAsJsonObject("params").get("fwVersion");
        this.fw = firmware != null ? firmware.getAsString() : "Not Applicable";
        this.parameters = new SonoffDeviceStateParameters();
        updateState(device);
    }

    public SonoffDeviceState updateState(JsonObject device) {
        if (device.get("ipaddress") != null) {
            setIpAddress(new StringType(device.get("ipaddress").getAsString()));
            setLocal(getIpAddress().toString().equals("") ? false : true);
        }
        if (device.get("online") != null) {
            setCloud(device.get("online").getAsBoolean());
        }
        if (device.get("params") != null) {
            if (device.getAsJsonObject("params").get("online") != null) {
                setCloud(device.getAsJsonObject("params").get("online").getAsBoolean());
            }
        }
        setParameters(device.getAsJsonObject("params"));
        if (uiid.equals(66) || uiid.equals(28)) {
            setSubDevices(device);
        }
        return this;
    }

    public SonoffDeviceState getState() {
        return this;
    }

    public SonoffDeviceStateParameters getParameters() {
        return parameters;
    }

    private void setParameters(JsonObject params) {
        // Switches
        if (params.get("switch") != null) {
            parameters.setSwitch0(params.get("switch").getAsString());
        } else {
            if (params.get("switches") != null) {
                JsonArray switches = params.getAsJsonArray("switches");
                if (switches.get(0) != null) {
                    parameters.setSwitch0(
                            params.getAsJsonArray("switches").get(0).getAsJsonObject().get("switch").getAsString());
                }
                if (switches.get(1) != null) {
                    if (switches.get(1).getAsJsonObject().get("switch") != null) {
                        parameters.setSwitch1(
                                params.getAsJsonArray("switches").get(1).getAsJsonObject().get("switch").getAsString());
                    }
                }
                if (switches.get(2) != null) {
                    if (switches.get(2).getAsJsonObject().get("switch") != null) {
                        parameters.setSwitch2(
                                params.getAsJsonArray("switches").get(2).getAsJsonObject().get("switch").getAsString());
                    }
                }
                if (switches.get(3) != null) {
                    if (switches.get(3).getAsJsonObject().get("switch") != null) {
                        parameters.setSwitch3(
                                params.getAsJsonArray("switches").get(3).getAsJsonObject().get("switch").getAsString());
                    }
                }
            }
        }
        // Electric
        if (params.get("power") != null) {
            parameters.setPower(params.get("power").getAsString());
        }

        if (params.get("voltage") != null) {
            parameters.setVoltage(params.get("voltage").getAsString());
        }

        if (params.get("current") != null) {
            parameters.setCurrent(params.get("current").getAsString());
        }

        if (params.get("battery") != null) {
            parameters.setBattery(params.get("battery").getAsDouble());
        }
        // Energy
        if (params.get("hundredDaysKwhData") != null) {
            String kwhData = params.get("hundredDaysKwhData").getAsString();
            if (!kwhData.equals("get")) {
                String[] hexValues;
                String[] splitData = kwhData.split("(?<=\\G.{6})");
                double total = 0.00;
                for (int i = 0; i < 100; i++) {
                    hexValues = splitData[i].split("(?<=\\G.{2})");
                    total = total + Double.parseDouble(Integer.parseInt(hexValues[0], 16) + "."
                            + Integer.parseInt(hexValues[1], 16) + Integer.parseInt(hexValues[2], 16));
                    if (i == 0) {
                        parameters.setTodayKwh(total);
                    }
                    if (i == 1) {
                        double newtotal = Double.parseDouble(Integer.parseInt(hexValues[0], 16) + "."
                                + Integer.parseInt(hexValues[1], 16) + Integer.parseInt(hexValues[2], 16));
                        parameters.setYesterdayKwh(newtotal);
                    }
                    if (i == 6) {
                        parameters.setSevenKwh(total);
                    }
                    if (i == 29) {
                        parameters.setThirtyKwh(total);
                    }
                    if (i == 99) {
                        parameters.setHundredKwh(total);
                    }
                }
            }
        }

        if (uiid.equals(15)) {
            // api returns a string always
            if (params.get("currentTemperature") != null) {
                JsonPrimitive p = params.get("currentTemperature").getAsJsonPrimitive();
                if (p.isString()) {
                    parameters.setTemperature(
                            p.getAsString().equals("unavailable") ? 0.00 : Double.parseDouble(p.getAsString()));
                }
            }
            // lan and websocket return a number most of the time
            if (params.get("temperature") != null) {
                JsonPrimitive p = params.get("temperature").getAsJsonPrimitive();
                if (p.isString()) {
                    parameters.setTemperature(
                            p.getAsString().equals("unavailable") ? 0.00 : Double.parseDouble(p.getAsString()));
                }
                if (p.isNumber()) {
                    parameters.setTemperature(p.getAsDouble());
                }
            }

            // api returns a string always
            if (params.get("currentHumidity") != null) {
                JsonPrimitive p = params.get("currentHumidity").getAsJsonPrimitive();
                if (p.isString()) {
                    parameters.setHumidity(
                            p.getAsString().equals("unavailable") ? 0.00 : Double.parseDouble(p.getAsString()));
                }
            }
            // lan and websocket return a number most of the time
            if (params.get("humidity") != null) {
                JsonPrimitive p = params.get("humidity").getAsJsonPrimitive();
                if (p.isString()) {
                    parameters.setHumidity(
                            p.getAsString().equals("unavailable") ? 0.00 : Double.parseDouble(p.getAsString()));
                }
                if (p.isNumber()) {
                    parameters.setHumidity(p.getAsDouble());
                }
            }
        } else {
            if (params.get("temperature") != null) {
                parameters.setTemperature(Double.valueOf((params.get("temperature").getAsInt() / 100)));
            }

            if (params.get("humidity") != null) {
                parameters.setHumidity(Double.valueOf((params.get("humidity").getAsInt() / 100)));
            }
        }

        // Sensors
        if (params.get("sensorType") != null) {
            parameters.setSensorType(params.get("sensorType").getAsString());
        }

        // Actions
        if (params.get("lastUpdate") != null) {
            parameters.setLastUpdate(params.get("lastUpdate").getAsString());
        }

        if (params.get("actionTime") != null) {
            parameters.setActionTime(params.get("actionTime").getAsString());
        }

        // RGB
        if (params.get("mode") != null) {
            parameters.setMode(params.get("mode").getAsInt());
            parameters.setMusicMode(params.get("mode").getAsInt() == 12 ? "on" : "off");
        }

        if (params.get("sensitive") != null) {
            parameters.setSensitivity(params.get("sensitive").getAsInt());
        }

        if (params.get("speed") != null) {
            parameters.setSpeed(params.get("speed").getAsInt());
        }

        if (params.get("colorR") != null && params.get("colorG") != null && params.get("colorB") != null) {
            parameters.setColor(params.get("colorR").getAsInt(), params.get("colorG").getAsInt(),
                    params.get("colorB").getAsInt());
        }

        if (params.get("ltype") != null) {
            parameters.setLtype(params.get("ltype").getAsString());
        }

        if (params.get("bright") != null) {
            parameters.setColorBrightness(params.get("bright").getAsInt());
        }

        // Colour CCT Bulb

        // White setting
        if (params.get("white") != null) {
            // Color Temperature
            Integer colorTemperature = params.get("white").getAsJsonObject().get("ct").getAsInt();
            if (!colorTemperature.equals(0)) {
                Double d = Double.valueOf(colorTemperature);
                Double e = d / 255 * 100;
                colorTemperature = (int) Math.round(e);
            }
            parameters.setColorTemperature(colorTemperature);

            // Brightness
            parameters.setWhiteBrightness(params.get("white").getAsJsonObject().get("br").getAsInt());
        }

        // Color Setting
        if (params.get("color") != null) {
            // Color
            parameters.setColor(params.get("color").getAsJsonObject().get("r").getAsInt(),
                    params.get("color").getAsJsonObject().get("g").getAsInt(),
                    params.get("color").getAsJsonObject().get("b").getAsInt());
            // Brightness
            parameters.setColorBrightness(params.get("color").getAsJsonObject().get("br").getAsInt());
        }

        // Other
        if (params.get("sledOnline") != null) {
            parameters.setNetworkLED(params.get("sledOnline").getAsString());
        }

        if (params.get("rssi") != null) {
            parameters.setRssi(params.get("rssi").getAsInt());
        }

        if (params.get("zled") != null) {
            parameters.setZigbeeLED(params.get("zled").getAsString());
        }

        // RF
        if (params.get("rfTrig0") != null) {
            parameters.setRf0(params.get("rfTrig0").getAsString());
        }

        if (params.get("rfTrig1") != null) {
            parameters.setRf1(params.get("rfTrig1").getAsString());
        }

        if (params.get("rfTrig2") != null) {
            parameters.setRf2(params.get("rfTrig2").getAsString());
        }

        if (params.get("rfTrig3") != null) {
            parameters.setRf3(params.get("rfTrig3").getAsString());
        }

        if (params.get("rfTrig4") != null) {
            parameters.setRf4(params.get("rfTrig4").getAsString());
        }

        if (params.get("rfTrig5") != null) {
            parameters.setRf5(params.get("rfTrig5").getAsString());
        }

        if (params.get("rfTrig6") != null) {
            parameters.setRf6(params.get("rfTrig6").getAsString());
        }

        if (params.get("rfTrig7") != null) {
            parameters.setRf7(params.get("rfTrig7").getAsString());
        }

        if (params.get("rfTrig8") != null) {
            parameters.setRf8(params.get("rfTrig8").getAsString());
        }

        if (params.get("rfTrig9") != null) {
            parameters.setRf9(params.get("rfTrig9").getAsString());
        }

        if (params.get("rfTrig10") != null) {
            parameters.setRf10(params.get("rfTrig10").getAsString());
        }

        if (params.get("rfTrig11") != null) {
            parameters.setRf11(params.get("rfTrig11").getAsString());
        }

        if (params.get("rfTrig12") != null) {
            parameters.setRf12(params.get("rfTrig12").getAsString());
        }

        if (params.get("rfTrig13") != null) {
            parameters.setRf13(params.get("rfTrig13").getAsString());
        }

        if (params.get("rfTrig14") != null) {
            parameters.setRf14(params.get("rfTrig14").getAsString());
        }

        if (params.get("rfTrig15") != null) {
            parameters.setRf15(params.get("rfTrig15").getAsString());
        }

        if (params.get("rfList") != null) {
            parameters.setRfCodeList(params.getAsJsonArray("rfList"));
        }

        // Zigbee

        if (params.get("trigTime") != null) {
            parameters.setTrigTime(params.get("trigTime").getAsString());
        }

        if (params.get("motion") != null) {
            parameters.setMotion(params.get("motion").getAsInt());
        }
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("Name", name);
        properties.put("Brand", brand);
        properties.put("Model", model);
        properties.put("FW Version", fw);
        properties.put("Device ID", deviceid);
        properties.put("Device Key", deviceKey);
        properties.put("UIID", uiid.toString());
        properties.put("deviceid", deviceid);
        return properties;
    }

    public Integer getUiid() {
        return this.uiid;
    }

    public String getDeviceid() {
        return this.deviceid;
    }

    public Boolean getLocal() {
        return this.local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public Boolean getCloud() {
        return this.cloud;
    }

    public void setCloud(Boolean cloud) {
        this.cloud = cloud;
    }

    public String getDeviceKey() {
        return this.deviceKey;
    }

    public StringType getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(StringType ipAddress) {
        this.ipAddress = ipAddress;
    }

    private void setSubDevices(JsonObject device) {
        JsonArray subDevices = null;
        if (uiid.equals(66)) {
            if (device.getAsJsonObject("params").getAsJsonArray("subDevices") != null) {
                subDevices = device.getAsJsonObject("params").getAsJsonArray("subDevices");
            }
        }
        if (uiid.equals(28)) {
            if (device.getAsJsonObject("tags") != null) {
                subDevices = device.getAsJsonObject("tags").getAsJsonArray("zyx_info");
            }
        }
        if (subDevices != null) {
            this.subDevices = subDevices;
        }
    }

    public JsonArray getSubDevices() {
        return subDevices;
    }
}
