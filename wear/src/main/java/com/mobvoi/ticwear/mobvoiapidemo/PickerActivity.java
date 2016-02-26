package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends Activity {

    private List<DataList> mDataLists = new ArrayList<DataList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        setupData();
        setupGridViewPager();
    }

    private void setupData() {
        setupLine1();
        setupLine2();
        setupLine3();
        setupLine4();
        setupLine5();
    }

    private void setupLine1() {
        List<String> title = new ArrayList<String>();
        title.add("Superman");
        title.add("Add one more row");
        mDataLists.add(new DataList("Superman", R.mipmap.superman, title));
    }

    private void setupLine2() {
        List<String> title = new ArrayList<String>();
        title.add("Batman");
        mDataLists.add(new DataList("Batman", R.mipmap.batman, title));
    }

    private void setupLine3() {
        List<String> title = new ArrayList<String>();
        title.add("Flashman");
        mDataLists.add(new DataList("Flashman", R.mipmap.flash, title));
    }

    private void setupLine4() {
        List<String> title = new ArrayList<String>();
        title.add("Green Lantern");
        mDataLists.add(new DataList("Green Lantern", R.mipmap.green, title));
    }

    private void setupLine5() {
        List<String> title = new ArrayList<String>();
        title.add("All");
        mDataLists.add(new DataList("All", R.mipmap.all, title));
    }

    private void setupGridViewPager() {
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new SampleGridPagerAdapter(this, mDataLists, getFragmentManager()));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.indicator);
        dotsPageIndicator.setPager(pager);
    }

    public static class DataList {
        private int mImageResource;
        private List<String> mTitle;
        private String mName;

        public DataList(String name, int imageResource, List<String> titles) {
            mName = name;
            mImageResource = imageResource;
            mTitle = titles;
        }

        public String getTitle(int page) {
            // Only the first page has a title
            if (page == 0) {
                return mName;
            } else {
                return null;
            }
        }

        public String getText(int page) {
            // First has no text
            if (page == 0) {
                return null;
            } else {
                return mTitle.get(page - 1);
            }
        }

        public int getPageCount() {
            return mTitle.size() + 1;
        }

        public int getImageResource() {
            return mImageResource;
        }
    }
}

