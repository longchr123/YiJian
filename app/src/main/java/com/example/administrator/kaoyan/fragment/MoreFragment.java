package com.example.administrator.kaoyan.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.kaoyan.MainActivity;
import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.activity.AdviceActivity;
import com.example.administrator.kaoyan.activity.AlarmActivity;
import com.example.administrator.kaoyan.activity.AnswerActivity;
import com.example.administrator.kaoyan.activity.CreateActivity;
import com.example.administrator.kaoyan.activity.NewsActivity;
import com.example.administrator.kaoyan.activity.UpDataActivity;
import com.example.administrator.kaoyan.activity.WebViewActivity;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/4/26.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private View view;
    private LinearLayout lin_news, lin_save, lin_about, lin_updata, lin_advice, lin_login, lin_use, lin_remind;
    private TextView tv_tui, tv_login;
    private SharedPreferences sp;
    private String number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.from(mainActivity).inflate(R.layout.fragment_more, null);
        initView();
        initData();
        initListener();
        return view;
    }


    private void initListener() {
        lin_news.setOnClickListener(this);
        lin_save.setOnClickListener(this);
        lin_about.setOnClickListener(this);
        lin_updata.setOnClickListener(this);
        lin_advice.setOnClickListener(this);
        lin_use.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_tui.setOnClickListener(this);
        lin_remind.setOnClickListener(this);
    }

    private void initData() {
        sp = mainActivity.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
    }

    private void initView() {
        lin_news = (LinearLayout) view.findViewById(R.id.lin_news);
        lin_save = (LinearLayout) view.findViewById(R.id.lin_save);
        lin_about = (LinearLayout) view.findViewById(R.id.lin_about);
        lin_updata = (LinearLayout) view.findViewById(R.id.lin_updata);
        lin_advice = (LinearLayout) view.findViewById(R.id.lin_advice);
        lin_login = (LinearLayout) view.findViewById(R.id.lin_login);
        lin_remind = (LinearLayout) view.findViewById(R.id.lin_remind);
        lin_use = (LinearLayout) view.findViewById(R.id.lin_use);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        tv_tui = (TextView) view.findViewById(R.id.tv_tui);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_news:
                Intent intentNews = new Intent(mainActivity, NewsActivity.class);
                startActivity(intentNews);
                break;
            case R.id.lin_save:
                Intent intentAnswer = new Intent(mainActivity, AnswerActivity.class);
                intentAnswer.putExtra("sqLitePath", ConfigUtil.saveFilename);
                intentAnswer.putExtra("position", -2);
                intentAnswer.putExtra("title", "我的收藏");
                startActivity(intentAnswer);
                break;
            case R.id.lin_about:
                Intent intentWeb = new Intent(mainActivity, WebViewActivity.class);
                intentWeb.putExtra("title", "关于我们");
                intentWeb.putExtra("url", "http://www.zhongyuedu.com/api/tk_aboutUs.htm");
                startActivity(intentWeb);
                break;
            case R.id.lin_updata:
                if (sp.getString("number", "").equals("")) {
                    Toast.makeText(mainActivity, "还没有登录，请先登录哦", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentUpData = new Intent(mainActivity, UpDataActivity.class);
                    startActivity(intentUpData);
                }
                break;
            case R.id.lin_advice:
                Intent intentAdvice = new Intent(mainActivity, AdviceActivity.class);
                startActivity(intentAdvice);
                break;
            case R.id.lin_remind:
                Intent intentRemind = new Intent(mainActivity, AlarmActivity.class);
                startActivity(intentRemind);
                break;
            case R.id.tv_login:
                if (sp.getString("number", "").equals("")) {
                    Intent intentLogin = new Intent(mainActivity, CreateActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.tv_tui:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(mainActivity);
                builder2.setMessage("确定退出账号吗？");
                builder2.setTitle("提示");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("number", "");
                        editor.commit();
                        tv_login.setText("登录");
                        lin_login.setVisibility(View.GONE);
                        Intent intent = new Intent(mainActivity, CreateActivity.class);
                        startActivity(intent);
                        mainActivity.finish();
                    }
                });
                builder2.setNegativeButton("取消", null);
                builder2.create().show();
                break;
            case R.id.lin_use:
                Intent intentUse = new Intent(mainActivity, WebViewActivity.class);
                intentUse.putExtra("title", "使用说明");
                intentUse.putExtra("url", "http://www.zhongyuedu.com/api/tk_ios_usage.htm");
                startActivity(intentUse);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MoreFragment");
        number = sp.getString("number", "");
        if (number != "") {
            lin_login.setVisibility(View.VISIBLE);
            tv_login.setText(number.substring(0, 3) + "****" + number.substring(7, 11));
        } else {
            lin_login.setVisibility(View.GONE);
            tv_login.setText("登录");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MoreFragment");
    }
}
