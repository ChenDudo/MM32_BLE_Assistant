package com.mindmotion.mm32blescanner.comm;

import com.mindmotion.blelib.data.BleDevice;

public interface Observer {
    void disConnected(BleDevice bleDevice);
}
