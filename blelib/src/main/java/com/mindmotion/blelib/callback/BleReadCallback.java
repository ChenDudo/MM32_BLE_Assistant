package com.mindmotion.blelib.callback;

import com.mindmotion.blelib.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback{

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);
}
