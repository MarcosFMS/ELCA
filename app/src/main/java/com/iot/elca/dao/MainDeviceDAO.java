package com.iot.elca.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.iot.elca.model.MainDevice;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcos on 26/03/2017.
 */

public class MainDeviceDAO {

    private static final String TABLE_NAME = "DEVICE";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    "ID INT PRIMARY KEY NOT NULL," +
                    "SSID VARCHAR(50)," +
                    "PASSWORD VARCHAR(50)," +
                    "IS_ON BIT NOT NULL," +
                    "IP VARCHAR(11))" ;

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static List<MainDevice> selectAllDevices(ElcaDbHelper dbHelper) {
        LinkedList<MainDevice> devices = new LinkedList<>();

        //setup the query to be executed
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT * FROM "+TABLE_NAME);

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(stringBuilderQuery.toString(), null);
        cursor.getColumnNames();
        /*Position the cursor on the first register*/
        cursor.moveToFirst();
        cursor.getColumnName(0);


        MainDevice device;

        //Reads until the cursor reaches the final register
        while (!cursor.isAfterLast()) {
            device = new MainDevice();

            device.setId(cursor.getString(cursor.getColumnIndex("ID")));
            device.setOn(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("IS_ON"))));

            devices.add(device);

            cursor.moveToNext();
        }
        return devices;
    }

    public static MainDevice selectDevice(ElcaDbHelper dbHelper, int id){
        //setup the query to be executed
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT * FROM "+TABLE_NAME+" WHERE ID = " + id);

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(stringBuilderQuery.toString(), null);
        /*Position the cursor on the first register*/
        cursor.moveToFirst();
        MainDevice d = new MainDevice();

        //Reads until the cursor reaches the final register
        d.setId(cursor.getString(cursor.getColumnIndex("ID")));
        d.setOn(cursor.getInt(cursor.getColumnIndex("IS_ON"))==1);

        return d;
    }

    public static void insertDevice(ElcaDbHelper dbHelper, MainDevice device) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", device.getId());
        contentValues.put("ip", device.getIp());
        contentValues.put("ssid", device.getSsid());
        contentValues.put("password", device.getPassword());
        contentValues.put("is_on", String.valueOf(device.isOn()));
        dbHelper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);

    }

    public static void updateDevice(ElcaDbHelper dbHelper, MainDevice device) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("is_on", String.valueOf(device.isOn()));
        dbHelper.getWritableDatabase().update(TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(device.getId())});
    }
}

