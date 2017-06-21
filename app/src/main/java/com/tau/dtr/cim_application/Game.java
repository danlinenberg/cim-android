package com.tau.dtr.cim_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.MutableContextWrapper;
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
import java.util.Random;

import static com.tau.dtr.cim_application.MainActivity.sharedPreferences;
import static com.tau.dtr.cim_application.MultiplayerManager.mGoogleApiClient;
import static com.tau.dtr.cim_application.MultiplayerManager.mMyId;
import static com.tau.dtr.cim_application.MultiplayerManager.mMyRoom;
import static com.tau.dtr.cim_application.Utils.Utils.getNthDigit;
import static com.tau.dtr.cim_application.Utils.Utils.is_debug;
import static com.tau.dtr.cim_application.Utils.Utils.log;
import static com.tau.dtr.cim_application.Utils.Utils.returnRandom;
import static com.tau.dtr.cim_application.Utils.Utils.revertTile;

/**
 * Created by dan on 10/06/2017.
 */

public class Game extends Activity{

    public static Game mContext = new Game();
    static Boolean myTurn;
    static Boolean ShotsCaller;
    static Integer myTile;
    static Integer opponentTile;
    static Integer turnNumber;
    static Integer lastTurnPowerup;
    static int numpressed;
    static Context mCtx;
    static Integer bombs;
    static Integer hp;
    static Boolean powerup_confusion;
    static Boolean powerup_godmode;
    static ArrayList<Integer> bombs_location;
    static ArrayList<Integer> powerup_location;
    static boolean canPlaceBomb;
    static boolean bombIntent;
    static boolean invulnerable;
    static boolean confused;


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
        turnNumber = 1;
        lastTurnPowerup = 1;
        powerup_confusion = false;
        powerup_godmode = false;
        bombs_location = new ArrayList<>();
        powerup_location = new ArrayList<>();
        canPlaceBomb = true;
        invulnerable = false;
        confused = false;

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
                ShotsCaller = true;
                showTimedAlertDialog("Your turn!", "Click on spot you want to move your brick to", 5);
            }else{
                myTurn = false;
                ShotsCaller = false;
                showTimedAlertDialog("Your opponent's move", "Wait for your opponent to make a move", 5);
            }
        }catch (NullPointerException e){
            myTurn = false;
            ShotsCaller = false;
        }
    }

    public void Decipher(String message){
        try{
            int tile = Integer.parseInt(message);
            MoveTileOpponent(tile);

        }catch (NumberFormatException e){
            // bomb
            if(message.equals("win")){
                onWin();
                return;
            }
            if(message.equals("opponent_confusion")){
                confused = true;
                return;
            }
            String[] sep = message.split(" ");
            String tile = sep[1];
            if(sep[0].equals("bomb")){
                placeBomb(Integer.parseInt(tile), true);
            }
            if(sep[0].equals("bomb_remove")){
                bombs_location.remove(Integer.parseInt(tile));
                return;
            }
            if(sep[0].equals("powerup")){
                placePowerup(tile);
                return;
            }
            if(sep[0].equals("powerup_remove")){
                powerup_location.remove(Integer.parseInt(tile));
                return;
            }
        }
    }

    public void getTilePressed(View v){
        numpressed = numpressed + 1;
        if(numpressed>4){
            myTurn = true;
            numpressed = 0;
            showToast("Override: Your turn!");
        }
        if(myTurn || is_debug){
            if(!MultiplayerManager.getInstance().enoughTimeBetweenCommands()){
                showToast("Please wait a second before issueing another command");
                return;
            }
            int id = v.getId();
            String resource = v.getResources().getResourceEntryName(id);
            String resource_str = resource.replace("square_", "");
            int tile = Integer.parseInt(resource_str);

            if(bombIntent){
                if(tile!=myTile && tile!=opponentTile && !bombs_location.contains(tile) && !powerup_location.contains(tile)){
                    canPlaceBomb = false;
                    placeBomb(tile, false);
                    return;
                }
            }
            if((tile==myTile+1 || tile==myTile-1 || tile==myTile+10 || tile==myTile+11 || tile == myTile+9 || tile==myTile-10 || tile ==myTile-11 || tile == myTile-9) && myTile != opponentTile){
                MoveTileSelf(tile);
                checkBombs(tile);
                checkPowerups(tile);
            }else{
                showToast("Can't move there imbecile");
            }
        }else{
            showToast("Wait for your turn imbecile");
        }
    }

    public void checkBombs(final Integer tile){
        if(bombs_location.contains(tile)){
            if(invulnerable){
                showTimedAlertDialog("PHEW!", "Your GODMODE saved your life", 5);
                invulnerable = false;
                return;
            }
            invulnerable = false;
            Lejos.makeSound_Boom();
            hp = hp-1;
            DrawHP();
            bombs_location.remove(tile);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MultiplayerManager.getInstance().SendMessage("bomb_remove " + revertTile(tile).toString());
                }
            }, 1000);
            if(hp==0){
                onLose();
            }
            showTimedAlertDialog("PWNED!", "You just stepped on a mine", 3);
        }
    }

    public void checkPowerups(final Integer tile){
        if(powerup_location.contains(tile)){
            Integer randomPowerup = returnRandom(0,4);
            switch (randomPowerup){
                case(0):
                    showTimedAlertDialog("Powerup Picked!", "+HEALTH", 3);
                    if(hp<3){
                        hp = hp +1;
                        DrawHP();
                    }
                    Lejos.makeSound_Powerup_hp();
                    break;
                case(1):
                    showTimedAlertDialog("Powerup Picked!", "+BOMBS", 3);
                    if(bombs<3){
                        bombs = bombs+1;
                        DrawBombs();
                    }
                    Lejos.makeSound_Powerup_bomb();
                    break;
                case(2):
                    showTimedAlertDialog("Powerup Picked!", "CONFUSION", 3);
                    powerup_confusion = true;
                    ImageView img_confusion = findImageButton("confusion_container");
                    img_confusion.setImageDrawable(getResources().getDrawable(R.drawable.confusion));
                    Lejos.makeSound_Powerup_confusion();
                    break;
                case(3):
                    showTimedAlertDialog("Powerup Picked!", "GOD MODE", 3);
                    powerup_godmode = true;
                    ImageView img_god = findImageButton("godmode_container");
                    img_god.setImageDrawable(getResources().getDrawable(R.drawable.godmode));
                    Lejos.makeSound_Powerup_godmode();
                    break;
            }
            powerup_location.remove(tile);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MultiplayerManager.getInstance().SendMessage("powerup_remove " + tile.toString());
                }
            }, 1000);
        }
    }

    public void MoveTileSelf(Integer position){

        if(confused){
            //random tile for confusion
            Integer tile;
            do{
                Integer tile1 = returnRandom(getNthDigit(position,1)-1,getNthDigit(position,1)+2);
                Integer tile2 = returnRandom(getNthDigit(position,2)-1,getNthDigit(position,2)+2); // between 0 and 2
                tile = Integer.parseInt(tile1.toString() + tile2.toString());
            }while (tile==myTile || tile==opponentTile || getNthDigit(tile,1)<1 || getNthDigit(tile,1)>7 || getNthDigit(tile,2)<1 || getNthDigit(tile,2)>7);
            position = tile;
            showTimedAlertDialog("You are confused!", "moving at a random direction", 5);
            confused = false;
        }

        MultiplayerManager.getInstance().SendMessage(position.toString());

        if(!is_debug){
            HandleLejos(myTile, position);
        }

        ImageView player1 = findImageButton("square_"+position.toString());
        ImageView player1_old = findImageButton("square_"+myTile.toString());
        player1.setImageDrawable(getResources().getDrawable(R.drawable.tank_blue));
        player1_old.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
        myTile = position;
        myTurn = false;
        turnNumber = turnNumber + 1;
    }

    public void MoveTileOpponent(Integer position){

        Integer position_inverted = revertTile(position);
        String position_inverted_str = String.valueOf(position_inverted);

        ImageView player2 = findImageButton("square_"+position_inverted_str);
        ImageView player2_old = findImageButton("square_"+opponentTile.toString());
        player2.setImageDrawable(getResources().getDrawable(R.drawable.tank_red));
        player2_old.setImageDrawable(getResources().getDrawable(android.R.color.transparent));

        opponentTile = position_inverted;
        myTurn = true;
        canPlaceBomb = true;
        turnNumber = turnNumber + 1;

        if(ShotsCaller){
            placePowerup(null);
        }
    }

    public void placePowerup(String tileInput){
        if(tileInput!=null){
            ImageView tile_img = findImageButton("square_"+tileInput);
            tile_img.setImageDrawable(getResources().getDrawable(R.drawable.box));
            return;
        }

        if(turnNumber < lastTurnPowerup + 3 || powerup_location.size()>3){
            return;
        }

        //random tile for powerup
        Integer tile;
        do{
            Integer tile1 = returnRandom(1,8); // between 0 and 2
            Integer tile2 = returnRandom(1,8); // between 0 and 2
            tile = Integer.parseInt(tile1.toString() + tile2.toString());
        }while (tile==myTile || tile==opponentTile || bombs_location.contains(tile));

        ImageView tile_img = findImageButton("square_"+tile.toString());
        tile_img.setImageDrawable(getResources().getDrawable(R.drawable.box));

        MultiplayerManager.getInstance().SendMessage("powerup " + revertTile(tile));
        lastTurnPowerup = turnNumber;
        powerup_location.add(tile);
    }

    public void confusionIntent(View v){
        if(!MultiplayerManager.getInstance().enoughTimeBetweenCommands()){
            showToast("Please wait a second before issueing another command");
            return;
        }

        if(!myTurn){
            showToast("Wait for your turn imbecile");
            return;
        }
        if(powerup_confusion){
            showTimedAlertDialog("CONFUSION activated!", "Opponent will move randomley next round", 5);
            powerup_confusion = false;
            ImageView img = findImageButton("confusion_container");
            img.setImageDrawable(getResources().getDrawable(R.drawable.confusion_disabled));
            MultiplayerManager.getInstance().SendMessage("opponent_confusion");
        }
    }

    public void godmodeIntent(View v){
        if(!myTurn){
            showToast("Wait for your turn imbecile");
            return;
        }
        if(powerup_godmode){
            showTimedAlertDialog("GODMODE activated!", "Move freely without being hurt by mines", 5);
            powerup_godmode = false;
            ImageView img = findImageButton("godmode_container");
            img.setImageDrawable(getResources().getDrawable(R.drawable.godmode_disabled));
            invulnerable = true;
        }
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
        if(!MultiplayerManager.getInstance().enoughTimeBetweenCommands()){
            showToast("Please wait a second before issueing another command");
            return;
        }

        bombs_location.add(tile);
        if(enemy){
            return;
        }

        ImageView bombPlace = findImageButton("square_"+tile.toString());
        bombPlace.setImageDrawable(getResources().getDrawable(R.drawable.bomb));
        bombs = bombs-1;
        DrawBombs();
        bombIntent = false;
        canPlaceBomb = false;
        MultiplayerManager.getInstance().SendMessage("bomb " + revertTile(tile).toString());
    }

    public void HandleLejos(Integer old_position, Integer new_position){
        int compare = new_position-old_position;
        switch (compare){
            case(1):
                Lejos.Right();
                break;
            case(-1):
                Lejos.Left();
                break;
            case(-10):
                Lejos.Forward();
                break;
            case(10):
                Lejos.Back();
                break;
            case(-9):
                Lejos.ForwardRight();
                break;
            case(9):
                Lejos.BackLeft();
                break;
            case(-11):
                Lejos.ForwardLeft();
                break;
            case(11):
                Lejos.BackRight();
                break;
        }
    }


    public void onLose(){


        MultiplayerManager.getInstance().SendMessage("win");
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("YOU LOSE").setMessage("You're not very good at this, are you?");
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
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
        handler.postDelayed(runnable, 10*1000);
    }

    public void onWin(){

        Lejos.Win();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("YOU WIN!!!").setMessage("You're the best, around! nothing's ever gonna keep you down");
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
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
        handler.postDelayed(runnable, 10*1000);
    }

    public void DrawHP(){
        ImageView hpImg = (ImageView) findViewById(R.id.img_hp);
        switch (hp){
            case(3):
                hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_33));
                break;
            case(2):
                hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_23));
                break;
            case(1):
                hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_13));
                break;
            case(0):
                hpImg.setImageDrawable(getResources().getDrawable(R.drawable.hp_03));
                break;
        }
    }

    public void DrawBombs(){
        ImageView bomb_container = findImageButton("bomb_container");
        switch (bombs){
            case(3):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_3));
                break;
            case(2):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_2));
                break;
            case(1):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_1));
                break;
            case(0):
                bomb_container.setImageDrawable(getResources().getDrawable(R.drawable.bomb_0));
                break;
        }
    }

    public ImageView findImageButton(String id){
        int resID = getApplicationContext().getResources().getIdentifier(id, "id", getPackageName());
        ImageView img = (ImageView) findViewById(resID);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return img;
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


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public void showTimedAlertDialog(final String header, final String msg, final Integer seconds){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(Game.this).setTitle(header).setMessage(msg);
        final AlertDialog alert = dialog.create();
        alert.show();

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

    public static Game getInstance(){
        return mContext;
    }

}
