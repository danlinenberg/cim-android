package com.tau.dtr.cim_application;

import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

/**
 * Created by dan on 07/06/2017.
 */

public class Utils{

    public static String LOG_TAG = "CIM_DTR";

    public static void log(String txt){
        Log.d(LOG_TAG, txt);
    }

}