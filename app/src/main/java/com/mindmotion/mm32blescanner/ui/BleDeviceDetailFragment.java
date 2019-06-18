package com.mindmotion.mm32blescanner.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mindmotion.blelib.BleManager;
import com.mindmotion.blelib.data.AllGattCharacteristics;
import com.mindmotion.blelib.data.AllGattServices;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.mm32blescanner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BleDeviceDetailFragment extends Fragment {
    private static Map<String, BleDevice> mBleDeviceMap;
    private static String mName;

    private BleDeviceDetailViewModel bleDeviceDetailViewModel;

    public static BleDeviceDetailFragment newInstance(Map<String, BleDevice> bleDeviceMap, String name) {
        BleDeviceDetailFragment bleDeviceDetailFragment = new BleDeviceDetailFragment();
        mBleDeviceMap = bleDeviceMap;
        mName = name;
        return bleDeviceDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleDeviceDetailViewModel = ViewModelProviders.of(this).get(BleDeviceDetailViewModel.class);

        Log.d("bleDevice", "onCreate: mBleDevice.get(mName).getMac():" + mBleDeviceMap.get(mName).getMac());
        String mac = mBleDeviceMap.get(mName).getMac();
        Log.d("bleDevice", "onCreate: mac:" + mac);

        bleDeviceDetailViewModel.setMac(mac);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_details, container, false);

        List<String> servicesList = new ArrayList<>();
        List<String> charList = new ArrayList<>();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(mBleDeviceMap.get(mName));

        final ExpandableListView expandableListView = root.findViewById(R.id.list_details);
        expandableListView.setGroupIndicator(null);

        ResultAdapter resultAdapter = new ResultAdapter(getActivity());
        expandableListView.setAdapter(resultAdapter);

        for (BluetoothGattService service: gatt.getServices()) {
            resultAdapter.addServiceResult(service);
        }
//
//        for (BluetoothGattService service: gatt.getServices()) {
//            resultAdapter.addServiceResult(service);
//        }

        resultAdapter.notifyDataSetChanged();

        return root;
    }

    private class ResultAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<BluetoothGattService> bluetoothGattServices;
        private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
        private Map<String,List<BluetoothGattCharacteristic>> charMap;

        ResultAdapter(Context context) {
            Log.d("bleDevice", "添加适配器： ResultAdapter");
            this.context = context;
            bluetoothGattServices = new ArrayList<>();
            bluetoothGattCharacteristics = new ArrayList<>();
            charMap = new HashMap<>();
        }

        void addServiceResult(BluetoothGattService service) {
            Log.d("bleDevice", "添加服务： " + service);
            bluetoothGattServices.add(service);
        }

        void addCharResult(String name, List<BluetoothGattCharacteristic> charList) {
            Log.d("bleDevice", "添加特征值： " + name);
            charMap.put(name, charList);
        }

        void clearService() {
            bluetoothGattServices.clear();
        }
        void clearCharacteristic() { bluetoothGattCharacteristics.clear(); }

        @Override
        public int getGroupCount() {
            Log.d("bleDevice", "获取组计数： " + bluetoothGattServices.size());
            return bluetoothGattServices.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Log.d("bleDevice", "获取子组计数： " + bluetoothGattServices.get(groupPosition).getCharacteristics().size());
            return bluetoothGattServices.get(groupPosition).getCharacteristics().size();
        }

        @Override
        public BluetoothGattService getGroup(int groupPosition) {
            Log.d("bleDevice", "获取组： " + groupPosition);
            if (groupPosition > bluetoothGattServices.size())
                return null;
            return bluetoothGattServices.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (groupPosition > bluetoothGattServices.size())
                return null;

            return bluetoothGattServices.get(groupPosition).getCharacteristics().get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.detail_service_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.service_name = convertView.findViewById(R.id.detail_service_name);
                viewHolder.service_uuid = convertView.findViewById(R.id.detail_service_uuid);
                viewHolder.service_type = convertView.findViewById(R.id.detail_service_type);
            }

            BluetoothGattService service = bluetoothGattServices.get(groupPosition);
            String uuid = service.getUuid().toString();

            String serviceName = AllGattServices.lookup(uuid);
            viewHolder.service_name.setText(serviceName);
            viewHolder.service_uuid.setText(uuid);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewChildHolder viewChildHolder;

            if (convertView != null) {
                viewChildHolder = (ViewChildHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.detail_characteristics_item, null);
                viewChildHolder = new ViewChildHolder();
                convertView.setTag(viewChildHolder);
                viewChildHolder.char_name = convertView.findViewById(R.id.detail_char_name);
                viewChildHolder.char_uuid = convertView.findViewById(R.id.detail_char_uuid);
                viewChildHolder.char_prop= convertView.findViewById(R.id.detail_char_properties);
                viewChildHolder.char_value= convertView.findViewById(R.id.detail_char_value);
            }

            BluetoothGattCharacteristic characteristic = bluetoothGattServices.get(groupPosition).getCharacteristics().get(childPosition);

            String uuid = characteristic.getUuid().toString();
            String charName = AllGattCharacteristics.lookup(uuid);
            StringBuilder prop = new StringBuilder();
            int charaProp = characteristic.getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                prop.append("Read, ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                prop.append("Write, ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                prop.append("Write No Response, ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                prop.append("Notify, ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                prop.append("Indicate, ");
            }

            if (prop.length() > 1) {
                prop.delete(prop.length() - 2, prop.length() - 1);
            }

            if (prop.length() > 0) {
                viewChildHolder.char_prop.setText(prop);
                viewChildHolder.char_prop.setVisibility(View.VISIBLE);
            } else {
                viewChildHolder.char_prop.setVisibility(View.INVISIBLE);
            }

            viewChildHolder.char_name.setText(charName);
            viewChildHolder.char_uuid.setText(uuid);
//            viewChildHolder.char_value.setText(value);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        class ViewHolder {
            TextView service_name;
            TextView service_uuid;
            TextView service_type;
        }

        class ViewChildHolder {
            TextView char_name;
            TextView char_uuid;
            TextView char_prop;
            TextView char_value;
        }
    }
}
