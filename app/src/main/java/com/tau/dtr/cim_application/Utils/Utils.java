package com.tau.dtr.cim_application.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by dan on 07/06/2017.
 */

public class Utils extends Activity{

    public static String LOG_TAG = "CIM_DTR";

    public static void log(String txt){
        Log.d(LOG_TAG, txt);
    }


    public static Boolean is_debug = false;

//    public static int firstDigit(int n) {
//        while (n < -9 || 9 < n) n /= 10;
//        return Math.abs(n);
//    }
    public static int getNthDigit(int a, int b) {
        return (int) ((a / Math.pow(10, b - 1)) % 10);
    }

    public static Integer returnRandom(int min, int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public static Integer revertTile(Integer tile){
        int secondDigit = getNthDigit(tile,1);
        int firstDigit = getNthDigit(tile, 2);
        int firstDigitInverted = 8-firstDigit;
        String combined = String.valueOf(firstDigitInverted) + String.valueOf(secondDigit);
        return Integer.parseInt(combined);

    }
}
