/**
 * mobvoi_api_demo
 *
 * @author jiancui
 * @date 2015-12-8
 */
package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    /**
     * The collection of all samples in the app. This gets instantiated in {@link
     * #onCreate(android.os.Bundle)} because the {@link Sample} constructor needs access to {@link
     * android.content.res.Resources}.
     */
    private static Sample[] mSamples;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the list of samples.
        mSamples = new Sample[]{
                new Sample(R.string.title_1, DataTransferActivity.class),
                new Sample(R.string.title_2, SensorActivity.class),
                new Sample(R.string.title_3, GeoActivity.class),
                new Sample(R.string.title_4, StepActivity.class),
                new Sample(R.string.title_5, WeatherActivity.class),
                new Sample(R.string.title_6, GestureActivity.class),
                new Sample(R.string.title_7, VoiceInputActivity.class),
                new Sample(R.string.title_8, Text2VoiceActivity.class),
                new Sample(R.string.title_9, SemanticActivity.class),
                new Sample(R.string.title_10, SearchActivity.class),
                new Sample(R.string.title_11, CardActivity.class),
                new Sample(R.string.title_12, NaonaoActivity.class),
                new Sample(R.string.title_13, UiActivity.class),
        };

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new ArrayAdapter<Sample>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                mSamples));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(MainActivity.this, mSamples[position].activityClass));
    }

    /**
     * This class describes an individual sample (the sample title, and the activity class that
     * demonstrates this sample).
     */
    private class Sample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public Sample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }
}
