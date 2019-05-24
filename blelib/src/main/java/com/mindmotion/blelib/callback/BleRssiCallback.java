package com.mindmotion.blelib.callback;

import com.mindmotion.blelib.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);
}
