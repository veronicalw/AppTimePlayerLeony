package com.example.myapplication.TimePlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.myapplication.TimePlayer.MyServices.TimeContainer;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyServices.class);
        if (TimeContainer.getInstance().isServiceRunning.get()){
            stopService(intent);
        } else {
            startService(intent);
        }
        finish();
    }
}
