package com.mindmotion.mm32blescanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mindmotion.blelib.BleManager;
import com.mindmotion.blelib.callback.BleGattCallback;
import com.mindmotion.blelib.callback.BleMtuChangedCallback;
import com.mindmotion.blelib.callback.BleRssiCallback;
import com.mindmotion.blelib.callback.BleScanCallback;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.blelib.exception.BleException;
import com.mindmotion.mm32blescanner.adapter.DeviceAdapter;
import com.mindmotion.mm32blescanner.comm.ObserverManager;
import com.mindmotion.mm32blescanner.ui.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    String ledName = "MMNJ LogoWall";
    private DeviceAdapter deviceAdapter;
    private ProgressDialog progressDialog;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewer();

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean scan_status = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (scan_status) {
            menu.findItem(R.id.action_stop).setVisible(false);
            menu.findItem(R.id.action_scan).setVisible(true);
            menu.findItem(R.id.action_scan_icon).setActionView(null);
        } else {
            menu.findItem(R.id.action_stop).setVisible(true);
            menu.findItem(R.id.action_scan).setVisible(false);
            menu.findItem(R.id.action_scan_icon).setActionView(R.layout.fragment_action_bar_scanning_icon);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_scan) {
            scan_start();
            return true;
        }

        if(id == R.id.action_stop) {
            scan_stop();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceAsColor")
    private void initViewer(){
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleText);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        progressDialog = new ProgressDialog(this);

        deviceAdapter = new DeviceAdapter(this);
        deviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);

                }
            }

            @Override
            public void onDisConnect(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }
        });

//        ListView listView_device = findViewById(R.id.list_devices);
//        listView_device.setAdapter(deviceAdapter);

        sectionsPagerAdapter = new SectionsPagerAdapter(deviceAdapter,this, getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }


    private void scan_start() {
        scan_status = false;
        checkPermissions();
        invalidateOptionsMenu();
    }

    private void scan_stop() {
        scan_status = true;
        invalidateOptionsMenu();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scanner) {
            // Handle the camera action
        } else if (id == R.id.nav_definitions) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_setings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                scan_stop();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanStarted(boolean success) {
                deviceAdapter.clearScanDevice();
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if(bleDevice.getName()==null) {
                    return;
                }
                //TODO select only MM32
//                else if(bleDevice.getName().equals(ledName)) {
//                    deviceAdapter.addDevice(bleDevice);
//                    deviceAdapter.notifyDataSetChanged();
//                }
                else{
                    deviceAdapter.addDevice(bleDevice);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            //Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 2);
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            permissionDeniedList.add(permission);
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                startScan();
                break;
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        deviceAdapter.clearConnectedDevice();
        for (BleDevice bleDevice : deviceList) {
            deviceAdapter.addDevice(bleDevice);
        }
        deviceAdapter.notifyDataSetChanged();
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.action_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                deviceAdapter.setConnectedDeviceNum(deviceAdapter.getConnectedDeviceNum() + 1);
                progressDialog.dismiss();
                deviceAdapter.addDevice(bleDevice);
                deviceAdapter.notifyDataSetChanged();
                sectionsPagerAdapter.addBleDevice(bleDevice.getName(), bleDevice);
                sectionsPagerAdapter.appendTabTitle(bleDevice.getName());
                viewPager.setAdapter(sectionsPagerAdapter);

                //　TODO: open LED setting


                if (bleDevice.getName().equals(ledName)) {

                    Intent intent = new Intent(MainActivity.this, MMNJ_LED.class);
                    intent.putExtra(MMNJ_LED.KEY_DATA, bleDevice);
                    startActivity(intent);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                deviceAdapter.removeDevice(bleDevice);

                List<String> pageList = sectionsPagerAdapter.getTab_title();
                int pageIdx = pageList.indexOf(bleDevice.getName());
                sectionsPagerAdapter.destroyItem(viewPager, pageIdx, sectionsPagerAdapter.getItem(pageIdx));
                sectionsPagerAdapter.deleteTabTitle(bleDevice.getName());
//                viewPager.setAdapter(sectionsPagerAdapter);
                sectionsPagerAdapter.notifyDataSetChanged();
                deviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(MainActivity.this, getString(R.string.action_disconnect), Toast.LENGTH_LONG).show();
                } else {
                    deviceAdapter.setConnectedDeviceNum(deviceAdapter.getConnectedDeviceNum() - 1);
                    Toast.makeText(MainActivity.this, getString(R.string.action_disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);

                }
            }
        });
    }

    private void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {
                Log.i(TAG, "onRssiFailure" + exception.toString());
            }

            @Override
            public void onRssiSuccess(int rssi) {
                Log.i(TAG, "onRssiSuccess: " + rssi);
            }
        });
    }

    private void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                Log.i(TAG, "onsetMTUFailure" + exception.toString());
            }

            @Override
            public void onMtuChanged(int mtu) {
                Log.i(TAG, "onMtuChanged: " + mtu);
            }
        });
    }
}
