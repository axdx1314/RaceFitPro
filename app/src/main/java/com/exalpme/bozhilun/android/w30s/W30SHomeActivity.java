package com.exalpme.bozhilun.android.w30s;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.exalpme.bozhilun.android.MyApp;
import com.exalpme.bozhilun.android.bleutil.MyCommandManager;
import com.exalpme.bozhilun.android.siswatch.utils.PhoneStateListenerInterface;
import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.exalpme.bozhilun.android.w30s.utils.NationalistinctionDUtils;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.adpter.FragmentAdapter;
import com.exalpme.bozhilun.android.siswatch.WatchBaseActivity;
import com.exalpme.bozhilun.android.siswatch.data.W30sNewsH9DataFragment;
import com.exalpme.bozhilun.android.siswatch.run.W30sNewRunFragment;
import com.exalpme.bozhilun.android.siswatch.utils.UpdateManager;
import com.exalpme.bozhilun.android.util.URLs;
import com.exalpme.bozhilun.android.w30s.fragment.W30SMineFragment;
import com.exalpme.bozhilun.android.w30s.fragment.W30SRecordFragment;
import com.exalpme.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/5 16:54
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHomeActivity extends WatchBaseActivity implements PhoneStateListenerInterface {
    private final String TAG = "----->>>" + this.getClass().toString();
    private List<Fragment> h18iFragmentList = new ArrayList<>();
    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    MyBroadcastReceiver myBroadcastReceivers = null;
    public BluetoothAdapter bluetoothAdapter;
    private String w30SBleName;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "--------home-w30-=ooncreate---");
        setContentView(R.layout.activity_w30s_home);
        ButterKnife.bind(this);
        myBroadcastReceivers = new MyBroadcastReceiver();
        try {
            initViews();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            scanConn();
            if (myBroadcastReceivers == null) myBroadcastReceivers = new MyBroadcastReceiver();

            //注册监听电话状态变化的监听
            MyApp.getCustomPhoneStateListener().setPhoneStateListenerInterface(this);
            //注册连接状态的广播
            registerReceiver(myBroadcastReceivers, makeGattUpdateIntentFilter());
            //MyApp.getmW30SBLEManage().openW30SBLEServices();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void scanConn() {
        try {
            boolean serviceRunning = W30SBLEServices.isServiceRunning(this, "com.suchengkeji.android.w30sblelibrary.W30SBLEServices");
            if (!serviceRunning) {
                MyApp.getmW30SBLEManage().openW30SBLEServices();
            }
            if (MyCommandManager.DEVICENAME == null) {
                handler.sendEmptyMessageDelayed(0x06, 500);
//            W30SBLEManage instance = W30SBLEManage.getInstance(MyApp.getContext());
//            MyApp.setmW30SBLEManage(instance);
//            MyApp.getmW30SBLEManage().openW30SBLEServices();
                //
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    public BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            try {
                Log.e("--------调试-", "-W30SHomeActivity-connect-扫描中-" + "==" + device.getAddress());
                String mylanmac = (String) SharedPreferenceUtil.get(MyApp.getContext(), "mylanmac", "");
                if (!WatchUtils.isEmpty(mylanmac) && device.getAddress().equals(mylanmac)) {
                    Log.e("--------调试-", "-W30SHomeActivity-connect-A-");

                    synchronized (this) {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                        Log.e("--------调试-", "-W30SHomeActivity-connect-B-" + mylanmac);
                        W30SBLEManage.mW30SBLEServices.connect(mylanmac);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_FINDE_AVAILABLE_DEVICE);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "--------home-w30-=onstop---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--------home-w30-=ondestory---");
        try {
            if (myBroadcastReceivers != null) {
                unregisterReceiver(myBroadcastReceivers);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (MyApp.AppisOne) {
                String appInfo = NationalistinctionDUtils.getAppInfo(W30SHomeActivity.this);
                if (!WatchUtils.isEmpty(appInfo)&&!appInfo.equals("com.bozlun.bozhilun.android")) {
                    //检查更新
                    UpdateManager updateManager =
                            new UpdateManager(W30SHomeActivity.this, URLs.HTTPs + URLs.getvision);
                    updateManager.checkForUpdate(false);
                    MyApp.AppisOne = false;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }
    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        h18iFragmentList.add(new W30SRecordFragment()); //记录
//        h18iFragmentList.add(new NewsH9DataFragment());   //数据
        h18iFragmentList.add(new W30sNewsH9DataFragment()); //数据
        h18iFragmentList.add(new W30sNewRunFragment());   //跑步
//        h18iFragmentList.add(new W30SFrendensFragment());   //联系人
        h18iFragmentList.add(new W30SMineFragment());   //我的
        if (h18iFragmentList == null) return;
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), h18iFragmentList);
        h18iViewPager.setAdapter(fragmentPagerAdapter);
        h18iBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        h18iViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:  //开跑
                        h18iViewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_data:     //数据
                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_my:   //我的
                        h18iViewPager.setCurrentItem(3);//4
                        break;
//                    case R.id.tab_frend:   //朋友
//                        h18iViewPager.setCurrentItem(3);
//                        break;
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void callPhoneData(int flag, String phoneNumber) {
        try {
            this.phoneNumber = phoneNumber;
            if (!W30SHomeActivity.this.isFinishing() && W30SBLEManage.mW30SBLEServices != null) {
                switch (flag) {
                    case 0: //挂断
                        isNo = true;
                        if (w30SBleName != null && w30SBleName.equals("W30")) {
                            missCallPhone();    //挂断电话
                        }

                        break;
                    case 2: //接通
                        isNo = true;
                        if (w30SBleName != null && w30SBleName.equals("W30")) {
                            //Log.d(TAG, "------通话中2---" + w30SBleName);
                            missCallPhone();    //挂断电话
                        }
                        break;
                    case 1: //来电
                        w30SBleName = (String) SharedPreferenceUtil.get(MyApp.getApplication(), "mylanya", "");
                        //Log.e(TAG, "------来电111----");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(555);
                            }
                        }, 1000);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    boolean isNo = true;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 555:
                    if (w30SBleName != null && w30SBleName.equals("W30")) {
                        if (isNo) {
                            isNo = false;
                            getPeople(phoneNumber, MyApp.getContext());
                        }
                    }
                    break;
                case 0x06:
                    handler.removeMessages(0x06);
                    bluetoothAdapter = W30SBLEManage.getmBluetoothAdapter();
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.startLeScan(leScanCallback);
                    } else {
                        W30SBLEManage instance = W30SBLEManage.getInstance(MyApp.getContext());
                        MyApp.setmW30SBLEManage(instance);
                        bluetoothAdapter = W30SBLEManage.getmBluetoothAdapter();
                    }
                    break;
            }
            return false;
        }
    });

    //挂断电话
    private void missCallPhone() {
        if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
            if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
                MyApp.getmW30SBLEManage().notifyMsgClose();
                MyApp.getmW30SBLEManage().notifyMsgClose();
            }
        }
    }

    /**
     * 通过输入获取电话号码
     */
    public void getPeople(String nunber, Context context) {
        boolean w30sswitch_Phone = (boolean) SharedPreferenceUtil.get(MyApp.context, "w30sswitch_Phone", true);
        //Log.d(TAG, "------收到了来电广播3---" + w30SBleName + "=====" + w30sswitch_Phone);
        try {
            Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, nunber), new String[]{
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LABEL}, null, null, null);

            if (cursor != null && !WatchUtils.isEmpty("" + cursor.getCount() + "")) {
                if (cursor.getCount() == 0) {
                    //没找到电话号码
                    if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
                        if (w30sswitch_Phone) {
                            //Log.d(TAG, "------收到了来电广播4---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + nunber);
                            MyApp.getmW30SBLEManage().notifacePhone(nunber, 0x01);
                        }
                        return;
                    }
                } else if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    //获取姓名
                    if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
                        if (w30sswitch_Phone) {
                            //Log.d(TAG, "------收到了来电广播5---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + cursor.getString(2));
                            MyApp.getmW30SBLEManage().notifacePhone(cursor.getString(2), 0x01);
                        }
                        return;
                    }
                }
            } else {
                //直接发送电话号码，无名字
                if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
                    if (w30sswitch_Phone) {
                        //Log.d(TAG, "------收到了来电广播5---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + phoneNumber);
                        MyApp.getmW30SBLEManage().notifacePhone(phoneNumber, 0x01);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
