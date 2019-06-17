package com.mindmotion.mm32blescanner.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mindmotion.mm32blescanner.R;
import com.mindmotion.mm32blescanner.adapter.DeviceAdapter;

public class BleDeviceFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    static DeviceAdapter mDeviceAdapter;

    public static BleDeviceFragment newInstance(DeviceAdapter deviceAdapter) {
        BleDeviceFragment bleDeviceFragment = new BleDeviceFragment();
        mDeviceAdapter = deviceAdapter;
        return bleDeviceFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_main, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });

        final ListView listView = root.findViewById(R.id.list_devices);
        listView.setAdapter(mDeviceAdapter);

        return root;
    }
}
