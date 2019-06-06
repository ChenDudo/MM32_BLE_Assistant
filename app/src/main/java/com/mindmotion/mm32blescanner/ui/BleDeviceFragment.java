package com.mindmotion.mm32blescanner.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mindmotion.mm32blescanner.R;
import com.mindmotion.mm32blescanner.adapter.DeviceAdapter;

public class BleDeviceFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "device_adapter";

    DeviceAdapter mDeviceAdapter;

    public static BleDeviceFragment newInstance(DeviceAdapter deviceAdapter) {
        BleDeviceFragment bleDeviceFragment = new BleDeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SECTION_NUMBER, deviceAdapter);
        bleDeviceFragment.setArguments(bundle);
        return bleDeviceFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDeviceAdapter = (DeviceAdapter) getArguments().getSerializable(ARG_SECTION_NUMBER);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_main, container, false);

        final ListView listView = root.findViewById(R.id.list_devices);
        listView.setAdapter(mDeviceAdapter);

        return root;
    }
}
