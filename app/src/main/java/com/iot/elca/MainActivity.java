package com.iot.elca;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.marcos.elca.R;
import com.iot.elca.activities.AddDeviceActivity;
import com.iot.elca.azure.manager.AzureDeviceManager;
import com.iot.elca.azure.manager.AzureStorageManager;
import com.iot.elca.dao.ElcaDbHelper;
import com.iot.elca.dao.MainDeviceDAO;
import com.iot.elca.model.MainDevice;
import com.iot.elca.view.TurnDeviceImageButton;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ElcaDbHelper dbHelper;
    TurnDeviceImageButton imgBtnTurnDeviceAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineButtonsActions();
        AzureStorageManager manager = AzureStorageManager.getInstance();
        dbHelper = ElcaDbHelper.getInstance(getApplicationContext());
        loadDeviceList();
        sendMessageToDevice();
    }

    private void sendMessageToDevice() {
        AzureDeviceManager.sendMessage("elca-main-device", getApplicationContext());
    }


    private void defineButtonsActions() {
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.btn_add_device);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDeviceActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDeviceList(){

        //Load the list of devices
        List<MainDevice> devices = MainDeviceDAO.selectAllDevices(dbHelper);
        /*List<Device> devices = new LinkedList<>();
        devices.add(new Device("Cafeteira", 1, null, false));
        devices.add(new Device("Geladeira", 2, null, false));
        devices.add(new Device("Televisão", 3, null, false));
        devices.add(new Device("Torradeira", 4, null, false));*/
        TableLayout tl = (TableLayout) findViewById(R.id.table_devices);
        for (MainDevice d : devices) {

            //Create device name column
            TableRow tr = new TableRow(getApplicationContext());
            tr.setBackgroundColor(getResources().getColor(R.color.table_devices_content_background));
            int padding = (int) getResources().getDimension(R.dimen.table_devices_content_padding);
            tr.setPadding(padding, padding, padding, padding);

            TextView tvNome = new TextView(getApplicationContext());
            tvNome.setText(d.getId());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 2f);
            lp.topMargin = 50;
            tvNome.setLayoutParams(lp);
            tvNome.setTextSize(15);
            tvNome.setTextAppearance(getApplicationContext(), R.style.TextAppearance_AppCompat_Button);
            tvNome.setTextColor(Color.BLACK);
            tvNome.setTextSize(20);

            tr.addView(tvNome);

            //create the button
            final TurnDeviceImageButton imgBtnTurnDevice = new TurnDeviceImageButton(getApplicationContext(), d);
            imgBtnTurnDevice.setImageResource(R.drawable.turn_off);
            imgBtnTurnDevice.setDevice(d);
            int size = (int) this.getResources().getDimension(R.dimen.dimen_turn_button_in_dp);
            imgBtnTurnDevice.setLayoutParams(new TableRow.LayoutParams(size, size));
            imgBtnTurnDevice.setScaleType(ImageView.ScaleType.CENTER);
            imgBtnTurnDevice.setAdjustViewBounds(true);
            imgBtnTurnDevice.setBackgroundColor(Color.TRANSPARENT);

            imgBtnTurnDevice.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    imgBtnTurnDeviceAux = (TurnDeviceImageButton) v;
                        turnDevice();
                    }
                });
            tr.addView(imgBtnTurnDevice);

            View hLine = new View(getApplicationContext());
            hLine.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            hLine.setBackgroundColor(Color.BLACK);

            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            tl.addView(hLine);
        }
    }

    public void turnDevice() {
        MainDevice device = imgBtnTurnDeviceAux.getDevice();
        final List<Pair<String, String>> parameters = new LinkedList<>();
        parameters.add(new Pair<>("ON", String.valueOf(device.isOn())));
        //update button
        if (device.isOn()) {
            imgBtnTurnDeviceAux.setImageResource(R.drawable.turn_off);
            device.setOn(false);
        } else {
            imgBtnTurnDeviceAux.setImageResource(R.drawable.turn_on);
            device.setOn(true);
        }

        Log.d("isOn", String.valueOf(device.isOn()));
        new AsyncTask<TurnDeviceImageButton, Void, Void>() {
            @Override
            protected Void doInBackground(TurnDeviceImageButton... params) {
                Log.d("onBackground", "started");

                Log.d("onBackground", "finish");
                return null;
            }
        }.execute();
    }

}
