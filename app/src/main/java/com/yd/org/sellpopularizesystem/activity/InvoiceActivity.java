package com.yd.org.sellpopularizesystem.activity;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yd.org.sellpopularizesystem.R;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

/**
 * 发票
 */

public class InvoiceActivity extends BaseActivity {
    private TextView consentTextView, resoluteTextView, subButton;
    private EditText reasonEdit;
    private RelativeLayout rlFirb;
    private View firbPwView;
    private PopupWindow firbSelectPopWindow;


    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //同意
                case R.id.consentTextView:
                    break;

                //拒绝
                case R.id.resoluteTextView:
                    firbSelectPopWindow.showAtLocation(InvoiceActivity.this.findViewById(R.id.primaryLinear), Gravity.BOTTOM, 0, 0);
                    break;

            }
        }
    };


    @Override
    protected int setContentView() {
        return R.layout.activity_invoice;
    }

    @Override
    public void initView() {
        hideRightImagview();
        setTitle(getString(R.string.invoice));

        consentTextView = getViewById(R.id.consentTextView);
        resoluteTextView = getViewById(R.id.resoluteTextView);


        showReasonInfo();

    }

    @Override
    public void setListener() {
        consentTextView.setOnClickListener(mOnClick);
        resoluteTextView.setOnClickListener(mOnClick);
    }

    private void showReasonInfo() {
        firbPwView = LayoutInflater.from(this).inflate(R.layout.invoce_layout, null);
        rlFirb = (RelativeLayout) firbPwView.findViewById(R.id.mainRelat);

        //拒绝理由
        reasonEdit = (EditText) firbPwView.findViewById(R.id.reasonEdit);
        subButton = (TextView) firbPwView.findViewById(R.id.subButton);

        firbSelectPopWindow = new PopupWindow(firbPwView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        firbSelectPopWindow.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable firb = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        firbSelectPopWindow.setBackgroundDrawable(firb);


        //提交数据
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firbSelectPopWindow.dismiss();
                String reasonString = reasonEdit.getText().toString().trim();
                summintInfo(reasonString);

            }
        });

        //点击外面取消
        rlFirb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firbSelectPopWindow.dismiss();
            }
        });


    }

    /**
     * 获取发票信息
     *
     * @param reasonString
     */
    private void getInvoiceInfo(String reasonString) {
        showDialog();

        FinalHttp finalHttp = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("", "");
        ajaxParams.put("", "");

        finalHttp.get("", ajaxParams, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                Log.e("获取发票信息","s:"+s);

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.e("获取发票信息","errorNo:"+errorNo);
            }
        });

    }

    /**
     * 提交拒绝信息
     *
     * @param reasonString
     */
    private void summintInfo(String reasonString) {
        showDialog();
        FinalHttp finalHttp = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("", "");
        ajaxParams.put("", "");

        finalHttp.post("", ajaxParams, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                Log.e("提交拒绝信息","s:"+s);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.e("提交拒绝信息","s:"+errorNo);
            }
        });

    }
}
