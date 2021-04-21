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
package org.openhab.binding.sonoff.internal.communication;

import java.security.SecureRandom;
import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link DtoHelper} defines standard constants and functions used when communicating with devices
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffCommandMessageUtilities {

    public static final String APPID = "oeVkj2lYFGnJu5XUtWisfW4utiN4u9Mq";
    public static final String APPSECRET = "6Nz4n0xA8s8qdxQf2GqurZj2Fs55FUvM";
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final Integer VERSION = 8;
    private static SecureRandom rnd = new SecureRandom();

    static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String getNonce() {
        return randomString(8);
    }

    public static synchronized Long getSequence() {
        return new Date().getTime();
    }

    public static synchronized Long getTs() {
        return new Date().getTime();
    }
}
