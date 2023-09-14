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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.JsonObject;

/**
 * The {@link SonoffCommandMessageEncryptionUtilities} contains uitilities that are used accross the binding
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffCommandMessageEncryptionUtilities {

    private static final String HMAC = "HmacSHA256";
    private static final String ENCRYPTION = "AES/CBC/PKCS5Padding";
    private static final String KEYALG = "AES";
    private static final String DIGESTALG = "MD5";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public String getAuthMac(String appSecret, String data)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = null;
        byte[] byteKey = appSecret.getBytes(CHARSET);
        mac = Mac.getInstance(HMAC);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC);
        mac.init(keySpec);
        byte[] macData = mac.doFinal(data.getBytes(CHARSET));
        return Base64.getEncoder().encodeToString(macData);
    }

    public String encrypt(String params, String deviceKey, String deviceId, Long sequence) {
        try {
            byte[] keyBytes = deviceKey.getBytes(CHARSET);
            byte[] byteToEncrypt = params.getBytes(CHARSET);
            MessageDigest digest = MessageDigest.getInstance(DIGESTALG);
            digest.update(keyBytes);
            byte[] key = digest.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, KEYALG);
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            AlgorithmParameters p = cipher.getParameters();
            byte[] iv = p.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(byteToEncrypt);
            String ivEncoded = new String(Base64.getEncoder().encode(iv), CHARSET);
            String payloadEncoded = new String(Base64.getEncoder().encode(ciphertext), CHARSET);
            JsonObject newPayload = new JsonObject();
            newPayload.addProperty("sequence", sequence + "");
            newPayload.addProperty("deviceid", deviceId);
            newPayload.addProperty("selfApikey", "123");
            newPayload.addProperty("iv", ivEncoded);
            newPayload.addProperty("encrypt", true);
            newPayload.addProperty("data", payloadEncoded);

            return newPayload.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String decrypt(JsonObject payload, String deviceKey) {
        try {
            MessageDigest md = MessageDigest.getInstance(DIGESTALG);
            byte[] bytesOfMessage = deviceKey.getBytes(CHARSET);
            byte[] keyBytes = md.digest(bytesOfMessage);
            SecretKeySpec key = new SecretKeySpec(keyBytes, KEYALG);
            String data1 = payload.get("data1") != null ? payload.get("data1").getAsString() : "";
            String data2 = payload.get("data2") != null ? payload.get("data2").getAsString() : "";
            String data3 = payload.get("data3") != null ? payload.get("data3").getAsString() : "";
            String data4 = payload.get("data4") != null ? payload.get("data4").getAsString() : "";
            String encoded = data1 + data2 + data3 + data4;
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            byte[] ciphertext = Base64.getDecoder().decode(encoded);
            String ivString = payload.get("iv").getAsString();
            byte[] ivBytes = Base64.getDecoder().decode(ivString);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decodedBytes = cipher.doFinal(ciphertext);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
            return decoded;
        } catch (Exception e) {
            return "";
        }
    }
}
