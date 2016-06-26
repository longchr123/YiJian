package com.example.administrator.kaoyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class ChapterListAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    public ChapterListAdapter(List<String> list,Context context){
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_xiti_list, null);
            tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(tv);
        } else {
            tv = (TextView) convertView.getTag();
        }
        tv.setText(list.get(position));
        return convertView;
    }
}
