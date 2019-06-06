package com.mindmotion.mm32blescanner.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.mm32blescanner.R;
import com.mindmotion.mm32blescanner.adapter.DeviceAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private DeviceAdapter mDeviceAdapter;
    private List<String> tab_title = new ArrayList<>();

    public SectionsPagerAdapter(DeviceAdapter deviceAdapter, Context context, FragmentManager fm) {
        super(fm);
        mDeviceAdapter= deviceAdapter;
        mContext = context;
        tab_title.add(mContext.getResources().getString(R.string.tab1_title));
        tab_title.add(mContext.getResources().getString(R.string.tab2_title));
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return BleDeviceFragment.newInstance(mDeviceAdapter);
        }

        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tab_title.get(position);
    }

    @Override
    public int getCount() {
        return tab_title.size();
    }

    public void appendTabTitle(String string) {
        tab_title.add(string);
    }

    public void deleteTabTitle(String string) {
        tab_title.remove(string);
    }
}
