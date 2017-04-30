package com.iot.elca.azure.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.iot.elca.azure.model.DevicePlugDataEntity;
import com.iot.elca.azure.util.Utility;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Marcos on 29/04/2017.
 */

public class AzureStorageManager extends AsyncTask<Object, Object, Void> {

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



    @Override
    protected Void doInBackground(Object... strings) {
        try {
            getDevicePlugData((String) strings[0]);
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getDevicePlugData(String deviceId)  throws StorageException {
        try
        {
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
    	        PARTITION_KEY,
    	        TableQuery.QueryComparisons.EQUAL,
    	        deviceId);

      	   /*
    	    // Specify a partition query, using "Smith" as the partition key filter.
    	    TableQuery<CustomerEntity> partitionQuery =
    	        TableQuery.from(CustomerEntity.class)
    	        .where(partitionFilter);

    	    	*/

            TableQuery<DevicePlugDataEntity> partitionQuery =
                    TableQuery.from(DevicePlugDataEntity.class).where(partitionFilter);
            // Loop through the results, displaying information about the entity.
            for (DevicePlugDataEntity entity : cloudTable.execute(partitionQuery)) {
                Log.d("azure-storage",entity.getPartitionKey() +
                        " " + entity.getRowKey() +
                        " " + entity.getMessage());
            }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }
}
