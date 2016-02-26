package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mobvoi.android.semantic.DatetimeTagValue;
import com.mobvoi.android.semantic.EntityTagValue;
import com.mobvoi.android.semantic.SemanticIntentApi;

public class SemanticActivity extends Activity {

    private static final String TAG = "SemanticActivity";
    private EntityTagValue mFrom;
    private EntityTagValue mTo;
    private DatetimeTagValue mTime;
    private TextView mInfoTv;
    private StringBuilder mInfo = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semantic);
        mInfoTv = (TextView) findViewById(R.id.semantic_text);
        mFrom = SemanticIntentApi.extractAsEntity(getIntent(), "from");
        mTo = SemanticIntentApi.extractAsEntity(getIntent(), "to");
        mTime = SemanticIntentApi.extractAsDatetime(getIntent(), "departure_date");
        if (mFrom != null) {
            Log.d(TAG, "Get from:" + mFrom.normData);
            mInfo.append("From ");
            mInfo.append(mFrom.normData);
        }

        if (mTo != null) {
            Log.d(TAG, "Get to:" + mTo.normData);
            mInfo.append(" to ");
            mInfo.append(mTo.normData);
        }

        if (mTime != null) {
            Log.d(TAG, "Get time:" + mTime.rawData);
            mInfo.append(" at time ");
            mInfo.append(mTime.rawData);
        }
        mInfoTv.setText(mInfo.toString());
    }
}
