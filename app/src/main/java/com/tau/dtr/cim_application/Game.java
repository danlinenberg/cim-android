package com.tau.dtr.cim_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;

import static com.tau.dtr.cim_application.MultiplayerManager.mGoogleApiClient;
import static com.tau.dtr.cim_application.MultiplayerManager.mMyId;
import static com.tau.dtr.cim_application.MultiplayerManager.mMyRoom;
import static com.tau.dtr.cim_application.Utils.Utils.getNthDigit;
import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 10/06/2017.
 */

public class Game extends Activity{

    public static Game mContext = new Game();
    static Boolean myTurn;
    static Integer myTile;
    static Integer opponentTile;
    static Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        mCtx = this;
        mContext = this;
        StartGame();
    }

    public void StartGame(){
        log("Game started");

        myTile = 74;
        opponentTile = 14;

        ImageView player2 = (ImageView) findViewById(R.id.square_14);
        ImageView player1 = (ImageView) findViewById(R.id.square_74);
        player1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player1.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
        player2.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));

        String playerStarterId = getIntent().getStringExtra(getResources().getString(R.string.game_player_starter));
        if(playerStarterId.equals(mMyId)){
            myTurn = true;
            showTimedAlertDialog("Your turn!", "Click on spot you want to move your brick to", 5);
        }else{
            myTurn = false;
            showTimedAlertDialog("Your opponent's move", "Wait for your opponent to make a move", 5);
        }
    }

    public void Decipher(String message){
        int tile = Integer.parseInt(message);
        int length = String.valueOf(tile).length();
        if(length>2){
            //powerup picked
            int firstDigit = getNthDigit(tile, 1);
            switch (firstDigit){
                case(1):
                    break;
                case(2):
                    break;
                case(3):
                    break;
            }
        }else{
            //just moved
            MoveTileOpponent(tile);
        }

        myTurn = true;
    }

    public void getTilePressed(View v){
        if(myTurn){
            int id = v.getId();
            String resource = v.getResources().getResourceEntryName(id);
            String resource_str = resource.replace("square_", "");
            int tile = Integer.parseInt(resource_str);
            if(tile==myTile+1 || tile==myTile-1 || tile==myTile+10 || tile==myTile+11 || tile == myTile+9 || tile==myTile-10 || tile ==myTile-11 || tile == myTile-9){
                MoveTileSelf(tile);
            }
        }
    }

    public void MoveTileSelf(Integer position){
        MultiplayerManager.getInstance().SendMessage(position.toString());

        int resID = getApplicationContext().getResources().getIdentifier("square_"+position.toString(), "id", getPackageName());
        int resID_old = getApplicationContext().getResources().getIdentifier("square_"+myTile.toString(), "id", getPackageName());
        ImageView player1 = (ImageView) findViewById(resID);
        ImageView player1_old = (ImageView) findViewById(resID_old);
        player1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player1.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
        player1_old.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
        myTile = position;
        myTurn = false;
    }

    public void MoveTileOpponent(Integer position){

        Integer position_inverted = revertTile(position);
        String position_inverted_str = String.valueOf(position_inverted);

        int resID = mCtx.getResources().getIdentifier("square_"+position_inverted_str, "id", getPackageName());
        int resID_old = mCtx.getApplicationContext().getResources().getIdentifier("square_"+opponentTile, "id", getPackageName());
        ImageView player2 = (ImageView) findViewById(resID);
        ImageView player2_old = (ImageView) findViewById(resID_old);
        player2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player2.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));
        player2_old.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
        opponentTile = position_inverted;
        myTurn = true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public static Game getInstance(){
        return mContext;
    }

    public void showTimedAlertDialog(String header, String msg, Integer seconds){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(header).setMessage(msg);
        final AlertDialog alert = dialog.create();
        alert.show();

// Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, seconds*1000);
    }

    public int revertTile(Integer tile){
        int secondDigit = getNthDigit(tile,1);
        int firstDigit = getNthDigit(tile, 2);
        int firstDigitInverted = 8-firstDigit;
        String combined = String.valueOf(firstDigitInverted) + String.valueOf(secondDigit);
        return Integer.parseInt(combined);

    }

}
