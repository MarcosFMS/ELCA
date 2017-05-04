package com.iot.elca.azure.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AzureDeviceManager {

    public static void sendMessage(String idDevice, Context c) {
        sendPostRequest(idDevice, c);
    }

    private static void sendPostRequest(String deviceId, final Context c) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://elca-iot.azure-devices.net/devices/" + deviceId + "/messages/events?api-version=2016-02-03)";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onPostResponse(response, c, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("error", "server error "+response.data+"!");
                }
                onPostResponse(error.getMessage(), c, false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/xml");
                params.put("Authorization", "SharedAccessSignature sr=elca-iot.azure-devices.net&sig=Bi7Tk1nGxGeo%2BZv%2FSzlqnyRfgIOn%2BHGeCJBOPuaracI%3D&se=1525372587&skn=iothubowner");
                params.put("Cache-Control", "no-cache");
                return params;
            }
            /*
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api-version","2016-02-03");
                return params;
            }*/
            @Override
            public String getBodyContentType() {
                return "application/text; charset=UTF-8";
            }
            @Override
            public byte[] getBody(){
                return "On".getBytes();
            }
        };
        try {
            Log.d("request", String.valueOf(stringRequest.getMethod()==Request.Method.POST));
        } catch (Exception authFailureError) {
            authFailureError.printStackTrace();
        }
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    private static void onPostResponse(String response, Context c, boolean ok) {
        if (ok) {
            Log.d("Response", response + "");
            Toast.makeText(c, "Mensagem enviada!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("Response", response + "");
            Toast.makeText(c, "Erro ao enviar mensagem!", Toast.LENGTH_LONG).show();
        }
    }


}