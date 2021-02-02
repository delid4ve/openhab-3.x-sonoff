
package org.openhab.binding.sonoff.internal.dto.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params implements Serializable {

    @SerializedName("init")
    @Expose
    private Integer init;
    @SerializedName("staMac")
    @Expose
    private String staMac;
    @SerializedName("rssi")
    @Expose
    private Integer rssi;
    @SerializedName("alarmCValue")
    @Expose
    private List<Integer> alarmCValue = new ArrayList<Integer>();
    @SerializedName("alarmVValue")
    @Expose
    private List<Integer> alarmVValue = new ArrayList<Integer>();
    @SerializedName("switch")
    @Expose
    private String _switch;
    @SerializedName("switches")
    @Expose
    private List<Switch> switches = new ArrayList<Switch>();
    @SerializedName("voltage")
    @Expose
    private String voltage;
    @SerializedName("oneKwh")
    @Expose
    private String oneKwh;
    @SerializedName("current")
    @Expose
    private String current;
    @SerializedName("alarmType")
    @Expose
    private String alarmType;
    @SerializedName("fwVersion")
    @Expose
    private String fwVersion;
    @SerializedName("power")
    @Expose
    private String power;
    @SerializedName("alarmPValue")
    @Expose
    private List<Integer> alarmPValue = new ArrayList<Integer>();
    @SerializedName("uiActive")
    @Expose
    private Integer uiActive;
    @SerializedName("timeZone")
    @Expose
    private Double timeZone;
    @SerializedName("hundredDaysKwh")
    @Expose
    private String hundredDaysKwh;
    @SerializedName("sledOnline")
    @Expose
    private String sledOnline;
    @SerializedName("startup")
    @Expose
    private String startup;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("pulse")
    @Expose
    private String pulse;
    @SerializedName("pulseWidth")
    @Expose
    private Integer pulseWidth;
    @SerializedName("timers")
    @Expose
    private List<Timer> timers = new ArrayList<Timer>();
    @SerializedName("only_device")
    @Expose
    private OnlyDevice onlyDevice;
    @SerializedName("ssid")
    @Expose
    private String ssid;
    @SerializedName("bssid")
    @Expose
    private String bssid;
    @SerializedName("configure")
    @Expose
    private List<Configure> configure = new ArrayList<Configure>();
    @SerializedName("sharedTo")
    @Expose
    private List<SharedTo> sharedTo = new ArrayList<SharedTo>();
    @SerializedName("pulses")
    @Expose
    private List<Pulse> pulses = new ArrayList<Pulse>();
    @SerializedName("senMode")
    @Expose
    private SenMode senMode;
    @SerializedName("savMode")
    @Expose
    private SavMode savMode;
    @SerializedName("alertMode")
    @Expose
    private AlertMode alertMode;

    @SerializedName("currentHumidity")
    @Expose
    private String currentHumidity;
    @SerializedName("currentTemperature")
    @Expose
    private String currentTemperature;
    @SerializedName("sensorType")
    @Expose
    private String sensorType;
    @SerializedName("deviceType")
    @Expose
    private String deviceType;
    @SerializedName("mainSwitch")
    @Expose
    private String mainSwitch;
    @SerializedName("bindInfos")
    @Expose
    private BindInfos bindInfos;

    private final static long serialVersionUID = 1205249120703729170L;

    public Integer getInit() {
        return init;
    }

    public void setInit(Integer init) {
        this.init = init;
    }

    public String getStaMac() {
        return staMac;
    }

    public void setStaMac(String staMac) {
        this.staMac = staMac;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public List<Integer> getAlarmCValue() {
        return alarmCValue;
    }

    public void setAlarmCValue(List<Integer> alarmCValue) {
        this.alarmCValue = alarmCValue;
    }

    public List<Integer> getAlarmVValue() {
        return alarmVValue;
    }

    public void setAlarmVValue(List<Integer> alarmVValue) {
        this.alarmVValue = alarmVValue;
    }

    public String getSwitch() {
        return _switch;
    }

    public void setSwitch(String _switch) {
        this._switch = _switch;
    }

    public List<Switch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getOneKwh() {
        return oneKwh;
    }

    public void setOneKwh(String oneKwh) {
        this.oneKwh = oneKwh;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public void setFwVersion(String fwVersion) {
        this.fwVersion = fwVersion;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public List<Integer> getAlarmPValue() {
        return alarmPValue;
    }

    public void setAlarmPValue(List<Integer> alarmPValue) {
        this.alarmPValue = alarmPValue;
    }

    public Integer getUiActive() {
        return uiActive;
    }

    public void setUiActive(Integer uiActive) {
        this.uiActive = uiActive;
    }

    public Double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Double timeZone) {
        this.timeZone = timeZone;
    }

    public String getHundredDaysKwh() {
        return hundredDaysKwh;
    }

    public void setHundredDaysKwh(String hundredDaysKwh) {
        this.hundredDaysKwh = hundredDaysKwh;
    }

    public String getSledOnline() {
        return sledOnline;
    }

    public void setSledOnline(String sledOnline) {
        this.sledOnline = sledOnline;
    }

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public Integer getPulseWidth() {
        return pulseWidth;
    }

    public void setPulseWidth(Integer pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public List<Timer> getTimers() {
        return timers;
    }

    public void setTimers(List<Timer> timers) {
        this.timers = timers;
    }

    public OnlyDevice getOnlyDevice() {
        return onlyDevice;
    }

    public void setOnlyDevice(OnlyDevice onlyDevice) {
        this.onlyDevice = onlyDevice;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public List<Configure> getConfigure() {
        return configure;
    }

    public void setConfigure(List<Configure> configure) {
        this.configure = configure;
    }

    public List<Pulse> getPulses() {
        return pulses;
    }

    public void setPulses(List<Pulse> pulses) {
        this.pulses = pulses;
    }

    public SenMode getSenMode() {
        return senMode;
    }

    public void setSenMode(SenMode senMode) {
        this.senMode = senMode;
    }

    public SavMode getSavMode() {
        return savMode;
    }

    public void setSavMode(SavMode savMode) {
        this.savMode = savMode;
    }

    public AlertMode getAlertMode() {
        return alertMode;
    }

    public void setAlertMode(AlertMode alertMode) {
        this.alertMode = alertMode;
    }

    public String getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(String currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMainSwitch() {
        return mainSwitch;
    }

    public void setMainSwitch(String mainSwitch) {
        this.mainSwitch = mainSwitch;
    }

    public BindInfos getBindInfos() {
        return bindInfos;
    }

    public void setBindInfos(BindInfos bindInfos) {
        this.bindInfos = bindInfos;
    }
}
