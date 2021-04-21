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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sonoff.internal.communication.SonoffCommandMessage;
import org.openhab.binding.sonoff.internal.dto.commands.Color;
import org.openhab.binding.sonoff.internal.dto.commands.SingleSwitch;
import org.openhab.binding.sonoff.internal.dto.commands.White;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SonoffLanDevice4Handler} allows the handling of commands and updates to Devices with uuid's:
 * 1
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffRGBCCTHandler extends SonoffBaseDeviceHandler {

    private final Logger logger = LoggerFactory.getLogger(SonoffRGBStripHandler.class);

    private String modeCache = "";
    private Boolean switchCache = false;
    private Integer brightnessCache = 0;
    private Boolean brightnessCheck = false;

    private Integer colorTemperatureCache = 0;
    private Boolean colorTemperatureCheck = false;

    private Boolean colorCheck = false;
    private PercentType redCache = new PercentType(0);
    private PercentType greenCache = new PercentType(0);
    private PercentType blueCache = new PercentType(0);

    public SonoffRGBCCTHandler(Thing thing) {
        super(thing);
    }

    public void startTasks() {
    }

    public void cancelTasks() {
    }

    private void changeSwitch(String onOff) {
        SingleSwitch singleSwitch = new SingleSwitch();
        singleSwitch.setSwitch(onOff);
        SonoffCommandMessage message = new SonoffCommandMessage("switch", this.deviceid, isLocalOut ? true : false,
                singleSwitch);
        queueMessage(message);
    }

    private void changeBrightness(Integer value) {
        if (this.modeCache.equals("white")) {
            White white = new White();
            white.getWhite().setBrightness(value);
            queueMessage(new SonoffCommandMessage("brightness", this.deviceid, false, white));
            return;
        }

        if (this.modeCache.equals("color")) {
            Color color = new Color();
            color.getColor().setBrightness(value);
            queueMessage(new SonoffCommandMessage("brightness", this.deviceid, false, color));
            return;
        }
    }

    private void changeColorTemperature(Integer value) {
        if (value != 0) {
            Double d = Double.valueOf(value.intValue());
            Double e = d / 100 * 255;
            value = (int) Math.round(e);
        }
        White white = new White();
        white.getWhite().setColorTemperature(value);
        queueMessage(new SonoffCommandMessage("colorTemperature", this.deviceid, false, white));
    }

    private void changeColor(Command command) {
        HSBType hsb = (HSBType) command;
        PercentType red = hsb.getRed();
        PercentType green = hsb.getGreen();
        PercentType blue = hsb.getBlue();
        int redr = (int) (red.doubleValue() * 255 / 100);
        int greenr = (int) (green.doubleValue() * 255 / 100);
        int bluer = (int) (blue.doubleValue() * 255 / 100);
        Color color = new Color();
        color.getColor().setColorB(bluer);
        color.getColor().setColorG(greenr);
        color.getColor().setColorR(redr);
        queueMessage(new SonoffCommandMessage("color", this.deviceid, false, color));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        } else {
            logger.debug("CCT received command on channel {}, with id {}", channelUID, channelUID.getId());
            switch (channelUID.getId()) {
                case "switch":
                    if (command instanceof OnOffType) {
                        changeSwitch(command.toString().toLowerCase());
                        if (command.toString().toLowerCase().equals("off")) {
                            updateState("brightness", new PercentType(0));
                        }
                    }
                    return;

                case "brightness":
                    if (command instanceof PercentType) {
                        PercentType br = (PercentType) command;
                        Integer brightness = br.intValue();

                        if (brightness.equals(0)) {
                            changeSwitch("off");
                            return;
                        }

                        if (!this.switchCache) {
                            this.brightnessCache = brightness;
                            this.brightnessCheck = true;
                            changeSwitch("on"); // We have to send this as there is a bug in the api where switch doesnt
                                                // update to on when changing the brightness but the bulb does come on
                        }
                        changeBrightness(brightness);

                        // updateState("switch", OnOffType.ON);
                        return;
                    }

                case "colorTemperature":
                    if (command instanceof PercentType) {
                        PercentType ct = (PercentType) command;
                        Integer colorTemperature = ct.intValue();

                        if (!this.switchCache) {
                            this.colorTemperatureCache = colorTemperature;
                            this.colorTemperatureCheck = true;
                            changeSwitch("on"); // We have to send this as there is a bug in the api where switch doesnt
                                                // update to on when changing the brightness but the bulb does come on
                        }
                        changeColorTemperature(colorTemperature);
                        if (this.modeCache.equals("color")) {
                            updateState("lightMode", new StringType("white"));
                            this.modeCache = "white";
                        }
                        return;
                    }

                case "color":
                    // if (command instanceof HSBType) { does not work
                    if (command.toString().contains(",")) {
                        HSBType color = (HSBType) command;

                        if (!this.switchCache) {
                            PercentType red = color.getRed();
                            PercentType green = color.getGreen();
                            PercentType blue = color.getBlue();
                            this.redCache = red;
                            this.greenCache = green;
                            this.blueCache = blue;
                            this.colorCheck = true;
                            changeSwitch("on"); // We have to send this as there is a bug in the api where switch doesnt
                                                // update to on when changing the color
                        }
                        changeColor(command);
                        if (this.modeCache.equals("color")) {
                            updateState("lightMode", new StringType("color"));
                            this.modeCache = "white";
                        }
                        return;
                    }

                    if (command instanceof OnOffType) {
                        logger.error("Please use the switch channel instead");
                        return;
                    }

                    if (command instanceof PercentType) {
                        logger.error("Please use the dimmer channel instead");
                        return;
                    }
                default:
                    logger.debug("Unable to send command as was null for device {}", this.deviceid);
            }
        }
    }

    @Override
    public void updateDevice(SonoffDeviceState newDevice) {
        updateState("switch", newDevice.getParameters().getSwitch0());
        this.switchCache = newDevice.getParameters().getSwitch0().toString().toLowerCase().equals("on") ? true : false;

        StringType mode = newDevice.getParameters().getLtype();
        updateState("lightMode", newDevice.getParameters().getLtype());
        this.modeCache = mode.toString();

        PercentType brightness = mode.toString().equals("color") ? newDevice.getParameters().getColorBrightness()
                : newDevice.getParameters().getWhiteBrightness();
        String switch0 = newDevice.getParameters().getSwitch0().toString().toLowerCase();

        if (this.brightnessCheck) {
            if (this.brightnessCache.equals(brightness.intValue())) {
                updateState("brightness", brightness);
                brightnessCheck = false;
            }
        } else {
            if (switch0.equals("off")) {
                updateState("brightness", new PercentType(0));
                this.brightnessCache = 0;
            } else {
                updateState("brightness", brightness);
                this.brightnessCache = brightness.intValue();
            }
        }

        HSBType color = newDevice.getParameters().getColor();
        if (this.colorCheck) {
            if (this.redCache.equals(color.getRed()) && this.greenCache.equals(color.getGreen())
                    && this.blueCache.equals(color.getBlue())) {
                updateState("color", color);
                colorCheck = false;
            }
        } else {
            updateState("color", color);
            this.redCache = color.getRed();
            this.greenCache = color.getGreen();
            this.blueCache = color.getBlue();
        }

        PercentType colorTemperature = newDevice.getParameters().getColorTemperature();
        if (this.colorTemperatureCheck) {
            if (this.colorTemperatureCache.equals(colorTemperature.intValue())) {
                updateState("colorTemperature", colorTemperature);
                colorTemperatureCheck = false;
            }
        } else {
            updateState("colorTemperature", colorTemperature);
            this.colorTemperatureCache = colorTemperature.intValue();
        }

        updateState("ipaddress", newDevice.getIpAddress());

        // Connections
        this.cloud = newDevice.getCloud();
        this.local = newDevice.getLocal();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateState("localOnline", this.local ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();
    }
}
