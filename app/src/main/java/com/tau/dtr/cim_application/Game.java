package com.tau.dtr.cim_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tau.dtr.cim_application.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.tau.dtr.cim_application.MainActivity.sharedPreferences;
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
    static Integer bombs;
    static Integer hp;
    static ArrayList<Integer> bombs_location;
    static boolean canPlaceBomb;
    static boolean bombIntent;

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
        bombs=3;
        hp=3;
        bombs_location = new ArrayList<>();
        canPlaceBomb = true;

        ImageView player2 = (ImageView) findViewById(R.id.square_14);
        ImageView player1 = (ImageView) findViewById(R.id.square_74);
        player1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player1.setImageDrawable(getResources().getDrawable(R.drawable.tank_blue));
        player2.setImageDrawable(getResources().getDrawable(R.drawable.tank_red));

//        String playerStarterId = getIntent().getStringExtra(getResources().getString(R.string.game_player_starter));
//        if(playerStarterId.equals(mMyId)){
//            myTurn = true;
//            showTimedAlertDialog("Your turn!", "Click on spot you want to move your brick to", 5);
//        }else{
//            myTurn = false;
//            showTimedAlertDialog("Your opponent's move", "Wait for your opponent to make a move", 5);
//        }
        try{
            if(sharedPreferences.getString("brick", null).contains("Arafat")){
                myTurn = true;
                showTimedAlertDialog("Your turn!", "Click on spot you want to move your brick to", 5);
            }else{
                myTurn = false;
                showTimedAlertDialog("Your opponent's move", "Wait for your opponent to make a move", 5);
            }
        }catch (NullPointerException e){
            myTurn = false;
        }
    }

    public void Decipher(String message){
        try{

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

        }catch (NumberFormatException e){
            // bomb
            String[] sep = message.split(" ");
            if(sep[0].equals("bomb")){
                String tile = sep[1];
                placeBomb(Integer.parseInt(tile), true);
            }
        }
    }

    public void getTilePressed(View v){
        if(myTurn){
            if(!MultiplayerManager.getInstance().enoughTimeBetweenCommands()){
                showToast("Please wait a second before issueing another command");
                return;
            }
            int id = v.getId();
            String resource = v.getResources().getResourceEntryName(id);
            String resource_str = resource.replace("square_", "");
            int tile = Integer.parseInt(resource_str);

            if(bombIntent){
                if(tile!=myTile && tile!=opponentTile && !bombs_location.contains(tile)){
                    canPlaceBomb = false;
                    placeBomb(tile, false);
                    return;
                }
            }
            if(tile==myTile+1 || tile==myTile-1 || tile==myTile+10 || tile==myTile+11 || tile == myTile+9 || tile==myTile-10 || tile ==myTile-11 || tile == myTile-9){
                MoveTileSelf(tile);
                checkBombs(tile);
            }
        }else{
            showToast("Wait for your turn imbecile");
        }
    }

    public void checkBombs(Integer tile){
        if(bombs_location.contains(tile)){
            ImageView hpImg = (ImageView) findViewById(R.id.img_hp);
            hpImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Lejos.makeSound_Boom();
            switch (hp){
                case(3):
                    hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_23));
                    hpImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case(2):
                    hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_13));
                    hpImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case(1):
                    hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_03));
                    hpImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
            }
            hp = hp-1;
            bombs_location.remove(tile);
            if(hp==0){
                showTimedAlertDialog("LOST!", "You're not very good at this are you?", 6);
                leaveGame();
            }
            showTimedAlertDialog("PWNED!", "You just stepped on a mine", 3);
        }
    }

    public void leaveGame(){
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public void MoveTileSelf(Integer position){
        MultiplayerManager.getInstance().SendMessage(position.toString());
        HandleLejos(myTile, position);

        int resID = getApplicationContext().getResources().getIdentifier("square_"+position.toString(), "id", getPackageName());
        int resID_old = getApplicationContext().getResources().getIdentifier("square_"+myTile.toString(), "id", getPackageName());
        ImageView player1 = (ImageView) findViewById(resID);
        ImageView player1_old = (ImageView) findViewById(resID_old);
        player1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        player1.setImageDrawable(getResources().getDrawable(R.drawable.tank_blue));
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
        player2.setImageDrawable(getResources().getDrawable(R.drawable.tank_red));
        player2_old.setImageDrawable(getResources().getDrawable(android.R.color.transparent));

        opponentTile = position_inverted;
        myTurn = true;
        canPlaceBomb = true;
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

    public void placeBombIntent(View v){
        if(!myTurn){
            showToast("Wait for your turn imbecile");
            return;
        }
        if(bombs>0 && canPlaceBomb){
            if(!bombIntent){
                showToast("Touch a tile to place a bomb");
                bombIntent = true;
            }else{
                showToast("No longer placing bombs");
                bombIntent = false;
            }
        }
    }

    public void placeBomb(Integer tile, boolean enemy){
        bombs_location.add(tile);
        if(enemy){
            return;
        }

        int resID = getApplicationContext().getResources().getIdentifier("square_"+tile.toString(), "id", getPackageName());
        ImageView bombPlace = (ImageView) findViewById(resID);
        bombPlace.setImageDrawable(getResources().getDrawable(R.drawable.bomb));
        bombPlace.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int resID_bomb = getApplicationContext().getResources().getIdentifier("bomb_container", "id", getPackageName());
        ImageView bomb_container = (ImageView) findViewById(resID_bomb );
        switch (bombs){
            case(3):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_2));
                bomb_container.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case(2):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_1));
                bomb_container.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case(1):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_0));
                bomb_container.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
        }
        bombs = bombs-1;
        bombIntent = false;
        canPlaceBomb = false;
        MultiplayerManager.getInstance().SendMessage("bomb " + revertTile(tile).toString());
    }


    public Integer revertTile(Integer tile){
        int secondDigit = getNthDigit(tile,1);
        int firstDigit = getNthDigit(tile, 2);
        int firstDigitInverted = 8-firstDigit;
        String combined = String.valueOf(firstDigitInverted) + String.valueOf(secondDigit);
        return Integer.parseInt(combined);

    }

    public void HandleLejos(Integer old_position, Integer new_position){
        int compare = new_position-old_position;
//        String direction = "f"; //default go forward
        switch (compare){
            case(1):
                //Right
                Lejos.Right();
//                direction = "r"; //114
                break;
            case(-1):
                Lejos.Left();
//                direction = "l"; //108
                break;
            case(-10):
                Lejos.Forward();
//                direction = "f"; //102
                break;
            case(10):
                Lejos.Back();
//                direction = "b"; //98
                break;
            case(11):
                Lejos.ForwardRight();
//                direction = "fr"; //102 114
                break;
            case(9):
                Lejos.BackLeft();
//                direction = "bl"; //98 108
                break;
            case(-9):
                Lejos.ForwardLeft();
//                direction = "fl"; //102 108
                break;
            case(-11):
                Lejos.BackRight();
//                direction = "br"; //102

                break;
        }
    }


    public void showToast(final String txt)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(getApplicationContext(),txt, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
