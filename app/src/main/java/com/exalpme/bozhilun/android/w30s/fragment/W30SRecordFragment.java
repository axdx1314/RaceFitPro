package com.exalpme.bozhilun.android.w30s.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.exalpme.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.exalpme.bozhilun.android.MyApp;
import com.exalpme.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.exalpme.bozhilun.android.bleutil.MyCommandManager;
import com.exalpme.bozhilun.android.h9.h9monitor.UpDatasBase;
import com.exalpme.bozhilun.android.h9.settingactivity.H9HearteDataActivity;
import com.exalpme.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.exalpme.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.exalpme.bozhilun.android.h9.utils.CusRefreshLayout;
import com.exalpme.bozhilun.android.h9.utils.H9TimeUtil;
import com.exalpme.bozhilun.android.siswatch.NewSearchActivity;
import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.exalpme.bozhilun.android.util.AnimationUtils;
import com.exalpme.bozhilun.android.util.Common;
import com.exalpme.bozhilun.android.util.SharedPreferencesUtils;
import com.exalpme.bozhilun.android.w30s.BaseFragment;
import com.exalpme.bozhilun.android.w30s.MyBroadcastReceiver;
import com.exalpme.bozhilun.android.w30s.SharePeClear;
import com.exalpme.bozhilun.android.w30s.activity.SleepHistoryActivity;
import com.exalpme.bozhilun.android.w30s.activity.StepHistoryDataActivity;
import com.exalpme.bozhilun.android.w30s.activity.W30SHearteDataActivity;
import com.exalpme.bozhilun.android.w30s.activity.W30SSettingActivity;
import com.exalpme.bozhilun.android.w30s.presenter.HomePresenter;
import com.exalpme.bozhilun.android.w30s.utils.W30BasicUtils;
import com.exalpme.bozhilun.android.w30s.bean.W30SHeartDataS;
import com.exalpme.bozhilun.android.w30s.views.W30S_SleepChart;
import com.example.bozhilun.android.R;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * @aboutContent: 联系人界面
 * @author： An
 * @crateTime: 2018/3/5 17:04
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SRecordFragment extends BaseFragment {//implements GetMaxStepServer.GetMaxStepForYearListener {
    private static final String TAG = "===W30SRecordFragment";
    boolean onesApp = true;
    @BindView(R.id.previousImage)
    ImageView previousImage;
    @BindView(R.id.b18irecordFm)
    LinearLayout b18irecordFm;
    @BindView(R.id.nextImage)
    ImageView nextImage;
    View b18iRecordView;
    Unbinder unbinder;
    @BindView(R.id.b18i_viewpager)
    ViewPager l38iViewpager;
    @BindView(R.id.text_stute)
    TextView textStute;
    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;
    private int PAGES = 0;//页码
    @BindView(R.id.line_pontion)
    LinearLayout linePontion;
    private float GOAL = 0;//默认目标
    private float STEP = 0;//步数
    @BindView(R.id.swipeRefresh)
    CusRefreshLayout swipeRefresh;//刷新控件
    //显示手表图标左上角
    @BindView(R.id.batteryshouhuanImg)
    ImageView shouhuanImg;
    //显示连接状态的TextView
    @BindView(R.id.battery_watch_connectStateTv)
    TextView watchConnectStateTv;
    //点击图标
    @BindView(R.id.watch_poorRel)
    RelativeLayout watchPoorRel;
    //显示日期的TextView
    @BindView(R.id.battery_watch_recordtop_dateTv)
    TextView watchRecordtopDateTv;
    //分享
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView watchRecordShareImg;
    //显示电量的图片
//    @BindView(R.id.batteryTopView)
//    BatteryView watchTopBatteryImgView;
    @BindView(R.id.batteryTopView)
    ImageView watchTopBatteryImgView;
    //显示电量
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    int kmormi; //距离显示是公制还是英制
    private static CallDataBackListe callDataBackListe;
    private static Handler mHandler = null;
    private static mHandlerCallBackLister mHandlerCallBackLister = null;
    private boolean isOneCreate = false;
    private boolean isOneonResume = false;
    private boolean isHaertNull = true;
    private List<W30S_SleepDataItem> sleepDataList;
    private static HomePresenter homePresenter;
    //private GetMaxStepServer getMaxStepServer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        //getMaxStepServer = new GetMaxStepServer(getActivity());
        //getMaxStepServer.setGetMaxStepForYearListener(this);
        if (mHandler != null) mHandler.sendEmptyMessageDelayed(0x03, 1000);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
        unbinder = ButterKnife.bind(this, b18iRecordView);
        isOneCreate = true;
        mHandlerCallBackLister = new mHandlerCallBackLister();
        mHandler = new Handler(mHandlerCallBackLister);
        try {
            //stuteLister();
            homePresenter = new HomePresenter(this);
            homePresenter.changeUI();
            //homePresenter.changeHttpsDataUI();

            saveStateToArguments();
            initStepList();
            setDatas();
            initViews();

//            getAxisLables();
//            getAxisPoints();
//            initLineChart();

            CALORIES = 0;
            DISTANCE = 0;
            //时间长按去查看设备时间
            watchRecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //startActivity(new Intent(getActivity(), Device_Time_Activity.class).putExtra("is18i", "W30"));
                    return false;
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }

        return b18iRecordView;
    }


    public void changeUI() {

//        if (getMaxStepServer != null) {
//            getMaxStepServer.getDataFromServer();
//        }
        //Log.d(TAG, "000-----回掉过来的链接状态");
        isOneCreate = true;
        isOneonResume = true;
        onesApp = true;
        if (MyCommandManager.DEVICENAME == null) {
            //Log.d(TAG, "001-----回掉过来的链接状态");
//            if (callDataBackListe != null) {
//                callDataBackListe = null;
//            }
        } else {
            //Log.d(TAG, "002-----回掉过来的链接状态");
            isStuta();
            getDatas();
        }
    }


    //本月中的最大步数和距离卡路里
    public void changeHttpsDataUI(int maxStep, String kacl, String disc, String dateStr) {
        //Log.d("---------------changeHttpsDataUI--", maxStep + "===" + kacl + "===" + disc + "===" + dateStr);
//        boolean w30sunit = (boolean) SharedPreferenceUtil.get(getContext(), "w30sunit", true);
//        if (watchRecordTagstepTv != null) {
//            if (w30sunit) {
//                watchRecordTagstepTv.setText(getResources().getString(R.string.string_one_day_record) + ":" + dateStr + "  " +
//                        maxStep + getResources().getString(R.string.steps)
//                        + "  " + disc + "m");
//            } else {
//                int round = (int) Math.floor(Integer.valueOf(disc) * 3.28);
//                watchRecordTagstepTv.setText(getResources().getString(R.string.string_one_day_record) + ":" + dateStr + "  " +
//                        maxStep + getResources().getString(R.string.steps) + "  "
//                        + round + "FT");
//            }
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveStateToArguments();
    }

