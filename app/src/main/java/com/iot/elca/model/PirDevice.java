package com.iot.elca.model;

/**
 * Created by Marcos on 10/05/2017.
 */

public class PirDevice extends Device{
    public PirDevice(String id,  String state, String ip, String ssid, String password) {
        super(id, state, ip, ssid, password);
    }

    public PirDevice(Device device) {
        super(device.getId(), device.getState(), device.getIp(), device.getSsid(), device.getPassword());
    }

    public PirDevice() {
        super();
    }

}
