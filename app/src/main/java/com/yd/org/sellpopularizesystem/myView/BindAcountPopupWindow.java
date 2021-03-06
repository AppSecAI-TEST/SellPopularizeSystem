package com.yd.org.sellpopularizesystem.myView;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.yd.org.sellpopularizesystem.R;

/**
 * Created by hejin on 2017/3/10.
 */

public class BindAcountPopupWindow extends PopupWindow {
    private View mView;
    private Button btnBind, btnCancel;

    public BindAcountPopupWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        mView = LayoutInflater.from(context).inflate(R.layout.bind_acount_popuwindow, null);
        btnCancel = (Button) mView.findViewById(R.id.cancelButton);
        btnBind = (Button) mView.findViewById(R.id.bindAcount);
        btnCancel.setOnClickListener(itemsOnClick);
        //photoAlbumButton.setOnClickListener(itemsOnClick);
        btnBind.setOnClickListener(itemsOnClick);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
