package com.iot.elca.azure.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.iot.elca.azure.model.DeviceDataEntity;
import com.iot.elca.azure.util.Utility;
import com.iot.elca.model.Device;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Iterator;

/**
 * Created by Marcos on 29/04/2017.
 */

public class AzureStorageManager{

    private static AzureStorageManager storageManager;


    private final String PARTITION_KEY = "PartitionKey";
    private final String ROW_KEY = "RowKey";
    private final String TIMESTAMP = "Timestamp";

    public static AzureStorageManager getInstance(){
        if(storageManager == null){
            storageManager = new AzureStorageManager();
        }
        return storageManager;
    }

    /**
     * Executes the sample.
     *
     *            No input args are expected from users.
     * @throws URISyntaxException
     * @throws InvalidKeyException
     */

    public Iterable<DeviceDataEntity> getDevicePlugData(String deviceId) throws StorageException, URISyntaxException, InvalidKeyException {
            // Define constants for filters.

            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Utility.storageConnectionString);

            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            // Create a cloud table object for the table.
            CloudTable cloudTable = tableClient.getTableReference("devicePlugData");

            // Create a filter condition where the partition key is "Smith".

    	    String partitionFilter = TableQuery.generateFilterCondition(
    	            "PartitionKey",
    	        TableQuery.QueryComparisons.EQUAL,
    	        deviceId);

      	   /*
    	    // Specify a partition query, using "Smith" as the partition key filter.
    	    TableQuery<CustomerEntity> partitionQuery =
    	        TableQuery.from(CustomerEntity.class)
    	        .where(partitionFilter);

    	    	*/

            TableQuery<DeviceDataEntity> partitionQuery =
                    TableQuery.from(DeviceDataEntity.class).where(partitionFilter);
            Iterable<DeviceDataEntity> dataList = cloudTable.execute(partitionQuery);
            /*
            for (DevicePlugDataEntity entity : dataList) {
                Log.d("azure-storage",entity.getPartitionKey() +
                        " " + entity.getRowKey() +
                        " " + entity.getState());
            }*/
            return dataList;
    }

    public boolean exists(String deviceId, Device.DeviceType deviceType) throws StorageException, URISyntaxException, InvalidKeyException{
        // Define constants for filters.

        // Retrieve storage account from connection-string.
        CloudStorageAccount storageAccount =
                CloudStorageAccount.parse(Utility.storageConnectionString);

        // Create the table client.
        CloudTableClient tableClient = storageAccount.createCloudTableClient();
        CloudTable cloudTable;

        if(deviceType == Device.DeviceType.PLUG) {
            // Create a cloud table object for the table.
            cloudTable = tableClient.getTableReference("devicePlugData");
        }else{
            cloudTable = tableClient.getTableReference("devicePirData");
        }

        // Create a filter condition where the partition key is "Smith".

        String partitionFilter = TableQuery.generateFilterCondition(
                "PartitionKey",
                TableQuery.QueryComparisons.EQUAL,
                deviceId);

      	   /*
    	    // Specify a partition query, using "Smith" as the partition key filter.
    	    TableQuery<CustomerEntity> partitionQuery =
    	        TableQuery.from(CustomerEntity.class)
    	        .where(partitionFilter);

    	    	*/

        TableQuery<DeviceDataEntity> partitionQuery =
                TableQuery.from(DeviceDataEntity.class).where(partitionFilter);
        Iterable<DeviceDataEntity> dataList = cloudTable.execute(partitionQuery);
            /*
            for (DevicePlugDataEntity entity : dataList) {
                Log.d("azure-storage",entity.getPartitionKey() +
                        " " + entity.getRowKey() +
                        " " + entity.getState());
            }*/
        return dataList.iterator().hasNext();
    }
}
