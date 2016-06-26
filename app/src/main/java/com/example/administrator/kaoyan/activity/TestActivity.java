package com.example.administrator.kaoyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.administrator.kaoyan.R;

import cn.jpush.android.api.JPushInterface;

//import com.umeng.analytics.MobclickAgent;

//展示推送信息
public class TestActivity extends AppCompatActivity {
    private static String TAG = "test";
    private TextView tv;
    private static WebView wv;
    private static String webUrl = "http:/yx.zhongyuedu.com/bencandy.php?fid=240&aid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            tv.setText(content);
        }
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
//        wv = (WebView) findViewById(R.id.wv);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
}
