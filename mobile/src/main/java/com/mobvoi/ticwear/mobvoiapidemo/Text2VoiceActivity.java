package com.mobvoi.ticwear.mobvoiapidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.speech.synthesizer.DefaultSpeechSynthesizerCallback;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerApi;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerCallbackInterface;
import com.mobvoi.android.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class Text2VoiceActivity extends AppCompatActivity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener {

    private static final String TAG = "Text2VoiceActivity";
    private static final String DEFAULT_NODE = "default_node";
    private static final String START_TEXT = "/start-text";
    private static final String SEND_JOKE = "/send-joke";
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static int mCount = 0;
    private List<String> mList;
    private boolean mResolvingError = false;
    private MobvoiApiClient mMobvoiApiClient;
    private TextView mTTSTv;
    private Button mStartActivity;
    private Button mPhonePlay;
    private Button mWearPlay;
    private Button mNextPlay;
    private String mCurJoke;

    private SpeechSynthesizerCallbackInterface mTTSCallback = new DefaultSpeechSynthesizerCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text2_voice);
        bindViews();
        init();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String joke;
                try {
                    joke = mList.get(mCount);
                    mCount = (mCount + 1) % 6;
                    mTTSTv.setText(joke);
                    mCurJoke = joke;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void init() {
        mList = new ArrayList<>();
        mList.add("蜈蚣被蛇咬了，为防毒液扩散必须截肢！蜈蚣想：幸亏偶腿多~！！ 大夫安慰道：兄弟，想开点，你以后就是蚯蚓了~ ");
        mList.add("一农户明天杀鸡，晚上喂鸡时说：快吃吧，这是你最后一顿！ 第二日见鸡已躺倒并留遗书：爷已吃老鼠药，你们也别想吃爷，爷他妈也不是好惹的~！");
        mList.add("鱼说：我时时刻刻睁开眼睛，就是为了能让你永远在我眼中~ 水说：我时时刻刻流淌不息，就是为了能永远把你拥抱~~ 锅说：都他妈快熟了，还这么贫！！");
        mList.add("跟我妈说了，我喜欢你，我要让你去我家，日日夜夜陪伴我，知道吗？通过这些日子的交往，我发现我已经不能没有你，可我妈不肯，她说：家里不准养猪！ ");
        mList.add("大象把粪便排在了路中央，一只蚂蚁正好路过，它抬头望了望那云雾缭绕的顶峰，不禁感叹道：呀啦唆，这就是青藏高原~~");
        mList.add("你都老大不小了，有些事该让你知道了！天，是用来刮风的；地，是用来长草的；我，是用来证明人类伟大的；你，就是用来炖粉条的~~");
    }

    private void bindViews() {
        mTTSTv = (TextView) findViewById(R.id.tts);
        mStartActivity = (Button) findViewById(R.id.start_text2voice);
        mPhonePlay = (Button) findViewById(R.id.phone_play);
        mWearPlay = (Button) findViewById(R.id.wear_play);
        mNextPlay = (Button) findViewById(R.id.next_play);
        mNextPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String joke = mList.get(mCount);
                        mCount = (mCount + 1) % 6;
                        mTTSTv.setText(joke);
                        mCurJoke = joke;
                    }
                });
            }
        });

        mWearPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, DEFAULT_NODE, SEND_JOKE,
                        mCurJoke.getBytes());
            }
        });

        mPhonePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechSynthesizerApi.startSynthesizer(getApplicationContext(), mTTSCallback,
                        mCurJoke, 5000);
            }
        });

        mStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, DEFAULT_NODE, START_TEXT,
                        new byte[0]);
            }
        });
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            mMobvoiApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (Exception e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        } else {
            mResolvingError = false;
        }
    }

}