//    public void stuteLister() {
//        MyBroadcastReceiver.setmBluetoothStateListenter(new MyBroadcastReceiver.BluetoothStateListenter() {
//            @Override
//            public void BluetoothStateListenter() {
//                Log.d(TAG, "000-----回掉过来的链接状态");
//                isOneCreate = true;
//                isOneonResume = true;
//                try {
//                    if (MyCommandManager.DEVICENAME == null) {
//                        Log.d(TAG, "001-----回掉过来的链接状态");
//                        if (callDataBackListe != null) {
//                            callDataBackListe = null;
//                        }
//                    } else {
//                        Log.d(TAG, "002-----回掉过来的链接状态");
//                        isStuta();
//                        getDatas();
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//            }
//        });
//    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            isHidden = true;
            regsBroad();
            if (isHidden) {
                mystute();
                if (onesApp) {
                    onesApp = false;
                    isStuta();
                }
                getDatas();
                if (recordwaveProgressBar != null) {
                    recordwaveProgressBar.setMaxValue(GOAL);
                    recordwaveProgressBar.setValue(STEP);
                    recordwaveProgressBar.postInvalidate();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public void mystute() {
        if (watchRecordtopDateTv != null)
            watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
        if (MyCommandManager.DEVICENAME == null) {
            if (textStute != null) {
                textStute.setText(getResources().getString(R.string.disconnted));
                textStute.setVisibility(View.VISIBLE);
            }
            if (watchConnectStateTv != null) {
                watchConnectStateTv.setText("" + "disconn.." + "");
                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                AnimationUtils.startFlick(watchConnectStateTv);
            }
            if (batteryLayout != null) {
                batteryLayout.setVisibility(View.GONE);
            }
        } else {
            if (watchConnectStateTv != null) {
                watchConnectStateTv.setText("" + "connected" + "");
                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                AnimationUtils.stopFlick(watchConnectStateTv);
            }
            if (batteryLayout != null) {
                batteryLayout.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            isOneonResume = true;
            isHidden = true;
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pageIsOne = 0;
        isHidden = false;
        isHaertNull = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        pageIsOne = 0;
        isHidden = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mHandler!=null){
                mHandler.removeCallbacksAndMessages(null);
            }
            pageIsOne = 0;
            isHaertNull = true;
            getContext().unregisterReceiver(mBroadcastReceiver);
            unbinder.unbind();
            saveStateToArguments();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 切换语言上下文置空处理
     */
    /**********************************************/
    private Context context;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null) context = getActivity();
    }

    /**********************************************/
    public void isStuta() {
        if (isHidden) {
            if (watchRecordtopDateTv != null)
                watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
            if (swipeRefresh != null) {
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
            if (MyCommandManager.DEVICENAME == null) {
                if (textStute != null) {
                    textStute.setText(getResources().getString(R.string.disconnted));
                    textStute.setVisibility(View.VISIBLE);
                }
                if (watchConnectStateTv != null) {
                    watchConnectStateTv.setText("" + "disconn.." + "");
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    AnimationUtils.startFlick(watchConnectStateTv);
                }
                if (batteryLayout != null) {
                    batteryLayout.setVisibility(View.GONE);
                }
                synchronized (this) {
                    mHandler.sendEmptyMessageDelayed(0x04, 10000);
                }
            } else {
                if (watchConnectStateTv != null) {
                    watchConnectStateTv.setText("" + "connected" + "");
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                    AnimationUtils.stopFlick(watchConnectStateTv);
                }
                if (textStute != null) {
                    textStute.setText(getResources().getString(R.string.syncy_data));
                    textStute.setVisibility(View.VISIBLE);
                }
                if (batteryLayout != null) {
                    batteryLayout.setVisibility(View.VISIBLE);
                }
                synchronized (this) {
                    mHandler.sendEmptyMessageDelayed(0x04, 5000);
                }
            }
        }
//        if (isHidden) {
//            if (MyCommandManager.DEVICENAME != null) {
//                a = 0;
//                if (stateHandler != null) stateHandler.removeMessages(1024);
//                if (watchConnectStateTv != null) {
//                    watchConnectStateTv.setText("" + "connect" + "");
//                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
//                    AnimationUtils.stopFlick(watchConnectStateTv);
//                }
//                if (textStute != null) {
//                    textStute.setText(getResources().getString(R.string.syncy_data));
//                    textStute.setVisibility(View.VISIBLE);
//                }
//                if (batteryLayout != null) batteryLayout.setVisibility(View.VISIBLE);
//            } else {
//                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
//                if (stateHandler != null) {
//                    if (a <= 1000) {
//                        stateHandler.sendEmptyMessageDelayed(1024, 1500);
//                    }
//                }
//                if (textStute != null) {
//                    textStute.setText(getResources().getString(R.string.disconnted));
//                    textStute.setVisibility(View.VISIBLE);
//                }
//                if (watchConnectStateTv != null) {
//                    watchConnectStateTv.setText("" + "disconn.." + "");
//                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
//                    AnimationUtils.startFlick(watchConnectStateTv);
//                }
//                if (batteryLayout != null) batteryLayout.setVisibility(View.INVISIBLE);
//            }
//        }
    }


    private int pageIsOne = 0;//界面第一次创建

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            Log.d("===================", "isVisible");
            if (pageIsOne == 1) {
//                //getDatas();
//                try {
////                    String w30S_p = (String) SharedPreferenceUtil.get(context, "W30S_P", "");
////                    if (!WatchUtils.isEmpty(w30S_p)) {
////                        setBatteryPowerShow(Integer.valueOf(w30S_p));   //显示电量
////                    }
//                    isHidden = true;
//                    if (MyCommandManager.DEVICENAME == null) return;
//                    String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
//                    if (!TextUtils.isEmpty(homeTime)) {
//                        String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                        int number = Integer.valueOf(timeDifference.trim());
//                        int number2 = Integer.parseInt(timeDifference.trim());
//                        if (number >= 2 || number2 >= 2) {
//                            //if (!timeDifference.trim().equals("1")) {
//                            //showLoadingDialog(getResources().getString(R.string.dlog));
//                            getDatas();
//                            SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
//                            //}
//                        }
//                    } else {
//                        getDatas();
//                        closeLoadingDialog();
//                        SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
//                    }
//                    if (isOnOnCreate) {
//                        isOnOnCreate = false;
//                        stateHandler.sendEmptyMessageDelayed(1000211, 1500);
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }

//                try {
//                    isHidden = true;
//                    String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
//                    if (MyCommandManager.DEVICENAME != null) {
//                        if (!TextUtils.isEmpty(homeTime)) {
//                            String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                            int number = Integer.valueOf(timeDifference.trim());
//                            int number2 = Integer.parseInt(timeDifference.trim());
//                            if (number >= 2 || number2 >= 2) {
//                                isStuta();
//                                getDatas();
//                                SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
//                            }
//                        } else {
//                            isStuta();
//                            getDatas();
//                            //closeLoadingDialog();
//                            SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }
            }
        } else {
            pageIsOne = 1;
            Log.d("===================", "No isVisible");
            isHidden = false;
            isHaertNull = true;
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        Log.d("===================", "onFragmentFirstVisible");
        pageIsOne = 1;
        isHaertNull = true;
    }


    private boolean isHidden = true;//离开界面隐藏显示同步数据的


    /**
     * 注册广播，监听刷新
     */
    private void regsBroad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyApp.RefreshBroad);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);
    }


    //同步用户信息
    private void syncUserInfoData() {
        try {
            String userData = (String) SharedPreferencesUtils.readObject(getContext(), "saveuserinfodata");
//        Log.d("-----用户资料-----AAA----", "--------" + userData);
            if (!WatchUtils.isEmpty(userData)) {
                try {
                    int weight;
                    JSONObject jsonO = new JSONObject(userData);
                    String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                    String userAge = jsonO.getString("birthday");   //生日
                    String userWeight = jsonO.getString("weight");  //体重
                    String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
//                Log.d("-----用户资料-----BBB----", userWeight + "====" + tempWeight);
                    if (tempWeight.contains(".")) {
                        weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim());
                    } else {
                        weight = Integer.valueOf(tempWeight);
                    }
                    String userHeight = ((String) SharedPreferencesUtils.getParam(getContext(), "userheight", "")).trim();
                    int sex;
                    if (userSex.equals("M")) {    //男
                        sex = 1;
                    } else {
                        sex = 2;
                    }
                    int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                    int height = Integer.valueOf(userHeight);


                    /**
                     * 设置用户资料
                     *
                     * @param isMale 1:男性 ; 2:女性
                     * @param age    年龄
                     * @param hight  身高cm
                     * @param weight 体重kg
                     */
                    SharedPreferenceUtil.put(getContext(), "user_sex", sex);
                    SharedPreferenceUtil.put(getContext(), "user_age", age);
                    SharedPreferenceUtil.put(getContext(), "user_height", height);
                    SharedPreferenceUtil.put(getContext(), "user_weight", weight);
//                Log.d("-----用户资料-----CCC---", sex + "===" + age + "===" + height + "===" + weight);
                    /**
                     * 设置用户资料
                     *
                     * @param isMale 1:男性 ; 2:女性
                     * @param age    年龄
                     * @param hight  身高cm
                     * @param weight 体重kg
                     */
                    MyApp.getmW30SBLEManage().setUserProfile(sex, age, height, weight);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    private void initViews() {
        if (shouhuanImg != null) shouhuanImg.setImageResource(R.mipmap.image_w30s);
        if (previousImage != null) previousImage.setVisibility(View.GONE);
        if (nextImage != null) nextImage.setVisibility(View.GONE);
        //手动刷新
        if (swipeRefresh != null) swipeRefresh.setOnRefreshListener(new RefreshListenter());
    }

    //目标选择列表
    private ArrayList<String> daily_numberofstepsList;

    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }
    }

//    //设置目标步数
//    private void setTagSteps() {
//        ProfessionPick stepsnumber = new ProfessionPick.Builder(getContext(), new ProfessionPick.OnProCityPickedListener() {
//            @Override
//            public void onProCityPickCompleted(String profession) {
//                if (profession == null || TextUtils.isEmpty(profession)) {
//                    profession = (String) SharedPreferenceUtil.get(context, "w30stag", "10000");
//                }
//                //设置目标步数
//                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + profession);
//                recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
//                int integer = Integer.valueOf(profession);
////                Log.d("==setTagSteps===", profession + "===" + integer);
//                MyApp.getmW30SBLEManage().setTargetStep(integer);
//                SharedPreferenceUtil.put(context, "w30stag", profession);
//                recordwaveProgressBar.setMaxValue(integer);
//                getDatas();
//
//            }
//        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
//                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
//                .btnTextSize(16) // button text size
//                .viewTextSize(25) // pick view text size
//                .colorCancel(Color.parseColor("#999999")) //color of cancel button
//                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
//                .setProvinceList(daily_numberofstepsList) //min year in loop
//                .dateChose((String) SharedPreferenceUtil.get(context, "w30stag", "10000")) // date chose when init popwindow
//                .build();
//        stepsnumber.showPopWin(getActivity());
//    }

    /**
     * 初次获取数据
     */
    public void getDatas() {
        synchronized (this) {
            try {
                if (textStute != null) {
                    int visibility = textStute.getVisibility();
                    if (visibility == 0x00000008) {
                        if (textAllSleepData != null) {
                            textAllSleepData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textAllSleepData.setVisibility(View.GONE);
                    }
                }
                DataAcy();
                if (isOneCreate) {
                    isOneCreate = false;
                    if (MyCommandManager.DEVICENAME != null) {
                        syncUserInfoData();
                        SharePeClear.sendCmdDatas(context);
                    }
                }
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            } catch (Exception e) {
                e.getMessage();
            }
        }
//        syncUserInfoData();
//        DataAcy();
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//
//                subscriber.onNext("设置用户数据ok");
//
//                subscriber.onNext("同步手表数据");
//                subscriber.onCompleted();
//            }
//        }).subscribe(new Observer<String>() {
//            @Override
//            public void onNext(String s) {
////                Log.d(TAG, "Item: " + s);
//            }
//            @Override
//            public void onCompleted() {
////                Log.d(TAG, "Completed!");
//                swipeRefresh.setRefreshing(false);
//            }
//            @Override
//            public void onError(Throwable e) {
////                Log.d(TAG, "Error!");
//                swipeRefresh.setRefreshing(false);
//            }
//        });

    }

    void DataAcy() {

        synchronized (MyApp.getContext()) {
            try {
                if (MyCommandManager.DEVICENAME != null) {
//                Log.d("======c========", "获取数据开始啦");
                    if (callDataBackListe == null) {
                        callDataBackListe = new CallDataBackListe();
                    }
                    MyApp.getmW30SBLEManage().syncTime(callDataBackListe);
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }

    }


//    int a = 0;
//    int bbb = 0;
//    Handler stateHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            try {
//                if (msg.what == 222222) {
//                    if (isHidden) {
//                        textStute.setVisibility(View.GONE);
//                        stateHandler.removeMessages(222222);
//                    }
//                } else if (msg.what == 1024) {
//                    a++;
//                    getDatas();
//                    stateHandler.removeMessages(1024);
//                } else if (msg.what == 1000211) {
//                    stateHandler.removeMessages(1000211);
//                    bbb++;
//                    if (bbb < 2) {
////                    Log.d(TAG, "手动连接成功，读数据");
//                        getDatas();
//                        stateHandler.sendEmptyMessageDelayed(1000211, 1500);
//                    }
//                }
//            } catch (Exception e) {
//                e.getMessage();
//            }
////            else if (msg.what == 333333) {
////                getDatas();
////                stateHandler.removeMessages(333333);
////            }
//            return false;
//        }
//    });

    private class mHandlerCallBackLister implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    if (isHidden) {
                        mHandler.removeMessages(0x01);

                        if (textAllSleepData != null) {
                            textAllSleepData.setVisibility(View.VISIBLE);
                        }
                        if (heartDatasMaxOrLad != null && heartDatasMaxOrLad.size() > 0) {
                            double div3 = (double) WatchUtils.div((double) NowVale, heartDatasMaxOrLad.size() - 1, 1);
                            if (autoHeartTextNumber != null) {
                                if (String.valueOf(div3).contains(".")) {
                                    String s = String.valueOf(div3).split("[.]")[0];
                                    autoHeartTextNumber.setText(s + "bpm");
                                } else {
                                    autoHeartTextNumber.setText(div3 + "bpm");
                                }
                            }
                        }
                        if (textStute != null) textStute.setVisibility(View.GONE);

                    }
                    break;
                case 0x02:
//                    if (getMaxStepServer != null) {
//                        getMaxStepServer.getDataFromServer();
//                    }
                    mHandler.removeMessages(0x02);
                    isStuta();
                    getDatas();
                    break;
                case 0x03:
                    if (MyCommandManager.DEVICENAME != null) {
                        mHandler.removeMessages(0x03);
                        //isStuta();
                        getDatas();
                    } else {
                        mHandler.sendEmptyMessageDelayed(0x03, 1000);
                    }
                    break;
                case 0x04:
                    mHandler.removeMessages(0x04);
                    if (isHidden) {
                        if (textStute != null) {
                            int visibility = textStute.getVisibility();
                            Log.d("=========", visibility + "");
                            if (visibility == 0x00000000) {
                                getDatas();
                                mHandler.sendEmptyMessageDelayed(0x04, 5000);
                            } else {
                                mHandler.removeMessages(0x04);
                            }
                        }
                    }
                    break;
                case 0x05:
                    getDatas();
                    isHaertNull = false;
                    break;
            }
            return false;
        }
    }

    /**
     * 手动刷新
     */
    private class RefreshListenter implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {

            Log.d(TAG, "手动刷新");
            isStuta();
            getDatas();
        }
    }


    //心率返回集合
    List<W30SHeartDataS> heartDatas = new ArrayList<>();
    List<Integer> heartDatasMaxOrLad = new ArrayList<>();

    /******************        ---------  扇形----------               **********************/
    int AWAKE2 = 0;//清醒
    int AWAKE = 0;//清醒次数
    int AOYE = 0;//熬夜
    int DEEP = 0;//深睡
    int SHALLOW = 0;//浅睡
    int ALLTIME = 0;//浅睡
    int NowVale = 0;//实时心率
    private boolean fanRoateAniamtionStart;
    private String timeFromMillisecondA = "0";
    private String timeFromMillisecondS = "0";
    private String timeFromMillisecondD = "0";


