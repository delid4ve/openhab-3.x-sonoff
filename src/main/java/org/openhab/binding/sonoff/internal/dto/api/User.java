
package org.openhab.binding.sonoff.internal.dto.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private ClientInfo clientInfo;
    private String id;
    private String email;
    private String password;
    private String appId;
    private String apikey;
    private String createdAt;
    private Integer v;
    private String lang;
    private Boolean online;
    private String onlineTime;
    private List<AppInfo> appInfos = null;
    private String ip;
    private String location;
    private String offlineTime;
    private String userStatus;
    private BindInfos bindInfos;
    private String countryCode;
    private String currentFamilyId;
    private String language;
    private Extra extra;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public List<AppInfo> getAppInfos() {
        return appInfos;
    }

    public void setAppInfos(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public BindInfos getBindInfos() {
        return bindInfos;
    }

    public void setBindInfos(BindInfos bindInfos) {
        this.bindInfos = bindInfos;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrentFamilyId() {
        return currentFamilyId;
    }

    public void setCurrentFamilyId(String currentFamilyId) {
        this.currentFamilyId = currentFamilyId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "User{" + "clientInfo=" + clientInfo + ", id='" + id + '\'' + ", email='" + email + '\'' + ", password='"
                + password + '\'' + ", appId='" + appId + '\'' + ", apikey='" + apikey + '\'' + ", createdAt='"
                + createdAt + '\'' + ", v=" + v + ", lang='" + lang + '\'' + ", online=" + online + ", onlineTime='"
                + onlineTime + '\'' + ", appInfos=" + appInfos + ", ip='" + ip + '\'' + ", location='" + location + '\''
                + ", offlineTime='" + offlineTime + '\'' + ", userStatus='" + userStatus + '\'' + ", bindInfos="
                + bindInfos + ", countryCode='" + countryCode + '\'' + ", currentFamilyId='" + currentFamilyId + '\''
                + ", language='" + language + '\'' + ", extra=" + extra + ", additionalProperties="
                + additionalProperties + '}';
    }
}
