package com.tau.dtr.cim_application;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import java.util.Set;

import static com.tau.dtr.cim_application.Utils.Utils.log;


/**
 * Created by dan on 07/06/2017.
 */

public class BluetoothManager extends Activity{

    public final int REQUEST_ENABLE_BT = 200;
    public final String DEVICE_NAME_KEY = "device_name";
    public static BluetoothManager mContext = new BluetoothManager();
    BluetoothAdapter mBluetoothAdapter=null;
    public MainInterface mInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
    }

    public static BluetoothManager getInstance(){
        return mContext;
    }

    public void StartQuery(String deviceName, MainInterface mainInterface){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mInterface = mainInterface;
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            log("device does not support bluetooth");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(DEVICE_NAME_KEY , deviceName);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            SeekDevices(deviceName);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                String device_name = data.getStringExtra(DEVICE_NAME_KEY );
                SeekDevices(device_name);
            }
        }
    }

    public void SeekDevices(String device_name){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (deviceName.equalsIgnoreCase(device_name.replace(" ",""))) {
                    log("Device matched with name " + device_name);
                    mInterface.onBluetoothComplete(device_name);
                    return;
                }
            }
        }
        mInterface.onBluetoothComplete(null);
    }


}
