package com.exalpme.bozhilun.android.w30s.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/9 18:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class FindFriendActivity extends WatchBaseActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.edit_seach)
    EditText editSeach;
    @BindView(R.id.w30s_listView)
    ListView w30sListView;
    @BindView(R.id.frend_find_ok)
    TextView frendFindOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.add_frendes));
        inEdit();
    }

    private void inEdit() {
        //rwb：取消EditText焦点，并且隐藏输入法。
        editSeach.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                editSeach.setFocusable(true);
                editSeach.setFocusableInTouchMode(true);
                editSeach.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(editSeach.getWindowToken(), 0);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
        editSeach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    frendFindOk.setVisibility(View.VISIBLE);
                } else {
                    frendFindOk.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.image_new_frend, R.id.frend_find_ok, R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_new_frend:
                startActivity(NewFindFriendActivity.class);
                break;
            case R.id.frend_find_ok:
                editSeach.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                break;
            case R.id.image_back:
                finish();
                break;
        }
    }

}
