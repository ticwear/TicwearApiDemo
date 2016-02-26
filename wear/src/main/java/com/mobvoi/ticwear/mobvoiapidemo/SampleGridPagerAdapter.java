package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.List;

/**
 * Created by jiancui on 12/21/15.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private static final float MAXIMUM_CARD_EXPANSION_FACTOR = 3.0f;

    private Context mContext;
    private List<PickerActivity.DataList> mData;

    public SampleGridPagerAdapter(Context context, List<PickerActivity.DataList> quoteLists, FragmentManager fm) {
        super(fm);
        mContext = context;
        mData = quoteLists;
    }

    @Override
    public CardFragment getFragment(int row, int column) {
        PickerActivity.DataList quoteList = mData.get(row);
        CardFragment fragment = CardFragment.create(quoteList.getTitle(column), quoteList.getText(column));
        fragment.setCardGravity(Gravity.BOTTOM);
        fragment.setExpansionEnabled(true);
        fragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
        fragment.setExpansionFactor(MAXIMUM_CARD_EXPANSION_FACTOR);
        return fragment;
    }

    @Override
    public int getRowCount() {
        return mData.size();
    }

    @Override
    public int getColumnCount(int row) {
        return mData.get(row).getPageCount();
    }

    @Override
    public Drawable getBackgroundForRow(int row) {
        return mContext.getResources().getDrawable(mData.get(row).getImageResource(), null);
    }
}
