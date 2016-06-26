package com.example.administrator.kaoyan.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.kaoyan.MainActivity;
import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.adapter.RealTimeAdapter;
import com.example.administrator.kaoyan.bean.RealTime;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.example.administrator.kaoyan.util.JsonUtil;
import com.example.administrator.kaoyan.util.TimeUtil;
import com.example.administrator.kaoyan.view.XListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/26.
 */
public class NewsFragment extends Fragment implements XListView.IXListViewListener {
    private MainActivity mainActivity;
    private View view;
    private XListView lv;
    private RealTimeAdapter adapter;
    private List<RealTime> list = new ArrayList<>();
    private RequestQueue mQueue;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mQueue = Volley.newRequestQueue(mainActivity);
        sp = mainActivity.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.from(mainActivity).inflate(R.layout.fragment_news, null);
        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (sp.getString("number", "") != "") {
                    order(position);
                } else {
                    Toast.makeText(mainActivity, "需要先登录才能预约哦", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NewsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NewsFragment");
    }

    //预约
    private void order(final int position) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://www.zhongyuedu.com/api/yy_jz_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", sp.getString("username", ""));
                map.put("yid", list.get(position).getYid());
                return map;
            }
        };
        mQueue.add(request);
    }

    private void initData() {
        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);
        adapter = new RealTimeAdapter(mainActivity, list);
        lv.setAdapter(adapter);
        getDataFromNet();
    }

    private void getDataFromNet() {
        StringRequest request = new StringRequest("http://www.zhongyuedu.com/api/yy_jz_list.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list = JsonUtil.parseJsonReal(response);
                        adapter.setData(list);
                        onLoad();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(request);
    }

    private void initView() {
        lv = (XListView) view.findViewById(R.id.lv);
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
        getDataFromNet();
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(mainActivity,"没有更多数据",Toast.LENGTH_SHORT).show();
        onLoad();
    }
}
