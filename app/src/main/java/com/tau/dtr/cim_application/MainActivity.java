package com.tau.dtr.cim_application;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.tau.dtr.cim_application.Utils.Utils;
import static com.tau.dtr.cim_application.Utils.Utils.is_debug;
import static com.tau.dtr.cim_application.Utils.Utils.log;

public class MainActivity extends AppCompatActivity implements MainInterface{

    public static MainActivity mContext = new MainActivity();
    public static MainInterface mInterface;
    public static CognitoCachingCredentialsProvider credentialsProvider;
    public static LambdaInvokerFactory factory;
    private DynamoDBMapper mapper;
    public EditText editText;
    static SharedPreferences sharedPreferences;

    /**
     * Starts the main activity of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterface = this;
        mContext = this;
        sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.SHARED_PREF), 0);
        editText= (EditText) findViewById(R.id.enter_name_input);
        if (sharedPreferences.getString("brick", null) != null) {
            editText.setText(sharedPreferences.getString("brick", null));
        }

        /**
         * Initiate bluetooth service (this happens before actively searching for the brick)
         */
        BluetoothController.getInstance().instantiateBluetooth(this);
    }


    /**
     * Callback returned from our interface - starting the match
     */
    public void onBluetoothComplete(String device_name){
        if(device_name != null){
            log("ready to begin match for device " + device_name);
            showToast("Ready to begin with brick " + device_name);

            //Start the multiplayer activity and create a room for the match
            Intent i = new Intent(getBaseContext(), MultiplayerManager.class);
            startActivity(i);
        }else{
            showToast("Cannot connect to brick");
        }
    }

    /**
     * Set credentials for Amazon Database (was eventually not used in this project, but can be used to store highscores etc)
     */
    public void startAWS(){
        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                getResources().getString(R.string.TABLE_ID), // Federated Identity Pool ID
                Regions.EU_WEST_1 // Region
        );
        factory = new LambdaInvokerFactory(
                getApplicationContext(),
                Regions.EU_WEST_1,
                credentialsProvider);
//        lambdaInterface = factory.build(LambdaInterface.class);
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        mapper = new DynamoDBMapper(ddbClient);
    }

    /***
     * handle button press - get the brick name from the user, and start looking for bluetooth devices
     * @param is_debug - allows for the game to run without using bricks
     */
    public void onButtonPressBluetooth(View v){
        String brick = editText.getText().toString();
        saveBrickName(brick);

        if(Utils.is_debug){
            Intent i = new Intent(getBaseContext(), MultiplayerManager.class);
            startActivity(i);
            return;
        }

        BluetoothController.getInstance().StartBluetoothQuery(brick, this);
    }

    /**
     * Sets debug mode (game without bricks)
     */
    public void setDebug(View v){
        if(is_debug){
            is_debug = false;
            showToast("Debug mode off");
        }else{
            is_debug = true;
            showToast("Debug mode on");
        }
    }

    /**
     * @param brick
     * saves the brick name to our persistant data
     */
    public void saveBrickName(String brick) {
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("brick", brick);
        editor.commit();
    }

    /**
     * @return instance of the class
     */
    public static MainActivity getInstance(){
        return mContext;
    }


    /**
     * shows a UI toast
     * @param txt
     */
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
