package com.iot.elca.model;

/**
 * Created by Marcos on 21/03/2017.
 */

public class PlugDevice {

    private String id;
    private String id_device;
    private String state;
    private String ip;
    private String ssid;
    private String password;

    public PlugDevice(String id, String state) {
        this.id_device = id;
        this.state = state;
    }

    public PlugDevice(String id, String state, String ssid, String password, String ip) {
        this.id_device = id;
        this.state = state;
        this.ssid = ssid;
        this.password = password;
        this.ip = ip;
    }

    public PlugDevice() {
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

    public String getIp() {
        return ip;
    }
}
