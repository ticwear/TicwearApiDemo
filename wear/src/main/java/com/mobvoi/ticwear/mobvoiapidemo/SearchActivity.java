package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.PendingResult;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.search.OneboxApi;
import com.mobvoi.android.search.OneboxRequest;
import com.mobvoi.android.search.OneboxTask;
import com.mobvoi.android.search.ParamItem;
import com.mobvoi.android.search.Search;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

public class SearchActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {

    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "SearchActivity";
    private Button mStartSearchBtn;
    private TextView mResultTv;
    private MobvoiApiClient mMobvoiApiClient;
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mStartSearchBtn = (Button) findViewById(R.id.search_button);
        mResultTv = (TextView) findViewById(R.id.search_result);
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Search.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mStartSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneboxRequest request = new OneboxRequest(OneboxTask.WEATHER);
                request.addParamItem(new ParamItem("location", "北京"));
                PendingResult<OneboxApi.OneboxResult> result = Search.OneboxApi.fetchOneboxResult(mMobvoiApiClient, request);
                result.setResultCallback(new ResultCallback<OneboxApi.OneboxResult>() {

                    @Override
                    public void onResult(OneboxApi.OneboxResult result) {
                        if (result.getStatus().isSuccess()) {
                            Log.d(TAG, result.getResponse().toString());
                            mText = result.getResponse().toString();
                            mResultTv.setText(mText);
                            sendMessagetoPhone(mText);
                        } else {
                            Log.d(TAG, "Fail to get the response");
                        }
                    }
                });
            }
        });
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

    private void sendMessagetoPhone(String result) {
        byte[] data = result.getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "/search_result", data).setResultCallback(
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