//睡眠饼状图（以舍弃）
//    public void setH9PieCharts(double awake, double deep, double shallow) {
//
//        if (deep <= 0) {
//            deep = 0;
//        }
//        if (shallow <= 0) {
//            shallow = 0;
//        }
//
//        if (awake <= 0) {
//            awake = 0;
//        }
//        if (awake == 0 && deep == 0 && shallow == 0) {
//            awake = 1;
//        }
//        double v = awake + shallow + deep;
//
//        if (qianshuiT != null) {
//            double v1 = WatchUtils.div(shallow, v, 2);
//            if (v > 0) {
//                qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0]);
//            }
//        }
//        if (qingxingT != null) {
//            double v1 = WatchUtils.div(awake, v, 2);
//            if (v > 0) {
//                qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0]);
//            }
//        }
//        if (shenshuiT != null) {
//            double v1 = WatchUtils.div(deep, v, 2);
//            if (v > 0) {
//                shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0]);
//            }
//        }
//        pieChartView.setFanClickAbleData(
//                new double[]{deep, shallow, awake},
//                new int[]{Color.parseColor("#b592d6"), Color.parseColor("#a6a8ff"), Color.parseColor("#fcd647")}, 0.05);
//        pieChartView.setIsFistOffSet(false);
//
////        pieChartView.setOnFanClick(new OnFanItemClickListener() {
////            @Override
////            public void onFanClick(final FanItem fanItem) {
////                if (!fanRoateAniamtionStart) {
////                    float to;
////                    float centre = (fanItem.getStartAngle() * 2 + fanItem.getAngle()) / 2;
////                    if (centre >= 270) {
////                        to = 360 - centre + 90;
////                    } else {
////                        to = 90 - centre;
////                    }
////                    RotateAnimation animation = new RotateAnimation(0, to, pieChartView.getFanRectF().centerX(), pieChartView.getFanRectF().centerY());
////                    animation.setDuration(800);
////                    animation.setAnimationListener(new Animation.AnimationListener() {
////
////                        @Override
////                        public void onAnimationStart(Animation animation) {
////                            fanRoateAniamtionStart = true;
////                        }
////
////                        @Override
////                        public void onAnimationEnd(Animation animation) {
////                            pieChartView.setToFirst(fanItem);
////                            pieChartView.clearAnimation();
////                            pieChartView.invalidate();
////                            fanRoateAniamtionStart = false;
//////                            Toast.makeText(getContext(), "当前选中:" + fanItem.getPercent() + "%", Toast.LENGTH_SHORT).show();
////                            Log.e(TAG, "----------------当前选中:" + fanItem.getPercent() + "%");
////                        }
////
////                        @Override
////                        public void onAnimationRepeat(Animation animation) {
////                        }
////                    });
////                    animation.setFillAfter(true);
////                    pieChartView.startAnimation(animation);
////                }
////            }
////        });
//    }


    //    View ----- 中的子控件
//    private PieChartView pieChartView;//睡眠饼状图（已舍弃）
    TextView L38iCalT, L38iDisT, textStepReach;
    TextView qingxingT, qianshuiT, shenshuiT, textTypeData;
    TextView autoHeartText, autoHeartTextNumber, maxHeartTextNumber, zuidiHeartTextNumber;//心率
    //TextView autoDatatext;//数据
    ImageView StepImageData, autoDataImage, SleepDatas;
    //-----清醒状态-------浅睡状态----深睡状态------清醒==改==》时常---浅睡----深睡
    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep, textAllSleepData;
    //---------入睡时间-----苏醒次数--------苏醒时间
    TextView textSleepInto, textSleepWake, textSleepTime;
    W30S_SleepChart w30S_sleepChart;
    SeekBar SleepseekBar;
    TextView text_sleep_type, text_sleep_start, text_sleep_lines, text_sleep_end, sleep_into_time, sleep_out_time, text_sleep_nodata;
    LinearLayout line_time_star_end;
    /**
     * view pager数据
     */
    private int DISTANCE = 0;//距离
    private int CALORIES = 0;//卡路里
    WaveProgress recordwaveProgressBar;
    TextView watchRecordTagstepTv;

    private void setDatas() {

        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_watch_record_change, null, false);
        recordwaveProgressBar = (WaveProgress) mView.findViewById(R.id.recordwave_progress_bar);
        L38iCalT = (TextView) mView.findViewById(R.id.watch_recordKcalTv);
        L38iDisT = (TextView) mView.findViewById(R.id.watch_recordMileTv);
        StepImageData = (ImageView) mView.findViewById(R.id.stepData_imageView);

        StepImageData.setVisibility(View.VISIBLE);
        StepImageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        StepHistoryDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        L38iCalT.setText("" + CALORIES + "");
        L38iDisT.setText("" + DISTANCE + "");
        String w30stag = (String) SharedPreferenceUtil.get(context, "w30stag", "10000");
        GOAL = Integer.valueOf(w30stag);
        recordwaveProgressBar.setMaxValue(GOAL);
        recordwaveProgressBar.setValue(STEP);
        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
        recordwaveProgressBar.setTagStepStr(getResources().getString(R.string.settarget_steps) + tagGoal);

        textStepReach = (TextView) mView.findViewById(R.id.text_step_reach);


        watchRecordTagstepTv = (TextView) mView.findViewById(R.id.watch_recordTagstepTv);
        watchRecordTagstepTv.setVisibility(View.GONE);

        //选择设置目标步数
        watchRecordTagstepTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setTagSteps();
            }
        });
        View view2 = LayoutInflater.from(context).inflate(R.layout.b18i_leaf_linechart_view, null, false);
        lineChart = (LineChartView) view2.findViewById(R.id.heart_chart);
        textTypeData = (TextView) view2.findViewById(R.id.data_type_text);
        textTypeData.setVisibility(View.VISIBLE);
        view2.findViewById(R.id.leaf_chart).setVisibility(View.GONE);
        view2.findViewById(R.id.heart_lines).setVisibility(View.VISIBLE);
        autoHeartText = (TextView) view2.findViewById(R.id.autoHeart_text);//心率---可点击
        autoHeartTextNumber = (TextView) view2.findViewById(R.id.autoHeart_text_number);//平均心率---可点击
        maxHeartTextNumber = (TextView) view2.findViewById(R.id.maxHeart_text_number);//最高心率---可点击
        zuidiHeartTextNumber = (TextView) view2.findViewById(R.id.zuidiHeart_text_number);//最低心率---可点击
        view2.findViewById(R.id.autoData_text).setVisibility(View.GONE);
        //autoDatatext = (TextView) view2.findViewById(R.id.autoData_text);//数据---可点击
        autoDataImage = (ImageView) view2.findViewById(R.id.autoData_imageView);//历史数据---可点击
        autoDataImage.setVisibility(View.VISIBLE);
        autoDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        H9HearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        autoHeartText.setOnClickListener(new MyViewLister());
        View view3 = LayoutInflater.from(context).inflate(R.layout.w30s_pie_chart_view, null, false);
