package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

public class NaonaoActivity extends Activity implements
        MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener,
        NodeApi.NodeListener, MessageApi.MessageListener {

    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "NaonaoActivity";
    private TextView mNaoTv;
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naonao);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mNaoTv = (TextView) findViewById(R.id.nao_nao);
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
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    public boolean onLongPressSidePanel(MotionEvent e) {
        Log.d(TAG, "onLongPressSidePanel");
        mNaoTv.setText(getString(R.string.long_pressed));
        sendMessagetoPhone(getString(R.string.long_pressed));
        return true;
    }

    public boolean onScrollSidePanel(MotionEvent e1, MotionEvent e2, float distanceX,
                                     float distanceY) {
        Log.d(TAG, "onScrollSidePanel " + distanceY);
        mNaoTv.setText(getString(R.string.scroll));
        sendMessagetoPhone(getString(R.string.scroll));
        return true;
    }

    public boolean onFlingSidePanel(MotionEvent e1, MotionEvent e2, float velocityX,
                                    float velocityY) {
        Log.d(TAG, "onFlingSidePanel " + velocityY);
        mNaoTv.setText(getString(R.string.fling));
        sendMessagetoPhone(getString(R.string.fling));
        return true;
    }

    public boolean onDoubleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onDoubleTapSidePanel");
        mNaoTv.setText(getString(R.string.double_tap));
        sendMessagetoPhone(getString(R.string.double_tap));
        return true;
    }

    public boolean onSingleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onSingleTapSidePanel");
        mNaoTv.setText(getString(R.string.single_tap));
        sendMessagetoPhone(getString(R.string.single_tap));
        return true;
    }

    private void sendMessagetoPhone(String naonao) {
        byte[] data = naonao.getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "/naonao", data).setResultCallback(
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

