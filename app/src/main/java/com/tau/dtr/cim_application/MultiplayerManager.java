package com.tau.dtr.cim_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 08/06/2017.
 */

public class MultiplayerManager extends FragmentActivity implements ResultCallback, GoogleApiClient.OnConnectionFailedListener,RealTimeMessageReceivedListener, RoomUpdateListener {

    public static MultiplayerManager mContext = new MultiplayerManager();
    public static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    final static int RC_WAITING_ROOM = 10002;
    final static int MIN_PLAYERS = 2;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    public static String mMyId;
    public static Room mMyRoom;

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        try{
            byte[] b = realTimeMessage.getMessageData();
            String msg = new String(b, "UTF-8");
            log("Message received: " + msg);
            Game.getInstance().Decipher(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SendMessage(String msg) {
        try{
            byte[] message = msg.getBytes("UTF-8");
            Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(mGoogleApiClient, message, mMyRoom.getRoomId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate);
        mContext = this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks( new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                            log("Connected to gamecenter");
                            StartGame();
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }
                    })
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("Connection Failed");
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if (!BaseGameUtils.resolveConnectionFailure(mContext,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, R.string.sign_in_other_error)) {
                mResolvingConnectionFailure = false;
            }
        }

        log("Need to login");
    }

    boolean shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    public void StartGame(){
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /**
         * Go to game screen
         */
    }

    public void OnButtonLogin(View v){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        /**
         * Sign in
         */
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                log("Logged in");
                mGoogleApiClient.connect();
            } else {
                log("trouble logging in");
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.sign_in_failed);
            }
        }
        /**
         * Waiting room
         */
        if (requestCode == RC_WAITING_ROOM) {
            if (resultCode == Activity.RESULT_OK) {
                // (start game)
                log("Ready to start game");
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mMyRoom.getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mMyRoom.getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mGoogleApiClient.disconnect();
//    }

    @Override
    public void onResult(@NonNull Result result) {
        log("Res: " + result);
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(new RoomStatusUpdateListener() {
                    @Override
                    public void onRoomConnecting(Room room) {
                        log("Connecting to room");
                        showToast("Connecting to room");
                    }

                    @Override
                    public void onRoomAutoMatching(Room room) {
                        log("Automatching...");
                    }

                    @Override
                    public void onPeerInvitedToRoom(Room room, List<String> list) {

                    }

                    @Override
                    public void onPeerDeclined(Room room, List<String> list) {

                    }

                    @Override
                    public void onPeerJoined(Room room, List<String> list) {
                        log("Peer joined");
                    }

                    @Override
                    public void onPeerLeft(Room room, List<String> list) {

                    }

                    @Override
                    public void onConnectedToRoom(Room room) {
                        showToast("Connected to room");
                        log("Connected to room");
                        Boolean shouldStart = shouldStartGame(room);
                        if(shouldStart){
                            log("All connected, starting game");
                            Intent intent = new Intent(mContext, Game.class);
                            //pick starter
                            String firstId = getFirstPlayer(room);
                            intent.putExtra(getResources().getString(R.string.game_player_starter), firstId);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onDisconnectedFromRoom(Room room) {
                        showToast("Disconnected");
                        log("Disconnected from room");
                    }

                    @Override
                    public void onPeersConnected(Room room, List<String> list) {
                        log("Peer connected");
                    }

                    @Override
                    public void onPeersDisconnected(Room room, List<String> list) {

                    }

                    @Override
                    public void onP2PConnected(String s) {
                        log("P2P Connected");
                    }

                    @Override
                    public void onP2PDisconnected(String s) {

                    }
                });
    }

    @Override
    public void onRoomCreated(int i, Room room) {

        try{
            String roomId = room.getRoomId();
            String id = room.getCreatorId();
            log("Created room #"+ roomId);
            mMyRoom = room;
            mMyId = id;

            Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
            startActivityForResult(intent, RC_WAITING_ROOM);
        }catch (Exception e){
            showToast("Cannot join room. Check your internet connection");
        }
    }

    @Override
    public void onLeftRoom(int i, String s) {
    }

    @Override
    public void onJoinedRoom(int i, Room room) {
        String roomId = room.getRoomId();
        ArrayList<Participant> participants = room.getParticipants();
        log("Joined room #"+ roomId);
        mMyRoom = room;
        for (Participant p : participants) {
            if (!p.getParticipantId().equals(room.getCreatorId())) {
                mMyId = p.getParticipantId();
            }
        }
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
        startActivityForResult(intent, RC_WAITING_ROOM);
    }

    @Override
    public void onRoomConnected(int i, Room room) {
    }

    /**
     * determines the player who has the first move by checking the bytes of their username. The player with more bytes that are <50 gets to start
     * @param room
     * @return id of the player who has the first move
     */
    public String getFirstPlayer(Room room){
        ArrayList<Participant> participants = room.getParticipants();
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for(Participant p : participants){
            try{
                Integer count = 0;
                for(byte b: p.getDisplayName().toString().getBytes("UTF-8")){
                    if(b<50){
                        count=count+1;
                    }
                }
                counts.put(p.getParticipantId(), count);
            }catch (UnsupportedEncodingException e){}
        }
        String firstId = "";
        Integer firstCount = 0;
        Iterator it = counts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Integer) pair.getValue() > firstCount){
                firstCount = (Integer) pair.getValue();
                firstId = pair.getKey().toString();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return firstId;
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

    public static MultiplayerManager getInstance(){
        return mContext;
    }

}
