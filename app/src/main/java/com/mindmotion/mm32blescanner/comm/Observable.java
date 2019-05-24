package com.mindmotion.mm32blescanner.comm;

import com.mindmotion.blelib.data.BleDevice;

public interface Observable {
    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);
}
