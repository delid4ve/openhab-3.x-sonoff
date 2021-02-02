package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device implements Serializable {

    @SerializedName("settings")
    @Expose
    private Settings settings;
    @SerializedName("family")
    @Expose
    private Family family;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("online")
    @Expose
    private Boolean online;
    @SerializedName("shareUsersInfo")
    @Expose
    private List<Object> shareUsersInfo = new ArrayList<Object>();
    @SerializedName("groups")
    @Expose
    private List<Object> groups = new ArrayList<Object>();
    @SerializedName("devGroups")
    @Expose
    private List<Object> devGroups = new ArrayList<Object>();
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("deviceid")
    @Expose
    private String deviceid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("apikey")
    @Expose
    private String apikey;
    @SerializedName("extra")
    @Expose
    private Extra extra;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("onlineTime")
    @Expose
    private String onlineTime;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("params")
    @Expose
    private Params params;
    @SerializedName("offlineTime")
    @Expose
    private String offlineTime;
    @SerializedName("tags")
    @Expose
    private Tags tags;
    @SerializedName("sharedTo")
    @Expose
    private List<Object> sharedTo = new ArrayList<Object>();
    @SerializedName("devicekey")
    @Expose
    private String devicekey;
    @SerializedName("deviceUrl")
    @Expose
    private String deviceUrl;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("showBrand")
    @Expose
    private Boolean showBrand;
    @SerializedName("brandLogoUrl")
    @Expose
    private String brandLogoUrl;
    @SerializedName("productModel")
    @Expose
    private String productModel;
    @SerializedName("devConfig")
    @Expose
    private DevConfig devConfig;
    @SerializedName("uiid")
    @Expose
    private Integer uiid;
    @SerializedName("localAddress")
    @Expose
    private String localAddress;
    @SerializedName("sequence")
    @Expose
    private String sequence;

    public String getSequence() {
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    private final static long serialVersionUID = 2958983062170485126L;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public List<Object> getShareUsersInfo() {
        return shareUsersInfo;
    }

    public void setShareUsersInfo(List<Object> shareUsersInfo) {
        this.shareUsersInfo = shareUsersInfo;
    }

    public List<Object> getGroups() {
        return groups;
    }

    public void setGroups(List<Object> groups) {
        this.groups = groups;
    }

    public List<Object> getDevGroups() {
        return devGroups;
    }

    public void setDevGroups(List<Object> devGroups) {
        this.devGroups = devGroups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
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

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
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

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public List<Object> getSharedTo() {
        return sharedTo;
    }

    public void setSharedTo(List<Object> sharedTo) {
        this.sharedTo = sharedTo;
    }

    public String getDevicekey() {
        return devicekey;
    }

    public void setDevicekey(String devicekey) {
        this.devicekey = devicekey;
    }

    public String getDeviceUrl() {
        return deviceUrl;
    }

    public void setDeviceUrl(String deviceUrl) {
        this.deviceUrl = deviceUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Boolean getShowBrand() {
        return showBrand;
    }

    public void setShowBrand(Boolean showBrand) {
        this.showBrand = showBrand;
    }

    public String getBrandLogoUrl() {
        return brandLogoUrl;
    }

    public void setBrandLogoUrl(String brandLogoUrl) {
        this.brandLogoUrl = brandLogoUrl;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public DevConfig getDevConfig() {
        return devConfig;
    }

    public void setDevConfig(DevConfig devConfig) {
        this.devConfig = devConfig;
    }

    public Integer getUiid() {
        return uiid;
    }

    public void setUiid(Integer uiid) {
        this.uiid = uiid;
    }

    public String getLocalAddress() {
        return this.localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }
}
