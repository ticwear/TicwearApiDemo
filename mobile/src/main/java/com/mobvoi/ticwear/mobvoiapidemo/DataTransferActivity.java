package com.mobvoi.ticwear.mobvoiapidemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DataTransferActivity extends AppCompatActivity {

    /**
     * 页面list
     **/
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    /**
     * 页面title list
     **/
    private List<String> mTitleList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        ViewPager vp = (ViewPager) findViewById(R.id.pager);
        mFragmentList.add(new NodeFragment());
        mFragmentList.add(new MessageFragment());
        mFragmentList.add(new DataFragment());
        mTitleList.add("NodeApi");
        mTitleList.add("MessageApi");
        mTitleList.add("DataApi");
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragmentList, mTitleList));
    }

    /**
     * 定义适配器
     *
     * @author jiancui 20125-12-9
     */
    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;
        private List<String> mTitleList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.mFragmentList = fragmentList;
            this.mTitleList = titleList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (mFragmentList == null || mFragmentList.size() == 0) ? null : mFragmentList.get(arg0);
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return (mTitleList.size() > position) ? mTitleList.get(position) : "";
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }
    }
}