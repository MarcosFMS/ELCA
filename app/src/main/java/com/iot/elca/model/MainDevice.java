package com.iot.elca.model;

/**
 * Created by Marcos on 21/03/2017.
 */

public class MainDevice {

    private String id;
    private String id_device;
    private boolean on;
    private String ip;
    private String ssid;
    private String password;

    public MainDevice(String id, boolean on) {
        this.id_device = id;
        this.on = on;
    }

    public MainDevice(String id, boolean on, String ssid, String password, String ip) {
        this.id_device = id;
        this.on = on;
        this.ssid = ssid;
        this.password = password;
        this.ip = ip;
    }

    public MainDevice() {
    }

    public String getId() {
        return id_device;
    }

    public void setId(String id) {
        this.id_device = id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
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
