package com.yd.org.sellpopularizesystem.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.adapter.SaleRecordAdapter;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.application.ExtraName;
import com.yd.org.sellpopularizesystem.internal.PullToRefreshLayout;
import com.yd.org.sellpopularizesystem.internal.PullableListView;
import com.yd.org.sellpopularizesystem.javaBean.ErrorBean;
import com.yd.org.sellpopularizesystem.javaBean.SaleOrderBean;
import com.yd.org.sellpopularizesystem.utils.ActivitySkip;
import com.yd.org.sellpopularizesystem.utils.BitmapUtil;
import com.yd.org.sellpopularizesystem.utils.SharedPreferencesHelps;
import com.yd.org.sellpopularizesystem.utils.ToasShow;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/**
 * 订单列表
 */
public class SaleRecordActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    private PullableListView lvSaleRecord;
    private PullToRefreshLayout ptrlSaleRecord;
    private int page = 1;
    private List<SaleOrderBean.ResultBean> sobRbData = new ArrayList<>();
    private SaleRecordAdapter saleAdapter;
    public static SaleRecordActivity saleRecordActivity;
    //上传合同首页还是首付款标志
    private String flag, picPath;
    private SaleOrderBean.ResultBean rBean;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getSaleData(page, true);
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.activity_sale_record;
    }

    @Override
    public void initView() {
        saleRecordActivity = this;
        hideRightImagview();
        setTitle(getString(R.string.sale_list));
        lvSaleRecord = getViewById(R.id.content_view);
        ptrlSaleRecord = getViewById(R.id.refresh_view);
        ptrlSaleRecord.setOnRefreshListener(this);
        //加载数据
        getSaleData(page, true);
    }


    private void getSaleData(int page, final boolean isRefresh) {
        showDialog();
        FinalHttp http = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("company_id", SharedPreferencesHelps.getCompanyId());
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        ajaxParams.put("page", page + "");
        ajaxParams.put("number", "50");
        http.get(Contants.INQUIRE_ORDER_LIST, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                closeDialog();
                Log.e("TAG", "onSuccess: " + s);
                if (null != s) {
                    parseJson(s, isRefresh);
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                closeDialog();
            }
        });
    }


    /**
     * 获取数据
     *
     * @param json
     * @param isRefresh
     */
    private void parseJson(String json, boolean isRefresh) {

        Gson gson = new Gson();
        SaleOrderBean saleOrderBean = gson.fromJson(json, SaleOrderBean.class);

        if (saleOrderBean.getCode() == 1) {
            sobRbData = saleOrderBean.getResult();

        }

        if (isRefresh) {
            if (sobRbData.size() == 0) {
                getViewById(R.id.noInfomation).setVisibility(View.VISIBLE);
                lvSaleRecord.setVisibility(View.GONE);
            } else {
                getViewById(R.id.noInfomation).setVisibility(View.GONE);
                lvSaleRecord.setVisibility(View.VISIBLE);
            }
            ptrlSaleRecord.refreshFinish(PullToRefreshLayout.SUCCEED);
            saleAdapter = new SaleRecordAdapter(this);
            lvSaleRecord.setAdapter(saleAdapter);
        }
        saleAdapter.addMore(sobRbData);
        ptrlSaleRecord.loadmoreFinish(PullToRefreshLayout.SUCCEED);
         locatedOrderIdPos();
    }

    private void locatedOrderIdPos() {
        if (getIntent().getExtras()!=null && getIntent().getExtras().getString("orderid")!=null){
            for (int i = 0; i <sobRbData.size() ; i++) {
                if (sobRbData.get(i).getProduct_orders_id()==Integer.parseInt(getIntent().getExtras().getString("orderid"))){
                    lvSaleRecord.setSelection(i);
                    Log.e("TAG", "initView: "+i );
                    return;
                }
            }
        }
    }


    public void startPhotos(SaleOrderBean.ResultBean resultBeans, String type) {
        rBean = resultBeans;
        flag = type;
        if (Build.VERSION.SDK_INT < 23) {
            //BitmapUtil.startImageCapture(CusOprateRecordActivity.this, ExtraName.TAKE_PICTURE);
            BitmapUtil.startImageCapture(SaleRecordActivity.this, ExtraName.TAKE_PICTURE);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new PermissionListener() {
                        @Override
                        public void onGranted() {// 全部授权成功回调
                            // 执行具体业务
                            //BitmapUtil.startImageCapture(CusOprateRecordActivity.this, ExtraName.TAKE_PICTURE);
                            BitmapUtil.startImageCapture(SaleRecordActivity.this, ExtraName.TAKE_PICTURE);
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissionList) {// 部分或全部未授权回调
                            for (String permission : deniedPermissionList) {
                                ToasShow.showToastCenter(SaleRecordActivity.this, permission.toString());
                            }
                        }
                    });
        }

    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    public void canceOrder(int orderId) {
        showDialog();
        FinalHttp http = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("order_id", orderId + "");
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        http.post(Contants.ORDER_CANCEL, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                closeDialog();
                Gson gson = new Gson();
                ErrorBean errorBean = gson.fromJson(s, ErrorBean.class);
                if (errorBean.getCode().equals("1")) {
                    ToasShow.showToastCenter(SaleRecordActivity.this, errorBean.getMsg());
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeDialog();
            }
        });
    }

    @Override
    public void setListener() {
        lvSaleRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaleRecordAdapter.ViewHolder viewHolder = (SaleRecordAdapter.ViewHolder) view.getTag();
                SaleOrderBean.ResultBean resultBean = viewHolder.resultBean;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", resultBean);
                ActivitySkip.forward(SaleRecordActivity.this, OrderDetailActivity.class, bundle);

            }
        });


    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        getSaleData(page, true);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        page++;
        getSaleData(page, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //拍照
                case ExtraName.TAKE_PICTURE:
                    if (resultCode == RESULT_OK) {
                        Uri photoUri = BitmapUtil.imgUri;
                        picPath = BitmapUtil.getImagePath(SaleRecordActivity.this, photoUri, null, null);
                        setPicPath(picPath, rBean);

                    }
                    break;


            }

        }

    }


    /**
     * 支付意向金
     *
     * @param resultBean
     */
    public void payMoney(SaleOrderBean.ResultBean resultBean) {
        Bundle bun = new Bundle();
        bun.putString("payurlId", resultBean.getOrder_money_url());
        bun.putString("payment_method", resultBean.getPayment_method() + "");
        ActivitySkip.forward(SaleRecordActivity.this, PaymentQrActivity.class, bun);

    }

    /**
     * 上传支付凭证
     *
     * @param picPath
     */
    private void setPicPath(String picPath, SaleOrderBean.ResultBean resultBean) {
        Log.e("picPath", "picPath:" + picPath);
        try {
            showDialog();
            FinalHttp finalHttp = new FinalHttp();
            AjaxParams ajaxParams = new AjaxParams();
            String strUrl = "";
            ajaxParams.put("order_id", resultBean.getProduct_orders_id() + "");

            //上次合同图片
            if (flag.equals("1")) {
                strUrl = Contants.UPLOAD_CONTRACT_PHOTO;
                if (null != picPath && !picPath.equals("")) {
                    File picFile = new File(picPath);
                    ajaxParams.put("file", picFile);
                }
            } else {
                //支付房款-上传凭证或在线支付
                strUrl = Contants.UPLOAD_FIRST_COMMISSION;
                ajaxParams.put("money_where", "1");
                ajaxParams.put("pay_method", resultBean.getPayment_method() + "");
                ajaxParams.put("pay_time", resultBean.getPay_time() + "");
                ajaxParams.put("amount", resultBean.getPrice() + "");
                ajaxParams.put("remark", resultBean.getRemark() + "");

                if (null != picPath && !picPath.equals("")) {
                    File picFile = new File(picPath);
                    ajaxParams.put("image[]", picFile);

                }

            }

            finalHttp.post(strUrl, ajaxParams, new AjaxCallBack<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.e("s**", "s:" + s);
                    closeDialog();
                    Gson gson = new Gson();
                    ErrorBean errorBean = gson.fromJson(s, ErrorBean.class);
                    if (errorBean.getCode().equals("1")) {
                        ToasShow.showToastCenter(SaleRecordActivity.this, getResources().getString(R.string.su));
                    } else {
                        ToasShow.showToastCenter(SaleRecordActivity.this, errorBean.getMsg());
                    }


                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    Log.e("s**", "s:" + errorNo);
                    ToasShow.showToastCenter(SaleRecordActivity.this, strMsg.toString());
                    closeDialog();

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void askntractO(SaleOrderBean.ResultBean resultBeans, String type) {
        SaleRecordActivity.saleRecordActivity.startPhotos(resultBeans, type);
    }
}
