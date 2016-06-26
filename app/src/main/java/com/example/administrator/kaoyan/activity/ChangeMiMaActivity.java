package com.example.administrator.kaoyan.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.kaoyan.R;
import com.example.administrator.kaoyan.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class ChangeMiMaActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private EditText et_phone, et_mima, et_yanzheng;
    private TextView tv_get, tv_remind;
    private Button bt_ok;
    private SharedPreferences sp;
    private RequestQueue mQueue;
    private StringRequest stringRequest;
    private int time = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mi_ma);
        initView();
        initListener();
        SMSSDK.initSDK(this, "129a87c28ef5a", "674368f9adda2ff9a47b25c34a1d1bfd");
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_get.setOnClickListener(this);
        bt_ok.setOnClickListener(this);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_mima = (EditText) findViewById(R.id.et_mima);
        et_yanzheng = (EditText) findViewById(R.id.et_yanzheng);
        tv_get = (TextView) findViewById(R.id.tv_get);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        tv_remind = (TextView) findViewById(R.id.tv_remind);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get:
                if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    if (et_phone.getText().toString().trim().length() == 11) {
                        tv_get.setVisibility(View.GONE);
                        SMSSDK.getVerificationCode("86", et_phone.getText().toString().trim());
                    } else {
                        Toast.makeText(ChangeMiMaActivity.this, "请输入完整手机号码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangeMiMaActivity.this, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_ok:
                SMSSDK.submitVerificationCode("86", et_phone.getText().toString().trim(), et_yanzheng.getText().toString().trim());
                break;
        }
    }

    //验证码送成功后提示文字
    private void reminderText() {
        tv_remind.setVisibility(View.VISIBLE);
        handlerText.sendEmptyMessageDelayed(1, 1000);
    }

    Handler handlerText = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (time > 0) {
                    tv_remind.setText("验证码已发送" + time + "秒");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                } else {
                    tv_remind.setText("提示信息");
                    time = 60;
                    tv_remind.setVisibility(View.GONE);
                    tv_get.setVisibility(View.VISIBLE);
                }
            } else {
                et_yanzheng.setText("");
                tv_remind.setText("提示信息");
                time = 60;
                tv_remind.setVisibility(View.GONE);
                tv_get.setVisibility(View.VISIBLE);
            }
        }

        ;
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            System.out.println("--------result---0" + event + "--------*" + result + "--------" + data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result---1" + event + "--------*" + result + "--------" + data);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    changeMiMa();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                    reminderText();
                }
            } else {
                tv_get.setVisibility(View.VISIBLE);
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(ChangeMiMaActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    };

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

    private void changeMiMa() {
        mQueue = Volley.newRequestQueue(this);
        String httpUrl = "http://www.zhongyuedu.com/api/jz_change_pwd.php";//公司服务器
        stringRequest = new StringRequest(Request.Method.POST, httpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String resultCode = jsonObject.getString("resultCode");
                    String result = jsonObject.getString("result");
                    if (resultCode.equals("0")) {
                        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("number", et_phone.getText().toString());
                        editor.commit();
                        finish();
                        Toast.makeText(ChangeMiMaActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangeMiMaActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangeMiMaActivity.this, "网络连接有问题", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", et_phone.getText().toString().trim());
                map.put("password", et_mima.getText().toString().trim());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }
}
