package com.mindmotion.blelib.callback;

import com.mindmotion.blelib.data.BleDevice;

public interface BleScanPresenterImp {
    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);
}
