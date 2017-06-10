package com.tau.dtr.cim_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

import static com.tau.dtr.cim_application.MultiplayerManager.mGoogleApiClient;
import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 10/06/2017.
 */

public class Game extends Activity{

    public static Game mContext = new Game();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        log("Game started");
    }

    public void Start(){
        log("Game started");
    }

    public void onClick(View v){
        MultiplayerManager.getInstance().SendMessage("test");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public static Game getInstance(){
        return mContext;
    }
}
