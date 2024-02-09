package com.cameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btCmIntent=findViewById(R.id.bt_cm_intent);
        btCmIntent.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,CameraIntentActivity.class);
            startActivity(intent);
        });

        Button btCmxApi=findViewById(R.id.bt_cmx_api);
        btCmxApi.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,CameraIntentActivity.class);
            startActivity(intent);
        });

    }


}