package com.example.administrator.kaoyan.activity;
//习题时专用界面，可错可收藏

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.adapter.AnswerVpAdapter;
import com.example.administrator.kaoyan.bean.TestItem;
import com.example.administrator.kaoyan.fragment.XiTiTestFragment;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.example.administrator.kaoyan.util.SQLiteUtil;
import com.umeng.analytics.MobclickAgent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class XiTiAnswerActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, XiTiTestFragment.FragmentCallBack {
    private ImageView iv_back, iv_last, iv_share, iv_save, iv_show, iv_next;
    private TextView tv_current, tv_all, tv_title;
    private ViewPager vp;
    private int currentPosition = 1;
    private List<Fragment> list = new ArrayList<>();
    private AnswerVpAdapter adapter;
    private SQLiteDatabase sqLiteDatabase, saveSqLite, cuoSqLite;//哪个数据库
    private String tableName, sqLitePath, cuosqLitePath, title;//哪个表
    private int position;//表格
    private List<TestItem> listItem = new ArrayList<>();
    private boolean isSave = false;//是否已经收藏
    private XiTiTestFragment currentFragment;
    private int examPosition, tablePosition;//第几个数据库，保存到第几题
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xi_ti_answer);
        getIntentData();
        initView();
        initData();
        initListener();
    }

    private void getIntentData() {
        sqLitePath = getIntent().getStringExtra("sqLitePath");
        cuosqLitePath = getIntent().getStringExtra("cuosqLitePath");
        examPosition = getIntent().getIntExtra("examPosition", 0);
        position = getIntent().getIntExtra("position", 0);
        title = getIntent().getStringExtra("title");
    }

    private void initListener() {
        vp.setOnPageChangeListener(this);
        iv_back.setOnClickListener(this);
        iv_last.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_save.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_show.setOnClickListener(this);
    }

    private void initData() {
        saveSqLite = SQLiteDatabase.openOrCreateDatabase(ConfigUtil.saveFilename, ConfigUtil.mi_ma, null);
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLitePath, ConfigUtil.mi_ma, null);
        cuoSqLite = SQLiteDatabase.openOrCreateDatabase(cuosqLitePath, ConfigUtil.mi_ma, null);
        tableName = SQLiteUtil.getTableEnglishName(sqLiteDatabase).get(position);
        listItem = SQLiteUtil.getTableData(sqLiteDatabase, tableName);
        tv_current.setText(currentPosition + "");
        tv_all.setText(listItem.size() + "");
        for (int i = 0; i < listItem.size(); i++) {
            XiTiTestFragment fragment = new XiTiTestFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", listItem.get(i).getId());
            bundle.putString("A", "A. " + listItem.get(i).getA());
            bundle.putString("B", "B. " + listItem.get(i).getB());
            bundle.putString("C", "C. " + listItem.get(i).getC());
            bundle.putString("D", "D. " + listItem.get(i).getD());
            bundle.putString("E", "E. " + listItem.get(i).getE());
            bundle.putString("answer", listItem.get(i).get答案());
            bundle.putString("analysis", listItem.get(i).get解析());
            bundle.putString("tigan", listItem.get(i).get题干());
            bundle.putString("cuosqLitePath", cuosqLitePath);
            bundle.putString("tableName", tableName);
            fragment.setArguments(bundle);
            list.add(fragment);
        }
        adapter = new AnswerVpAdapter(getSupportFragmentManager(), list);
        vp.setAdapter(adapter);
        tv_title.setText(title);

        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        tablePosition = sp.getInt(examPosition + "" + position, 1);
        new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("您上次做到第" + tablePosition + "题,继续上次答题？").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vp.setCurrentItem(tablePosition - 1);
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_current = (TextView) findViewById(R.id.tv_current);
        tv_all = (TextView) findViewById(R.id.tv_all);
        vp = (ViewPager) findViewById(R.id.vp);
        iv_last = (ImageView) findViewById(R.id.iv_last);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_save = (ImageView) findViewById(R.id.iv_save);
        iv_show = (ImageView) findViewById(R.id.iv_show);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position + 1;
        tv_current.setText(currentPosition + "");
        //根据题干判断是否已经收藏，如果收藏数据库有和该题目题干一致，则已经收藏
        Cursor cursor = saveSqLite.query("saveData", null, null, null, null, null, null, null);
        iv_save.setImageResource(R.mipmap.collect);
        isSave = false;
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex("题干"));
            if (content.equals(listItem.get(position).get题干())) {
                iv_save.setImageResource(R.mipmap.collect_ok);
                isSave = true;
            }
        }
        cursor.close();
        iv_show.setImageResource(R.mipmap.show);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_last:
                if (currentPosition == 1) {
                    Toast.makeText(this, "已经到达第一题", Toast.LENGTH_SHORT).show();
                } else {
                    vp.setCurrentItem(currentPosition - 2);
                }
                break;
            case R.id.iv_share:
                Toast.makeText(this, "我们将在下个版本添加该功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_show:
                iv_show.setImageResource(R.mipmap.show_ok);
                currentFragment = (XiTiTestFragment) list.get(currentPosition - 1);
                currentFragment.show();
                break;
            case R.id.iv_save://根据题干判断是否已经收藏
                if (isSave) {
                    saveSqLite.delete("saveData", "题干=?", new String[]{listItem.get(currentPosition - 1).get题干()});
                    iv_save.setImageResource(R.mipmap.collect);
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                    isSave = false;
                } else {
                    ContentValues values = new ContentValues();
                    values.put("题干", listItem.get(currentPosition - 1).get题干());
                    values.put("A", listItem.get(currentPosition - 1).getA());
                    values.put("B", listItem.get(currentPosition - 1).getB());
                    values.put("C", listItem.get(currentPosition - 1).getC());
                    values.put("D", listItem.get(currentPosition - 1).getD());
                    values.put("E", listItem.get(currentPosition - 1).getE());
                    values.put("答案", listItem.get(currentPosition - 1).get答案());
                    values.put("解析", listItem.get(currentPosition - 1).get解析());
                    saveSqLite.insert("saveData", "id", values);
                    iv_save.setImageResource(R.mipmap.collect_ok);
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                    isSave = true;
                }
                break;
            case R.id.iv_next:
                if (currentPosition == listItem.size()) {
                    Toast.makeText(this, "已经到达最后一题", Toast.LENGTH_SHORT).show();
                } else {
                    vp.setCurrentItem(currentPosition);
                }
                break;
        }
    }

    @Override
    public void addToSql(int testId) {
        if (!panDuan(testId)) {
            ContentValues values = new ContentValues();
            values.put("题干", listItem.get(currentPosition - 1).get题干());
            values.put("A", listItem.get(currentPosition - 1).getA());
            values.put("B", listItem.get(currentPosition - 1).getB());
            values.put("C", listItem.get(currentPosition - 1).getC());
            values.put("D", listItem.get(currentPosition - 1).getD());
            values.put("E", listItem.get(currentPosition - 1).getE());
            values.put("答案", listItem.get(currentPosition - 1).get答案());
            values.put("解析", listItem.get(currentPosition - 1).get解析());
            values.put("testId", testId);
            cuoSqLite.insert(tableName, "id", values);
        }
        Toast.makeText(this, "回答错误，已经加入错题复习", Toast.LENGTH_LONG).show();
    }

    private boolean panDuan(int id) {//true不要加，false要加
        Cursor cursor = cuoSqLite.query(tableName, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int testId = cursor.getInt(cursor.getColumnIndex("testId"));
            if (id == testId) {
                return true;
            }
        }
        cursor.close();
        return false;
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
    protected void onDestroy() {
        super.onDestroy();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (saveSqLite != null) {
            saveSqLite.close();
        }
        if (cuoSqLite != null) {
            cuoSqLite.close();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(examPosition + "" + position, currentPosition);
        editor.commit();
    }
}
