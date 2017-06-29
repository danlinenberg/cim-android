package com.tau.dtr.cim_application;

import android.os.Handler;

/**
 * Created by dan on 14/06/2017.
 */


/**
 * A class that sends bluetooth datastreams to the brick
 */
public class Lejos {

    static final long TIME_STRAIGHT = 4000;
    static final long TIME_SIDE = 1650;
    static long TimeToBombSound = 0;
    static final Handler handler = new Handler();

    public static void Forward(){
        BluetoothController.getInstance().SendMessage("f");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("s");
            }
        }, TIME_STRAIGHT);
    }
    public static void Back(){
        BluetoothController.getInstance().SendMessage("b");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("s");
            }
        }, TIME_STRAIGHT);
    }
    public static void TurnRight(){
        BluetoothController.getInstance().SendMessage("r");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("s");
            }
        }, TIME_SIDE);
    }
    public static void TurnLeft(){
        BluetoothController.getInstance().SendMessage("r");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("s");
            }
        }, TIME_SIDE);

    }
    public static void Right(){
        BluetoothController.getInstance().SendMessage("r");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("f");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("l");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("s");
                            }
                        }, TIME_SIDE);
                    }
                }, TIME_STRAIGHT);
            }
        }, TIME_SIDE);
    }

    public static void Left(){
        BluetoothController.getInstance().SendMessage("l");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("f");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("r");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("s");
                            }
                        }, TIME_SIDE);
                    }
                }, TIME_STRAIGHT);
            }
        }, TIME_SIDE);
    }

    public static void ForwardRight(){
        BluetoothController.getInstance().SendMessage("f");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("r");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("f");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("l");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluetoothController.getInstance().SendMessage("s");
                                    }
                                }, TIME_SIDE);
                            }
                        }, TIME_STRAIGHT);
                    }
                }, TIME_SIDE);
            }
        }, TIME_STRAIGHT);

    }
    public static void ForwardLeft(){
        BluetoothController.getInstance().SendMessage("f");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("l");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("f");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("r");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluetoothController.getInstance().SendMessage("s");
                                    }
                                }, TIME_SIDE);
                            }
                        }, TIME_STRAIGHT);
                    }
                }, TIME_SIDE);
            }
        }, TIME_STRAIGHT);

    }
    public static void BackRight(){
        BluetoothController.getInstance().SendMessage("b");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("r");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("f");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("l");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluetoothController.getInstance().SendMessage("s");
                                    }
                                }, TIME_SIDE);
                            }
                        }, TIME_STRAIGHT);
                    }
                }, TIME_SIDE);
            }
        }, TIME_STRAIGHT);

    }
    public static void BackLeft(){
        BluetoothController.getInstance().SendMessage("b");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("l");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothController.getInstance().SendMessage("f");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().SendMessage("r");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluetoothController.getInstance().SendMessage("s");
                                    }
                                }, TIME_SIDE);
                            }
                        }, TIME_STRAIGHT);
                    }
                }, TIME_SIDE);
            }
        }, TIME_STRAIGHT);
    }

    public static void makeSound_Boom(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("o");
            }
        }, TimeToBombSound+600);
    }


    public static void makeSound_Powerup_hp(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("1");
            }
        }, TIME_STRAIGHT*2+TIME_SIDE*2+600);
    }
    public static void makeSound_Powerup_bomb(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("2");
            }
        }, TIME_STRAIGHT*2+TIME_SIDE*2+600);
    }
    public static void makeSound_Powerup_godmode(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("3");
            }
        }, TIME_STRAIGHT*2+TIME_SIDE*2+600);
    }
    public static void makeSound_Powerup_confusion(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothController.getInstance().SendMessage("4");
            }
        }, TIME_STRAIGHT*2+TIME_SIDE*2+600);
    }
    public static void Win(){
        BluetoothController.getInstance().SendMessage("w");
    }



}
