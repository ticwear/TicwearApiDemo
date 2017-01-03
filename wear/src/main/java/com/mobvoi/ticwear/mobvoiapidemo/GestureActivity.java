package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.gesture.GestureType;
import com.mobvoi.android.gesture.MobvoiGestureClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

public class GestureActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {

    private final static String TAG = "GestureActivity";
    private static final String DEFAULT_NODE = "default_node";
    private MobvoiGestureClient mMbvoiGestureClient;
    private TextView mGesTv;
    private Handler mHandler;
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGesTv = (TextView) findViewById(R.id.gesture);
        mHandler = new Handler();
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
        mMbvoiGestureClient = MobvoiGestureClient.getInstance(GestureType.GROUP_TURN_WRIST);
        mMbvoiGestureClient.register(this, new MobvoiGestureClient.IGestureDetectedCallback() {
            @Override
            public void onGestureDetected(final int type) {
                Log.d(TAG, "onGestureDetected type: " + type);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String s = "";
                        if (type == GestureType.TYPE_TWICE_TURN_WRIST) {
                            s = getString(R.string.twice);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGesTv.setText(getString(R.string.twice));
                                }
                            });
                            sendMessagetoPhone(s);
                        } else if (type == GestureType.TYPE_TURN_WRIST_UP) {
                            s = getString(R.string.up);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGesTv.setText(getString(R.string.up));
                                }
                            });
                            sendMessagetoPhone(s);
                        } else if (type == GestureType.TYPE_TURN_WRIST_DOWN) {
                            s = getString(R.string.down);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGesTv.setText(getString(R.string.down));
                                }
                            });
                            sendMessagetoPhone(s);
                        } else {
                            s = "未知";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGesTv.setText("未知");
                                }
                            });
                            sendMessagetoPhone(s);
                        }
                    }
                });
            }
        });
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
    public void onMessageReceived(MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConncted:");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected:");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMbvoiGestureClient.unregister(this);
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    private void sendMessagetoPhone(String gesture) {
        byte[] data = String.valueOf(gesture).getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "/gestures", data).setResultCallback(
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
}
