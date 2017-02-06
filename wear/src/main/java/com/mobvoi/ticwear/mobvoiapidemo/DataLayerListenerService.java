package com.mobvoi.ticwear.mobvoiapidemo;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {

    public static final String COUNT_PATH = "/count";
    public static final String IMAGE_PATH = "/image";
    public static final String IMAGE_KEY = "photo";
    private static final String TAG = "DataLayer";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_SENSOR_PATH = "/start-sensor";
    private static final String START_LOCATION_PATH = "/start-location";
    private static final String START_WEATHER_PATH = "/start-weather";
    private static final String START_STEP_PATH = "/start-step";
    private static final String START_GESTURE = "/start-gesture";
    private static final String START_VOICE = "/start-voice";
    private static final String START_TEXT = "/start-text";
    private static final String SEND_JOKE = "/send-joke";
    private static final String START_SEARCH = "/start-search";
    private static final String START_CARD = "/start-card";
    private static final String START_NAO = "/start-nao";
    private static final String START_UI = "/start-ui";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    private static final String COUNT_KEY = "count";

    private MobvoiApiClient mMobvoiApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        ConnectionResult connectionResult = mMobvoiApiClient
                .blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            return;
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();
                // Send the rpc
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
        // Check to see if the message is to start an activity
        switch (messageEvent.getPath()) {
            case START_ACTIVITY_PATH: {
                Intent startIntent = new Intent(this, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                Log.d(TAG, "MessageReceived! Open MainActivity");
                break;
            }
            case START_SENSOR_PATH: {
                Intent sensorIntent = new Intent(this, SensorActivity.class);
                sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
                Log.d(TAG, "MessageReceived! Open Sensors!");
                break;
            }
            case START_LOCATION_PATH: {
                Intent sensorIntent = new Intent(this, LocationActivity.class);
                sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
                Log.d(TAG, "MessageReceived! Open Location!");
                break;
            }
            case START_STEP_PATH: {
                Intent stepIntent = new Intent(this, StepActivity.class);
                stepIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(stepIntent);
                Log.d(TAG, "MessageReceived! Open StepDemo!");
                break;
            }
            case START_WEATHER_PATH: {
                Intent weatherIntent = new Intent(this, WeatherActivity.class);
                weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(weatherIntent);
                Log.d(TAG, "MessageReceived! Open WeatherDemo!");
                break;
            }
            case START_GESTURE: {
                Intent gestureIntent = new Intent(this, GestureActivity.class);
                gestureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gestureIntent);
                Log.d(TAG, "MessageReceived! Open GestureDemo!");
                break;
            }
            case START_VOICE: {
                Intent voiceIntent = new Intent(this, VoiceInputActivity.class);
                voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(voiceIntent);
                Log.d(TAG, "MessageReceived! Open VoiceInputDemo!");
                break;
            }
            case START_TEXT: {
                Intent textIntent = new Intent(this, Text2VoiceActivity.class);
                textIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(textIntent);
                Log.d(TAG, "MessageReceived! Open Text2VoiceDemo!");
                break;
            }
            case SEND_JOKE: {
                break;
            }
            case START_SEARCH: {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(searchIntent);
                Log.d(TAG, "MessageReceived! Open SearchDemo!");
                break;
            }
            case START_CARD: {
                Intent cardIntent = new Intent(this, CardActivity.class);
                cardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cardIntent);
                Log.d(TAG, "MessageReceived! Open CardDemo!");
                break;
            }
            case START_NAO: {
                Intent naoIntent = new Intent(this, NaonaoActivity.class);
                naoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(naoIntent);
                Log.d(TAG, "MessageReceived! Open NaonaoDemo!");
                break;
            }
            case START_UI: {
                Intent uiIntent = new Intent(this, UiListActivity.class);
                uiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uiIntent);
                Log.d(TAG, "MessageReceived! Open UIList!");
                break;
            }
            default: {
                Intent startIntent = new Intent(this, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                Log.d(TAG, "MessageReceived! Open MainActivity");
                break;
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }

}
