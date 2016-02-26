package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CardActivity extends Activity implements Handler.Callback {
    private static int DURATION = 1000;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessage(0);
    }

    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\n HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTime();
    }

    private void updateTime() {
        TextView txt = (TextView) findViewById(R.id.text);
        txt.setText(getDate());
    }

    @Override
    public boolean handleMessage(Message msg) {
        mHandler.sendEmptyMessageDelayed(0, DURATION);
        updateTime();
        return true;
    }
}