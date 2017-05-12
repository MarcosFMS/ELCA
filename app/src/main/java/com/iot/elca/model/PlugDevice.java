package com.iot.elca.model;

/**
 * Created by Marcos on 21/03/2017.
 */

public class PlugDevice extends Device{
    public PlugDevice(String id, String state, String ip, String ssid, String password) {
        super( id, state, ip, ssid, password);
    }

    public PlugDevice(Device device) {
        super(device.getId(), device.getState(), device.getIp(), device.getSsid(), device.getPassword());
    }
    public PlugDevice() {
        super();
    }
}
