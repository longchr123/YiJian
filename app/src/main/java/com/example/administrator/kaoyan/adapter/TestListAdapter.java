package com.example.administrator.kaoyan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ASUSK557 on 2016/2/17.
 */
public class TestListAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private HashMap<Integer, Integer> hashMap = new HashMap<>();
    private HashMap<Integer, Integer> hashMap2 = new HashMap<>();

    public TestListAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            hashMap.put(i, 0);
        }
        for (int i = 0; i < list.size(); i++) {
            hashMap2.put(i, 0);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void changeTextColor(int position, int position2) {
        hashMap.clear();
        hashMap2.clear();
        for (int i = 0; i < list.size(); i++) {
            if (i == position) {
                hashMap.put(i, 1);
            } else {
                hashMap.put(i, 0);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i == position2) {
                hashMap2.put(i, 1);
            } else {
                hashMap2.put(i, 0);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        private TextView tv;
        private ImageView iv_unselect, iv_corr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_lv, null);
            vh = new ViewHolder();
            vh.tv = (TextView) convertView.findViewById(R.id.tv);
            vh.iv_unselect = (ImageView) convertView.findViewById(R.id.iv_unselect);
            vh.iv_corr = (ImageView) convertView.findViewById(R.id.iv_corr);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(list.get(position));
        if (hashMap.get(position) == 1) {
            vh.tv.setTextColor(Color.RED);
            vh.iv_unselect.setImageResource(R.mipmap.select);
        } else {
            vh.tv.setTextColor(Color.BLACK);
            vh.iv_unselect.setImageResource(R.mipmap.unselect);
        }

        if (hashMap2.get(position) == 1) {
            vh.iv_corr.setVisibility(View.VISIBLE);
        } else {
            vh.iv_corr.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
