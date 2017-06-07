package com.tau.dtr.cim_application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.tau.dtr.cim_application.Utils.log;

public class MainActivity extends AppCompatActivity implements MainInterface{

    public MainInterface mInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterface = this;
        BluetoothManager.getInstance().StartQuery("Smarteyeglass", mInterface);
    }

    public void onBluetoothComplete(String device_name){
        log("ready to begin match for device " + device_name);
    }
}
