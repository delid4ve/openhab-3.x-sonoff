package org.openhab.binding.sonoff.internal.connections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lan {
    private final Logger logger = LoggerFactory.getLogger(Lan.class);
    private final Map<String, ServiceListener> listeners = new HashMap<>();
    private JmDNS mdns;
    private final String ipaddress;

    public Lan(String ipaddress) throws UnknownHostException, IOException {
        this.ipaddress = ipaddress;
    }

    public void addListener(String thingUID, ServiceListener listener) {
        listeners.put(thingUID, listener);
        mdns.addServiceListener("_ewelink._tcp.local.", listener);
    }

    public void start() throws UnknownHostException, IOException {
        mdns = JmDNS.create(InetAddress.getByName(ipaddress));
    }

    public void removeListener(String thingUID, ServiceListener listener) {
        listeners.remove(thingUID);
        mdns.removeServiceListener("_ewelink._tcp.local.", listener);
    }

    public void stop() throws Exception {
        logger.debug("Sonoff - Stopping LAN connection");
        if (mdns != null) {
            mdns.close();
        }
    }
}