//        pieChartView = (PieChartView) view3.findViewById(R.id.pieChartView);//睡眠饼状图（已经舍弃）
        w30S_sleepChart = (W30S_SleepChart) view3.findViewById(R.id.sleep_chart);
        text_sleep_nodata = (TextView) view3.findViewById(R.id.text_sleep_nodata);
        w30S_sleepChart.setVisibility(View.GONE);
        text_sleep_nodata.setVisibility(View.VISIBLE);
        SleepseekBar = (SeekBar) view3.findViewById(R.id.seek_bar_my);
        text_sleep_type = (TextView) view3.findViewById(R.id.text_sleep_type);
        text_sleep_start = (TextView) view3.findViewById(R.id.text_sleep_start);
        text_sleep_lines = (TextView) view3.findViewById(R.id.text_sleep_lines);
        text_sleep_end = (TextView) view3.findViewById(R.id.text_sleep_end);
        sleep_into_time = (TextView) view3.findViewById(R.id.sleep_into_time);
        sleep_out_time = (TextView) view3.findViewById(R.id.sleep_out_time);
        line_time_star_end = (LinearLayout) view3.findViewById(R.id.line_time_star_end);
        //line_time_star_end.setVisibility(View.GONE);
        text_sleep_type.setText(" ");
        text_sleep_start.setText(" ");
        if (MyApp.AppisOneStar) {
            MyApp.AppisOneStar = false;
            text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));
        } else {
            text_sleep_lines.setText(" ");
        }
        text_sleep_end.setText(" ");
        SleepseekBar.setEnabled(false);
        w30S_sleepChart.setmDataTypeListenter(new W30S_SleepChart.DataTypeListenter() {
            @Override
            public void OnDataTypeListenter(String type, String startTime, String endTime) {
                if (text_sleep_lines != null) text_sleep_lines.setText(" -- ");
                if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                    text_sleep_type.setText(getResources().getString(R.string.waking_state));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                } else if (type.equals("2")) {  //潜睡状态
                    text_sleep_type.setText(getResources().getString(R.string.shallow_sleep));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                } else if (type.equals("3")) {  //深睡
                    text_sleep_type.setText(getResources().getString(R.string.deep_sleep));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                }
            }
        });

        SleepseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("========", progress + "");
                w30S_sleepChart.setSeekBar((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                boolean clickable = SleepseekBar.isClickable();
                if (!clickable) {
                    SleepseekBar.setProgress(0);
                    SleepseekBar.clearAnimation();
                    SleepseekBar.invalidate();
                    w30S_sleepChart.setSeekBar(0);
                    if (text_sleep_type != null) text_sleep_type.setText(" ");
                    if (text_sleep_start != null) text_sleep_start.setText(" ");
                    if (text_sleep_lines != null) text_sleep_lines.setText(" ");
                    if (text_sleep_end != null) text_sleep_end.setText(" ");
                }

            }
        });
        awakeState = (TextView) view3.findViewById(R.id.awakeState);
        shallowState = (TextView) view3.findViewById(R.id.shallowState);
        deepState = (TextView) view3.findViewById(R.id.deepState);
        textAllSleepData = (TextView) view3.findViewById(R.id.text_all_sleep_data);
        SleepDatas = (ImageView) view3.findViewById(R.id.sleepData_imageView);
        SleepDatas.setVisibility(View.VISIBLE);
        SleepDatas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        SleepHistoryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        awakeSleep = (TextView) view3.findViewById(R.id.awake_sleep);
        shallowSleep = (TextView) view3.findViewById(R.id.shallow_sleep);
        deepSleep = (TextView) view3.findViewById(R.id.deep_sleep);

        qingxingT = (TextView) view3.findViewById(R.id.w30_qingxing_text);
        qianshuiT = (TextView) view3.findViewById(R.id.w30_qianshui_text);
        shenshuiT = (TextView) view3.findViewById(R.id.w30_shenshui_text);

        textSleepInto = (TextView) view3.findViewById(R.id.text_sleep_into);//入睡时间
        textSleepWake = (TextView) view3.findViewById(R.id.text_sleep_wake);//苏醒次数
        textSleepTime = (TextView) view3.findViewById(R.id.text_sleep_time);//苏醒时间
        awakeSleep.setText(timeFromMillisecondA);
        shallowSleep.setText(timeFromMillisecondS);
        deepSleep.setText(timeFromMillisecondD);
        awakeState.setText(getResources().getString(R.string.string_qingxing));//清醒状态
        shallowState.setText(getResources().getString(R.string.sleep_light));//浅睡眠
        deepState.setText(getResources().getString(R.string.sleep_deep));//深睡眠
        List<View> fragments = new ArrayList<>();
        fragments.add(mView);
        fragments.add(view2);
        fragments.add(view3);
        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
        l38iViewpager.setOffscreenPageLimit(3);
        //l38iViewpager.setCurrentItem(3);
        setLinePontion(fragments);
        l38iViewpager.setAdapter(adapter);
        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
    }


    private LineChartView lineChart;//第三方控件，心率图
    private String[] StringDate = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};//X轴的标注
    private List<lecho.lib.hellocharts.model.PointValue> mPointValues = new ArrayList<>();
    private List<AxisValue> mAxisValues = new ArrayList<>();

    private void initLineChart() {
        lecho.lib.hellocharts.model.Line line = new lecho.lib.hellocharts.model.Line(mPointValues).setColor(Color.WHITE).setCubic(false);  //折线的颜色
        List<lecho.lib.hellocharts.model.Line> lines = new ArrayList<lecho.lib.hellocharts.model.Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(false);//曲线是否平滑
        line.setFilled(true);//是否填充曲线的面积
        line.setPointRadius((int) 3);
        line.setStrokeWidth((int) 2);
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);


        //坐标轴
        lecho.lib.hellocharts.model.Axis axisX = new lecho.lib.hellocharts.model.Axis(); //X轴
        //对x轴，数据和属性的设置
        axisX.setTextSize(8);//设置字体的大小
        axisX.setHasTiltedLabels(false);//x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setLineColor(Color.WHITE);
        axisX.setValues(mAxisValues); //设置x轴各个坐标点名称
        //axisX.setName("");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        //axisX.setMaxLabelChars(24);  //最多几个X轴坐标
        data.setAxisXBottom(axisX); //x 轴在底部
//      data.setAxisXTop(axisX);  //x 轴在顶部

        lecho.lib.hellocharts.model.Axis axisY = new lecho.lib.hellocharts.model.Axis();  //Y轴
//        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setHasLines(true);
        axisY.setLineColor(Color.parseColor("#30FFFFFF"));
        List<AxisValue> values = new ArrayList<>();
//        for(int i = 0; i < 100; i+= 50){
        for (int i = 0; i < 16; i++) {
            int i1 = i * 20;
            AxisValue value = new AxisValue(i1);
            String label = "" + i1;
            value.setLabel(label);
            values.add(value);
        }
        axisY.setValues(values);
        axisY.setName("");//y轴标注
        axisY.setTextColor(Color.WHITE);  //设置字体颜色
        axisY.setTextSize(7);//设置字体大小
        //axisY.setMaxLabelChars(6);//max label length, for example 60
        data.setAxisYLeft(axisY);  //Y轴设置在左边
//      data.setAxisYRight(axisY);  //y轴设置在右边


        data.setValueLabelBackgroundColor(getResources().getColor(R.color.colorAccent));//此处设置坐标点旁边的文字背景
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelsTextColor(Color.WHITE);//此处设置坐标点旁边的文字颜色

        //设置行为属性，支持缩放、滑动以及平移
//        lineChart.setInteractive(true);
//        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
//        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
//        lineChart.setLineChartData(data);
//        lineChart.setVisibility(View.VISIBLE);

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setPaddingRelative(0, 30, 0, 0);
        lineChart.setInteractive(false);
