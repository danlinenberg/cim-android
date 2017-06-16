package com.tau.dtr.cim_application;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import me.aflak.bluetooth.Bluetooth;

import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 12/06/2017.
 */

public class BluetoothController {

    public static BluetoothController mContext = new BluetoothController();
    public static String device_name;
    static MainInterface mainInterface;

    static Bluetooth bluetooth;

    public void instantiateBluetooth(Activity act){
        bluetooth = new Bluetooth(act);
        bluetooth.enableBluetooth();

        bluetooth.setDiscoveryCallback(new Bluetooth.DiscoveryCallback() {
            @Override
            public void onFinish() {
                // scan finished
                mainInterface.onBluetoothComplete(null);
            }

            @Override
            public void onDevice(BluetoothDevice device) {
                // device found
                try {
                    if (device.getName().equals(device_name)) {
                        bluetooth.pair(device);
                    }
                }catch (Exception e){
                    mainInterface.onBluetoothComplete(null);
                }

            }

            @Override
            public void onPair(BluetoothDevice device) {
                // device paired
                log("Paired!!!");
                bluetooth.connectToDevice(device);
            }

            @Override
            public void onUnpair(BluetoothDevice device) {
                // device unpaired
            }

            @Override
            public void onError(String message) {
                // error occurred
                mainInterface.onBluetoothComplete(null);
                log("error");
            }
        });

        bluetooth.setCommunicationCallback(new Bluetooth.CommunicationCallback() {
            @Override
            public void onConnect(BluetoothDevice device) {
                // device connected
                log("Connected!!!");
                mainInterface.onBluetoothComplete(device.getName());
            }

            @Override
            public void onDisconnect(BluetoothDevice device, String message) {
                // device disconnected
            }

            @Override
            public void onMessage(String message) {
                // message received (it has to end with a \n to be received)
            }

            @Override
            public void onError(String message) {
                // error occurred
            }

            @Override
            public void onConnectError(BluetoothDevice device, String message) {
                // error during connection
                log("cannot connect");
                mainInterface.onBluetoothComplete(null);
            }
        });

    }


    public void StartBluetoothQuery(String name, MainInterface inter){

        device_name = name.replace(" ","");
        mainInterface = inter;

        boolean found = false;

        List<BluetoothDevice> deviceList = bluetooth.getPairedDevices();
        ArrayList<String> names = new ArrayList<>();
        for(BluetoothDevice d: deviceList){
            names.add(d.getName());
            if(d.getName().equalsIgnoreCase(device_name)){
                found = true;
                bluetooth.connectToDevice(d);
                break;
            }
        }
        if(!found){
            bluetooth.scanDevices();
        }
    }

    public void SendMessage(String msg){
        try {
            bluetooth.send(msg);
        }catch (Exception e){
            e.printStackTrace();
        };
    }

    public static BluetoothController getInstance(){
        return mContext;
    }


}