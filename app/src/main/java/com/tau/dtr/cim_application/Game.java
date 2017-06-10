package com.tau.dtr.cim_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

        PlacePieces();
    }

    public void PlacePieces(){
        log("Game started");
        ImageView player2 = (ImageView) findViewById(R.id.square_14);
        ImageView player1 = (ImageView) findViewById(R.id.square_74);

        player1.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
        player2.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));

    }

    public void Decipher(String message){

    }

//    public void onClick(View v){
//        MultiplayerManager.getInstance().SendMessage("test");
//    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public static Game getInstance(){
        return mContext;
    }
}
