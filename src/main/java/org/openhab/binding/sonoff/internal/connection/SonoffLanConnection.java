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
package org.openhab.binding.sonoff.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SonoffLanConnection} class is the Http/mDNS Connection to local ewelink enabled
 * devices and uses the shared httpClient
 *
 * @author David Murton - Initial contribution
 */

@NonNullByDefault
public class SonoffLanConnection {
    private final Logger logger = LoggerFactory.getLogger(SonoffLanConnection.class);
    private final HttpClient httpClient;
    private final SonoffLanConnectionListener listener;
    private static final String SERVICE = "_ewelink._tcp.local.";

    private final Map<InetAddress, JmDNS> instances = new ConcurrentHashMap<>();

    public SonoffLanConnection(SonoffLanConnectionListener listener, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.listener = listener;
    }

    public void start() {
        try {
            for (InetAddress address : getAddresses()) {
                instances.putIfAbsent(address, JmDNS.create(address, "JmDNS-" + address.toString()));
                logger.debug("mDNS service has been started on IP {}", address.getHostAddress());
                JmDNS jmDns = instances.get(address);
                if (jmDns != null) {
                    jmDns.addServiceListener(SERVICE, this.listener);
                }
            }
            listener.lanConnected(true);
        } catch (IOException e) {
            listener.lanConnected(false);
        }
    }

    public Boolean stop() {
        logger.debug("Sonoff - Stopping LAN connection");
        for (Map.Entry<InetAddress, JmDNS> entry : instances.entrySet()) {
            entry.getValue().unregisterAllServices();
            closeQuietly(entry.getValue());
            logger.debug("mDNS service has been stopped ({})", entry.getValue().getName());
            instances.remove(entry.getKey());
        }
        return true;
        // listener.lanConnected(false);
    }

    private void closeQuietly(JmDNS jmdns) {
        try {
            jmdns.close();
        } catch (IOException e) {
        }
    }

    public void addSubService(String deviceid) {
        String service = "eWeLink_" + deviceid + "._ewelink._tcp.local.";
        for (JmDNS jmdns : instances.values()) {
            jmdns.addServiceListener(service, this.listener);
        }
    }

    public void removeSubService(String deviceid) {
        String service = "eWeLink_" + deviceid + "._ewelink._tcp.local.";
        for (JmDNS jmdns : instances.values()) {
            jmdns.removeServiceListener(service, this.listener);
        }
    }

    private Set<InetAddress> getAddresses() throws SocketException {
        final Set<InetAddress> addresses = new HashSet<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                int npf = interfaceAddress.getNetworkPrefixLength();
                InetAddress address = interfaceAddress.getAddress();
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null && npf != 8) {
                } else {
                    addresses.add(address);
                }
            }
        }
        return addresses;
    }

    public void sendMessage(String url, String payload) {
        logger.debug("Sending LAN Update to {}", url);
        try {
            httpClient.newRequest(url).method("POST").header("accept", "application/json")
                    .header("Content-Type", "application/json; utf-8")
                    .content(new StringContentProvider(payload), "application/json").timeout(10, TimeUnit.SECONDS)
                    .send(new Response.Listener.Adapter() {
                        @Override
                        public void onContent(@Nullable Response response, @Nullable ByteBuffer buffer) {
                            if (buffer != null) {
                                String content = StandardCharsets.UTF_8.decode(buffer).toString();
                                logger.debug("Lan response received: {}", content);
                                listener.lanResponse(content);
                            }
                        }
                    });
        } catch (Exception e) {
            // listener.lanConnected(false);
            logger.warn("Failed to send LAN update:{}", e.getMessage());
        }
    }
}
