package com.mobvoi.ticwear.mobvoiapidemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;


public class SensorActivity extends AppCompatActivity implements View.OnClickListener,
        MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener, NodeApi.NodeListener {

    private static final String TAG = "SensorActivity";
    private static final String START_ACTIVITY_PATH = "/start-sensor";

    private TextView mSensorXTv;
    private TextView mSensorYTv;
    private TextView mSensorZTv;
    private Button mStartButton;
    private MobvoiApiClient mMobvoiApiClient;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mSensorXTv = (TextView) findViewById(R.id.sensor_textx);
        mSensorYTv = (TextView) findViewById(R.id.sensor_texty);
        mSensorZTv = (TextView) findViewById(R.id.sensor_textz);
        mStartButton = (Button) findViewById(R.id.start_sensor);
        mStartButton.setOnClickListener(this);
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mHandler = new Handler();
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
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "Node Connected");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "Node Disconnected");
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getPath().equals("/accelerometer")) {
            byte[] data = event.getData();
            final String SensorMessage = new String(data);
            final String[] xyz = SensorMessage.split(",");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSensorXTv.setText(getString(R.string.sensor_x) + xyz[0]);
                    mSensorYTv.setText(getString(R.string.sensor_y) + xyz[1]);
                    mSensorZTv.setText(getString(R.string.sensor_z) + xyz[2]);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_sensor: {
                Log.d(TAG, "Button Clicked");
                new StartWearableActivityTask().execute();
            }
        }
    }

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.d(TAG, "Success");
                        }
                    }
                }
        );
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }
}
