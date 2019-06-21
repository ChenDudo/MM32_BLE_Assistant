package com.mindmotion.mm32blescanner;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
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

public class MMNJ_LED extends AppCompatActivity{

    public static final String KEY_DATA = "key_data";
    private static final String TAG = MMNJ_LED.class.getSimpleName();
    private boolean LEDStatus = false;
    private BleDevice bleDevice;
    private int pwmDutyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmnj__led);

        initData();
        initView();

        boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
        if(isConnected)
            Log.i(TAG, "connect ok");
        else {
            Log.i(TAG, "connect ERROR");
            Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
            startActivity(intent);
        }

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
            hexlen ++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        }else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2){
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j ++;
        }
        return result;
    }

    private void sendOFFcmd(){
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray("0102"),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, "关闭 已发送！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, "关闭 发送失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        LEDStatus = false;
    }

    private void sendONcmd(){
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray("0101"),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, "打开 已发送！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, "打开 发送失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        LEDStatus = true;
    }

    private void sendPWMData(int i){
        String sData = "0102";

        Log.d("send data", "i: " + i);
        if(i <= 0){
            sendOFFcmd();
        }
        else {
            if (LEDStatus == false)
                sendONcmd();
            if (i <= 0xf) {
                sData = "020" + Integer.toHexString(i);
            } else if (i <= 0xff) {
                sData = "02" + Integer.toHexString(i);
            }
            Log.d("send data", "string: " + sData);
            Log.d("send data", "byte: " + hexToByteArray(sData));

            BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                    "00000002-fc0a-4c04-9df8-245fc68a5036",
                    hexToByteArray(sData),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Toast.makeText(MMNJ_LED.this, pwmDutyData + "% 发送", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Toast.makeText(MMNJ_LED.this, pwmDutyData + "% 未发送", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
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
                    sendONcmd();
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
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
                    sendOFFcmd();
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        final SeekBar pwm = findViewById(R.id.pwmset);
        final TextView pwmValue = findViewById(R.id.textView2);
        pwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Log.d("Seekbar", "i: " + i);
                pwmValue.setText("PWM DutyCycle: "+ String.valueOf(i) + "%");
                pwmDutyData = i;
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
                    sendPWMData(pwmDutyData);
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
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
        if (bleDevice == null){
            finish();
            Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
