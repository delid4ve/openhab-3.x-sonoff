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
import org.openhab.binding.sonoff.internal.dto.commands.Brightness;
import org.openhab.binding.sonoff.internal.dto.commands.Mode;
import org.openhab.binding.sonoff.internal.dto.commands.RGBLight;
import org.openhab.binding.sonoff.internal.dto.commands.SLed;
import org.openhab.binding.sonoff.internal.dto.commands.Sensitivity;
import org.openhab.binding.sonoff.internal.dto.commands.SingleSwitch;
import org.openhab.binding.sonoff.internal.dto.commands.Speed;
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
public class SonoffRGBStripHandler extends SonoffBaseDeviceHandler {

    private final Logger logger = LoggerFactory.getLogger(SonoffRGBStripHandler.class);
    private Integer previousMode = 1;

    public SonoffRGBStripHandler(Thing thing) {
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
        Brightness brightness = new Brightness();
        brightness.setBrightness(value);
        SonoffCommandMessage message = new SonoffCommandMessage("brightness", this.deviceid, false, brightness);
        queueMessage(message);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        SonoffCommandMessage message = null;
        if (command instanceof RefreshType) {
            return;
        } else {
            switch (channelUID.getId()) {
                case "switch":
                    if (command instanceof OnOffType) {
                        changeSwitch(command.toString().toLowerCase());
                    }
                    return;
                case "sled":
                    SLed sled = new SLed();
                    sled.setSledOnline(command.toString().toLowerCase());
                    message = new SonoffCommandMessage("sledOnline", this.deviceid, false, sled);
                    break;
                case "brightness":
                    if (command instanceof PercentType) {
                        PercentType br = (PercentType) command;
                        Integer brightness = br.intValue();
                        changeBrightness(brightness);
                    }
                    return;
                case "speed":
                    Speed speed = new Speed();
                    speed.setSpeed(Integer.parseInt(command.toString().toLowerCase()));
                    message = new SonoffCommandMessage("speed", this.deviceid, false, speed);
                    break;
                case "mode":
                    Mode mode = new Mode();
                    mode.setMode(Integer.parseInt(command.toString().toLowerCase()));
                    message = new SonoffCommandMessage("mode", this.deviceid, false, mode);
                    break;
                case "sensitivity":
                    Sensitivity sensitivity = new Sensitivity();
                    sensitivity.setSensitivity(Integer.parseInt(command.toString().toLowerCase()));
                    message = new SonoffCommandMessage("sensitivity", this.deviceid, false, sensitivity);
                    break;
                case "color":
                    if (command.toString().contains(",")) {
                        HSBType hsb = new HSBType(command.toString());
                        PercentType red = hsb.getRed();
                        PercentType green = hsb.getGreen();
                        PercentType blue = hsb.getBlue();
                        int redr = (int) (red.doubleValue() * 255 / 100);
                        int greenr = (int) (green.doubleValue() * 255 / 100);
                        int bluer = (int) (blue.doubleValue() * 255 / 100);
                        RGBLight rgb = new RGBLight();
                        rgb.setColorB(bluer);
                        rgb.setColorG(greenr);
                        rgb.setColorR(redr);
                        message = new SonoffCommandMessage("color", this.deviceid, false, rgb);
                        break;
                    }
                    if (command instanceof OnOffType) {
                        logger.error("Please use the switch channel instead");
                        break;
                    }

                    if (command instanceof PercentType) {
                        logger.error("Please use the dimmer channel instead");
                        break;
                    }
                case "musicSwitch":
                    Mode modeChange = new Mode();
                    String musicSwitch = command.toString().toLowerCase();
                    if (musicSwitch.equals("on")) {
                        modeChange.setMode(12);
                    } else {
                        modeChange.setMode(previousMode);
                    }
                    message = new SonoffCommandMessage("mode", this.deviceid, false, modeChange);
                    break;
            }
            if (message != null) {
                queueMessage(message);
            } else {
                logger.debug("Unable to send command as was null for device {}", this.deviceid);
            }
        }
    }

    @Override
    public void updateDevice(SonoffDeviceState newDevice) {
        updateState("switch", newDevice.getParameters().getSwitch0());
        updateState("color", newDevice.getParameters().getColor());
        updateState("brightness", newDevice.getParameters().getColorBrightness());
        updateState("rssi", newDevice.getParameters().getRssi());
        updateState("sled", newDevice.getParameters().getNetworkLED());
        updateState("mode", newDevice.getParameters().getMode());
        if (Integer.parseInt(newDevice.getParameters().getMode().toString()) != 12) {
            previousMode = Integer.parseInt(newDevice.getParameters().getMode().toString());
        }
        updateState("speed", newDevice.getParameters().getSpeed());
        updateState("sensitivity", newDevice.getParameters().getSensitivity());
        updateState("musicSwitch", newDevice.getParameters().getMusicMode());
        // Connections
        this.cloud = newDevice.getCloud();
        updateState("cloudOnline", this.cloud ? new StringType("Connected") : new StringType("Disconnected"));
        updateStatus();
    }
}