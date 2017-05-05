package com.iot.elca.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.iot.elca.azure.manager.AzureStorageManager;
import com.iot.elca.azure.model.DevicePlugDataEntity;
import com.microsoft.azure.storage.StorageException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Date;

public class DevicePlugService extends Service {

    public DevicePlugService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String deviceId;
    private AzureStorageManager stManager;
    private long timeLimit = 1000L;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            while(true) {
                monitor();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                //stopSelf(msg.arg1);
            }
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        stManager = AzureStorageManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        deviceId = intent.getExtras().getString("deviceId");

        //Log.d("time", String.valueOf(deviceId));
        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void monitor(){
        try {
            Iterable<DevicePlugDataEntity> dataList = stManager.getDevicePlugData(deviceId);
            long time = 0;
            Date dTime;
            boolean first = true;
            for(DevicePlugDataEntity d:dataList){
                if(first){
                    if(d.getIsOn().equals("0")){
                        break;
                    }else{
                        dTime = d.getTimestamp();
                        time = dTime.getTime();
                        first = false;
                    }
                }else{
                    if(d.getIsOn().equals("0")) {
                        dTime = d.getTimestamp();
                        time -= dTime.getTime();
                    }
                }
            }
            Log.d("time", String.valueOf(time));
            if(time > timeLimit){
                sendNotification();
                //TODO turnoff device
            }
        } catch (StorageException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sendNotification() {
    }
}
