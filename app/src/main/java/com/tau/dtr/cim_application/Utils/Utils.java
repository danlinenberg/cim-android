package com.tau.dtr.cim_application.Utils;

import android.util.Log;
import java.util.Random;

/**
 * Created by dan on 07/06/2017.
 */

public class Utils {

    public static String LOG_TAG = "CIM_DTR";

    public static void log(String txt){
        Log.d(LOG_TAG, txt);
    }

    public static Boolean is_debug = false;


    /**
     * get digit N from tile
     * @param a
     * @param b
     * @return
     */
    public static int getNthDigit(int a, int b) {
        return (int) ((a / Math.pow(10, b - 1)) % 10);
    }


    /**
     * random integer
     * @param min
     * @param max
     * @return
     */
    public static Integer returnRandom(int min, int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }


    /**
     * revert the tile of the opponent (so the dispaly would fit the player's perspective
     * @param tile
     * @return
     */
    public static Integer revertTile(Integer tile){
        int secondDigit = getNthDigit(tile,1);
        int firstDigit = getNthDigit(tile, 2);
        int firstDigitInverted = 8-firstDigit;
        int secondDigitInverted = 8-secondDigit;
        String combined = String.valueOf(firstDigitInverted) + String.valueOf(secondDigitInverted);
        return Integer.parseInt(combined);
    }

}
