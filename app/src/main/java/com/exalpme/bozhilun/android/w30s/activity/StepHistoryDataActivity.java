package com.exalpme.bozhilun.android.w30s.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exalpme.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.siswatch.WatchBaseActivity;
import com.exalpme.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.exalpme.bozhilun.android.util.SharedPreferencesUtils;
import com.exalpme.bozhilun.android.util.URLs;
import com.exalpme.bozhilun.android.w30s.utils.ScreenUtils;
import com.exalpme.bozhilun.android.w30s.utils.W30BasicUtils;
import com.exalpme.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.exalpme.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/4/3 18:26
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class StepHistoryDataActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "StepHistoryDataActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.step_or_sleep_list)
    ListView stepOrSleepList;
    @BindView(R.id.image_data_type)
    ImageView imageDataType;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //步数的相关
    List<WatchDataDatyBean> stepList;
    @BindView(R.id.bar_mores)
    TextView barMores;
    private HistoryCustomPopuWindow customPopuWindow;//popWinow
    private StepDataAdapter stepDataAdapter;
    private RequestPressent requestPressent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_sleep_or_step_history_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.sports_history));
//        barMores.setVisibility(View.GONE);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStepData(WatchUtils.getCurrentDate());
        customPopuWindow = new HistoryCustomPopuWindow(StepHistoryDataActivity.this, listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (customPopuWindow != null && customPopuWindow.isShowing()) {
            customPopuWindow.dismiss();
        }
    }

    @OnClick({R.id.image_back, R.id.bar_mores})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                if (customPopuWindow == null) {
                    customPopuWindow = new HistoryCustomPopuWindow(StepHistoryDataActivity.this, listener);
                }
                setBack();//yyyy-MM-dd
                int screenWidth = ScreenUtils.getScreenWidth(StepHistoryDataActivity.this);
                int screenHeight = ScreenUtils.getScreenHeight(StepHistoryDataActivity.this);
                int width = screenWidth / 8 * 7;
                int height = screenHeight / 3;
                customPopuWindow.setWidth(width);
                customPopuWindow.setHeight(height);
                customPopuWindow.showAtLocation(barMores,
                        Gravity.CENTER_HORIZONTAL, 0, -(height / 3 * 2));
                break;
        }
    }

    private void setBack() {
        String currentDate2 = W30BasicUtils.getCurrentDate2();
        String substring = (String) SharedPreferenceUtil.get(StepHistoryDataActivity.this, "w30s_Step_Stuta", currentDate2.substring(5, 7));
        strlyChage();
        switch (substring) {
            case "01":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "01");
                customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "02":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "02");
                customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "03":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "03");
                customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "04":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "04");
                customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "05":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "05");
                customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "06":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "06");
                customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "07":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "07");
                customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "08":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "08");
                customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "09":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "09");
                customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "10":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "10");
                customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "11":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "11");
                customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "12":
                SharedPreferenceUtil.put(StepHistoryDataActivity.this, "w30s_Step_Stuta", "12");
                customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_selete);
                break;
        }
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customPopuWindow.dismiss();
            strlyChage();
            String currentDate2 = W30BasicUtils.getCurrentDate2();//2017-12:15
            switch (v.getId()) {
                case R.id.one_motoh:
                    customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-01-05");
                    break;
                case R.id.two_motoh:
                    customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-02-05");
                    break;
                case R.id.three_motoh:
                    customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-03-05");
                    break;
                case R.id.four_motoh:
                    customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-04-05");
                    break;
                case R.id.five_motoh:
                    customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-05-05");
                    break;
                case R.id.six_motoh:
                    customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-06-05");
                    break;
                case R.id.senve_motoh:
                    customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-07-05");
                    break;
                case R.id.eight_motoh:
                    customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-08-05");
                    break;
                case R.id.niece_motoh:
                    customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-09-05");
                    break;
                case R.id.ten_motoh:
                    customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-10-05");
                    break;
                case R.id.ten_one_motoh:
                    customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-11-05");
                    break;
                case R.id.ten_two_motoh:
                    customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-12-05");
                    break;
            }

        }
    };

    public void strlyChage() {
        customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_unselete);
    }


    /**
     * 获取运动数据
     *
     * @param timeDatas
     */
    private void getStepData(String timeDatas) {
        Log.d("---------timeData----------", timeDatas);
        if (stepList != null) {
            stepList.clear();
        }
        if (stepDataAdapter != null) {
            stepDataAdapter.notifyDataSetChanged();
        }

        boolean isToMoth = false;
        if ((WatchUtils.getCurrentDate().substring(0, 7)).equals(timeDatas.substring(0, 7))) {
            isToMoth = true;
        }
        if (!(WatchUtils.getCurrentDate().substring(5, 7)).equals(timeDatas.substring(5, 7))
                && Integer.valueOf((WatchUtils.getCurrentDate().substring(5, 7))) < Integer.valueOf(timeDatas.substring(5, 7))) {
            return;
        }
        Log.d("---------timeData----------", WatchUtils.getCurrentDate().substring(0, 7));
        //tring类型转换为date类型
        Date dateString = W30BasicUtils.stringToDate(timeDatas, "yyyy-MM-dd");
        //根据提供的年月日获取该月份的第一天
        String supportBeginDayofMonth = W30BasicUtils.getSupportBeginDayofMonth(dateString);
        //根据提供的年月获取该月份的最后一天
        String supportEndDayofMonth = W30BasicUtils.getSupportEndDayofMonth(dateString);

        String timetodateS = W30BasicUtils.timesW30s(supportBeginDayofMonth);
        String timetodateE = W30BasicUtils.timesW30s(supportEndDayofMonth);

        Date dateStart = W30BasicUtils.stringToDate(timetodateS, "yyyy-MM-dd HH:mm:ss");
        Date dateEnd = W30BasicUtils.stringToDate(timetodateE, "yyyy-MM-dd HH:mm:ss");
        Date dateDay = W30BasicUtils.stringToDate(W30BasicUtils.getCurrentDate2(), "yyyy-MM-dd");

//        Log.d(TAG, "======AAAAAa===========第一天=" + timetodateS + "----最后一天==" + timetodateE);
//        Date dateStart = W30BasicUtils.longToDate((long) Long.valueOf(supportBeginDayofMonth), "yyyy-MM-dd");
//        Date dateEnd = W30BasicUtils.longToDate((long) Long.valueOf(supportEndDayofMonth), "yyyy-MM-dd");
        Log.d(TAG, "=================第一天=" + dateStart + "----最后一天==" + dateEnd);

        //获取两个日期之间的间隔天数
        int dayNumber = W30BasicUtils.getGapCount(dateStart, dateEnd);
        int dayNumberS = W30BasicUtils.getGapCount(dateStart, dateDay);
        Log.d(TAG, "========距离多少==========" + dayNumber + "");

        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(StepHistoryDataActivity.this, "userId"));
            jsonObect.put("deviceCode", SharedPreferenceUtil.get(StepHistoryDataActivity.this, "mylanmac", ""));

            if (isToMoth) {
                jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumberS))));
            } else {
                jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumber))));
            }
            if (isToMoth) {
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                jsonObect.put("endDate", nextDay.substring(0,10));
            } else {
                jsonObect.put("endDate", timetodateE.substring(0, 10));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(1, url, StepHistoryDataActivity.this, jsonObect.toString(), 0);
        }

