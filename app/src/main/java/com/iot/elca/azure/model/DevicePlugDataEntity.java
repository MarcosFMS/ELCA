package com.iot.elca.azure.model;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by Marcos on 29/04/2017.
 */

public class DevicePlugDataEntity extends TableServiceEntity {

    public String IsOn;
    public String DeviceId;
    public String Message;

    // Note: An entity's partition and row key uniquely identify the entity in the table.
    // Entities with the same partition key can be queried faster than those with different partition keys.
    public DevicePlugDataEntity(String deviceId, String measureDate) {
        this.partitionKey = deviceId;
        this.rowKey = measureDate;
    }

    public DevicePlugDataEntity() { }

    public String getIsOn() {
        return IsOn;
    }

    public void setIsOn(String isOn) {
        IsOn = isOn;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}
