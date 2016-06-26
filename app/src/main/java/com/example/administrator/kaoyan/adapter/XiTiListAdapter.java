package com.example.administrator.kaoyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.bean.ExamSmallItem;

import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class XiTiListAdapter extends BaseAdapter {
    private List<ExamSmallItem> list;
    private Context context;

    public XiTiListAdapter(List<ExamSmallItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setData(List<ExamSmallItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_xiti_list, null);
            vh = new ViewHolder();
            vh.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(list.get(position).getTitle());
        return convertView;
    }
}
