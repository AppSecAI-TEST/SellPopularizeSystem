package com.yd.org.sellpopularizesystem.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.javaBean.CustomBean;
import com.yd.org.sellpopularizesystem.javaBean.LawyerBean;
import com.yd.org.sellpopularizesystem.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List<CustomBean.ResultBean> list = new ArrayList<>();
    private List<LawyerBean.ResultBean> data = new ArrayList<>();
    private Context mContext;
    private String flag = "custome";


    public SortGroupMemberAdapter(Context mContext, String flag) {
        this.mContext = mContext;
        this.flag = flag;

    }


    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<CustomBean.ResultBean> list, List<LawyerBean.ResultBean> data) {
        if (list.size() != 0) {
            this.list = list;
        } else {
            this.data = data;
        }

        notifyDataSetChanged();
    }

    public void addData(List<CustomBean.ResultBean> list, List<LawyerBean.ResultBean> data) {
        if (list.size() != 0) {
            this.list.addAll(list);
        } else {
            this.data.addAll(data);
        }

        notifyDataSetChanged();
    }

    public void addMore(List<CustomBean.ResultBean> datas) {
        this.list.addAll(datas);
        notifyDataSetChanged();
    }

    public int getCount() {

        if (list.size() != 0) {
            return this.list.size();
        } else {
            return this.data.size();
        }

    }

    public Object getItem(int position) {

        if (list.size() != 0) {
            return list.get(position);
        } else {
            return data.get(position);
        }

    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_group_member_item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.customName);
            viewHolder.tvNickName = (TextView) view.findViewById(R.id.tvNickName);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.customImageiView);
            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }


        if (flag.equals("custome")) {

            viewHolder.resultBean = list.get(position);
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);


            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(viewHolder.resultBean.getSortLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            viewHolder.tvNickName.setText(list.get(position).getEn_name().equals("") ? "" : list.get(position).getEn_name());

            if (TextUtils.isEmpty(list.get(position).getMid_name()) || list.get(position).getMid_name() == null) {
                viewHolder.tvTitle.setText(list.get(position).getSurname() + mContext.getString(R.string.single_blank_space) + list.get(position).getFirst_name());

            } else {
                viewHolder.tvTitle.setText(list.get(position).getSurname() + mContext.getString(R.string.single_blank_space) + list.get(position).getMid_name() + mContext.getString(R.string.single_blank_space) + list.get(position).getFirst_name());

            }

            if (!TextUtils.isEmpty(list.get(position).getHead_img()) && list.get(position).getHead_img() != null && list.get(position).getHead_img() != "") {
                BitmapUtil.loadImageView(mContext, Contants.DOMAIN + "/" + list.get(position).getHead_img(), viewHolder.imageView);
                Log.e("imageview","tt:"+Contants.DOMAIN + "/" + list.get(position).getHead_img());

            } else {
                viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.settingbt));
            }


        } else {
            viewHolder.m = data.get(position);

            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);


            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(viewHolder.m.getSortLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }
            viewHolder.tvTitle.setText(data.get(position).getLawyer_name());

            viewHolder.imageView.setVisibility(View.GONE);

        }


        return view;

    }

    public class ViewHolder {
        TextView tvLetter;
        TextView tvTitle, tvNickName;
        ImageView imageView;
        public CustomBean.ResultBean resultBean;
        public LawyerBean.ResultBean m;

    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */

    @Override
    public int getSectionForPosition(int position) {
        if (flag.equals("custome")) {
            return list.get(position).getSortLetters().charAt(0);
        } else {
            return data.get(position).getSortLetters().charAt(0);
        }

    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        String sortStr;
        for (int i = 0; i < getCount(); i++) {
            if (flag.equals("custome")) {
                sortStr = list.get(i).getSortLetters();
            } else {
                sortStr = data.get(i).getSortLetters();
            }

            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}