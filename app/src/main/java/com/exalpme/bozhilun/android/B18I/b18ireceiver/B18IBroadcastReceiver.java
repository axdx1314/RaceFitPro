package com.exalpme.bozhilun.android.B18I.b18ireceiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.exalpme.bozhilun.android.B18I.evententity.B18iEventBus;
import com.exalpme.bozhilun.android.MyApp;
import com.exalpme.bozhilun.android.bleutil.MyCommandManager;
import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @aboutContent: 监听蓝牙状态的广播
 * @author： 安
 * @crateTime: 2017/9/12 10:17
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18IBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "--B18IBroadcastReceiver--";
    BluetoothAdapter bluetoothAdapter;

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, intent.getAction());
        String mylanya = (String) SharedPreferenceUtil.get(context, "mylanya", "");
        final String mylanmac = (String) SharedPreferenceUtil.get(context, "mylanmac", "");
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.e(TAG, blueState + "");
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON://13
                        Log.e(TAG, "onReceive----蓝牙状态开启-----STATE_TURNING_ON");
                        try {
                            boolean serviceRunning = W30SBLEServices.isServiceRunning(MyApp.getContext(), "com.suchengkeji.android.w30sblelibrary.W30SBLEServices");
                            Log.e(TAG, "-B18IBroadcastReceiver-STATE_TURNING_ON--" + serviceRunning);
                            if (!serviceRunning) {
                                MyApp.getmW30SBLEManage().openW30SBLEServices();
                            }
                            if (!WatchUtils.isEmpty(mylanya) && mylanya.equals("W30")) {
                                Log.e(TAG, "onReceive----蓝牙(MAC == W30)" + mylanya);
                                W30SBLEManage instance = W30SBLEManage.getInstance(context);
                                MyApp.setmW30SBLEManage(instance);
                                bluetoothAdapter = W30SBLEManage.getmBluetoothAdapter();
//                                MyApp.setmW30SBLEManage(W30SBLEManage.getInstance(context));
//                                instance.openW30SBLEServices();
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }

                        break;
                    case BluetoothAdapter.STATE_ON://13
                        Log.e(TAG, "onReceive----蓝牙开启-----STATE_ON");
                        try {
                            EventBus.getDefault().post(new B18iEventBus("STATE_ON"));
                            if (!WatchUtils.isEmpty(mylanya) && mylanya.equals("W30")) {
                                if (bluetoothAdapter != null) {
                                    bluetoothAdapter.startLeScan(leScanCallback);
                                }
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF://11
                        Log.e(TAG, "onReceive----本地蓝牙关闭-----STATE_TURNING_OFF");
//                        try {
//                            EventBus.getDefault().post(new B18iEventBus("STATE_TURNING_OFF"));
//                            if (!WatchUtils.isEmpty(mylanya) && mylanya.equals("W30")) {
//                                if ( W30SBLEManage.mW30SBLEServices!=null){
//                                    W30SBLEManage.mW30SBLEServices.close();
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.getMessage();
//                        }
                        //isStopScan = false;
                        break;
                    case BluetoothAdapter.STATE_OFF://10
                        Log.e(TAG, "onReceive----本地蓝牙是关闭着的-----STATE_OFF");
                        try {
                            EventBus.getDefault().post(new B18iEventBus("STATE_OFF"));
                            if (!WatchUtils.isEmpty(mylanya) && mylanya.equals("W30")) {
                                Log.e(TAG, "onReceive----" + mylanya);
                                MyCommandManager.DEVICENAME = null;
//                                if (W30SBLEManage.mW30SBLEServices != null) {
//                                    W30SBLEManage.mW30SBLEServices.close();
//                                }
                                //isStopScan = false;
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        break;
                }
                break;
        }
    }


    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String mylanmac = (String) SharedPreferenceUtil.get(MyApp.getContext(), "mylanmac", "");
            if (device.getAddress().equals(mylanmac)) {
                Log.e(TAG, "onReceive----mac_w30_mylanmac-----" + mylanmac);
                synchronized (this) {
                    Log.e(TAG, "-onReceive-onStart-connect-" + mylanmac);
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    W30SBLEManage.mW30SBLEServices.connect(mylanmac);
//                    if (!isStopScan){
//                        handler.sendEmptyMessageDelayed(0x02, 6*1000);
//                    }
                }

            }
        }
    };

//    boolean isStopScan = false;
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == 0x01){
//                isStopScan = true;
//                bluetoothAdapter.stopLeScan(leScanCallback);
//            }
//            return false;
//        }
//    });
}
