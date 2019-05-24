package com.mindmotion.blelib.data;

public enum  BleScanState {

    STATE_IDLE(-1),
    STATE_SCANNING(0x01);

    private int code;

    BleScanState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
