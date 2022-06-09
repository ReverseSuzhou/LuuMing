package com.example.one;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.one.util.StorePicturesUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DetailedPersonalInformationActivity extends AppCompatActivity {
    private ImageView iv_userPicture;
    private Bitmap bitmap;
    private TextView tv_userName;
    private TextView tv_gender;
    private TextView tv_age;
    private TextView tv_Signature;
    private TextView tv_email;
    private TextView tv_identity;
    private TextView tv_focus;
    private TextView tv_fan;
    private TextView tv_forumt;
    DBUtils db;
    Thread t;
    ResultSet rs;

    @Override
    protected  void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_personal_information);

        iv_userPicture = findViewById(R.id.detailed_personal_information_1_button_userpicture);
        tv_userName = findViewById(R.id.detailed_personal_information_1_textview_username);
        tv_gender = findViewById(R.id.detailed_personal_information_2_textview_gender);
        tv_age = findViewById(R.id.detailed_personal_information_2_textview_age);
        tv_Signature = findViewById(R.id.detailed_personal_information_textview_signature);
        tv_email = findViewById(R.id.detailed_personal_information_textview_email);
        tv_identity = findViewById(R.id.detailed_personal_information_textview_identity);
        tv_focus = findViewById(R.id.detailed_personal_information_textview_focus);
        tv_fan = findViewById(R.id.detailed_personal_information_textview_fan);
        tv_forumt = findViewById(R.id.detailed_personal_information_textview_forumt);
        String sql = "select * from user where User_phone = '" + getIntent().getStringExtra("user_phone") + "';";

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                db = new DBUtils();
                rs = db.query(sql);
            }
        });
        t.start();
        while (t.isAlive()) ;
        try {
            if (rs.isBeforeFirst()) {
                rs.next();
                tv_userName.setText(rs.getString("User_name"));
                tv_gender.setText("性别：" + rs.getString("User_sex"));
                tv_age.setText("年龄：" + rs.getString("User_age"));
                tv_Signature.setText("个性签名：\n"+rs.getString("U_signature"));
                tv_email.setText("邮箱：\n" + rs.getString("User_email"));
                String picture = rs.getString("img");
                StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                bitmap = storePicturesUtil.stringToBitmap(picture);
                iv_userPicture.setImageBitmap(bitmap);
                int access=rs.getInt("User_access");
                if(access==1)
                    tv_identity.setText("身份：认证达人");
                else
                    tv_identity.setText("身份：普通用户");
                tv_focus.setText("累计粉丝数："+rs.getInt("User_focus"));
                tv_fan.setText("累计关注数："+rs.getInt("User_fans"));
                tv_forumt.setText("累计发帖数："+rs.getInt("User_forumt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
