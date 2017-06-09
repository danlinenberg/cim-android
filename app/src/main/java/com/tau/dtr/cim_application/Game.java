package com.tau.dtr.cim_application;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

import static com.tau.dtr.cim_application.MultiplayerManager.mGoogleApiClient;

/**
 * Created by dan on 10/06/2017.
 */

public class Game extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);


        MultiplayerManager.getInstance().SendMessage("test");
    }
}
