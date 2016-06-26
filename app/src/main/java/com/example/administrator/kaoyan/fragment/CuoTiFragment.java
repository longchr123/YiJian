package com.example.administrator.kaoyan.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.kaoyan.MainActivity;
import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.activity.ChapterActivity;
import com.example.administrator.kaoyan.adapter.XiTiListAdapter;
import com.example.administrator.kaoyan.bean.ExamSmallItem;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.example.administrator.kaoyan.util.SQLiteUtil;
import com.umeng.analytics.MobclickAgent;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class CuoTiFragment extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity mainActivity;
    private View view;
    private ListView lv;
    private XiTiListAdapter adapter;
    private List<ExamSmallItem> examSmallItems = new ArrayList<>();
    private SQLiteDatabase examSqLite;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.from(mainActivity).inflate(R.layout.fragment_cuoti, null);
        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        lv.setOnItemClickListener(this);
    }

    private void initData() {
        adapter = new XiTiListAdapter(examSmallItems, mainActivity);
        lv.setAdapter(adapter);
        sp = mainActivity.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        getDataFromSqLite();
    }

    private void getDataFromSqLite() {
        examSqLite = SQLiteDatabase.openOrCreateDatabase(ConfigUtil.examTypeFileName, ConfigUtil.mi_ma, null);
        examSmallItems = SQLiteUtil.getExamTableData(examSqLite, "exam");
        adapter.setData(examSmallItems);
    }

    private void initView() {
        lv = (ListView) view.findViewById(R.id.lv);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mainActivity, ChapterActivity.class);
        intent.putExtra("chapter", examSmallItems.get(position).getTitle());
        intent.putExtra("position", position);
        intent.putExtra("isXiTi", false);
        if (sp.getBoolean(position + "", true)) {
            Toast.makeText(mainActivity, "还没有数据哦，请先下载", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CuoTiFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CuoTiFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (examSqLite != null) {
            examSqLite.close();
        }
    }
}
