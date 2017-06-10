package com.tau.dtr.cim_application;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

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
    }

    public void onClick(View v){
        MultiplayerManager.getInstance().SendMessage("test");
    }
}
