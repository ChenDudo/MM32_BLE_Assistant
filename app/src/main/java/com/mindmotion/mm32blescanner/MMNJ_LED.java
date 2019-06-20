package com.mindmotion.mm32blescanner;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mindmotion.blelib.callback.BleWriteCallback;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.blelib.BleManager;

import com.mindmotion.blelib.exception.BleException;
import com.mindmotion.mm32blescanner.comm.Observable;
import com.mindmotion.mm32blescanner.comm.ObserverManager;

public class MMNJ_LED extends AppCompatActivity{
    private static final String TAG = MMNJ_LED.class.getSimpleName();
    public static final String KEY_DATA = "key_data";

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;
    private int pwmSendata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_mmnj__led);

        initData();
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().clearCharacterCallback(bleDevice);

    }

    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button ledon = findViewById(R.id.bt_ledon);
        ledon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.i(TAG, "connect ok");
                    BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                            "00000002-fc0a-4c04-9df8-245fc68a5036",
                            hexToByteArray("0101"),
                            new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    Toast.makeText(MMNJ_LED.this, "打开 指令发送成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {
                                    Toast.makeText(MMNJ_LED.this, "打开 指令发送失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                }
            }
        });

        Button ledoff = findViewById(R.id.bt_ledoff);
        ledoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.i(TAG, "connect ok");
                    BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                            "00000002-fc0a-4c04-9df8-245fc68a5036",
                            hexToByteArray("0102"),
                            new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    Toast.makeText(MMNJ_LED.this, "关闭 指令发送成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {
                                    Toast.makeText(MMNJ_LED.this, "关闭 指令发送失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                }
            }
        });

        final SeekBar pwm = findViewById(R.id.pwmset);
        final TextView pwmValue = findViewById(R.id.textView2);
        pwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("Seekbar", "i: " + i);
                pwmValue.setText("PWM DutyCycle: "+ String.valueOf(i) + "%");
                pwmSendata = i * 10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button ledsend = findViewById(R.id.bt_send);
        ledsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.d("Ledsend", "ledsend: " + pwmCmdCir(pwmSendata));
                    BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                            "00000002-fc0a-4c04-9df8-245fc68a5036",
                            hexToByteArray(pwmCmdCir(pwmSendata)),
                            new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    Toast.makeText(MMNJ_LED.this, pwmSendata + " 发送成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {
                                    Toast.makeText(MMNJ_LED.this, pwmSendata + " 发送失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "如意外退出，请断开并刷新连接后可进入本页面", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null)
            finish();
        
    }

    private String pwmCmdCir(int i){
        if (i < 0xfff && i > 0xff) {
            return "020" + Integer.toHexString(i);
        } else if (i > 0xf) {
            return "0200" + Integer.toHexString(i);
        } else {
            return "02000" + Integer.toHexString(i);
        }
    }

}
