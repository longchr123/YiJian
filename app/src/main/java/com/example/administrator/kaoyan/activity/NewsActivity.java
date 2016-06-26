package com.example.administrator.kaoyan.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.adapter.NewsListAdapter;
import com.example.administrator.kaoyan.bean.NewsItem;
import com.example.administrator.kaoyan.util.JsonUtil;
import com.example.administrator.kaoyan.util.TimeUtil;
import com.example.administrator.kaoyan.view.XListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener {
    private ImageView iv_back;
    private XListView lv;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private int page = 1;
    private List<NewsItem> list = new ArrayList<>();
    private NewsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        requestQueue = Volley.newRequestQueue(this);
        initView();
        initData();
        downData(page);
        initListener();
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsActivity.this, WebViewActivity.class);
                intent.putExtra("title", "新闻资讯");
                intent.putExtra("url", list.get(position - 1).getUrl());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);
        adapter = new NewsListAdapter(this, list);
        lv.setAdapter(adapter);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (XListView) findViewById(R.id.lv);
    }

    public void downData(final int pages) {
        String httpUrl = "http://www.zhongyuedu.com/api/tk_jz_newsList.php ";//公司服务器
        stringRequest = new StringRequest(Request.Method.POST, httpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //服务器返回的内容
                list.addAll(JsonUtil.parseJsonNews(response));
                adapter.setData(list);
                onLoad();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewsActivity.this, "网络连接有问题", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("groupId", "11");
                map.put("page", pages + "");
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    //加载或者刷新完成之后需要调用该方法
    private void onLoad() {
        lv.stopRefresh();
        lv.stopLoadMore();
        String time = TimeUtil.getFormated2DateTime(new Date().getTime());
        lv.setRefreshTime(time);
    }

    @Override
    public void onRefresh() {
        list.clear();
        page = 1;
        downData(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onLoadMore() {
        downData(++page);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
