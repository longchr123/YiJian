package com.example.administrator.kaoyan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.adapter.ChapterListAdapter;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.example.administrator.kaoyan.util.SQLiteUtil;
import com.umeng.analytics.MobclickAgent;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lv;
    private List<String> list = new ArrayList<>();
    private ChapterListAdapter adapter;
    private TextView tv_exam;
    private ImageView iv_back;
    private String exam, sqLitePath;//数据库位置
    private int examPosition;//第几个考试类型的数据库
    private SQLiteDatabase sqLiteDatabase;
    private boolean isXiTi;//是否为习题或者错题
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chaper);
        getIntentData();
        initView();
        initData();
        initListener();
    }

    private void getIntentData() {
        exam = getIntent().getStringExtra("chapter");
        examPosition = getIntent().getIntExtra("position", 0);
        isXiTi = getIntent().getBooleanExtra("isXiTi", true);
    }

    private void initListener() {
        lv.setOnItemClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void initData() {
        if (isXiTi) {
            sqLitePath = ConfigUtil.path + ConfigUtil.getNormalSqLite(examPosition);
        } else {
            sqLitePath = ConfigUtil.path + ConfigUtil.getCuoSqLite(examPosition);
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLitePath, ConfigUtil.mi_ma, null);
        list = SQLiteUtil.getTableChineseName(sqLiteDatabase);
        adapter = new ChapterListAdapter(list, this);
        lv.setAdapter(adapter);
        tv_exam.setText(exam);
        dialog = new AlertDialog.Builder(this).setTitle("加载中...").setCancelable(false).create();
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        tv_exam = (TextView) findViewById(R.id.tv_exam);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isXiTi) {
            dialog.show();
            Intent intent = new Intent(this, XiTiAnswerActivity.class);
            intent.putExtra("sqLitePath", sqLitePath);
            intent.putExtra("cuosqLitePath", ConfigUtil.path + ConfigUtil.getCuoSqLite(examPosition));
            intent.putExtra("position", position);
            intent.putExtra("examPosition", examPosition);
            intent.putExtra("title", list.get(position));
            startActivity(intent);
        } else {
            dialog.show();
            Intent intent = new Intent(this, AnswerActivity.class);
            intent.putExtra("sqLitePath", sqLitePath);
            intent.putExtra("position", position);
            intent.putExtra("title", list.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
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
    protected void onStop() {
        super.onStop();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
