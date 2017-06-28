package com.yd.org.sellpopularizesystem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.application.BaseApplication;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.javaBean.OrderDetailBean;
import com.yd.org.sellpopularizesystem.javaBean.SaleOrderBean;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class OrderDetailActivity extends BaseActivity {
    TextView tvOrderDes,tvOrderNum,tvtype,tvFirb,tvSale,tvCus,tvCusAdd,tvLawyer,tvGoal,tvPrice,tvComplete;
    @Override
    protected int setContentView() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView() {
        SaleOrderBean.ResultBean saleOrderBean= (SaleOrderBean.ResultBean) getIntent().getExtras().get("order");
        initWidgets();
        if (saleOrderBean!=null){
            getOrderDetail(saleOrderBean.getProduct_orders_id());
        }
    }

    private void getOrderDetail(int product_orders_id) {
        FinalHttp ftth=new FinalHttp();
        AjaxParams ajaxParams=new AjaxParams();
        ajaxParams.put("order_id",product_orders_id+"");
        ftth.get(Contants.ORDER_DETAIL, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                Gson gson=new Gson();
                OrderDetailBean  orderDetailBean=gson.fromJson(s,OrderDetailBean.class);
                if (orderDetailBean.getCode().equals("1") && orderDetailBean.getMsg().equals(getString(R.string.order_success_info))){
                    initData(orderDetailBean.getResult());
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    private void initData(OrderDetailBean.ResultBean result) {
        tvOrderDes.setText(result.getProduct_name()+"-"+result.getProduct_info().getProduct_childs_unit_number());
        tvOrderNum.setText(result.getProduct_orders_id()+"");
        tvtype.setText(result.getOrder_type()+"");
        tvFirb.setText(result.getIs_firb()==0?getString(R.string.yes):getString(R.string.bushi));
        tvSale.setText(result.getSales_id()+"");
        tvCus.setText(result.getCustomer_surname());
        tvCusAdd.setText(BaseApplication.getInstance().getResultBean().getAddress());
        tvLawyer.setText(result.getLawyer_name());
        tvGoal.setText(result.getPurchaseReason());
        tvPrice.setText(result.getPrice());
    }

    private void initWidgets() {
        tvOrderDes=getViewById(R.id.tvOrderDes);
        tvOrderNum=getViewById(R.id.tvOrderNum);
        tvtype=getViewById(R.id.tvtype);
        tvFirb=getViewById(R.id.tvFirb);
        tvSale=getViewById(R.id.tvSale);
        tvCus=getViewById(R.id.tvCus);
        tvCusAdd=getViewById(R.id.tvCusAdd);
        tvLawyer=getViewById(R.id.tvLawyer);
        tvGoal=getViewById(R.id.tvGoal);
        tvPrice=getViewById(R.id.tvPrice);
        tvComplete=getViewById(R.id.tvComplete);
    }

    @Override
    public void setListener() {

    }
}