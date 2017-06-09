package com.tau.dtr.cim_application;

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
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.List;

import static com.tau.dtr.cim_application.Utils.Utils.log;

/**
 * Created by dan on 08/06/2017.
 */

public class MultiplayerManager extends FragmentActivity implements ResultCallback, GoogleApiClient.OnConnectionFailedListener,RealTimeMessageReceivedListener, RoomUpdateListener {

    public static MultiplayerManager mContext = new MultiplayerManager();
    public static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResult(@NonNull Result result) {
        log("Res: " + result);
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {

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
                        log("Connecting to room");
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
                    }

                    @Override
                    public void onDisconnectedFromRoom(Room room) {
                        showToast("Disconnected");
                        log("Disconnected from room");
                    }

                    @Override
                    public void onPeersConnected(Room room, List<String> list) {

                    }

                    @Override
                    public void onPeersDisconnected(Room room, List<String> list) {

                    }

                    @Override
                    public void onP2PConnected(String s) {
                        showToast("P2P connected");
                        log("P2P Connected");
                    }

                    @Override
                    public void onP2PDisconnected(String s) {

                    }
                });
    }

    @Override
    public void onRoomCreated(int i, Room room) {
        log("Created room #"+i);
    }

    @Override
    public void onLeftRoom(int i, String s) {
        log("Left room #"+i);
    }

    @Override
    public void onJoinedRoom(int i, Room room) {
        log("Joined room  #" +i);
    }

    @Override
    public void onRoomConnected(int i, Room room) {
        log("Connected room #" + i);
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
