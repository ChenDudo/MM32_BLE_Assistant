package com.mindmotion.mm32blescanner.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mindmotion.blelib.BleManager;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.mm32blescanner.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<BleDevice> bleDeviceList;
    private int addDevicePosition = 0;
    private int connectedDeviceNum = 0;
    private int scanedDeviceNum = 0;

    public DeviceAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);

//        scanedDeviceNum++;
//
//        if (connectedDeviceNum != 0 && connectedDeviceNum != scanedDeviceNum)
//            addDevicePosition = connectedDeviceNum - 1;
//        if (connectedDeviceNum != 0 && connectedDeviceNum == scanedDeviceNum)
//            addDevicePosition = connectedDeviceNum;
//        else
//            addDevicePosition = 0;

        bleDeviceList.add(addDevicePosition, bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                scanedDeviceNum--;
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.img_blue = (ImageView) convertView.findViewById(R.id.device_img);
            holder.txt_name = (TextView) convertView.findViewById(R.id.device_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.device_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.device_rssi);
            holder.layout_idle = (LinearLayout) convertView.findViewById(R.id.layout_idle);
            holder.layout_connected = (LinearLayout) convertView.findViewById(R.id.layout_connected);
            holder.btn_disconnect = (Button) convertView.findViewById(R.id.disconnect_btn);
            holder.btn_connect = (Button) convertView.findViewById(R.id.connect_btn);
        }

        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            String mmBleMac = "ED:67:17";
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            boolean isMM32Mac = mac.substring(0,mmBleMac.length()).equals(mmBleMac);
            int rssi = bleDevice.getRssi();
            if (name == null)
                name = "N/A";
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (isConnected) {
                if (isMM32Mac)
                    holder.img_blue.setImageResource(R.mipmap.ic_launcher_round);
                else
                    holder.img_blue.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24px);
                holder.txt_name.setTextColor(0xFF0099ff);
                holder.txt_mac.setTextColor(0xFFff8000);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
            } else {
                if (isMM32Mac)
                    holder.img_blue.setImageResource(R.mipmap.ic_launcher);
                else
                    holder.img_blue.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24px);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
        }

        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        holder.btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        return convertView;
    }

    class ViewHolder implements Serializable{
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
        LinearLayout layout_connected;
        Button btn_disconnect;
        Button btn_connect;
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

    public void setConnectedDeviceNum(int connectedDeviceNum) {
        this.connectedDeviceNum = connectedDeviceNum;
    }

    public int getConnectedDeviceNum() {
        return connectedDeviceNum;
    }
}
