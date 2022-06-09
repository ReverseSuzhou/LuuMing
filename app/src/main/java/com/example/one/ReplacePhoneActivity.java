package com.example.one;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ReplacePhoneActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private Button mBtnVcode;
    private EditText mEtVcode;
    private EditText OldPhoneNumber;
    private EditText NewPhoneNumber;
    private Button mBtnAckRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replace_phone_page);

        //控件部分
        mBtn_back = findViewById(R.id.replace_phone_page_1_1_button_back);
        mEtVcode = findViewById(R.id.replace_phone_page_1_2_edittext_verification_code);
        mBtnVcode = findViewById(R.id.btn_check);
        OldPhoneNumber=findViewById(R.id.OldPhoneNumber);
        NewPhoneNumber=findViewById(R.id.NewPhoneNumber);
        mBtnAckRegister=findViewById(R.id.submit);

        EventHandler handler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_LONG).show();
                                String sql = "update user set User_phone = '" + NewPhoneNumber.getText().toString() + "' where User_phone = '" + OldPhoneNumber.getText().toString() + "';";
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
                                //如果正确，跳转
                                Intent intent = new Intent(ReplacePhoneActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReplacePhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    //失败回调
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)) {
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
            }

        };
        SMSSDK.registerEventHandler(handler);

        mBtnVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NewPhoneNumber.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "未输入手机号", Toast.LENGTH_LONG).show();
                }
                else if (NewPhoneNumber.getText().toString().length() > 15||NewPhoneNumber.getText().toString().length() <10) {
                    Toast.makeText(getApplicationContext(), "手机号格式不对", Toast.LENGTH_LONG).show();
                }
                else {
                    //如果没问题则发送验证码
                    //Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
                    String phone=NewPhoneNumber.getText().toString();
                    SMSSDK.getVerificationCode("86",phone);
                }
            }
        });

        mBtnAckRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //可以增加一条在判断旧phone是否和数据库中的相同
                    String phone=NewPhoneNumber.getText().toString();
                    String number = mEtVcode.getText().toString();
                    SMSSDK.submitVerificationCode("86",phone,number);

            }
        });
        //返回
        mBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ReplacePhoneActivity.this,PersonalActivity.class);
                startActivity(intent);
            }
        });
    }
}