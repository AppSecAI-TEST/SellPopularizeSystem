package com.yd.org.sellpopularizesystem.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.adapter.CommissionAdapter;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.internal.PullToRefreshLayout;
import com.yd.org.sellpopularizesystem.internal.PullableListView;
import com.yd.org.sellpopularizesystem.javaBean.CommissionBean;
import com.yd.org.sellpopularizesystem.utils.ActivitySkip;
import com.yd.org.sellpopularizesystem.utils.SharedPreferencesHelps;
import com.yd.org.sellpopularizesystem.utils.ToasShow;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

public class CommissionActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    private PullableListView listView;
    private PullToRefreshLayout ptrl;
    private int page = 1;
    private List<CommissionBean.ResultBean> datas = new ArrayList<>();
    private CommissionAdapter commissionAdapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_commission;
    }

    @Override
    public void initView() {
        hideRightImagview();
        setTitle(getString(R.string.commission));
        //下拉加载
        ptrl = getViewById(R.id.refresh_view);
        ptrl.setOnRefreshListener(this);
        listView = getViewById(R.id.content_view);
        getInfo(1, true);

    }

    @Override
    public void setListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommissionAdapter.ViewHoler viewHoler = (CommissionAdapter.ViewHoler) view.getTag();
                CommissionBean.ResultBean resultBean = viewHoler.resultBean;
                Bundle bundle = new Bundle();
                bundle.putSerializable("commission", resultBean);
                ActivitySkip.forward(CommissionActivity.this, CommissionDetailsActivity.class, bundle);

            }
        });

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        getInfo(page, true);

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

        page++;
        getInfo(page, false);

    }

    private void getInfo(int page, final boolean isRefresh) {
        showDialog();
        FinalHttp finalHttp = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        ajaxParams.put("customer_id", "");
        ajaxParams.put("page", page + "");
        ajaxParams.put("number", "10");

        finalHttp.get(Contants.COMMOSSION_LIST, ajaxParams, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                closeDialog();
                if (null != s) {
                    paserJSON(s, isRefresh);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                closeDialog();
                ToasShow.showToastCenter(CommissionActivity.this, strMsg);
            }
        });


    }

    private void paserJSON(String data, boolean isRefresh) {

        Gson s = new Gson();
        CommissionBean commission = s.fromJson(data, CommissionBean.class);
        datas = commission.getResult();

        if (isRefresh) {
            ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
            commissionAdapter = new CommissionAdapter(CommissionActivity.this);
            listView.setAdapter(commissionAdapter);
        }
        ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
        commissionAdapter.addMore(datas);


    }
}
