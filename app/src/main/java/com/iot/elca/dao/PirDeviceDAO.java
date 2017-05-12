package com.iot.elca.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import com.iot.elca.model.PirDevice;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcos on 10/05/2017.
 */

public class PirDeviceDAO {

    private static final String TABLE_NAME = "PIR_DEVICE";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    "ID VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "SSID VARCHAR(50)," +
                    "NAME VARCHAR(50)," +
                    "PASSWORD VARCHAR(50)," +
                    "STATE VARCHAR(3) NOT NULL," +
                    "IP VARCHAR(11))" ;

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static List<PirDevice> selectAllDevices(ElcaDbHelper dbHelper) {
        LinkedList<PirDevice> devices = new LinkedList<>();

        //setup the query to be executed
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT * FROM "+TABLE_NAME);

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(stringBuilderQuery.toString(), null);
        cursor.getColumnNames();
        /*Position the cursor on the first register*/
        cursor.moveToFirst();
        cursor.getColumnName(0);


        PirDevice device;

        //Reads until the cursor reaches the final register
        while (!cursor.isAfterLast()) {
            device = new PirDevice();

            device.setId(cursor.getString(cursor.getColumnIndex("ID")));
            device.setState(cursor.getString(cursor.getColumnIndex("STATE")));

            devices.add(device);

            cursor.moveToNext();
        }
        return devices;
    }

    public static PirDevice selectDevice(ElcaDbHelper dbHelper, int id){
        //setup the query to be executed
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT * FROM "+TABLE_NAME+" WHERE ID = " + id);

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(stringBuilderQuery.toString(), null);
        /*Position the cursor on the first register*/
        cursor.moveToFirst();
        PirDevice d = new PirDevice();

        //Reads until the cursor reaches the final register
        d.setId(cursor.getString(cursor.getColumnIndex("ID")));
        d.setState(cursor.getString(cursor.getColumnIndex("STATE")));

        return d;
    }

    public static void insertDevice(ElcaDbHelper dbHelper, PirDevice device) throws SQLiteConstraintException{
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", device.getId());
            contentValues.put("ip", device.getIp());
            contentValues.put("ssid", device.getSsid());
            contentValues.put("password", device.getPassword());
            contentValues.put("state", String.valueOf(device.getState()));
            dbHelper.getWritableDatabase().insertOrThrow(TABLE_NAME, null, contentValues);
        }catch(SQLiteConstraintException ex){
            throw ex;
        }

    }

    public static void updateDevice(ElcaDbHelper dbHelper, PirDevice device) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("STATE", device.getState());
        dbHelper.getWritableDatabase().update(TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(device.getId())});
    }
}
