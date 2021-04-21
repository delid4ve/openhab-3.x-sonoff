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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sonoff.internal.handler.SonoffDeviceState;
import org.openhab.core.OpenHAB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The {@link SonoffCacheProvider} provides file operations for the device cache
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public class SonoffCacheProvider {
    private static final Logger logger = LoggerFactory.getLogger(SonoffCacheProvider.class);
    private final String saveFolderName;
    private final @Nullable Gson gson;

    public SonoffCacheProvider(Gson gson) {
        this.saveFolderName = OpenHAB.getUserDataFolder() + "/" + SonoffBindingConstants.BINDING_ID;
        final File saveFolder = new File(saveFolderName);
        this.gson = gson;

        // Create path for serialization.
        if (!saveFolder.exists()) {
            logger.debug("Creating directory {}", saveFolderName);
            saveFolder.mkdirs();
        }
    }

    public SonoffCacheProvider() {
        this.saveFolderName = OpenHAB.getUserDataFolder() + "/" + SonoffBindingConstants.BINDING_ID;
        final File saveFolder = new File(saveFolderName);
        this.gson = null;

        // Create path for serialization.
        if (!saveFolder.exists()) {
            logger.debug("Creating directory {}", saveFolderName);
            saveFolder.mkdirs();
        }
    }

    public void newFile(String deviceid, String thing) {
        File file = new File(this.saveFolderName, deviceid + ".txt");
        BufferedWriter writer = null;
        logger.debug("Device {}: writing to file {}", deviceid, file.getPath());
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.write(thing);
            writer.flush();
        } catch (IOException e) {
            logger.error("Device {}: Error writing to file: {}", deviceid, e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public List<String> getFiles() {
        String[] filenameList;
        List<String> fileList = new ArrayList<String>();
        File file = new File(this.saveFolderName);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(@Nullable File f, @Nullable String name) {
                return name.endsWith(".txt");
            }
        };
        filenameList = file.list(filter);
        for (int i = 0; i < filenameList.length; i++) {
            String content = getFile(filenameList[i]);
            if (!content.equals("")) {
                fileList.add(content);
            }
        }
        return fileList;
    }

    public Boolean checkFile(String deviceid) {
        File file = new File(this.saveFolderName, deviceid + ".txt");
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }

    public String getFile(String filename) {
        File file = new File(this.saveFolderName, filename);
        BufferedReader reader = null;

        if (!file.exists()) {
            logger.debug("Device {}: Error getting file content: file does not exist.", filename);
            return "";
        }

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String response = new String();
            for (String line; (line = reader.readLine()) != null; response += line)
                ;
            return response;
        } catch (IOException e) {
            logger.debug("Device File {}: Error serializing from file: {}", filename, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }

    public Map<String, SonoffDeviceState> getStates() {
        Map<String, SonoffDeviceState> deviceStates = new HashMap<String, SonoffDeviceState>();
        List<String> deviceList = getFiles();
        for (int i = 0; i < deviceList.size(); i++) {
            JsonObject device = gson.fromJson(deviceList.get(i), JsonObject.class);
            if (device != null) {
                SonoffDeviceState state = new SonoffDeviceState(device);
                deviceStates.put(state.getDeviceid(), state);
                logger.debug("Added new state for device {}", state.getDeviceid());
            }
        }
        return deviceStates;
    }

    public @Nullable SonoffDeviceState getState(String deviceid) {
        String deviceJson = getFile(deviceid + ".txt");
        JsonObject device = gson.fromJson(deviceJson, JsonObject.class);
        if (device != null) {
            return new SonoffDeviceState(device);
        } else {
            return null;
        }
    }
}
