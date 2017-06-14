package com.tau.dtr.cim_application.Debug;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tau.dtr.cim_application.BluetoothController;
import com.tau.dtr.cim_application.MainInterface;
import com.tau.dtr.cim_application.R;

/**
 * Created by dan on 14/06/2017.
 */

/**
 * Speed - 200
 * Acceleartion 1000
 */
public class Debug_Tester extends Activity implements MainInterface{

    public EditText editText1;
    public EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);
        editText1= (EditText) findViewById(R.id.millis);
        editText2= (EditText) findViewById(R.id.millis2);
        editText1.setText("2000");
        editText2.setText("1500");
        BluetoothController.getInstance().instantiateBluetooth(this);
    }

    public void onForward(View v){
        long time_input = Long.parseLong(editText1.getText().toString());
        BluetoothController.getInstance().SendMessage("f");
        onWait(time_input);
    }

    public void onBack(View v){
        long time_input = Long.parseLong(editText1.getText().toString());
        BluetoothController.getInstance().SendMessage("b");
        onWait(time_input);
    }

    public void onRight(View v){
        long time_input = Long.parseLong(editText2.getText().toString());
        BluetoothController.getInstance().SendMessage("r");
        onWait(time_input);
    }

    public void onLeft(View v){
        long time_input = Long.parseLong(editText2.getText().toString());
        BluetoothController.getInstance().SendMessage("l");
        onWait(time_input);
    }

    public void onConnect(View v){
        BluetoothController.getInstance().StartBluetoothQuery("Ohazan4", this);
    }

    public void onBluetoothComplete(String device_name) {
        showToast(device_name);
    }
    public void onWait(long time){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("s");
            }
        }, time);
    }

    public void showToast(final String txt)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(getApplicationContext(),txt, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
