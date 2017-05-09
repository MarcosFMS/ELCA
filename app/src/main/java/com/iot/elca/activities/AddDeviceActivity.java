package com.iot.elca.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marcos.elca.R;
import com.iot.elca.dao.ElcaDbHelper;
import com.iot.elca.dao.PlugDeviceDAO;
import com.iot.elca.MainActivity;
import com.iot.elca.model.PlugDevice;

import java.util.Calendar;
import java.util.List;

public class AddDeviceActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    long device_connect_timeout = 30000;
    private WifiManager wifiManager;
    private boolean isConnected = false;
    private String wifissid;
    private String wifipassword;
    private PlugDevice device;
    private ElcaDbHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        dbHelper = ElcaDbHelper.getInstance(getApplicationContext());
        getWifiSsid();
        readDeviceInfo();
    }

    public void getWifiSsid() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (checkConnection(getApplicationContext())) {
            WifiInfo wifiinfo = wifiManager.getConnectionInfo();
            wifissid = wifiinfo.getSSID().replaceAll("\"", "");
            isConnected = true;
            Log.d("SSID", wifissid);
        }
    }

    public void readDeviceInfo() {
        scanQR();
    }

    public boolean checkConnection(Context context, String ssid) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();
        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED && wifiinfo.getSSID().replaceAll("\"", "").equals(ssid);
    }

    public boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    //product qr code mode
    public void scanQR() {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showQrReaderNotFoundDialog(AddDeviceActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private AlertDialog showQrReaderNotFoundDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    anfe.printStackTrace();
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                returnToDevicesPage();
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.d("qrcode_return", "Content:" + contents + " Format:" + format);
                decodeDeviceInfo(contents);
                connectToDevice();
                getWifiInfo();
            }
        }
    }

    public void decodeDeviceInfo(String info) {
        try {
            String[] infos = info.split(";");
            String id = infos[0];
            String ssid = infos[1];
            String password = infos[2];
            String ip = infos[3];

            Log.d("info", id+", "+ssid+", "+password+", "+ip);
            device = new PlugDevice(id, "on", ssid, password, ip);
        } catch (IndexOutOfBoundsException ex) {
            Toast.makeText(getApplicationContext(), "Código Inválido", Toast.LENGTH_LONG).show();
            returnToDevicesPage();
        }
    }

    //TODO adicionar tela de loading depois de ler qrcode
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void connectToDevice() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", device.getSsid());
        wifiConfig.preSharedKey = String.format("\"%s\"", device.getPassword());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        disableNetworks();
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();

        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Calendar c = Calendar.getInstance();
        long timeInit = c.getTimeInMillis();
        long timeLimit = timeInit + device_connect_timeout;

        Log.d("loop", timeInit +", "+timeLimit);
        while (!checkConnection(getApplicationContext(), device.getSsid()) && timeInit < timeLimit) {
            timeInit = Calendar.getInstance().getTimeInMillis();
        }
        //TODO Retirar
        Log.d("wifi_data", device.getSsid()+", "+wifiManager.getConnectionInfo().getSSID().replaceAll("\"", "")+" : "+device.getSsid().equals(wifiManager.getConnectionInfo().getSSID().replaceAll("\"", "")));
        //
        if (timeInit >= timeLimit) {
            Toast.makeText(getApplicationContext(), "Erro ao conectar com dispositivo!", Toast.LENGTH_LONG).show();
            returnToDevicesPage();
        } else {
            //Toast.makeText(getApplicationContext(), "Conectou com sucesso!", Toast.LENGTH_LONG).show();
            getWifiInfo();
        }
    }

    public void disableNetworks(){
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            wifiManager.disableNetwork(i.networkId);
            wifiManager.saveConfiguration();
        }
    }

    private void returnToDevicesPage() {
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void getWifiInfo() {
        if (wifissid == null) {
            EditText edtTxtSsid = (EditText) findViewById(R.id.edtxt_ssid);
            edtTxtSsid.setVisibility(View.VISIBLE);
        }

        EditText edtTxtSenha = (EditText) findViewById(R.id.edtxt_password);
        edtTxtSenha.setVisibility(View.VISIBLE);
    }

    public void registerDevice(View view) {
        view.setEnabled(false);
        EditText edtTxtSsid = (EditText) findViewById(R.id.edtxt_ssid);
        EditText edtTxtPassword = (EditText) findViewById(R.id.edtxt_password);
        if (wifissid == null)
            wifissid = edtTxtSsid.getText().toString();
        wifipassword = edtTxtPassword.getText().toString();
        if(wifipassword.replaceAll(" ", "").equals("") || wifissid.replaceAll(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "Digite a o ssid e senha do wifi", Toast.LENGTH_LONG).show();
        else
            sendWIFIData();
    }

    public void sendWIFIData() {
        getRequest();
    }

    public void getRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://"+device.getIp();
        url += "?wifidata=" + wifissid + "&" + wifipassword + "|";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AddDeviceActivity.this.onResponse(response, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Caso dê erro
                onResponse(error.getMessage(), false);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void onResponse(String response, boolean ok) {
        if (ok) {
            Log.d("Response", response+"");
            PlugDeviceDAO.insertDevice(dbHelper, device);
            Toast.makeText(getApplicationContext(), "Dispositivo cadastrado!", Toast.LENGTH_LONG).show();
            returnToDevicesPage();
        } else {
            Log.d("Response", response+"");
            Toast.makeText(getApplicationContext(), "Erro ao cadastrar dispositivo!", Toast.LENGTH_LONG).show();
        }
    }

}