//
//        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
//            @Override
//            public void onNext(String result) {
//                W30BasicUtils.e(TAG, "----步数-------one-----" + result);
//                if (result != null) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(result);
//                        if (jsonObject.getString("resultCode").equals("001")) {
//                            String daydata = jsonObject.getString("day");
//                            if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
//                                stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
//                                }.getType());
//                                StepDataAdapter stepDataAdapter = new StepDataAdapter(StepHistoryDataActivity.this, stepList);
//                                stepOrSleepList.setAdapter(stepDataAdapter);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, StepHistoryDataActivity.this);
//        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObect.toString());

    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        imageDataType.setVisibility(View.VISIBLE);
        stepOrSleepList.setVisibility(View.GONE);
        if (object != null) {
            try {
                JSONObject jsonObject = new JSONObject(object.toString());
                if (jsonObject.getString("resultCode").equals("001")) {
                    String daydata = jsonObject.getString("day");
                    if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
                        stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());
                        if (stepList == null || stepList.size() <= 0) {
                            imageDataType.setVisibility(View.VISIBLE);
                            stepOrSleepList.setVisibility(View.GONE);
                        } else {
                            stepDataAdapter = new StepDataAdapter(StepHistoryDataActivity.this, stepList);
                            stepOrSleepList.setAdapter(stepDataAdapter);
                            stepOrSleepList.setVisibility(View.VISIBLE);
                            imageDataType.setVisibility(View.GONE);
                        }

                    } else {
                        imageDataType.setVisibility(View.VISIBLE);
                        stepOrSleepList.setVisibility(View.GONE);
                    }
                } else {
                    imageDataType.setVisibility(View.VISIBLE);
                    stepOrSleepList.setVisibility(View.GONE);
                    Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            imageDataType.setVisibility(View.VISIBLE);
            stepOrSleepList.setVisibility(View.GONE);
            Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        e.getMessage();
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    public class StepDataAdapter extends BaseAdapter {
        private Context mContext;
        List<WatchDataDatyBean> stepDataList;
        private LayoutInflater layoutInflater;

        public StepDataAdapter(Context mContext, List<WatchDataDatyBean> stepDataList) {
            this.mContext = mContext;
            this.stepDataList = stepDataList;
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return stepDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return stepDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.w30s_step_data_item, parent, false);
                holder = new ViewHolder();
                holder.textStepData = (TextView) convertView.findViewById(R.id.text_step_data);
                holder.textStepMi = (TextView) convertView.findViewById(R.id.text_step_mi);
                holder.textStepKcl = (TextView) convertView.findViewById(R.id.text_step_kcl);
                holder.textStepNumber = (TextView) convertView.findViewById(R.id.text_step_number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (stepDataList != null) {
                int size = stepDataList.size();
                String rtc = stepDataList.get(position).getRtc();
                int stepNumber = stepDataList.get(position).getStepNumber();
                String calories = stepDataList.get(position).getCalories();
                String distance = stepDataList.get(position).getDistance();
                Log.d("--->" + this.getClass(), "总共长度：" + size + "时间：" + rtc + "步数：" + stepNumber + "卡里路：" + calories + "距离：" + distance);
                String substring = rtc.substring(0, 10);//2017-11-21
                holder.textStepData.setText(substring);
                holder.textStepNumber.setText(stepNumber + "step");
                boolean w30sunit = (boolean) SharedPreferenceUtil.get(StepHistoryDataActivity.this, "w30sunit", true);
                if (w30sunit) {
                    double setScale = (double) WatchUtils.div((double) Double.valueOf(distance.trim()), 1000, 1);
                    holder.textStepMi.setText(setScale + "km");
                } else {
                    int round = (int) Math.round(Double.valueOf(distance) * 3.28);
                    holder.textStepMi.setText(String.valueOf(WatchUtils.div(round, 1, 2)).split("[.]")[0] + " ft");
                }
                holder.textStepKcl.setText(calories + "kcl");
            }
            return convertView;
        }

        class ViewHolder {
            TextView textStepData, textStepMi, textStepKcl, textStepNumber;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null) {
            requestPressent.detach();
        }
    }
}
