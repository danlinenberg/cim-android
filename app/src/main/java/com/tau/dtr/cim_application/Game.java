package com.tau.dtr.cim_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;

import static com.tau.dtr.cim_application.MultiplayerManager.mMyId;
import static com.tau.dtr.cim_application.MultiplayerManager.mMyRoom;
import static com.tau.dtr.cim_application.Utils.Utils.firstDigit;
import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 10/06/2017.
 */

public class Game extends Activity{

    public static Game mContext = new Game();
    private Boolean myTurn;
    private Integer myTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        StartGame();
    }

    public void StartGame(){
        log("Game started");

        myTile = 74;

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
        Integer tile = Integer.parseInt(message);
        int length = String.valueOf(tile).length();
        if(length>2){
            //powerup picked
            int firstDigit = firstDigit(tile);
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
            Integer tile = Integer.parseInt(v.getResources().getResourceName(v.getId()).replace("square_", ""));
            if(tile==myTile+1 || tile==myTile-1 || tile==myTile+10 || tile==myTile+11 || tile == myTile+9 || tile==myTile-10 || tile ==myTile-11 || tile == myTile-9){
                MoveTileSelf(tile);
            }
        }
    }

    public void MoveTileSelf(Integer position){
        MultiplayerManager.getInstance().SendMessage(position.toString());
        int resID = getResources().getIdentifier("square_"+position.toString(), "id", getPackageName());
        ImageView player1 = (ImageView) findViewById(resID);
        player1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player1.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
        myTurn = false;
    }

    public void MoveTileOpponent(Integer position){
        int resID = getResources().getIdentifier("square_"+position.toString(), "id", getPackageName());
        ImageView player2 = (ImageView) findViewById(resID);
        player2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player2.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));
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
}
