package org.openhab.binding.sonoff.internal;

import static org.openhab.binding.sonoff.internal.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerService;
import org.openhab.binding.sonoff.internal.dto.api.Device;
import org.openhab.binding.sonoff.internal.dto.api.Devices;
import org.openhab.binding.sonoff.internal.handler.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link lightwaverfBindingConstants} class defines common constants, which
 * are used across the whole binding.
 *
 * @author David Murton - Initial contribution
 */

@Component(service = sonoffDiscoveryService.class, immediate = true, configurationPid = "discovery.sonoff")

public class sonoffDiscoveryService extends AbstractDiscoveryService implements ThingHandlerService, DiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(sonoffDiscoveryService.class);
    private static final int DISCOVER_TIMEOUT_SECONDS = 10;
    private AccountHandler account;
    private ScheduledFuture<?> scanTask;
    private ThingTypeUID thingTypeUid;

    public sonoffDiscoveryService() {
        super(Constants.DISCOVERABLE_THING_TYPE_UIDS, DISCOVER_TIMEOUT_SECONDS, true);
    }

    @Override
    protected void activate(Map<String, Object> configProperties) {
        logger.debug("Activate Background Discovery");
        super.activate(configProperties);
    }

    @Override
    public void deactivate() {
        logger.debug("Deactivate Background discovery");
        super.deactivate();
    }

    @Override
    @Modified
    protected void modified(Map<String, Object> configProperties) {
        super.modified(configProperties);
    }

    @Override
    public void startBackgroundDiscovery() {
        logger.debug("Start Background Discovery");
        try {
            discover();
        } catch (Exception e) {
        }
    }

    @Override
    protected void startScan() {
        // logger.debug("Start Scan");
        if (this.scanTask != null) {
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
        // logger.debug("Stop Scan");
        super.stopScan();

        if (this.scanTask != null) {
            this.scanTask.cancel(true);
            this.scanTask = null;
        }
    }

    private void discover() {
        logger.debug("Sonoff - Start Discovery");
        ThingUID bridgeUID = account.getThing().getUID();
        try {
            Devices devices = account.getApi().discover();
            for (int i = 0; i < devices.getDevicelist().size(); i++) {
                thingTypeUid = ((Constants.createMap().get(devices.getDevicelist().get(i).getUiid())) != null)
                        ? Constants.createMap().get(devices.getDevicelist().get(i).getUiid())
                        : THING_TYPE_UNKNOWNDEVICE;
                ThingUID deviceThing = new ThingUID(thingTypeUid, account.getThing().getUID(),
                        devices.getDevicelist().get(i).getDeviceid());
                Map<String, Object> dProperties = new HashMap<>();
                dProperties.put("deviceId", devices.getDevicelist().get(i).getDeviceid());
                dProperties.put("Name", devices.getDevicelist().get(i).getName());
                dProperties.put("Type", devices.getDevicelist().get(i).getProductModel());
                dProperties.put("API Key", devices.getDevicelist().get(i).getApikey());
                dProperties.put("deviceKey", devices.getDevicelist().get(i).getDevicekey());
                dProperties.put("IP Address", devices.getDevicelist().get(i).getIp());
                dProperties.put("Brand", devices.getDevicelist().get(i).getBrandName());
                String label = createLabelDevice(devices.getDevicelist().get(i));
                thingDiscovered(DiscoveryResultBuilder.create(deviceThing).withLabel(label).withProperties(dProperties)
                        .withRepresentationProperty(devices.getDevicelist().get(i).getDeviceid().toString())
                        .withBridge(bridgeUID).build());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        if (handler instanceof AccountHandler) {
            account = (AccountHandler) handler;
        }
    }

    public String createLabelDevice(Device device) {
        StringBuilder sb = new StringBuilder();
        sb.append(device.getName());
        return sb.toString();
    }

    @Override
    public ThingHandler getThingHandler() {
        return account;
    }
}
