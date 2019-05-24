package com.mindmotion.blelib.data;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BleDevice implements Parcelable {

    private BluetoothDevice device;
    private byte[] scanRecord;
    private int rssi;
    private long timestampNanos;

    public BleDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.timestampNanos = timestampNanos;
    }

    public BleDevice(Parcel source) {
        device = source.readParcelable(BluetoothDevice.class.getClassLoader());
        scanRecord = source.createByteArray();
        rssi = source.readInt();
        timestampNanos = source.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, flags);
        dest.writeByteArray(this.scanRecord);
        dest.writeInt(this.rssi);
        dest.writeLong(this.timestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    public String getName() {
        if (this.device != null)
            return this.device.getName();
        return null;
    }

    public String getMac() {
        if (this.device != null)
            return this.device.getAddress();
        return null;
    }

    public String getKey() {
        if (this.device != null)
            return this.device.getName() + this.device.getAddress();
        return "";
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimestampNanos() {
        return timestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.timestampNanos = timestampNanos;
    }
}
