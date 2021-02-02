package org.openhab.binding.sonoff.internal.dto.payloads;

public class ApiLoginRequest {

    private String appid;
    private String email;
    private String phoneNumber;
    private String password;
    private String ts;
    private String version;
    private String nonce;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "LoginRequest{" + "appid='" + appid + '\'' + ", email='" + email + '\'' + ", phoneNumber='" + phoneNumber
                + '\'' + ", password='" + password + '\'' + ", ts='" + ts + '\'' + ", version='" + version + '\''
                + ", nonce='" + nonce + '\'' + '}';
    }
}
