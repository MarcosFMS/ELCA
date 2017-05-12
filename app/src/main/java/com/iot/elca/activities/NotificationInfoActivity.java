package com.iot.elca.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.marcos.elca.R;

public class NotificationInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);
        Intent i = this.getIntent();
        TextView tv = (TextView) findViewById(R.id.txtViewIntentMessage);
        tv.setText(i.getExtras().getString("message"));
    }
}
