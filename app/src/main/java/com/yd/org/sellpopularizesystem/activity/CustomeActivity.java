package com.yd.org.sellpopularizesystem.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.adapter.SortGroupMemberAdapter;
import com.yd.org.sellpopularizesystem.application.BaseApplication;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.application.ExtraName;
import com.yd.org.sellpopularizesystem.custom.CharacterParser;
import com.yd.org.sellpopularizesystem.custom.PinyinComparator;
import com.yd.org.sellpopularizesystem.custom.SideBar;
import com.yd.org.sellpopularizesystem.internal.PullToRefreshLayout;
import com.yd.org.sellpopularizesystem.internal.PullableListView;
import com.yd.org.sellpopularizesystem.javaBean.CustomBean;
import com.yd.org.sellpopularizesystem.javaBean.LawyerBean;
import com.yd.org.sellpopularizesystem.myView.SearchEditText;
import com.yd.org.sellpopularizesystem.utils.ActivitySkip;
import com.yd.org.sellpopularizesystem.utils.ObjectSaveUtil;
import com.yd.org.sellpopularizesystem.utils.SharedPreferencesHelps;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 客户管理
 */
public class CustomeActivity extends BaseActivity implements SectionIndexer, PullToRefreshLayout.OnRefreshListener {
    public static CustomeActivity customeActivity;
    private PullableListView listView;
    private PullToRefreshLayout ptrl;
    private int page = 1;
    private SideBar sideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private List<LawyerBean.ResultBean> lawyersData = new ArrayList<>();
    private LinearLayout titleLayout;
    private TextView tvNofriends;
    private SearchEditText searchEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<CustomBean.ResultBean> SourceDateList = new ArrayList<>();

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    //用以判断跳转不同界面
    String str1 = "default", jsonStr = "";
    private List filterDateList;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                getCustomeListData(true, 1);
            }
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.activity_custome;
    }

    @Override
    public void initView() {
        customeActivity = this;
        setTitle(getResources().getString(R.string.inverstigation));
        initViews();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //从homeActivity传过来的值用以判断跳转不同界面
        str1 = bundle.getString(ExtraName.SCALETOCUSTOME);
        jsonStr = BaseApplication.getInstance().getaCache().getAsString("customer_list");
        getCustomeListData(true, page);

    }

    private void initViews() {

        searchEditText = getViewById(R.id.activity_main_input_edittext);
        titleLayout = getViewById(R.id.title_layout);
        tvNofriends = getViewById(R.id.noInfomation);
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = getViewById(R.id.sidrbar);
        dialog = getViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        ptrl = getViewById(R.id.refresh_view);
        ptrl.setOnRefreshListener(this);
        listView = getViewById(R.id.content_view);
        setListener();


    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List filledData(List date) {
        List list = new ArrayList();
        List<CustomBean.ResultBean> mSortList = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            CustomBean.ResultBean sortModel = (CustomBean.ResultBean) date.get(i);
            // 汉字转换成拼音
            if (!TextUtils.isEmpty(sortModel.getSurname())) {
                String pinyin = characterParser.getSelling(sortModel.getSurname());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
                mSortList.add(sortModel);
            }
        }
        list.clear();
        list.addAll(mSortList);
        return list;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (TextUtils.isEmpty(filterStr)) {
            tvNofriends.setVisibility(View.GONE);
            adapter.updateListView(SourceDateList, null);
        } else {
            filterDateList = new ArrayList<>();
            for (CustomBean.ResultBean sortModel : SourceDateList) {
                String name = sortModel.getSurname();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr) || characterParser.getSelling(name).startsWith(filterStr.toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList, lawyersData);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position).getSortLetters().charAt(0);

    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private void getCustomeListData(final boolean b, int page) {
        showDialog();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        ajaxParams.put("page", String.valueOf(page));
        ajaxParams.put("number", "100");
        final FinalHttp fh = new FinalHttp();
        fh.get(Contants.CUSTOMER_LIST, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                closeDialog();
                if (null != s) {

                    jsonParse(s, b);

                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeDialog();
            }
        });

    }

    private void jsonParse(String json, boolean isRefresh) {
        Gson gson = new Gson();
        //客户信息
        CustomBean product = gson.fromJson(json, CustomBean.class);
        if (product.getCode() == 1) {
            SourceDateList = filledData(product.getResult());
        }


        if (isRefresh) {

            if (SourceDateList.size() == 0) {
                tvNofriends.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                tvNofriends.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }


            // 根据a-z进行排序源数据
            Collections.sort(SourceDateList, pinyinComparator);
            adapter = new SortGroupMemberAdapter(this, "custome");
            adapter.addData(SourceDateList, lawyersData);
            listView.setAdapter(adapter);
        } else {
            ptrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            adapter.addMore(SourceDateList);
        }

    }


    @Override
    public void setListener() {
        if (str1.equals(ExtraName.SCALETOCUSTOME)) {
            changeLeftImageView(R.mipmap.close, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(0, R.anim.out_anim);
                }
            });
        } else {
            clickRightImageView(R.mipmap.addim, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("add", "add");
                    ActivitySkip.forward(CustomeActivity.this, CustomDetailedActivity.class, bundle);

                }
            });
        }


        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });


        // 根据输入框输入值的改变来过滤搜索
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 这个时候不需要挤压效果 就把他隐藏掉
                titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchEditText.getText().toString().length() == 0) {
                    adapter.updateListView(SourceDateList, null);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Bundle bundle = new Bundle();
                SortGroupMemberAdapter.ViewHolder viewHolder = (SortGroupMemberAdapter.ViewHolder) view.getTag();
                CustomBean.ResultBean resultBean = viewHolder.resultBean;
                ObjectSaveUtil.saveObject(CustomeActivity.this,"custome",resultBean);
                //产品选择客户
                if (str1.equals(ExtraName.SCALETOCUSTOME)) {
                    bundle.putString("add", "list");
                    /*BaseApplication app = BaseApplication.getInstance();
                    app.setResultBean(resultBean);*/
                    ActivitySkip.forward(CustomeActivity.this, ScaleActivity.class, bundle);
                    finish();
                } else if (str1.equals(ExtraName.TORESVER_TOCUSTOME)) {//预约界面选客户
                    Intent i = new Intent();
                    i.putExtra("custome", resultBean);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                } else {//正常选择客户
                    bundle.putSerializable("custome", resultBean);
                    bundle.putString("add", "list");
                    ActivitySkip.forward(CustomeActivity.this, CustomDetailedActivity.class, bundle);
                }
                Log.e("---", "onItemClick: "+((CustomBean.ResultBean)ObjectSaveUtil.readObject(CustomeActivity.this,"custome")).getFirst_name());
            }
        });


        //删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (str1.equals(ExtraName.TORESVER_TOCUSTOME)) {
                    SortGroupMemberAdapter.ViewHolder viewHolder = (SortGroupMemberAdapter.ViewHolder) view.getTag();
                    CustomBean.ResultBean resultBean = viewHolder.resultBean;
                    deleteCustomer(resultBean);

                }

                return false;
            }
        });

    }

    private void deleteCustomer(CustomBean.ResultBean resultBean) {
        showDialog();
        FinalHttp finalHttp = new FinalHttp();
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("user_id", SharedPreferencesHelps.getUserID());
        ajaxParams.put("customer_id", resultBean.getCustomer_id() + "");

        finalHttp.post(Contants.USER_CUSTOMER_DEL, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                closeDialog();

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                closeDialog();
            }
        });
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
        page = 1;
        getCustomeListData(true, page);
    }


    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

        page++;
        getCustomeListData(false, page);
    }

}
