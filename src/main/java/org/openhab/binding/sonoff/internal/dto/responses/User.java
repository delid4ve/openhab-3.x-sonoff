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

package org.openhab.binding.sonoff.internal.dto.responses;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author David Murton - Initial contribution
 */
public class User implements Serializable {

    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("apikey")
    @Expose
    private String apikey;
    @SerializedName("accountLevel")
    @Expose
    private Long accountLevel;
    @SerializedName("accountConsult")
    @Expose
    private Boolean accountConsult;
    @SerializedName("denyRecharge")
    @Expose
    private Boolean denyRecharge;
    @SerializedName("ipCountry")
    @Expose
    private String ipCountry;
    private static final long serialVersionUID = 1537085058552260707L;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(Long accountLevel) {
        this.accountLevel = accountLevel;
    }

    public Boolean getAccountConsult() {
        return accountConsult;
    }

    public void setAccountConsult(Boolean accountConsult) {
        this.accountConsult = accountConsult;
    }

    public Boolean getDenyRecharge() {
        return denyRecharge;
    }

    public void setDenyRecharge(Boolean denyRecharge) {
        this.denyRecharge = denyRecharge;
    }

    public String getIpCountry() {
        return ipCountry;
    }

    public void setIpCountry(String ipCountry) {
        this.ipCountry = ipCountry;
    }
}
