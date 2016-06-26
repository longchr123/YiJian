package com.example.administrator.kaoyan;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.example.administrator.kaoyan.fragment.CuoTiFragment;
import com.example.administrator.kaoyan.fragment.MoreFragment;
import com.example.administrator.kaoyan.fragment.NewsFragment;
import com.example.administrator.kaoyan.fragment.XiTiFragment;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.example.administrator.kaoyan.util.ExampleUtil;
import com.umeng.analytics.MobclickAgent;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener {
    private RadioGroup rg;
    private FragmentManager manager;
    private XiTiFragment xiTiFragment;
    private NewsFragment newsFragment;
    private CuoTiFragment cuoTiFragment;
    private MoreFragment moreFragment;
    private Fragment mCurrentFragment;
    private FragmentTransaction transaction;
    private GifView gv;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private int alarmHour, alarmMinute;
    private boolean openAlarm;
    private TextView tv_title;
    private ImageView iv_yiqiandao;
    public static boolean isForeground=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase.loadLibs(this);
        initView();
        initDate();
        initListener();
        //开启闹铃
        if (openAlarm) {
            setAlarm(alarmHour, alarmMinute);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        isForeground=true;
        MobclickAgent.onResume(this);
        alarmHour = sp.getInt("alarmHour", 20);
        alarmMinute = sp.getInt("alarmMinute", 0);
        openAlarm = sp.getBoolean("openAlarm", true);
    }

    private void setAlarm(int hour, int minute) {
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        long firstTime = systemTime + time;
        setAlarmTime(this, firstTime);
    }

    private void initDate() {
//        gv.setGifImage(R.mipmap.qiandao);
//        gv.setShowDimension(200,200);
//        gv.setGifImageType(GifView.GifImageType.COVER);
//        iv_yiqiandao.setImageResource(R.mipmap.yiqiandao);
        manager = getFragmentManager();
        xiTiFragment = new XiTiFragment();
        newsFragment = new NewsFragment();
        cuoTiFragment = new CuoTiFragment();
        moreFragment = new MoreFragment();
        transaction = manager.beginTransaction();
        transaction.add(R.id.lin, xiTiFragment).commit();
        mCurrentFragment = xiTiFragment;
        sp = this.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
//        alarmHour = sp.getInt("alarmHour", 20);
//        alarmMinute = sp.getInt("alarmMinute", 0);
//        openAlarm = sp.getBoolean("openAlarm", true);
    }

    private void initListener() {
        iv_yiqiandao.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
        gv.setOnClickListener(this);
    }

    private void initView() {
        iv_yiqiandao = (ImageView) findViewById(R.id.iv_yiqiandao);
        tv_title = (TextView) findViewById(R.id.tv_title);
        gv = (GifView) findViewById(R.id.gv) ;
        rg = (RadioGroup) findViewById(R.id.rg);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        transaction = manager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_xiti:
                changeFragment(xiTiFragment);
                tv_title.setText("题目练习");
                break;
            case R.id.rb_news:
                changeFragment(newsFragment);
                tv_title.setText("直播课");
                break;
            case R.id.rb_cuoti:
                changeFragment(cuoTiFragment);
                tv_title.setText("错题复习");
                break;
            case R.id.rb_more:
                changeFragment(moreFragment);
                tv_title.setText("更多");
                break;
        }
    }

    //要先hide再show
    public void changeFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        transaction = manager.beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.lin, fragment);
        }
        transaction.commit();
        mCurrentFragment = fragment;
    }

    private void setAlarmTime(Context context, long firstTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.alarm.yijian.action");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = 60 * 1000 * 60 * 24;//闹铃时间间隔
        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, interval, sender);
    }

    private long mExitTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        isForeground=false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gv :
                Toast.makeText(this,"签到成功,祝您学习愉快！！",Toast.LENGTH_SHORT).show();
                gv.setVisibility(View.GONE);
                iv_yiqiandao.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_yiqiandao :
                Toast.makeText(this,"一天只能签一次噢！！",Toast.LENGTH_SHORT).show();
        }
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }
}
