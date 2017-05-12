package com.iot.elca.azure.model;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by Marcos on 29/04/2017.
 */

public class DeviceDataEntity extends TableServiceEntity {

    private String state;
    public static String ON = "on", OFF = "off";

    // Note: An entity's partition and row key uniquely identify the entity in the table.
    // Entities with the same partition key can be queried faster than those with different partition keys.
    public DeviceDataEntity(String deviceId, String measureDate) {
        this.partitionKey = deviceId;
        this.rowKey = measureDate;
    }

    public DeviceDataEntity() { }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
