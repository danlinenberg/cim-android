package com.tau.dtr.cim_application;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tau.dtr.cim_application.Utils.Utils;

import java.io.IOException;

import static com.tau.dtr.cim_application.Utils.Utils.log;

public class MainActivity extends AppCompatActivity implements MainInterface{

    public static MainActivity mContext = new MainActivity();
    public MainInterface mInterface;
    public static CognitoCachingCredentialsProvider credentialsProvider;
//    public static LambdaInterface lambdaInterface;
    public static LambdaInvokerFactory factory;
    static SharedPreferences sharedPreferences;
    private DynamoDBMapper mapper;
    public EditText editText;
    public static GoogleApiClient mGoogleApiClient;

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

//        startAWS();
    }


    public void onBluetoothComplete(String device_name){
        if(device_name != null){
            log("ready to begin match for device " + device_name);
            showToast("Ready to begin with brick " + device_name);

            /**
             * Start match
             */
            Intent i = new Intent(getBaseContext(), MultiplayerManager.class);
            startActivity(i);
        }else{
            showToast("Cannot connect to brick");
        }
    }

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

    public void onButtonPressBluetooth(View v){
        if(Utils.is_debug){
            Intent i = new Intent(getBaseContext(), MultiplayerManager.class);
            startActivity(i);
            return;
        }
        String brick = editText.getText().toString();
        if(!brick.equals("Your Brick Name") && !brick.equals("")){
            android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("brick", brick);
            editor.commit();
            Intent i = new Intent(getBaseContext(), BluetoothController.class);
            startActivity(i);
            BluetoothController.getInstance().StartBluetoothQuery(brick, this);
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

    public static MainActivity getInstance(){
        return mContext;
    }

}
