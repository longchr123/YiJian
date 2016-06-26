package com.example.administrator.kaoyan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;

public class AlarmActivity extends AppCompatActivity {
    private TextView tv_time;
    private ToggleButton toggleButton;
    private ImageView iv_back;
    private RelativeLayout rl;
    private View view;
    private TimePicker tp;
    private TimeListener times;
    private int chooseHour, chooseMinute;
    private String alarmHour, alarmMinute;
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean openAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initView();
        initData();
        createView();
        initListener();
    }

    private void createView() {
        builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setTitle("设置闹铃时间");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putInt("alarmHour", chooseHour);
                editor.putInt("alarmMinute", chooseMinute);
                Log.i("222设置的时间",""+chooseHour);
                Log.i("222设置的时间",""+chooseMinute);
                editor.commit();
                setTimeType();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        dialog = builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    private void initData() {
        tp.setIs24HourView(true);
        times = new TimeListener();
        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        chooseHour = sp.getInt("alarmHour", 20);
        Log.i("111初始",""+chooseHour);
        chooseMinute = sp.getInt("alarmMinute", 0);
        Log.i("111初始",""+chooseMinute);
        setTimeType();
        openAlarm = sp.getBoolean("openAlarm", true);
        if (openAlarm) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }
        editor = sp.edit();
    }

    public void setTimeType() {
        if (chooseHour < 10) {
            alarmHour = "0" + chooseHour;
        } else {
            alarmHour = "" + chooseHour;
        }
        if (chooseMinute < 10) {
            alarmMinute = "0" + chooseMinute;
        } else {
            alarmMinute = "" + chooseMinute;
        }
        tv_time.setText(alarmHour + ":" + alarmMinute + "  每日闹铃");
    }

    //dialog的监听器
    private void initListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    editor.putBoolean("openAlarm", true);
                    editor.commit();
                    Toast.makeText(AlarmActivity.this, "闹铃已开启", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("openAlarm", false);
                    editor.commit();
                    Toast.makeText(AlarmActivity.this, "闹铃已关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        tp.setOnTimeChangedListener(times);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        view = LayoutInflater.from(AlarmActivity.this).inflate(R.layout.alarm_dialog, null);
        tv_time = (TextView) findViewById(R.id.tv_time);
        toggleButton = (ToggleButton) findViewById(R.id.tb);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl = (RelativeLayout) findViewById(R.id.rl);
        tp = (TimePicker) view.findViewById(R.id.tp);
    }

    //timePicker监听器
    class TimeListener implements TimePicker.OnTimeChangedListener {

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            chooseHour = hourOfDay;
            chooseMinute = minute;
        }

    }
}
