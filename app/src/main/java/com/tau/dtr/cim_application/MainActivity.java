package com.tau.dtr.cim_application;

import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import static com.tau.dtr.cim_application.Utils.log;

public class MainActivity extends AppCompatActivity implements MainInterface{

    public MainInterface mInterface;
    public static CognitoCachingCredentialsProvider credentialsProvider;
//    public static LambdaInterface lambdaInterface;
    public static LambdaInvokerFactory factory;
    static SharedPreferences sharedPreferences;
    private DynamoDBMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterface = this;
        startAWS();


        BluetoothManager.getInstance().StartQuery("Smarteyeglass", mInterface);
    }

    public void onBluetoothComplete(String device_name){
        log("ready to begin match for device " + device_name);
    }

    public void startAWS(){
        sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.SHARED_PREF), 0);
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
}
