package com.mindmotion.mm32blescanner;

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

import com.mindmotion.blelib.callback.BleNotifyCallback;
import com.mindmotion.blelib.callback.BleWriteCallback;
import com.mindmotion.blelib.data.BleDevice;
import com.mindmotion.blelib.BleManager;

import com.mindmotion.blelib.exception.BleException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MMNJ_LED extends AppCompatActivity{

    public static final String KEY_DATA = "key_data";
    private static final String TAG = MMNJ_LED.class.getSimpleName();
    private boolean LEDStatus = false;
    private boolean pwmTimStatus = false;
    private boolean autoModeFlag = false;
    private BleDevice bleDevice;
    private int pwmDutyData;
    private int pwmTimeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmnj__led);
        initData();

        final String SERVICE_UUID = "00006a00-0000-1000-8000-00805f9b34fb";
        final String NOTIFY_UUID = "00006a00-0000-1000-8000-00805f9b34fb";
        final String WRITE_UUID = "00006a00-0000-1000-8000-00805f9b34fb";
        final String NOTIFY_DESCRIPTOR  = "0";


        initView();
        tempReadData();

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


    char symbol=176;

    void tempReadData(){
        final TextView tempValue = findViewById(R.id.curTep_text1);
        BleManager.getInstance().notify(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000001-fc0a-4c04-9df8-245fc68a5036",
                true,
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        if (data[0] == 0x3) {
                            double tempD = data[1] + data[2] * 1.0 / 100;
                            tempValue.setText(String.valueOf(tempD)+" "+String.valueOf(symbol)+"C");
                        }
                    }
                });

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

        Button tempBt = findViewById(R.id.bt_temp);
        tempBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempReadData();
            }
        });

        final SeekBar pwm = findViewById(R.id.pwmset);
        final TextView pwmValue = findViewById(R.id.textView2);
        pwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Log.d("Seekbar", "i: " + i);
                pwmValue.setText("PWM Duty Cycle: "+ String.valueOf(i) + "%");
                pwmDutyData = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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


        //TODO: pwm LED
        Button pwmon = findViewById(R.id.bt_pwmon);
        pwmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.i(TAG, "connect ok");
                    sendPWMOnCmd();
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button pwmoff = findViewById(R.id.bt_pwmoff);
        pwmoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    Log.i(TAG, "connect ok");
                    sendPWMOffCmd();
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        final SeekBar pwmTim = findViewById(R.id.pwmTim);
        pwmTim.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Log.d("Seekbar", "i: " + i);
                pwmValue.setText("Breathing Duty Length: "+ String.valueOf(i) + "x");
                pwmTimeData = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    sendPWMTimData(pwmTimeData);
                }
                else {
                    Intent intent = new Intent(MMNJ_LED.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        Button pwmsend = findViewById(R.id.bt_PWM);
        pwmsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                if(isConnected){
                    sendPWMTimData(pwmTimeData);
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


        Button timecheck = findViewById(R.id.bt_time);
        timecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE");
                Date curDate = new Date(System.currentTimeMillis());
                String strTime = sd.format(curDate);
                pwmValue.setText(strTime);

                //TODO： send time format
                sd = new SimpleDateFormat("yyyyMMddHHmmss");
                curDate = new Date(System.currentTimeMillis());
                strTime = sd.format(curDate);

                Calendar calendar = Calendar.getInstance();//日历对象
                int week = calendar.get(Calendar.DAY_OF_WEEK);

                //strTime = strTime + Integer.toString(week);

                switch(week){
                    case 1:
                        strTime = strTime + "01";
                        break;
                    case 2:
                        strTime = strTime + "02";
                        break;
                    case 3:
                        strTime = strTime + "03";
                        break;
                    case 4:
                        strTime = strTime + "04";
                        break;
                    case 5:
                        strTime = strTime + "05";
                        break;
                    case 6:
                        strTime = strTime + "06";
                        break;
                    case 7:
                        strTime = strTime + "07";
                        break;
                }
                Log.d(TAG, "onClick: timeValue: " + strTime);

                sendTimeData(strTime);
//                String weekStr = "";
//                switch (week) {
//                    case 1:
//                        weekStr = "周日";
//                        break;
//                    case 2:
//                        weekStr = "周一";
//                        break;
//                    case 3:
//                        weekStr = "周二";
//                        break;
//                    case 4:
//                        weekStr = "周三";
//                        break;
//                    case 5:
//                        weekStr = "周四";
//                        break;
//                    case 6:
//                        weekStr = "周五";
//                        break;
//                    case 7:
//                        weekStr = "周六";
//                        break;
//                    default:
//                        break;
//                }

            }
        });

        //todo：切记此处是反逻辑
        Button autoMode = findViewById(R.id.bt_timmode);
        autoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(autoModeFlag) {
                    sendAutoCmd(1); //close
                }
                else {
                    sendAutoCmd(0); //open
                }
            }
        });

    }

    private void sendAutoCmd(int i) {
        String sAutoDat;
        if(1 == i){
            sAutoDat = "0701";      //close
            autoModeFlag = false;
        }
        else {
            sAutoDat = "0702";      //open
            autoModeFlag = true;
        }
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray(sAutoDat),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, autoModeFlag + " 发送", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, autoModeFlag + " 未发送", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void sendTimeData(final String strTime) {
        String sTimeDat;
        sTimeDat = "06" + strTime;
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray(sTimeDat),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, strTime + " 发送", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, strTime + " 未发送", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }



    private void sendPWMOnCmd() {
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray("0401"),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, "呼吸模式 开启！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, "呼吸灯控制 发送失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        pwmTimStatus = true;
    }

    private void sendPWMOffCmd() {
        BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                "00000002-fc0a-4c04-9df8-245fc68a5036",
                hexToByteArray("0402"),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Toast.makeText(MMNJ_LED.this, "呼吸模式 关闭！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(MMNJ_LED.this, "呼吸灯控制 发送失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        pwmTimStatus = false;
    }

    private void sendPWMTimData(int i){
        String sData = "0402";

        Log.d("send data", "i: " + i);
        if(i <= 0){
            sendPWMOffCmd();
        }
        else {
            if (pwmTimStatus == false)
                sendPWMOnCmd();
            if (i <= 0xf) {
                sData = "050" + Integer.toHexString(i);
            } else if (i <= 0xff) {
                sData = "05" + Integer.toHexString(i);
            }
            Log.d("send data", "string: " + sData);
            Log.d("send data", "byte: " + hexToByteArray(sData));

            BleManager.getInstance().write(bleDevice, "00000000-fc0a-4c04-9df8-245fc68a5036",
                    "00000002-fc0a-4c04-9df8-245fc68a5036",
                    hexToByteArray(sData),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Toast.makeText(MMNJ_LED.this, "时长 " + pwmTimeData + " 发送", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Toast.makeText(MMNJ_LED.this, "时长 " + pwmTimeData + " 未发送", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
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
