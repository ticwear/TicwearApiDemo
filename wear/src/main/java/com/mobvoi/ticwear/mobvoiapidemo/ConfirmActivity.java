package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;

public class ConfirmActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener {

    private DelayedConfirmationView mDelayedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        mDelayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        mDelayedView.setListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDelayedView.setTotalTimeMs(3000);
        mDelayedView.start();
    }

    @Override
    public void onTimerFinished(View view) {
        // User didn't cancel, perform the action
        Intent intent = new Intent(this, FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onTimerSelected(View view) {
        // User canceled, abort the action
        mDelayedView.reset();
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                getString(R.string.msg_fail));
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDelayedView.reset();
    }
}
