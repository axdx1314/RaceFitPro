package com.exalpme.bozhilun.android.w30s.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/16 11:55
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SReminderActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.switch_Skype)
    Switch switchSkype;
    @BindView(R.id.switch_WhatsApp)
    Switch switchWhatsApp;
    @BindView(R.id.switch_Facebook)
    Switch switchFacebook;
    @BindView(R.id.switch_LinkendIn)
    Switch switchLinkendIn;
    @BindView(R.id.switch_Twitter)
    Switch switchTwitter;
    @BindView(R.id.switch_Viber)
    Switch switchViber;
    @BindView(R.id.switch_LINE)
    Switch switchLINE;
    @BindView(R.id.switch_WeChat)
    Switch switchWeChat;
    @BindView(R.id.switch_QQ)
    Switch switchQQ;
    @BindView(R.id.switch_Msg)
    Switch switchMsg;
    @BindView(R.id.switch_Phone)
    Switch switchPhone;
    @BindView(R.id.watch_msgOpenAccessBtn)
    RelativeLayout watch_msgOpenAccessBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_reminder);
        ButterKnife.bind(this);

        barTitles.setText(getResources().getString(R.string.string_application_reminding));
        watch_msgOpenAccessBtn.setVisibility(View.GONE);
        getSwitchState();
        initSwitch();
    }

    private void getSwitchState() {
        boolean w30sswitch_skype = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Skype", false);
        boolean w30sswitch_whatsApp = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_WhatsApp", false);
        boolean w30sswitch_facebook = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Facebook", false);
        boolean w30sswitch_linkendIn = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_LinkendIn", false);
        boolean w30sswitch_twitter = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Twitter", false);
        boolean w30sswitch_viber = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Viber", false);
        boolean w30sswitch_line = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_LINE", false);
        boolean w30sswitch_weChat = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_WeChat", false);
        boolean w30sswitch_qq = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_QQ", false);
        boolean w30sswitch_msg = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Msg", false);
        boolean w30sswitch_Phone = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Phone", true);
        switchSkype.setChecked(w30sswitch_skype);
        switchWhatsApp.setChecked(w30sswitch_whatsApp);
        switchFacebook.setChecked(w30sswitch_facebook);
        switchLinkendIn.setChecked(w30sswitch_linkendIn);
        switchTwitter.setChecked(w30sswitch_twitter);
        switchViber.setChecked(w30sswitch_viber);
        switchLINE.setChecked(w30sswitch_line);
        switchWeChat.setChecked(w30sswitch_weChat);
        switchQQ.setChecked(w30sswitch_qq);
        switchMsg.setChecked(w30sswitch_msg);
        switchPhone.setChecked(w30sswitch_Phone);
    }

    private void initSwitch() {
        switchSkype.setOnCheckedChangeListener(this);
        switchWhatsApp.setOnCheckedChangeListener(this);
        switchFacebook.setOnCheckedChangeListener(this);
        switchLinkendIn.setOnCheckedChangeListener(this);
        switchTwitter.setOnCheckedChangeListener(this);
        switchViber.setOnCheckedChangeListener(this);
        switchLINE.setOnCheckedChangeListener(this);
        switchWeChat.setOnCheckedChangeListener(this);
        switchQQ.setOnCheckedChangeListener(this);
        switchMsg.setOnCheckedChangeListener(this);
        switchPhone.setOnCheckedChangeListener(this);
    }


    @OnClick({R.id.image_back, R.id.watch_msgOpenNitBtn, R.id.watch_msgOpenAccessBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.watch_msgOpenNitBtn:
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 101);
                break;
            case R.id.watch_msgOpenAccessBtn:
                Intent ints = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(ints, 102);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_Skype:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Skype", isChecked);
                break;
            case R.id.switch_WhatsApp:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_WhatsApp", isChecked);
                break;
            case R.id.switch_Facebook:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Facebook", isChecked);
                break;
            case R.id.switch_LinkendIn:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_LinkendIn", isChecked);
                break;
            case R.id.switch_Twitter:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Twitter", isChecked);
                break;
            case R.id.switch_Viber:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Viber", isChecked);
                break;
            case R.id.switch_LINE:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_LINE", isChecked);
                break;
            case R.id.switch_WeChat:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_WeChat", isChecked);
                break;
            case R.id.switch_QQ:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_QQ", isChecked);
                break;
            case R.id.switch_Msg:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Msg", isChecked);
                break;
            case R.id.switch_Phone:
               SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Phone", isChecked);
                break;
        }
    }
}
