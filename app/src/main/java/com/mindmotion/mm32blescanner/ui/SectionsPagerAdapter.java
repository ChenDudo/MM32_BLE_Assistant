package com.mindmotion.mm32blescanner.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.mm32blescanner.R;
import com.mindmotion.mm32blescanner.adapter.DeviceAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    private DeviceAdapter mDeviceAdapter;
    private Map<String, BleDevice> mBleDeviceList ;
    private List<String> tab_title = new ArrayList<>();

    public SectionsPagerAdapter(DeviceAdapter deviceAdapter, Context context, FragmentManager fm) {
        super(fm);
        mDeviceAdapter = deviceAdapter;
        mContext = context;
        tab_title.add(mContext.getResources().getString(R.string.tab1_title));
        mBleDeviceList = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return BleDeviceFragment.newInstance(mDeviceAdapter);
        }

        if (position > 0) {
            return BleDeviceDetailFragment.newInstance(mBleDeviceList, tab_title.get(position));
        }

        return null;
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public Map<String, BleDevice> getBleDeviceList() {
        return mBleDeviceList;
    }

    public void addBleDevice(String name, BleDevice mBleDevice) {
        if (mBleDevice != null) {
            mBleDeviceList.put(name, mBleDevice);
        }
    }

    public List<String> getTab_title() {
        return tab_title;
    }

    public void appendTabTitle(String string) {
        tab_title.add(string);
    }

    public void deleteTabTitle(String string) {
        tab_title.remove(string);
    }
}
