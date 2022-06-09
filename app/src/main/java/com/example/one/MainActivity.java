package com.example.one;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.one.util.StorePicturesUtil;
import com.example.one.util.ToastUtil;
import androidx.annotation.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements PermissionInterface {

    //声明控件
    private Button mBtnRegister;
    private Button mBtnForget;
    private Button mBtnLogin;
    private CheckBox rBtAutomaticLogin;
    private EditText mEtPhone;
    private EditText mEtPassword;
    private PermissionHelper mPermissionHelper;
    private Boolean isOK = false;
    private Thread t;
    private DBUtils dbUtils;
    private ResultSet rs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
        if (SaveSharedPreference.getUser().exists()) {
            saveSharedPreference.open();
            Intent intent = null;
            intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.login);

            //初始化并发起权限申请
            mPermissionHelper = new PermissionHelper(this, this);
            mPermissionHelper.requestPermissions();

            //这是最底下的那个跑马灯的特效
            TextView huizhi = findViewById(R.id.under);
            huizhi.setSelected(true);

            //控件部分
            mEtPhone = findViewById(R.id.etPhone);
            mEtPassword = findViewById(R.id.etPassword);
            mBtnLogin = findViewById(R.id.btn_login);
            mBtnRegister = findViewById(R.id.btn_register);
            mBtnForget = findViewById(R.id.btn_forget);
            rBtAutomaticLogin = findViewById(R.id.cbx_automatic_login);

            //注册
            mBtnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            //忘记密码
            mBtnForget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
                    startActivity(intent);
                }
            });
            //点击登录
            mBtnLogin.setOnClickListener(this::onClick);
        }
    }

    //点击登录
    public void onClick (View v) {
        String ok = "登录成功！";
        String fail = "登录失败！";

        if (mEtPhone.getText().toString().equals("") || mEtPassword.getText().toString().equals("")) {
            com.example.one.util.ToastUtil.showMsg(MainActivity.this, "没有输入手机号或者密码");
        }else if (mEtPhone.getText().toString().length() > 15 || mEtPassword.getText().toString().length() > 10) {
            com.example.one.util.ToastUtil.showMsg(MainActivity.this, "手机号或者密码格式不对");
        }else {
            String sqlfinduser = "select * from user where User_phone = '" + mEtPhone.getText().toString() + "';";
            Intent intent = null;
            isOK = false;
            try {
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dbUtils = new DBUtils();
                        rs = dbUtils.query(sqlfinduser);
                        try {
                            if (rs.isBeforeFirst()) {
                                rs.next();
                                if (rs.getString("U_password").equals(mEtPassword.getText().toString())) isOK = true;
                                if (isOK) {
                                    SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
                                    saveSharedPreference.setUsername(rs.getString("User_name"));
                                    saveSharedPreference.setUserId(rs.getInt("User_id"));
                                    saveSharedPreference.setPassword(rs.getString("U_password"));
                                    saveSharedPreference.setPhone(rs.getString("User_phone"));
                                    if (rBtAutomaticLogin.isChecked()) {
                                        saveSharedPreference.open();
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (t.isAlive());
            if (isOK) {
                //toast
                Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            } else {
                //不正确
                ToastUtil.showMsg(MainActivity.this, fail);
//            //登录失败居中显示
//            Toast toastcenter = Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_LONG);
//            toastcenter.setGravity(Gravity.CENTER, 0, 0);
//            toastcenter.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, //读取存储权限
                Manifest.permission.WRITE_EXTERNAL_STORAGE, //写入存储权限
                Manifest.permission.READ_PHONE_STATE, //读取电话权限
                Manifest.permission.CAMERA, //相机权限
                Manifest.permission.SEND_SMS, //发送信息
                //Manifest.permission.
                Manifest.permission.ACCESS_FINE_LOCATION,

        };
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        //finish(); //这里就直接闪退了
        ToastUtil.showMsg(MainActivity.this, "建议打开相应的权限");

    }

    private void initViews(){
        //已经拥有所需权限，可以放心操作任何东西了
    }

}