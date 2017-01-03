package com.mobvoi.ticwear.mobvoiapidemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.speech.SpeechRecognitionApi;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;

public class VoiceInputActivity extends SpeechRecognitionApi.SpeechRecogActivity implements
        MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "VoiceInputActivity";
    private Button mStartVoiceBtn;
    private TextView mVoiceTv;
    private MobvoiApiClient mMobvoiApiClient;
    private String mText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_input);
        mStartVoiceBtn = (Button) findViewById(R.id.test_button);
        mStartVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        mVoiceTv = (TextView) findViewById(R.id.speak_tip);
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onRecognitionSuccess(String text) {
        mVoiceTv.setText(text);
        mText = text;
    }


    @Override
    public void onRecognitionFailed() {
        mVoiceTv.setText("onRecognitionFailed");
        mText = "识别失败";
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected!");
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        if (mText != null) {
            sendMessagetoPhone(mText);
        }
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
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    private void sendMessagetoPhone(String voice) {
        byte[] data = voice.getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "/voice", data).setResultCallback(
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

