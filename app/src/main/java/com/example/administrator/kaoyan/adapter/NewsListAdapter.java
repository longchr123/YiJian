package com.example.administrator.kaoyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.bean.NewsItem;
import com.example.administrator.kaoyan.util.TimeUtil;

import java.util.List;


/**
 * Created by Administrator on 2016/1/25.
 */
public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private List<NewsItem>list;
    public NewsListAdapter(Context context, List<NewsItem> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }


    //用于更新数据
    public void setData(List<NewsItem>list){
        this.list=list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_news_lv,null);
            vh=new ViewHolder();
            vh.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
            vh.tv_content= (TextView) convertView.findViewById(R.id.tv_content);
            vh.tv_time= (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        vh.tv_title.setText(list.get(position).getTitle());
        vh.tv_content.setText(list.get(position).getDescription());
        vh.tv_time.setText(TimeUtil.getFormatedDateTime(Long.parseLong(list.get(position).getPosttime())));
        return convertView;
    }
    class ViewHolder{
        private TextView tv_title,tv_content,tv_time;
    }
}
