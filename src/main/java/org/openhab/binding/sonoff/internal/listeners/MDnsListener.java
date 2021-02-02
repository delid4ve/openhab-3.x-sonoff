
package org.openhab.binding.sonoff.internal.listeners;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.openhab.binding.sonoff.internal.handler.AccountHandler;

/**
 * Created by ldoguin on 13/02/15.
 */
public class MDnsListener implements ServiceListener {

    private AccountHandler account;

    public MDnsListener(AccountHandler account) {
        this.account = account;
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        account.resolved(event.getInfo());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        account.resolved(event.getInfo());
    }
}
