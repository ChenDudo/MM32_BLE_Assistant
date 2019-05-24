package com.mindmotion.blelib.callback;

import com.mindmotion.blelib.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);
}
