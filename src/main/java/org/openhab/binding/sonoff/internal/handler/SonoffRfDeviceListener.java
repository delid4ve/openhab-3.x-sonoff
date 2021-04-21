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
import org.openhab.core.library.types.DateTimeType;

/**
 * The {@link SonoffRfDeviceListener} passes RF messages from the bridge to the correct device
 *
 * @author David Murton - Initial contribution
 */
@NonNullByDefault
public interface SonoffRfDeviceListener {

    void rfTriggered(Integer chl, DateTimeType date);

    void rfCode(Integer chl, String rfVal);
}
