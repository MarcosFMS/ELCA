package com.iot.elca.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.marcos.elca.R;
import com.iot.elca.activities.NotificationInfoActivity;
import com.iot.elca.azure.manager.AzureStorageManager;
import com.iot.elca.azure.model.DeviceDataEntity;
import com.iot.elca.dao.ElcaDbHelper;
import com.iot.elca.dao.PirDeviceDAO;
import com.iot.elca.model.PirDevice;
import com.microsoft.azure.storage.StorageException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DevicePirService extends Service {

    public DevicePirService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ElcaDbHelper dbHelper;
    private AzureStorageManager stManager;
    private long timeLimit = 2L;//time in minutes
    private List<PirDevice> plugDevices;
    private long timeLimitInactive = 5L;//time in minutes

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        private class Monitor implements Runnable {
            private PirDevice device;
            private boolean inactive = false;

            public PirDevice getDevice() {
                return device;
            }

            public void setDevice(PirDevice device) {
                this.device = device;
            }

            public Monitor(PirDevice device) {
                super();
                setDevice(device);
            }

            @Override
            public void run() {
                while (true) {
                    monitor(device.getId());
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

            public void monitor(String deviceId) {
                try {
                    Iterable<DeviceDataEntity> dataIterable = stManager.getDevicePlugData(deviceId);
                    List<DeviceDataEntity> dataList = new ArrayList<>();
                    for (DeviceDataEntity d : dataIterable) {
                        dataList.add(d);
                    }
                    long time = 0;
                    boolean first = true;
                    DeviceDataEntity dAux;
                    for (int i = dataList.size() - 1; i > 0; i--) {
                        dAux = dataList.get(i);
                        if (first) {
                            long cTime = parseToUsTimeZone(TimeUnit.MILLISECONDS.toMinutes(parseToUsTimeZone(Calendar.getInstance().getTime().getTime())));
                            time = TimeUnit.MILLISECONDS.toMinutes(dAux.getTimestamp().getTime());
                            if (cTime - time >= timeLimitInactive) {
                                inactive = true;
                                break;
                            } else if (dAux.getState().equals(DeviceDataEntity.OFF)) {
                                time = 0;
                                break;
                            }
                            first = false;
                        } else {
                            if (dAux.getState().equals(DeviceDataEntity.OFF)) {
                                time -= TimeUnit.MILLISECONDS.toMinutes(dAux.getTimestamp().getTime());
                                break;
                            }
                        }
                    }
                    //Log.d("time", String.valueOf(time));
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    if (inactive) {
                        String date = dateFormat.format(Calendar.getInstance().getTime());
                        String intentMessage =  "O equipamento foi encontrado desconectado da rede às "+date;
                        sendNotification("Equipamento Inativo", "Equipamento desconectado da rede!", intentMessage);
                        Log.d("Notification", "Inactive!!");
                    } else if (time > timeLimit) {
                        String date = dateFormat.format(Calendar.getInstance().getTime());
                        String intentMessage =  "Foi detectada um tempo de permanência anormal em um cômodo da casa às "+date;
                        sendNotification("Alerta", "A pessoa está no local a muito tempo!", intentMessage);
                        //TODO turnoff device
                        Log.d("Notification", "Alert!!");
                    }
                } catch (StorageException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            private void sendNotification(String title, String text, String intentMessage) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.plug)
                                .setContentTitle(title)
                                .setContentText(text);
                Intent resultIntent = new Intent(getApplicationContext(), NotificationInfoActivity.class);
                // Because clicking the notification opens a new ("special") activity, there's
                // no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                resultIntent.putExtra("type",1);
                resultIntent.putExtra("deviceId",device.getId());
                resultIntent.putExtra("message", intentMessage);

                mBuilder.setContentIntent(resultPendingIntent);
                // Sets an ID for the notification
                int mNotificationId = 001;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());


            }

            private long parseToUsTimeZone(long time) throws ParseException {
                time -= 5;
                return time;
            }
        }


        @Override
        public void handleMessage(Message msg) {
            Monitor monitor;
            Thread thread;
            for (PirDevice d : plugDevices) {
                monitor = new Monitor(d);
                thread = new Thread(monitor);
                thread.start();
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
        dbHelper = ElcaDbHelper.getInstance(getApplicationContext());
        stManager = AzureStorageManager.getInstance();
        getPlugDevices();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        //Log.d("time", String.valueOf(deviceId));
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private void getPlugDevices() {
        plugDevices = PirDeviceDAO.selectAllDevices(dbHelper);
    }

}

