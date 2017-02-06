package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;

public class FirstActivity extends Activity implements WearableListView.ClickListener {
    private static final String TAG = "FirstActivity";
    // Sample dataset for the list
    private final String[] mElements = {"数据传输", "传感器", "地理位置", "健康数据", "天气", "手势", "语音识别",
            "语音合成", "语义", "搜索", "快捷卡片", "挠挠", "UI库Demo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);

        // Assign an adapter to the list
        listView.setAdapter(new ListAdapter(this, mElements));

        // Set a click listener
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        switch (tag) {
            case 0: {
                Intent startIntent = new Intent(this, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                Log.d(TAG, "MessageReceived! Open MainActivity");
                break;
            }
            case 1: {
                Intent sensorIntent = new Intent(this, SensorActivity.class);
                sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
                Log.d(TAG, "MessageReceived! Open Sensors!");
                break;
            }
            case 2: {
                Intent sensorIntent = new Intent(this, LocationActivity.class);
                sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
                Log.d(TAG, "MessageReceived! Open Location!");
                break;
            }
            case 3: {
                Intent stepIntent = new Intent(this, StepActivity.class);
                stepIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(stepIntent);
                Log.d(TAG, "MessageReceived! Open StepDemo!");
                break;
            }
            case 4: {
                Intent weatherIntent = new Intent(this, WeatherActivity.class);
                weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(weatherIntent);
                Log.d(TAG, "MessageReceived! Open WeatherDemo!");
                break;
            }
            case 5: {
                Intent gestureIntent = new Intent(this, GestureActivity.class);
                gestureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gestureIntent);
                Log.d(TAG, "MessageReceived! Open GestureDemo!");
                break;
            }
            case 6: {
                Intent voiceIntent = new Intent(this, VoiceInputActivity.class);
                voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(voiceIntent);
                Log.d(TAG, "MessageReceived! Open VoiceInputDemo!");
                break;
            }
            case 7: {
                Intent textIntent = new Intent(this, Text2VoiceActivity.class);
                textIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(textIntent);
                Log.d(TAG, "MessageReceived! Open Text2VoiceDemo!");
                break;
            }
            case 8: {
                Intent semanticIntent = new Intent(this, WarnActivity.class);
                semanticIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(semanticIntent);
                Log.d(TAG, "MessageReceived! Open SemanticActivity!");
                break;
            }
            case 9: {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(searchIntent);
                Log.d(TAG, "MessageReceived! Open SearchDemo!");
                break;
            }
            case 10: {
                Intent cardIntent = new Intent(this, CardActivity.class);
                cardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cardIntent);
                Log.d(TAG, "MessageReceived! Open CardDemo!");
                break;
            }
            case 11: {
                Intent naoIntent = new Intent(this, NaonaoActivity.class);
                naoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(naoIntent);
                Log.d(TAG, "MessageReceived! Open NaonaoDemo!");
                break;
            }
            case 12: {
                Intent uiIntent = new Intent(this, UiListActivity.class);
                uiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uiIntent);
                Log.d(TAG, "MessageReceived! Open UiListActivity!");
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
    public void onTopEmptyRegionClick() {
    }
}