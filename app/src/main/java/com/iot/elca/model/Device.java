package com.iot.elca.model;

/**
 * Created by Marcos on 10/05/2017.
 */

public class Device {

    public enum DeviceType {PLUG, PIR};

    private String id;
    private String id_device;
    private String state;
    private String ip;
    private String ssid;
    private String password;

    public Device(String id, String state, String ip, String ssid, String password) {
        this.id_device = id;
        this.state = state;
        this.ip = ip;
        this.ssid = ssid;
        this.password = password;
    }

    public Device() {
    }

    public String getId() {
        return id_device;
    }

    public void setId(String id) {
        this.id_device = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
