package com.example.one;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {
    //声明控件
    private Button mBtnAckRegister;
    private Button mBtnVcode;
    private EditText mEtVcode;
    private EditText mEtPhoneNumber;
    private EditText mEtPassword;
    private EditText mEtSurePassword;
    private EditText mEtUserName;
    Thread t;
    DBUtils d;
    ResultSet r;
    private TimeCount time;
    EventHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobSDK.submitPolicyGrantResult(true, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        MobSDK.init(this, "3598b2e09a55a","48beefd31be58bc75a9cdedda26e67cc");

        //控件部分


        mBtnAckRegister = findViewById(R.id.btn_ackRegister);
        mBtnVcode = findViewById(R.id.btn_check);
        mEtVcode = findViewById(R.id.re_et_2);
        mEtPhoneNumber = findViewById(R.id.re_et_1);
        mEtPassword = findViewById(R.id.re_et_3);
        mEtSurePassword = findViewById(R.id.re_et_4);
        mEtUserName = findViewById(R.id.re_et_5);
        time=new TimeCount(60000,1000);
        //验证信息
        handler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_LONG).show();
                                String sql = "insert into user(User_name, U_password, User_phone) values ('" + mEtUserName.getText().toString() + "', '" + mEtPassword.getText().toString() + "', '" + mEtPhoneNumber.getText().toString() + "');";
                                DBUtils dbUtils = new DBUtils();
                                try {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbUtils.update(sql);
                                        }
                                    }).start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //跳转页面
                                Intent intent = null;
                                intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"验证码已发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else if (result == SMSSDK.RESULT_ERROR){
                    //失败回调
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "验证码错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.printf("chongfufasong");
                }
            }

        };
        SMSSDK.registerEventHandler(handler);
        //获取验证码
        mBtnVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtPhoneNumber.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "未输入手机号", Toast.LENGTH_LONG).show();
                }
                else if (mEtPhoneNumber.getText().toString().length() > 15||mEtPhoneNumber.getText().toString().length() <10) {
                    Toast.makeText(getApplicationContext(), "手机号格式不对", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                d = new DBUtils();
                                r = d.query("select * from user where User_phone = '" + mEtPhoneNumber.getText().toString() + "';");
                            }
                        });
                        t.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    while (t.isAlive());
                    try {
                        if (r.isBeforeFirst()) {
                            r.next();
                            Toast.makeText(getApplicationContext(), "手机号已注册", Toast.LENGTH_LONG).show();
                            return ;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //如果没问题则发送验证码
                    //Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
                    String phone=mEtPhoneNumber.getText().toString();
                    SMSSDK.getVerificationCode("86",phone);
                    time.start();
                }
            }
        });

        //确认注册
        mBtnAckRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mEtPhoneNumber.getText().toString().equals("") || mEtPassword.getText().toString().equals("") || mEtSurePassword.getText().toString().equals("") || mEtVcode.getText().toString().equals("") || mEtUserName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "信息不全", Toast.LENGTH_LONG).show();
                }
                //else if () {
                //    验证码
                //}
                else if (mEtPassword.getText().toString().length() > 10 || mEtSurePassword.getText().toString().length() > 10 || mEtUserName.getText().toString().length() > 10) {
                    Toast.makeText(getApplicationContext(), "密码或用户名不符合规范", Toast.LENGTH_LONG).show();
                }
                else if (mEtPassword.getText().toString().equals(mEtSurePassword.getText().toString())) {
                    //toast
                    String phone=mEtPhoneNumber.getText().toString();
                    String number = mEtVcode.getText().toString();
                    SMSSDK.submitVerificationCode("86",phone,number);

                }
                else {
                    //不正确
                    Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            mBtnVcode.setClickable(false);
            mBtnVcode.setText("("+millisUntilFinished / 1000 +") 秒重新发送");
        }

        @Override
        public void onFinish() {
            mBtnVcode.setText("获取验证码");
            mBtnVcode.setClickable(true);


        }
    }
}