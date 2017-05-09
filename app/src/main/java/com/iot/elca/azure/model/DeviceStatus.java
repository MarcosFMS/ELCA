package com.iot.elca.azure.model;

/**
 * Created by Marcos on 06/05/2017.
 */

public class DeviceStatus {
    public String SourceDeviceId;
    public String Time;
    public String Command;
    public String CommandACK;
    public String TargetDeviceId;

    public DeviceStatus(String deviceId, String time, String command, String commandACK, String targetDeviceId) {
        SourceDeviceId = deviceId;
        Time = time;
        Command = command;
        CommandACK = commandACK;
        TargetDeviceId = targetDeviceId;
    }

}
