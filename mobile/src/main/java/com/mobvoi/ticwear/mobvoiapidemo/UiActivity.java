package com.mobvoi.ticwear.mobvoiapidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;

public class UiActivity extends AppCompatActivity implements MobvoiApiClient.OnConnectionFailedListener,
        MobvoiApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final String START_ACTIVITY_PATH = "/start-ui";
    private static final String DEFAULT_NODE = "default_node";

    private Button mStartUiBtn;
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        mStartUiBtn = (Button) findViewById(R.id.start_ui);
        mStartUiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wearable.MessageApi.sendMessage(
                        mMobvoiApiClient, DEFAULT_NODE, START_ACTIVITY_PATH, new byte[0])
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    }
                                }
                        );
            }
        });

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onMessageReceived(MessageEvent event) {

    }
}
