package com.yd.org.sellpopularizesystem.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.javaBean.ResultBean;
import com.yd.org.sellpopularizesystem.utils.ActivitySkip;
import com.yd.org.sellpopularizesystem.utils.SharedPreferencesHelps;
import com.yd.org.sellpopularizesystem.utils.ToasShow;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class ChangePassWordActivity extends BaseActivity {
    private EditText oldPassWord, newPassWord, surePassWord;
    private TextView sureChangeTextview;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //修改密码
                case R.id.sureChangeTextview:
                    getInfo();
                    break;
            }
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.activity_change_pass_word;
    }

    @Override
    public void initView() {
        setTitle(getResources().getString(R.string.setting_changepassword));
        hideRightImagview();
        oldPassWord = getViewById(R.id.oldPassWord);
        newPassWord = getViewById(R.id.newPassWord);
        surePassWord = getViewById(R.id.surePassWord);
        sureChangeTextview = getViewById(R.id.sureChangeTextview);


        /**
         * 键盘是完成按钮的功能,
         */
        surePassWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getInfo();
                }
                return false;
            }
        });


    }

    private void getInfo() {
        String oldPW = oldPassWord.getText().toString().trim();
        String newPW = newPassWord.getText().toString().trim();
        String surePW = surePassWord.getText().toString().trim();


        if (TextUtils.isEmpty(oldPW)) {
            ToasShow.showToast(this, getResources().getString(R.string.old_password_ch));
            return;
        } else if (TextUtils.isEmpty(newPW)) {
            ToasShow.showToast(this, getResources().getString(R.string.new_password_ch));
            return;
        } else if (TextUtils.isEmpty(surePW)) {
            ToasShow.showToast(this, getResources().getString(R.string.new_password_ch));
            return;
        } else if (!newPW.equals(surePW)) {
            ToasShow.showToast(this, getResources().getString(R.string.sure_password_en));
            return;
        } else {
            commintInfo(oldPW, surePW);
        }


    }

    private void commintInfo(String old_password, String new_password) {
        showDialog();
        final FinalHttp fh = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        ajaxParams.put("old_password", old_password);
        ajaxParams.put("new_password", new_password);
        fh.post(Contants.CHANGE_PASSWORD, ajaxParams, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                closeDialog();
                if (null != s) {
                    Gson gs = new Gson();
                    ResultBean result = gs.fromJson(s, ResultBean.class);
                    if (result.getCode().equals("1")) {
                        ToasShow.showToast(ChangePassWordActivity.this, result.getMsg());
                        ActivitySkip.forward(ChangePassWordActivity.this, LoginActivity.class);
                        finish();

                    } else {
                        ToasShow.showToast(ChangePassWordActivity.this, result.getMsg());
                    }

                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeDialog();
                ToasShow.showToast(ChangePassWordActivity.this, getResources().getString(R.string.network_error));
            }
        });

    }

    @Override
    public void setListener() {
        sureChangeTextview.setOnClickListener(mOnClickListener);
    }
}
