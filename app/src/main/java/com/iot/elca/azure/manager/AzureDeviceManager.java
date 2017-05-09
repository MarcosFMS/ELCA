package com.iot.elca.azure.manager;
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.iot.elca.azure.model.DeviceStatus;
import com.microsoft.azure.sdk.iot.device.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Sends a number of event messages to an IoT Hub.
 */
public class AzureDeviceManager {

    private static final int D2C_MESSAGE_TIMEOUT = 2000; // 2 seconds
    private static List failedMessageListOnClose = new ArrayList(); // List of messages that failed on close
    private static String deviceId = "androidapp";
    private static String connectionString = "HostName=SendersIotHub.azure-devices.net;DeviceId=androidapp;SharedAccessKey=dKistdt6WAe8XWGkaUCJJv+FZhkqwZrbJc/26XgLeK4=";
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;


    protected static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            Message msg = (Message) context;

            Log.d("IoT Response","IoT Hub responded to message " + msg.getMessageId() + " with status " + status.name());

            if (status == IotHubStatusCode.MESSAGE_EXPIRED) {
                failedMessageListOnClose.add(msg.getMessageId());
            }
        }
    }

    public static void sendEvent(String targetDeviceId, String command) {
        try {
            DeviceClient client = new DeviceClient(connectionString, protocol);
            client.open();
            DeviceStatus status = new DeviceStatus(deviceId, Calendar.getInstance().getTime().toString(), command, "0", targetDeviceId);
            String messageString = new Gson().toJson(status);
            Log.d("json", messageString);
            Message message = new Message(messageString.getBytes(StandardCharsets.US_ASCII));
            client.sendEventAsync(message, new EventCallback(), message);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}