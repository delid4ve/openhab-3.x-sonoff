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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SonoffBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffBindingConstants {

    public static final String BINDING_ID = "sonoff";

    public static final Set<Integer> LAN_IN = Collections.unmodifiableSet(Stream
            .of(1, 2, 3, 4, 5, 6, 7, 8, 9, 14, 15, 28, 32, 44, 77, 78, 103, 104, 126).collect(Collectors.toSet()));

    public static final Set<Integer> LAN_OUT = Collections.unmodifiableSet(
            Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 14, 28, 32, 44, 77, 78, 126).collect(Collectors.toSet()));

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_1 = new ThingTypeUID(BINDING_ID, "1"); // S20 , S26 , BASIC , MINI, Mini
                                                                                       // PCiE Card
    public static final ThingTypeUID THING_TYPE_2 = new ThingTypeUID(BINDING_ID, "2"); // DUALR2
    public static final ThingTypeUID THING_TYPE_3 = new ThingTypeUID(BINDING_ID, "3"); // SOCKET_3 Unknown Model
    public static final ThingTypeUID THING_TYPE_4 = new ThingTypeUID(BINDING_ID, "4"); // SOCKET_4 Unknown Model
    public static final ThingTypeUID THING_TYPE_5 = new ThingTypeUID(BINDING_ID, "5"); // POW
    public static final ThingTypeUID THING_TYPE_6 = new ThingTypeUID(BINDING_ID, "6"); // T11C , TX1C , G1
    public static final ThingTypeUID THING_TYPE_7 = new ThingTypeUID(BINDING_ID, "7"); // T12C , TX2C
    public static final ThingTypeUID THING_TYPE_8 = new ThingTypeUID(BINDING_ID, "8"); // T13C , TX3C
    public static final ThingTypeUID THING_TYPE_9 = new ThingTypeUID(BINDING_ID, "9"); // SWITCH_4 Unknown Model
    public static final ThingTypeUID THING_TYPE_14 = new ThingTypeUID(BINDING_ID, "14"); // BASIC (old)
    public static final ThingTypeUID THING_TYPE_15 = new ThingTypeUID(BINDING_ID, "15"); // TH10 , TH16, TH16R2
    public static final ThingTypeUID THING_TYPE_24 = new ThingTypeUID(BINDING_ID, "24"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_27 = new ThingTypeUID(BINDING_ID, "27"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_28 = new ThingTypeUID(BINDING_ID, "28"); // RF-BRIDGE (RF3)
    public static final ThingTypeUID THING_TYPE_29 = new ThingTypeUID(BINDING_ID, "29"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_30 = new ThingTypeUID(BINDING_ID, "30"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_31 = new ThingTypeUID(BINDING_ID, "31"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_32 = new ThingTypeUID(BINDING_ID, "32"); // POWR2
    public static final ThingTypeUID THING_TYPE_59 = new ThingTypeUID(BINDING_ID, "59"); // LED CONTROLLER
    public static final ThingTypeUID THING_TYPE_66 = new ThingTypeUID(BINDING_ID, "66"); // ZIGBEE Bridge
    public static final ThingTypeUID THING_TYPE_77 = new ThingTypeUID(BINDING_ID, "77"); // MICRO (USB)
    public static final ThingTypeUID THING_TYPE_78 = new ThingTypeUID(BINDING_ID, "78"); // unknown
    public static final ThingTypeUID THING_TYPE_81 = new ThingTypeUID(BINDING_ID, "81"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_82 = new ThingTypeUID(BINDING_ID, "82"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_83 = new ThingTypeUID(BINDING_ID, "83"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_84 = new ThingTypeUID(BINDING_ID, "84"); // GSM Socket
    public static final ThingTypeUID THING_TYPE_102 = new ThingTypeUID(BINDING_ID, "102"); // Magnetic Switch OPL-DMA,
                                                                                           // DW2
    public static final ThingTypeUID THING_TYPE_104 = new ThingTypeUID(BINDING_ID, "104"); // B05 Bulb
    public static final ThingTypeUID THING_TYPE_126 = new ThingTypeUID(BINDING_ID, "126"); // DUAL R3
    public static final ThingTypeUID THING_TYPE_107 = new ThingTypeUID(BINDING_ID, "107"); // GSM Socket

    // Zigbee Child Devices
    public static final ThingTypeUID THING_TYPE_2026 = new ThingTypeUID(BINDING_ID, "2026"); // Motion Sensor
    public static final ThingTypeUID THING_TYPE_ZCONTACT = new ThingTypeUID(BINDING_ID, "zcontact"); // Contact Sensor
    public static final ThingTypeUID THING_TYPE_ZWATER = new ThingTypeUID(BINDING_ID, "zwater"); // Water Sensor
    public static final ThingTypeUID THING_TYPE_1770 = new ThingTypeUID(BINDING_ID, "1770"); // Temp Sensor
    public static final ThingTypeUID THING_TYPE_ZSWITCH1 = new ThingTypeUID(BINDING_ID, "zswitch1"); // 1 way Switch
    public static final ThingTypeUID THING_TYPE_ZSWITCH2 = new ThingTypeUID(BINDING_ID, "zswitch2"); // 2 way Switch
    public static final ThingTypeUID THING_TYPE_ZSWITCH3 = new ThingTypeUID(BINDING_ID, "zswitch3"); // 3 way Switch
    public static final ThingTypeUID THING_TYPE_ZSWITCH4 = new ThingTypeUID(BINDING_ID, "zswitch4"); // 4 way Switch
    public static final ThingTypeUID THING_TYPE_ZLIGHT = new ThingTypeUID(BINDING_ID, "zlight"); // White Light

    // RF Child Devices
    public static final ThingTypeUID THING_TYPE_RF1 = new ThingTypeUID(BINDING_ID, "rfremote1"); // 1 Button RF Remote
    public static final ThingTypeUID THING_TYPE_RF2 = new ThingTypeUID(BINDING_ID, "rfremote2"); // 2 Button RF Remote
    public static final ThingTypeUID THING_TYPE_RF3 = new ThingTypeUID(BINDING_ID, "rfremote3"); // 3 Button RF Remote
    public static final ThingTypeUID THING_TYPE_RF4 = new ThingTypeUID(BINDING_ID, "rfremote4"); // 4 Button RF Remote
    public static final ThingTypeUID THING_TYPE_RF6 = new ThingTypeUID(BINDING_ID, "rfsensor"); // RF Sensor

    // For unknowns
    public static final ThingTypeUID THING_TYPE_UNKNOWNDEVICE = new ThingTypeUID(BINDING_ID, "device");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPE_UIDS = Collections.unmodifiableSet(Stream.of(
            THING_TYPE_ACCOUNT, THING_TYPE_1, THING_TYPE_2, THING_TYPE_3, THING_TYPE_4, THING_TYPE_5, THING_TYPE_6,
            THING_TYPE_7, THING_TYPE_8, THING_TYPE_9,

            THING_TYPE_14, THING_TYPE_15, THING_TYPE_24, THING_TYPE_27, THING_TYPE_29, THING_TYPE_30, THING_TYPE_31,

            THING_TYPE_28, THING_TYPE_RF1, THING_TYPE_RF2, THING_TYPE_RF3, THING_TYPE_RF4, THING_TYPE_RF6,

            THING_TYPE_32, THING_TYPE_59,

            THING_TYPE_66, THING_TYPE_2026, THING_TYPE_ZCONTACT, THING_TYPE_ZWATER, THING_TYPE_1770, THING_TYPE_ZLIGHT,
            THING_TYPE_ZSWITCH1, THING_TYPE_ZSWITCH2, THING_TYPE_ZSWITCH3, THING_TYPE_ZSWITCH4,

            THING_TYPE_77, THING_TYPE_78, THING_TYPE_81, THING_TYPE_82, THING_TYPE_83, THING_TYPE_84, THING_TYPE_102,
            THING_TYPE_104, THING_TYPE_107, THING_TYPE_126

    ).collect(Collectors.toSet()));

    public static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPE_UIDS = Collections.unmodifiableSet(Stream.of(
            THING_TYPE_1, THING_TYPE_2, THING_TYPE_3, THING_TYPE_4, THING_TYPE_5, THING_TYPE_6, THING_TYPE_7,
            THING_TYPE_8, THING_TYPE_9,

            THING_TYPE_14, THING_TYPE_15, THING_TYPE_24, THING_TYPE_27, THING_TYPE_29, THING_TYPE_30, THING_TYPE_31,

            THING_TYPE_28, THING_TYPE_RF1, THING_TYPE_RF2, THING_TYPE_RF3, THING_TYPE_RF4, THING_TYPE_RF6,

            THING_TYPE_32, THING_TYPE_59,

            THING_TYPE_66, THING_TYPE_2026, THING_TYPE_ZCONTACT, THING_TYPE_ZWATER, THING_TYPE_1770, THING_TYPE_ZLIGHT,
            THING_TYPE_ZSWITCH1, THING_TYPE_ZSWITCH2, THING_TYPE_ZSWITCH3, THING_TYPE_ZSWITCH4,

            THING_TYPE_77, THING_TYPE_78, THING_TYPE_81, THING_TYPE_82, THING_TYPE_83, THING_TYPE_84, THING_TYPE_102,
            THING_TYPE_104, THING_TYPE_107, THING_TYPE_126).collect(Collectors.toSet()));

    public static final Map<Integer, ThingTypeUID> createMap() { // thing type denotes number of channels
        Map<Integer, ThingTypeUID> deviceTypes = new HashMap<>();
        deviceTypes.put(1, THING_TYPE_1);
        deviceTypes.put(2, THING_TYPE_2);
        deviceTypes.put(3, THING_TYPE_3);
        deviceTypes.put(4, THING_TYPE_4);
        deviceTypes.put(5, THING_TYPE_5);
        deviceTypes.put(6, THING_TYPE_6);
        deviceTypes.put(7, THING_TYPE_7);
        deviceTypes.put(8, THING_TYPE_8);
        deviceTypes.put(9, THING_TYPE_9);
        deviceTypes.put(14, THING_TYPE_14);
        deviceTypes.put(15, THING_TYPE_15);

        deviceTypes.put(24, THING_TYPE_24);
        deviceTypes.put(27, THING_TYPE_27);
        deviceTypes.put(28, THING_TYPE_28);
        deviceTypes.put(29, THING_TYPE_29);
        deviceTypes.put(30, THING_TYPE_30);
        deviceTypes.put(31, THING_TYPE_31);

        deviceTypes.put(32, THING_TYPE_32);
        deviceTypes.put(59, THING_TYPE_59);

        deviceTypes.put(66, THING_TYPE_66);
        deviceTypes.put(77, THING_TYPE_77);
        deviceTypes.put(77, THING_TYPE_77);
        deviceTypes.put(78, THING_TYPE_78);
        deviceTypes.put(81, THING_TYPE_81);
        deviceTypes.put(82, THING_TYPE_82);
        deviceTypes.put(83, THING_TYPE_83);
        deviceTypes.put(84, THING_TYPE_84);

        deviceTypes.put(102, THING_TYPE_102);
        deviceTypes.put(104, THING_TYPE_104);
        deviceTypes.put(107, THING_TYPE_107);
        deviceTypes.put(126, THING_TYPE_126);

        return Collections.unmodifiableMap(deviceTypes);
    }

    public static final Map<Integer, ThingTypeUID> createSensorMap() { // thing type denotes number of channels
        Map<Integer, ThingTypeUID> sensorTypes = new HashMap<>();
        sensorTypes.put(4, THING_TYPE_RF1);
        sensorTypes.put(4, THING_TYPE_RF2);
        sensorTypes.put(4, THING_TYPE_RF3);
        sensorTypes.put(4, THING_TYPE_RF4);
        sensorTypes.put(6, THING_TYPE_RF6);

        return Collections.unmodifiableMap(sensorTypes);
    }

    public static final Map<Integer, ThingTypeUID> createZigbeeMap() { // thing type denotes number of channels
        Map<Integer, ThingTypeUID> zigbeeTypes = new HashMap<>();
        zigbeeTypes.put(1000, THING_TYPE_ZSWITCH1);
        zigbeeTypes.put(1009, THING_TYPE_ZSWITCH1);
        zigbeeTypes.put(1256, THING_TYPE_ZSWITCH1);
        zigbeeTypes.put(1257, THING_TYPE_ZLIGHT);
        zigbeeTypes.put(1770, THING_TYPE_1770);
        zigbeeTypes.put(2026, THING_TYPE_2026);
        zigbeeTypes.put(3026, THING_TYPE_ZCONTACT);
        zigbeeTypes.put(4026, THING_TYPE_ZWATER);
        zigbeeTypes.put(2256, THING_TYPE_ZSWITCH2);
        zigbeeTypes.put(3256, THING_TYPE_ZSWITCH3);
        zigbeeTypes.put(4256, THING_TYPE_ZSWITCH4);
        return Collections.unmodifiableMap(zigbeeTypes);
    }
}
