package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;

public class UiListActivity extends Activity implements WearableListView.ClickListener {
    private static final String TAG = "UiListActivity";
    private final String[] mElements = {"BoxInsetLayout", "WatchViewStub", "CardFragment",
            "CirledImageView", "ConfirmationActivity", "DismissOverlayView", "GridViewPager"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_list);

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_uilist);

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
                Intent boxIntent = new Intent(this, BoxInsetActivity.class);
                boxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(boxIntent);
                Log.d(TAG, "MessageReceived! Open BoxInsetActivity");
                break;
            }
            case 1: {
                Intent watchStubIntent = new Intent(this, WatchStubActivity.class);
                watchStubIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(watchStubIntent);
                Log.d(TAG, "MessageReceived! Open watchStubIntent!");
                break;
            }
            case 2: {
                Intent cardIntent = new Intent(this, CardFrameActivity.class);
                cardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cardIntent);
                Log.d(TAG, "MessageReceived! Open CardFrameActivity!");
                break;
            }
            case 3: {
                Intent circleIntent = new Intent(this, CircledImageActivity.class);
                circleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(circleIntent);
                Log.d(TAG, "MessageReceived! Open CircledImageView!");
                break;
            }
            case 4: {
                Intent confirmIntent = new Intent(this, ConfirmActivity.class);
                confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(confirmIntent);
                Log.d(TAG, "MessageReceived! Open ConfirmationActivity!");
                break;
            }
            case 5: {
                Intent dismissIntent = new Intent(this, DissmissOverlayActivity.class);
                dismissIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dismissIntent);
                Log.d(TAG, "MessageReceived! Open DismissOverlayActivity!");
                break;
            }
            case 6: {
                Intent pickerIntent = new Intent(this, PickerActivity.class);
                pickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pickerIntent);
                Log.d(TAG, "MessageReceived! Open PickerActivity!");
                break;
            }
            default: {
                Intent startIntent = new Intent(this, BoxInsetActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                Log.d(TAG, "MessageReceived! Open BoxInsetActivity");
                break;
            }
        }
    }

    @Override
    public void onTopEmptyRegionClick() {
    }
}