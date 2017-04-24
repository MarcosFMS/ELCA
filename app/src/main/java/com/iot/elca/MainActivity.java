package com.iot.elca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.FloatingActionButton;

import com.example.marcos.elca.R;
import com.iot.elca.Activities.AddDeviceActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineButtonsActions();
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
}