//        lineChart.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
//        lineChart.setMaxZoom((float) 3);//缩放比例
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 尼玛搞的老子好辛苦！！！见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         * 下面几句可以设置X轴数据的显示个数（x轴0-7个数据），当数据点个数小于（29）的时候，缩小到极致hellochart默认的是所有显示。当数据点个数大于（29）的时候，
         * 若不设置axisX.setMaxLabelChars(int count)这句话,则会自动适配X轴所能显示的尽量合适的数据个数。
         * 若设置axisX.setMaxLabelChars(int count)这句话,
         * 33个数据点测试，若 axisX.setMaxLabelChars(10);里面的10大于v.right= 7; 里面的7，则
         刚开始X轴显示7条数据，然后缩放的时候X轴的个数会保证大于7小于10
         若小于v.right= 7;中的7,反正我感觉是这两句都好像失效了的样子 - -!
         * 并且Y轴是根据数据的大小自动设置Y轴上限
         * 若这儿不设置 v.right= 7; 这句话，则图表刚开始就会尽可能的显示所有数据，交互性太差
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 24;
        lineChart.setCurrentViewport(v);
        lineChart.postInvalidate();
    }

    /**
     * X 轴的显示
     */
    private void getAxisLables() {
        try {
            if (mPointValues != null) mPointValues.clear();
            if (mAxisValues != null) mAxisValues.clear();
            for (int i = 0; i < StringDate.length; i++) {
//                Log.d("========", StringDate[i] + "");
                mAxisValues.add(new AxisValue(i).setLabel(StringDate[i]));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        try {
            if (heartDatas != null && heartDatas.size() > 0) {
                if (textTypeData != null) {
                    textTypeData.setVisibility(View.GONE);
                }
                for (int i = 0; i < heartDatas.size(); i++) {
                    long time = heartDatas.get(i).getTime();
                    String dateToString = W30BasicUtils.getDateToString(time, "yyyy/MM/dd HH:mm");
//                mAxisValues.add(new AxisValue(i).setLabel(dateToString.substring(11, 13)));
                    int value = heartDatas.get(i).getValue();
                    String currentDate1 = W30BasicUtils.getCurrentDate1();//yyyy-MM-dd HH:mm:ss
                    String substring = dateToString.substring(11, 13);//17
                    String substring1 = currentDate1.substring(11, 13);//17
                    if (Integer.valueOf(substring) <= Integer.valueOf(substring1)) {
                        if (value <= 0) {
                            value = 60;
                        }
                    }
                    //图表的每个点的显示
                    mPointValues.add(new lecho.lib.hellocharts.model.PointValue(i, value));
//                    Log.d(TAG, "=========X,Y数据" + dateToString.substring(11, 13) + "===" + heartDatas.get(i).getValue());

                }
            } else {
                if (textTypeData != null) {
                    if (lineChart != null) {
                        lineChart.setVisibility(View.GONE);
                    }
                    textTypeData.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 滑动小圆点
     *
     * @param fragments
     */

    private void setLinePontion(List<View> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setPadding(3, 0, 3, 0);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
            if (i == 0) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
            }
            imageView.setMaxHeight(1);
            imageView.setMaxWidth(1);
            imageView.setMinimumHeight(1);
            imageView.setMinimumWidth(1);
            linePontion.addView(imageView);
        }
    }

    /**
     * 内部Adapter
     */
    public class MyHomePagerAdapter extends PagerAdapter {
        List<View> stringList;

        public MyHomePagerAdapter(List<View> stringList) {
            this.stringList = stringList;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(stringList.get(position));
            return stringList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(stringList.get(position));
        }
    }

    /**
     * ViewPager页面改变监听
     */
    private class PagerChangeLister implements ViewPager.OnPageChangeListener {
        private List<View> fragments;

        public PagerChangeLister(List<View> fragments) {
            this.fragments = fragments;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            recordwaveProgressBar.postInvalidate();
        }

        @Override
        public void onPageSelected(int position) {
            PAGES = position;
            if (position == 2) {
                if (textStute != null) {
                    int visibility = textStute.getVisibility();
                    if (visibility == 0x00000008) {
                        if (textAllSleepData != null) {
                            textAllSleepData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textAllSleepData.setVisibility(View.GONE);
                    }
                }
            }
            PointSetting(position);
        }

        private void PointSetting(int position) {
            l38iViewpager.setCurrentItem(position);
            if (isOneonResume) {
                isOneonResume = false;
                //isStuta();
                getDatas();
            }
            for (int j = 0; j < fragments.size(); j++) {
                ImageView childAt1 = (ImageView) linePontion.getChildAt(j);
                childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
                childAt1.setMaxHeight(1);
                childAt1.setMaxWidth(1);
//                childAt1.setAlpha(80);
            }
            ImageView childAt = (ImageView) linePontion.getChildAt(position);
            childAt.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
            childAt.setMaxHeight(1);
            childAt.setMaxWidth(1);
//            childAt.setAlpha(225);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    @OnClick({R.id.watch_poorRel, R.id.battery_watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_poorRel:    //点击是否连接
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    startActivity(new Intent(getActivity(), W30SSettingActivity.class).putExtra("is18i", "W30S"));
                } else {
                    startActivity(new Intent(getContext(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                startActivity(new Intent(context, SharePosterActivity.class).putExtra("is18i", "W30S"));
                break;
        }
    }

    //分享
    private void doShareClick() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
        ScreenShot.shoot(getActivity(), new File(filePath));
        Common.showShare(getActivity(), null, false, filePath);
    }

    //显示电量
    private void setBatteryPowerShow(int battery) {
        try {
//            Log.e(TAG, "----------battery=" + battery);
            int batterys = 0;
            if (battery >= 0 && battery < 20) {
                batterys = 0;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
            } else if (battery >= 20 && battery < 40) {
                batterys = 25;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (battery >= 40 && battery < 60) {
                batterys = 50;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (battery >= 60 && battery < 80) {
                batterys = 75;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (battery == 80) {
                batterys = 100;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            batteryPowerTv.setText(batterys + "%");
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private class MyViewLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), HeartRateActivity.class).putExtra("is18i", "H9");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
                startActivity(new Intent(context,
                        W30SHearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S_HEART"));
            } else if (MyCommandManager.DEVICENAME != null && !MyCommandManager.DEVICENAME.equals("W30")) {
                startActivity(new Intent(context,
                        H9HearteTestActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }

        }
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "-------广播----action=" + action);
            try {
                switch (action) {
                    case MyApp.RefreshBroad:
                        if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
                            if (isHidden) {
                                Log.d(TAG, "W30S同步成功");
                                //isStuta();
                                getDatas();
                            }
//                            try {
//                                RefreshBroadcastReceivers.setMyCallBack(new RefreshBroadcastReceivers.MyCallBack() {
//                                    @Override
//                                    public void setMyCallBack(Message msg) {
//                                        if (isHidden) {
//                                            if (msg.what == RefreshBroadcastReceivers.MessageNumber) {
//                                                textStute.setText(getContext().getString(R.string.syncy_data));
//                                                textStute.setVisibility(View.VISIBLE);
////                                                Log.d(TAG, "W30S同步成功");
//                                                isStuta();
//                                                getDatas();
//                                                RefreshBroadcastReceivers.getMyHandler().removeMessages(RefreshBroadcastReceivers.MessageNumber);
//                                            }
//                                        }
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.getMessage();
//                            }
                        }
                        break;
                    case W30SBLEServices.ACTION_GATT_CONNECTED:
                        Log.d("----------w30--", "ACTION_GATT_CONNECTED");
                        MyCommandManager.DEVICENAME = "W30";
                        onesApp = true;
                        mHandler.sendEmptyMessageDelayed(0x02, 1500);
                        break;
                    case W30SBLEServices.ACTION_GATT_DISCONNECTED:
                        Log.d("----------w30--", "ACTION_GATT_DISCONNECTED");
                        MyCommandManager.DEVICENAME = null;
                        mHandler.sendEmptyMessageDelayed(0x02, 1500);
                        break;
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    private class CallDataBackListe implements W30SBLEServices.CallDatasBackListenter {

        @Override
        public void CallDatasBackListenter(final W30SSportData objects) {
            if (objects == null) return;
            try {
                STEP = 0;
                //转为Kc保留两位
                CALORIES = 0;
                DISTANCE = 0;
                //2018-03-09===0.0===0.0===0
//                            Log.d(TAG, "解析运动数据 日期 =  " + objects.getData());
//                            Log.d(TAG, "解析运动数据 步数 =  " + objects.getSportStep());
//                            Log.d(TAG, "解析运动数据 卡路里 =  " + objects.getCalory());
//                            Log.d(TAG, "解析运动数据 距离 =  " + objects.getDistance());
//                            Log.d(TAG, "解析运动数据 数据 =  " + objects.getSport_data().toString());
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 0);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                String sportData = objects.getData();
                boolean isSharpe = true;
                if (objects.equals(nextDay)) {
                    isSharpe = true;
                } else {
                    isSharpe = false;
                }
                String homeTime = (String) SharedPreferenceUtil.get(getActivity(), "upSportTime", "2017-11-02 15:00:00");
                int sportStep = objects.getSportStep();
                String calory = String.valueOf(objects.getCalory());
                String distance = String.valueOf(objects.getDistance());

                if (sportStep >= 0) {
                    STEP = objects.getSportStep();
                }
                if (!WatchUtils.isEmpty(calory)) {
                    CALORIES = Integer.valueOf(calory.split("[.]")[0]);
                }
                if (!WatchUtils.isEmpty(distance)) {
                    DISTANCE = Integer.valueOf(distance.split("[.]")[0]);
                }
                SharedPreferenceUtil.put(getContext(), "step_number", STEP + "");


                if (!TextUtils.isEmpty(homeTime)) {
                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
                    int number = Integer.valueOf(timeDifference.trim());
                    int number2 = Integer.parseInt(timeDifference.trim());
                    if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
                        //上传运动数据
                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
//                        Log.d("====解析运动数据=isSharpe=1==", isSharpe + "=========" + number);
                    }
                } else {
                    //上传运动数据
                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
                }
                if (isSharpe) {
                    SharedPreferenceUtil.put(getActivity(), "upSportTime", B18iUtils.getSystemDataStart());
                }
//                                String upSportTime = (String) SharedPreferenceUtil.get(getActivity(), "upSportTime", "2017-11-02 15:00:00");
//                                if (!TextUtils.isEmpty(upSportTime)) {
//                                    String timeDifference = H9TimeUtil.getTimeDifferencesec(upSportTime, B18iUtils.getSystemDataStart());
//                                    int number = Integer.valueOf(timeDifference.trim());
//                                    int number2 = Integer.parseInt(timeDifference.trim());
//                                    if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
//                                        //上传运动数据
//                                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
//                                        Log.d("====解析运动数据=isSharpe=1==", isSharpe + "========="+number);
//                                        if (isSharpe) {
//                                            SharedPreferenceUtil.put(getActivity(),"upSportTime",B18iUtils.getSystemDataStart());
//                                        }
//                                    }
//                                } else {
//                                    //上传运动数据
//                                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
////                                Log.d("====解析运动数据=isSharpe=2==", isSharpe + "");
//                                    if (isSharpe) {
//                                        SharedPreferenceUtil.put(getActivity(),"upSportTime",B18iUtils.getSystemDataStart());
//                                    }
//                                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHidden) {

                            String w30stag = (String) SharedPreferenceUtil.get(context, "w30stag", "10000");
                            GOAL = Integer.valueOf(w30stag);
                            double v = (double) STEP / (double) GOAL;
                            String s = String.valueOf(v * 100).split("[.]")[0] + "%";
                            if (textStepReach != null) textStepReach.setText(s);
                            String data = objects.getData();
                            if (!TextUtils.isEmpty(data)) {
                                if (watchRecordtopDateTv != null)
                                    watchRecordtopDateTv.setText(data);
                                if (L38iCalT != null)
                                    L38iCalT.setText("" + CALORIES + "");
                                boolean w30sunit = (boolean) SharedPreferenceUtil.get(getContext(), "w30sunit", true);
                                if (w30sunit) {
                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_mi);// 获取res下的图片drawable
                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
                                    if (L38iDisT != null)
                                        L38iDisT.setCompoundDrawables(null, top, null, null);
                                    //L38iDisT.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);

                                    if (L38iDisT != null)
                                        L38iDisT.setText("" + DISTANCE + "");
                                } else {
                                    //image_w30s_unti_ft
                                    float distances = objects.getDistance();
//                                                int round = (int) Math.round();
                                    int round = (int) Math.floor(distances * 3.28);
//                                                Log.d("------round------", round + "");
                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_unti_ft);// 获取res下的图片drawable
                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
                                    if (L38iDisT != null)
                                        L38iDisT.setCompoundDrawables(null, top, null, null);
                                    //L38iDisT.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);

                                    if (L38iDisT != null)
                                        L38iDisT.setText(String.valueOf(round).split("[.]")[0]);
                                }

                                if (recordwaveProgressBar != null) {
                                    recordwaveProgressBar.postInvalidate();
                                    recordwaveProgressBar.setValue(STEP);
                                    recordwaveProgressBar.postInvalidate();
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

        }


        @Override
        public void CallDatasBackListenter(W30SSleepData sleepDatas) {
//            Log.d(TAG, "解析睡眠数据 = 日期 = " + sleepDatas.getSleepData());
            try {
                Log.d(TAG, "解析睡眠数据 = 数据 = " + sleepDatas.getSleepDataList().toString());
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                String sleepData = sleepDatas.getSleepData();
                SHALLOW = 0;
                DEEP = 0;
                AWAKE = 0;
                AOYE = 0;
                ALLTIME = 0;
                AWAKE2 = 0;
                if (sleepDataList != null) {
                    sleepDataList.clear();
                    sleepDataList = null;
                }
                sleepDataList = sleepDatas.getSleepDataList();
                boolean isSharpe = true;
                if (sleepData.equals(nextDay)) {
                    Log.d("====解析睡眠数据====", sleepDatas.getSleepDataList().toString());
                    isSharpe = true;
                } else {
                    isSharpe = false;
                }

                if (sleepDataList != null || sleepDataList.size() > 0) {
                    for (int i = 0; i < sleepDataList.size(); i++) {
                        String startTime = null;
                        String startTimeLater = null;
                        String sleep_type = null;
                        if (i >= (sleepDataList.size() - 1)) {
                            startTime = sleepDataList.get(i).getStartTime();
                            startTimeLater = sleepDataList.get(i).getStartTime();
                            sleep_type = sleepDataList.get(i).getSleep_type();
                        } else {
                            startTime = sleepDataList.get(i).getStartTime();
                            startTimeLater = sleepDataList.get(i + 1).getStartTime();
                            sleep_type = sleepDataList.get(i).getSleep_type();
                        }
//                String timeExpend = W30BasicUtils.getTimeExpend(yearTime + " " + startTime, yearTime + " " + startTimeLater);
                        String[] starSplit = startTime.split("[:]");
                        String[] endSplit = startTimeLater.split("[:]");

                        int startHour = Integer.valueOf(starSplit[0]);
                        int endHour = Integer.valueOf(endSplit[0]);

                        int startMin = Integer.valueOf(starSplit[1]);
                        int endMin = (Integer.valueOf(endSplit[1]));
                        //Log.d("----------------", "开始时：" + startHour + "结束时：" + endHour + "开始分：" + startMin + "结束分：" + endMin);
                        //String timeExpend = "";
                        //Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
                        //String yearTime = W30BasicUtils.dateToString(dateBefore, "yyyy-MM-dd");
                        //Date dateBeforeNew = H9TimeUtil.getDateBefore(new Date(), 0);
                        //String yearTimeNew = W30BasicUtils.dateToString(dateBeforeNew, "yyyy-MM-dd");
                        if (startHour > endHour) {
                            endHour = endHour + 24;
                        }
                        //timeExpend = W30BasicUtils.getTimeExpend(yearTimeNew + " " + startTime, yearTimeNew + " " + startTimeLater);
                        //String[] split = timeExpend.split("[:]");
                        //double all_m = Math.abs(Integer.valueOf(split[0])) * 60 + Math.abs(Integer.valueOf(split[1]));
                        int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                        //Log.e("----------timeExpend--------", all_m + "-----type---" + sleep_type);
                        if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
                            AWAKE2 += all_m;
                            ALLTIME += all_m;
                        } else if (sleep_type.equals("4")) {
                            AWAKE2 += all_m;
                            ALLTIME += all_m;
                            AWAKE++;
                        } else if (sleep_type.equals("2")) {
                            //潜水
                            SHALLOW += all_m;
                            ALLTIME += all_m;
                        } else if (sleep_type.equals("3")) {
                            //深水
                            DEEP += all_m;
                            ALLTIME += all_m;
                        }
                    }
                }
                //Log.e("==========睡眠=", DEEP + "===" + SHALLOW);
//                                double SLEEPAOYE = 0.00;
//                                double SLEEPRUSHUI = 0.00;
//                                double SLEEPQIANSHUI = 0.00;
//                                double SLEEPSHENSHUI = 0.00;
//                                double SLEEPSHUIXING = 0.00;
//                                double SLEEPTUICHUSHUI = 0.00;
//                                String SleepInto = "00:00";//入睡时间
//                                int SleepWakeCount = 0;//苏醒次数
//                                String SleepWakeTime = "00:00";//苏醒时间
//                                boolean isOne = true;
//
//
//                                List<W30S_SleepDataItem> sleepDataList = sleepDatas.getSleepDataList();
////                            自定义测试假数据
////                            List<W30S_SleepDataItem> sleepDataList = new ArrayList<W30S_SleepDataItem>();
////                            for (int i = 0; i < 12; i++) {
////                                W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
////                                if (i < 2) {
////                                    w30S_sleepDataItem.setSleep_type("0");
////                                    if (i == 1) {
////                                        w30S_sleepDataItem.setStartTime("23:30");
////                                    } else {
////                                        w30S_sleepDataItem.setStartTime("24:12");
////                                    }
////                                } else if (i == 2) {
////                                    w30S_sleepDataItem.setSleep_type("1");
////                                    w30S_sleepDataItem.setStartTime("24:50");
////                                } else if (2 < i && i < 5) {
////                                    w30S_sleepDataItem.setSleep_type("2");
////                                    if (i == 3) {
////                                        w30S_sleepDataItem.setStartTime("01:12");
////                                    } else if (i == 4) {
////                                        w30S_sleepDataItem.setStartTime("02:12");
////                                    }
////                                } else if (4 < i && i < 8) {
////                                    w30S_sleepDataItem.setSleep_type("3");
////                                    if (i == 5) {
////                                        w30S_sleepDataItem.setStartTime("03:15");
////                                    } else if (i == 6) {
////                                        w30S_sleepDataItem.setStartTime("04:12");
////                                    } else if (i == 7) {
////                                        w30S_sleepDataItem.setStartTime("05:22");
////                                    }
////                                } else if (7 < i && i < 12) {
////                                    w30S_sleepDataItem.setSleep_type("4");
////                                    if (i == 8) {
////                                        w30S_sleepDataItem.setStartTime("06:15");
////                                    } else if (i == 9) {
////                                        w30S_sleepDataItem.setStartTime("07:12");
////                                    } else if (i == 10) {
////                                        w30S_sleepDataItem.setStartTime("08:22");
////                                    } else if (i == 10) {
////                                        w30S_sleepDataItem.setStartTime("09:22");
////                                    } else if (i == 10) {
////                                        w30S_sleepDataItem.setStartTime("10:22");
////                                    }
////                                } else if (i == 12) {
////                                    w30S_sleepDataItem.setSleep_type("5");
////                                    w30S_sleepDataItem.setStartTime("11:22");
////                                }
////                                sleepDataList.add(w30S_sleepDataItem);
////                            }
//                                for (int i = 1; i <= sleepDataList.size(); i++) {
////                                Log.d("--------->>>==", "数据串" + sleepDataList.get(i - 1).toString() + "类型==="
////                                        + sleepDataList.get(i - 1).getSleep_type()
////                                        + "时间===" + sleepDataList.get(i - 1).getStartTime());
//                                    //String sleep_type = sleepDataList.get(i - 1).getSleep_type();
//                                    String startTime = sleepDataList.get(i - 1).getStartTime();
//                                    String startTimeLater;
//                                    if (i == sleepDataList.size()) {
//                                        startTimeLater = sleepDataList.get(i - 1).getStartTime();
//                                    } else {
//                                        startTimeLater = sleepDataList.get(i).getStartTime();
//                                    }
//                                    String timeExpend;
//
//                                    String[] t = startTime.split("[:]");
//                                    String[] tl = startTimeLater.split("[:]");
//
//                                    Log.d("--------->>>==", "----------------------------------------------" + t[0] + "----" + t[1]);
//                                    Log.d("--------->>>==", "-----------------------AAA-----------------------" + t[0] + "----" + tl[0]);
//
//                                    if ((Integer.valueOf(t[0]) >= 22)) {
//                                        Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
//                                        String yearTime = W30BasicUtils.dateToString(dateBefore, "yyyy-MM-dd");
//                                        timeExpend = W30BasicUtils.getTimeExpend(yearTime + " " + startTime,
//                                                sleepDatas.getSleepData() + " " + startTimeLater);
//                                        Log.d("--------->>>==", "--------------------------1--------------------" + timeExpend);
//                                    } else {
//                                        timeExpend = W30BasicUtils.getTimeExpend(sleepDatas.getSleepData() + " " + startTime,
//                                                sleepDatas.getSleepData() + " " + startTimeLater);
//                                        Log.d("--------->>>==", "--------------------------2--------------------" + timeExpend);
//                                    }
//
//                                    if (Integer.valueOf(tl[0]) <= 24 && Integer.valueOf(tl[0]) >= 22) {
//
//                                        Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
//                                        String yearTime = W30BasicUtils.dateToString(dateBefore, "yyyy-MM-dd");
//                                        timeExpend = W30BasicUtils.getTimeExpend(yearTime + " " + startTime,
//                                                yearTime + " " + startTimeLater);
//                                        Log.d("--------->>>==", "--------------------------3--------------------" + timeExpend);
//                                    }
//
//
//                                    Log.d("--------->>>==", "----------------------------------------------" + timeExpend);
//                                    String[] split = timeExpend.split("[:]");
//
//
//                                    double all_m = Math.abs(Integer.valueOf(split[0])) * 60 + Math.abs(Integer.valueOf(split[1]));
////                                double all_m = starhour * 60 + Integer.valueOf(split[1]);
//                                    Log.d("--------->>>==", "-------------all_m---------------------------------" + all_m);
//
//
//                                    switch (sleepDataList.get(i - 1).getSleep_type()) {
//                                        case "0":
//                                            SLEEPAOYE = SLEEPAOYE + all_m;
//                                            int div0 = (int) WatchUtils.div(SLEEPRUSHUI, 60, 0);
////                                        int div0 = (int) WatchUtils.div(SLEEPAOYE, 60, 0);
//                                            //ALLTIME = div0;
//                                            Log.d("--------->>>==", "--熬夜--------------------------------------------" + String.valueOf(SLEEPAOYE).split("[.]")[0] + "====" + SLEEPAOYE);
//                                            ALLTIME = Integer.valueOf(String.valueOf(SLEEPAOYE).split("[.]")[0]);
//                                            AOYE = Integer.valueOf(String.valueOf(SLEEPAOYE).split("[.]")[0]);
//                                            break;
//                                        case "1":
//                                            SLEEPRUSHUI = SLEEPRUSHUI + all_m;
//                                            int div1 = (int) WatchUtils.div(SLEEPRUSHUI, 60, 0);
//                                            if (isOne) {
//                                                SleepInto = startTime;
//                                                ALLTIME = Integer.valueOf(String.valueOf(SLEEPRUSHUI).split("[.]")[0]);
//                                                isOne = false;
//                                            }
//                                            Log.d("--------->>>==", "--入睡--------------------------------------------" + div1 + "====" + SLEEPRUSHUI);
//                                            break;
//                                        case "2":
//                                            SLEEPQIANSHUI = SLEEPQIANSHUI + all_m;
//                                            int div2 = (int) WatchUtils.div(SLEEPQIANSHUI, 60, 0);
//                                            Log.d("--------->>>==", "--浅睡--------------------------------------------" + div2 + "====" + SLEEPQIANSHUI);
//                                            SHALLOW = Integer.valueOf(String.valueOf(SLEEPQIANSHUI).split("[.]")[0]);
//                                            ALLTIME = Integer.valueOf(String.valueOf(SLEEPQIANSHUI).split("[.]")[0]);
//                                            break;
//                                        case "3":
//                                            SLEEPSHENSHUI = SLEEPSHENSHUI + all_m;
//                                            int div3 = (int) WatchUtils.div(SLEEPSHENSHUI, 60, 0);
//                                            Log.d("--------->>>==", "--深睡--------------------------------------------" + div3 + "====" + SLEEPSHENSHUI);
//                                            DEEP = Integer.valueOf(String.valueOf(SLEEPSHENSHUI).split("[.]")[0]);
//                                            ALLTIME = Integer.valueOf(String.valueOf(SLEEPSHENSHUI).split("[.]")[0]);
//                                            break;
//                                        case "4":
//                                            SLEEPSHUIXING = SLEEPSHUIXING + all_m;
//                                            int div4 = (int) WatchUtils.div(SLEEPSHUIXING, 60, 0);
//                                            Log.d("--------->>>==", "--醒来--------------------------------------------" + div4 + "====" + SLEEPSHUIXING);
//                                            AWAKE = Integer.valueOf(String.valueOf(SLEEPSHUIXING).split("[.]")[0]);
//                                            ALLTIME = Integer.valueOf(String.valueOf(SLEEPSHUIXING).split("[.]")[0]);
//                                            SleepWakeCount++;
//                                            SleepWakeTime = startTime;
//                                            break;
//                                        case "5":
//                                            SLEEPTUICHUSHUI = SLEEPTUICHUSHUI + all_m;
//                                            int div5 = (int) WatchUtils.div(SLEEPSHUIXING, 60, 0);
//                                            ALLTIME = Integer.valueOf(String.valueOf(SLEEPSHUIXING).split("[.]")[0]);
//                                            SleepWakeTime = startTime;
//                                            Log.d("--------->>>==", "--退出--------------------------------------------" + div5 + "====" + SLEEPTUICHUSHUI);
//                                            break;
//                                    }
//                                }
//
//                                final String finalSleepInto = SleepInto;
//                                final int finalSleepWakeCount = SleepWakeCount;
//                                final String finalSleepWakeTime = SleepWakeTime;
//Log.d("--------->>>==", "--时间sssssssss----------------" + AWAKE2 + "===" + DEEP + "====" + SHALLOW);
//                                String d = String.valueOf(DEEP);
//                                final String s = String.valueOf(SHALLOW);
//                                String[] splitD = d.split("[.]");
//                                String[] splitS = s.split("[.]");
                String homeTime = (String) SharedPreferenceUtil.get(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
                if (!TextUtils.isEmpty(homeTime)) {
                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
                    int number = Integer.valueOf(timeDifference.trim());
                    int number2 = Integer.parseInt(timeDifference.trim());
                    if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
                        if (DEEP > 0 || SHALLOW > 0) {
                            UpDatasBase.upDataSleep(DEEP + "", SHALLOW + "", sleepData);//上传睡眠数据
                        }

                    }
                } else {
                    if (DEEP > 0 || SHALLOW > 0) {
                        UpDatasBase.upDataSleep(DEEP + "", SHALLOW + "", sleepData);//上传睡眠数据
                    }

                }
                if (isSharpe) {
                    SharedPreferenceUtil.put(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.e("==========睡眠=", DEEP + "===" + SHALLOW);
                        //Log.d("--------->>>==", "浅睡：" + SHALLOW + "===" + "深睡：" + DEEP + "===" + "清醒：" + (AWAKE + AOYE) +
                        //                    "入睡时间：" + finalSleepInto + "苏醒次数：" + finalSleepWakeCount + "苏醒时间：" + finalSleepWakeTime + "===所有时间：" + ALLTIME);
                        if (isHidden) {
                            if (sleepDataList == null && sleepDataList.size() <= 0) {
                                if (w30S_sleepChart != null)
                                    w30S_sleepChart.setVisibility(View.GONE);
                                if (text_sleep_nodata != null)
                                    text_sleep_nodata.setVisibility(View.VISIBLE);
                            } else {
                                if (w30S_sleepChart != null)
                                    w30S_sleepChart.setVisibility(View.VISIBLE);
                                if (text_sleep_nodata != null)
                                    text_sleep_nodata.setVisibility(View.GONE);
                                if (w30S_sleepChart != null)
                                    w30S_sleepChart.setBeanList(sleepDataList);


                                //入睡时间
                                if (sleep_into_time != null) {
                                    sleep_into_time.setVisibility(View.VISIBLE);
                                    if (sleepDataList.size() > 0) {
                                        Log.d("=========aaa==", sleepDataList.get(0).getStartTime());
                                        sleep_into_time.setText(sleepDataList.get(0).getStartTime());
                                    }
                                }
                                //醒来时间
                                if (sleep_out_time != null) {
                                    sleep_out_time.setVisibility(View.VISIBLE);
                                    if (sleepDataList.size() > 0)
                                        sleep_out_time.setText(sleepDataList.get(sleepDataList.size() - 1).getStartTime());
                                }

                                //总睡眠设置为可拖动最大进度
                                if (SleepseekBar != null) {
                                    SleepseekBar.setMax(ALLTIME);
                                    SleepseekBar.setEnabled(true);
                                }

                                DecimalFormat df = new DecimalFormat("#.#");
                                df.setRoundingMode(RoundingMode.FLOOR);
                                String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60);

                                //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);

                                if (deepSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
                                    deepSleep.setText(setScale + getResources().getString(R.string.hour));
                                }
                                if (shallowSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
                                    if (!WatchUtils.isEmpty(div3)) {
                                        double v = Double.valueOf(div3) - setScale;
                                        if (v > 0) {
                                            double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
                                            //double setScale = (double) WatchUtils.div((double) SHALLOW, 60, 1);
                                            shallowSleep.setText(setScale1 + getResources().getString(R.string.hour));
                                        }
                                    }
                                }

                                if (awakeSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) AWAKE2, 60, 1);
                                    awakeSleep.setText(setScale + getResources().getString(R.string.hour));
                                }

                                double hour = (double) (DEEP + SHALLOW) / (double) 60;
                                if (hour <= 0) return;
                                String format = new DecimalFormat("0.00").format(hour);
                                String[] split = format.split("[.]");
                                int integer = Integer.valueOf(split[0]);
                                String s1 = String.valueOf(((hour - integer) * 60));
                                String[] split1 = s1.split("[.]");
                                String a = "0";
                                if (split1[0] != null) {
                                    a = split1[0];
                                }

                                String w30ssleep = (String) SharedPreferenceUtil.get(getContext(), "w30ssleep", "8");
                                if (!WatchUtils.isEmpty(w30ssleep)) {
                                    int standardSleepAll = Integer.valueOf(w30ssleep) * 60;
                                    int allSleep = integer * 60 + Integer.valueOf(a);
                                    double standardSleep = (double) WatchUtils.div(allSleep, standardSleepAll, 2);
                                    //int standar = (allSleep / standardSleepAll) * 100;
                                    String strings = String.valueOf((standardSleep * 100));
                                    if (textAllSleepData != null) {
                                        textAllSleepData.setVisibility(View.VISIBLE);
                                        if (strings.contains(".")) {
                                            textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 +
                                                    getResources().getString(R.string.hour)
                                                    + "  " + getResources().getString(R.string.string_standar) + "  " + strings.split("[.]")[0] + "%"
                                                    + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
                                        } else {
                                            textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 +
                                                    getResources().getString(R.string.hour)
                                                    + "  " + getResources().getString(R.string.string_standar) + "  " + (standardSleep * 100) + "%"
                                                    + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
                                        }
                                    }
                                } else {
                                    if (textAllSleepData != null) {
                                        textAllSleepData.setVisibility(View.VISIBLE);
                                        textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 +
                                                getResources().getString(R.string.hour) + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
                                    }
                                }


                                double v = SHALLOW + AWAKE2 + DEEP;
                                if (qianshuiT != null) {
                                    double v1 = WatchUtils.div(SHALLOW, v, 2);
                                    if (v1 > 0) {
                                        qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }
                                if (qingxingT != null) {
                                    double v1 = WatchUtils.div(AWAKE2, v, 2);
                                    if (v1 > 0) {
                                        qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }
                                if (shenshuiT != null) {
                                    double v1 = WatchUtils.div(DEEP, v, 2);
                                    if (v1 > 0) {
                                        shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }

                            }
                            //setH9PieCharts(AWAKE2, DEEP, SHALLOW);
                        }
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        @Override
        public void CallDatasBackListenter(final W30SDeviceData objects) {
            if (objects == null) return;
//                        Log.d(TAG, "解析设备信息 = 设备电量 = " + objects.getDevicePower());
//                        Log.d(TAG, "解析设备信息 = 设备类型 = " + objects.getDeviceType());
//                        Log.d(TAG, "解析设备信息 = 设备版本 = " + objects.getDeviceVersionNumber());
            SharedPreferenceUtil.put(context, "W30S_V", String.valueOf(objects.getDeviceVersionNumber()));
            SharedPreferenceUtil.put(context, "W30S_P", String.valueOf(objects.getDeviceVersionNumber()));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (isHidden)
                            setBatteryPowerShow(objects.getDevicePower());   //显示电量
                    } catch (Exception e) {
                        e.getMessage();
                    }

                }
            });
        }

        @Override
        public void CallDatasBackListenter(W30SHeartData objects) {
            if (objects == null) return;
//          Log.d(TAG, "解析运动心率 日期 =  " + objects.getDate());
            //Log.d(TAG, "解析运动心率 数据 =  " + objects.getWo_heart_data().toString());
            try {
                isHaertNull = false;
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 0);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                String das = objects.getDate();
                boolean isSharpe = true;
                if (das.equals(nextDay)) {
                    isSharpe = true;
                } else {
                    isSharpe = false;
                }

                String homeTime = (String) SharedPreferenceUtil.get(context, "upHeartTime", "2017-11-02 15:00:00");
                String heartHour = "";
                final List wo_heart_data = objects.getWo_heart_data();
                if (wo_heart_data == null || wo_heart_data.size() <= 0) return;
                if (heartDatas != null) {
                    heartDatas.clear();
                }
                if (heartDatasMaxOrLad != null) {
                    heartDatasMaxOrLad.clear();
                }
                for (int i = 0; i < wo_heart_data.size(); i++) {
                    if (!WatchUtils.isEmpty(wo_heart_data.get(i).toString().trim()) && Integer.valueOf(wo_heart_data.get(i).toString().trim()) > 0) {
                        //Log.d("===================添加=", wo_heart_data.get(i).toString().trim());
                        heartDatasMaxOrLad.add(Integer.valueOf(wo_heart_data.get(i).toString().trim()));
                    }
                }
                for (int i = 1; i <= 24; i++) {
                    String datas = (String) wo_heart_data.get((12 * i) - 1).toString();
                    int xl2 = 0;
                    int ValueCount = 0;
                    for (int j = (12 * i) - 12; j <= (12 * i) - 1; j++) {
                        if ((int) wo_heart_data.get(j) > 0) {
                            ValueCount++;
                        }
                        xl2 += (int) wo_heart_data.get(j);
//                      Log.d("===AAAAA===", "===rrrr==dd==" + i + "================" + xl2 + "=========" + wo_heart_data.get(j));
                    }
                    if (ValueCount == 0) {
                        ValueCount = 1;
                    }
                    datas = String.valueOf((xl2 / ValueCount)).split("[.]")[0];//取每半小时的平均心率
//                  Log.d("===AAAAA===", "====d===" + i + "================" + xl2 + "===" + xl2 / ValueCount + "=======================" + datas);
                    int i1 = (wo_heart_data.size() / (wo_heart_data.size() / i)) - 1;
                    if (i1 <= 9) {
                        heartHour = "0" + i1 + ":" + "00" + ":";
                    } else {
                        heartHour = i1 + ":" + "00" + ":";
                    }
                    String[] split = objects.getDate().trim().split("[-]");
                    String times = "";
                    for (int j = 0; j < split.length; j++) {
                        times += split[j] + "/";
                    }
                    String tim = times.substring(0, times.length() - 1).trim() + " " + heartHour;
                    long lon = W30BasicUtils.getStringToDate(tim, "yyyy/MM/dd HH:mm");
//                  Log.d("===AAAAA===", tim);
//                  Log.d("===AAAAA===", datas + "===" + tim);
                    W30SHeartDataS heartData = new W30SHeartDataS();
                    heartData.setTime(lon);
                    heartData.setValue(Integer.valueOf(datas));
                    heartDatas.add(heartData);
                }
                String dataTime;
                for (int i = 1; i <= 48; i++) {
                    String datas = "";
                    int xl = 0;
                    int ValeCont = 0;
                    for (int j = (6 * i) - 6; j <= (6 * i) - 1; j++) {
                        if ((int) wo_heart_data.get(j) > 0) {
                            ValeCont++;
                        }
                        xl += (int) wo_heart_data.get(j);
                    }
                    if (ValeCont == 0) {
                        ValeCont = 1;
                    }
                    datas = String.valueOf((xl / ValeCont)).split("[.]")[0];//取每半小时的平均心率
//                                Log.d("====assssss==", "=======" + i + "================" + xl + "===" + xl / ValeCont + "=======================" + datas);
                    String[] split = objects.getDate().trim().split("[-]");
                    String times = "";
                    for (int j = 0; j < split.length; j++) {
                        times += split[j] + "/";
                    }
                    double timesHour = (double) ((i - 1) * 0.5);
                    int hours = 0;
                    int mins = 0;
                    String[] splitT = String.valueOf(timesHour).split("[.]");
                    if (splitT.length >= 2) {
                        hours = Integer.valueOf(splitT[0]);
                        mins = Integer.valueOf(splitT[1]) * 60 / 10;
                    } else {
                        hours = Integer.valueOf(splitT[0]);
                        mins = 0;
                    }
                    String timeHour = "";
                    String timeMin = "";
                    if (hours <= 9) {
                        timeHour = "0" + hours;
                    } else {
                        timeHour = "" + hours;
                    }
                    if (mins <= 9) {
                        timeMin = "0" + mins;
                    } else {
                        timeMin = "" + mins;
                    }
                    String upDataTime = timeHour + ":" + timeMin;
                    String tim = times.substring(0, times.length() - 1).trim() + " " + upDataTime;
//                                Log.d("====assssss==", tim);
//                                Log.d("====assssss==", datas + "===" + i + "===" + tim);
//                    if (Integer.valueOf(datas) > 0) {
//                        NowVale = datas;
//                    }


                    if (!TextUtils.isEmpty(homeTime)) {
                        String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
                        if (!WatchUtils.isEmpty(timeDifference)) {
                            int number = Integer.valueOf(timeDifference.trim());
                            int number2 = Integer.parseInt(timeDifference.trim());
                            if (number >= 54000 || number2 >= 54000) {
                                //Log.d("====解析睡眠数据=isSharpe=1==", isSharpe + "====" + number);
                                UpDatasBase.upDataHearte(String.valueOf(datas), tim);//上传心率
                            }
                        }
                    } else {
                        UpDatasBase.upDataHearte(String.valueOf(datas), tim);//上传心率
                    }
//                   UpDatasBase.upDataHearte(String.valueOf(datas), tim);//上传心率
                }
                if (isSharpe) {
                    SharedPreferenceUtil.put(context, "upHeartTime", B18iUtils.getSystemDataStart());
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getAxisLables();
                        getAxisPoints();
                        initLineChart();
                        if (textStute != null) {
                            textStute.setVisibility(View.GONE);
                        }
//                        if (autoHeartTextNumber != null) {
//                            if (Integer.valueOf(NowVale) >= 0) {
//                                autoHeartTextNumber.setText(NowVale + "bpm");
//                            }
//                        }
                        NowVale = 0;
                        if (heartDatasMaxOrLad != null && heartDatasMaxOrLad.size() > 0) {
                            if (maxHeartTextNumber != null) {
                                int max = Collections.max(heartDatasMaxOrLad);
                                maxHeartTextNumber.setText(max + "bpm");
                            }
                            if (zuidiHeartTextNumber != null) {
                                int min = Collections.min(heartDatasMaxOrLad);
                                zuidiHeartTextNumber.setText(min + "bpm");
                            }

                            for (int i = 0; i < heartDatasMaxOrLad.size(); i++) {
                                NowVale += heartDatasMaxOrLad.get(i);
                            }
                            //除数不能为0
                            if (NowVale != 0) {
                                int i = heartDatasMaxOrLad.size() - 1;
                                if (i != 0) {
                                    double div3 = (double) WatchUtils.div((double) NowVale, i, 1);
                                    if (autoHeartTextNumber != null) {
                                        if (String.valueOf(div3).contains(".")) {
                                            String s = String.valueOf(div3).split("[.]")[0];
                                            autoHeartTextNumber.setText(s + "bpm");
                                        } else {
                                            autoHeartTextNumber.setText(div3 + "bpm");
                                        }
                                    }
                                }
                            }

                        }


                        if (textStute != null) {
                            textStute.setVisibility(View.GONE);
                        }

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
        }

        @Override
        public void CallDatasBackListenterIsok() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //closeLoadingDialog();
                    if (isHaertNull) {
                        mHandler.sendEmptyMessage(0x05);
                        return;
                    }
                    mHandler.sendEmptyMessage(0x01);
//                    stateHandler.sendEmptyMessageDelayed(222222, 6000);
                }
            });
        }
    }

//    //返回一年中的最大步数和距离卡路里
//    @Override
//    public void reabackData(int maxStep, String kacl, String disc, String dateStr) {
//        Log.d("---------------reabackData--", maxStep + "===" + kacl + "===" + disc);
//        boolean w30sunit = (boolean) SharedPreferenceUtil.get(getContext(), "w30sunit", true);
//        if (watchRecordTagstepTv != null) {
//            if (w30sunit) {
//                watchRecordTagstepTv.setText("单日最高纪录:" + dateStr+ " " +
//                        maxStep+ " " + getResources().getString(R.string.steps)
//                        + " "+ disc + "m");
//            } else {
//                int round = (int) Math.floor(Integer.valueOf(disc) * 3.28);
//                watchRecordTagstepTv.setText("单日最高纪录:" + dateStr + " " +
//                        maxStep + " " + getResources().getString(R.string.steps) + " "
//                        + round + "FT");
//            }
//
//        }
//    }

}

