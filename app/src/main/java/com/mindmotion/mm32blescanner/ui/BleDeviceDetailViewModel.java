package com.mindmotion.mm32blescanner.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.mindmotion.blelib.data.BleDevice;

public class BleDeviceDetailViewModel extends ViewModel {

    private MutableLiveData<String> mName, mMac = new MutableLiveData<>();
    private MutableLiveData<BleDevice> mBleDevice = new MutableLiveData<>();

    private LiveData<String> mText = Transformations.map(mMac, new Function<String, String>() {
        @Override
        public String apply(String input) {
            return "This device mac: " + input;
        }
    });

    public void setName(String name) {
        mName.setValue(name);
    }

    public void setMac(String mac) {
        mMac.setValue(mac);
    }

    public void setBleDevice(MutableLiveData<BleDevice> bleDevice) {
        mBleDevice = bleDevice;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<BleDevice> getBleDevice() {
        return mBleDevice;
    }
}
