package com.tau.dtr.cim_application;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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

    /**
     * Initiates the bluetooth service, defines callbacks for pairing and connections
     * @param act the main activity
     */
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
                /**
                 * Checks if the device we found in the scan is the brick. If so - we try to pair with it
                 */
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
                // device paired)
                /**
                 * we paired with the brick, and now try to connect to it
                 */
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
                /**
                 * Returns to the main activity once we're connected with the brick (at this point, the brick's LCD will display "connected")
                 */
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

    /**
     * Retrieves all the paired devices, and checks if the brick exists there. If yes - we try to connect to is. If not - we scan all available devices who aren't paired
     * @param name
     * @param inter
     */
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

    /**
     * sending a bluetooth datastream message to our brick. The brick will intercept it via the lejos library and act accordingly
     * @param msg
     */
    public void SendMessage(String msg){
        try {
            bluetooth.send(msg);
        }catch (Exception e){
            e.printStackTrace();
        };
    }

    /**
     * @return instance of the class
     */
    public static BluetoothController getInstance(){
        return mContext;
    }


}