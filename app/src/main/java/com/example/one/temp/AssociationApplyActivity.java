package com.example.one.temp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.one.DBUtils;
import com.example.one.HomePage;
import com.example.one.PersonalActivity;
import com.example.one.R;
import com.example.one.util.StorePicturesUtil;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class AssociationApplyActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private EditText mEt_community_name;
    private EditText mEt_phone_number;
    private Button mBtn_get_verification_code;
    private EditText mEt_upload_photo_evidence;
    private ImageButton mBtn_upload_photo;
    private Button mBtn_button_ensure;
    private RadioGroup rg;
    private RadioButton rb_manager, rb_member;
    private TextView tv_label_tip;
    private int id;
    EventHandler handler;
    //private PermissionHelper mPermissionHelper;
    private ImageButton mBtn_insert_picture;

    DBUtils db;
    ResultSet rs,rs1, rs2;
    Thread t;
    boolean tmp = true;
    boolean tmp1 = false;
    boolean tmp2 = false;
    String Association_id = null;
    Uri photouri;
    private Bitmap bitmap;

    ActivityResultLauncher<String> perssion_camera = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
//                        Toast.makeText(getApplicationContext(),"wonderful", Toast.LENGTH_LONG).show();
                        photouri = createImageUri();
                        req_camera.launch(photouri);
                    }
                }
            });

    ActivityResultLauncher<String> perssion_album = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
//                        Toast.makeText(getApplicationContext(),"wonderful", Toast.LENGTH_LONG).show();
                        req_album.launch("image/*");
                    }
                }
            });

    ActivityResultLauncher<Uri>  req_camera = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        launchImageCrop(photouri);
                    }
                }
            }
    );

    ActivityResultLauncher<String> req_album = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    launchImageCrop(result);

                }
            }
    );

    //裁剪图片注册
    private final ActivityResultLauncher<CropImageResult> mActLauncherCrop =
            registerForActivityResult(new CropImage(), result -> {
                //裁剪之后的图片Uri，接下来可以进行压缩处理
                Bitmap bmp;
                try {
                    bmp = CompressImage.getBitmapFormUri(AssociationApplyActivity.this, result);
                    mBtn_insert_picture.setImageBitmap(bmp);
                    bitmap = bmp;

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("nooooooo");                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobSDK.submitPolicyGrantResult(true, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.association_apply_page);

        MobSDK.init(this, "3598b2e09a55a", "48beefd31be58bc75a9cdedda26e67cc");

        //控件部分
        mBtn_back = findViewById(R.id.association_apply_page_1_button_back);
        mEt_community_name = findViewById(R.id.association_apply_page_edittext_community_name);
        mEt_phone_number = findViewById(R.id.association_apply_page_edittext_phone_number);
        mBtn_get_verification_code = findViewById(R.id.association_apply_page_button_get_verification_code);
        mEt_upload_photo_evidence = findViewById(R.id.association_apply_page_edittext_upload_photo_evidence);
        mBtn_upload_photo = findViewById(R.id.association_apply_page_button_upload_photo);
        mBtn_button_ensure = findViewById(R.id.association_apply_page_button_ensure);
        rb_manager = findViewById(R.id.association_apply_page_radiobutton_manager);
        rb_member = findViewById(R.id.association_apply_page_radiobutton_member);
        rg = findViewById(R.id.association_apply_page_radiogroup);
        tv_label_tip = findViewById(R.id.association_apply_page_textview_label_tip);
        mBtn_insert_picture = findViewById(R.id.association_apply_page_button_upload_photo);
        rg.setOnCheckedChangeListener(cBoxListener);


        //返回
        mBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(AssociationApplyActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });
        //验证信息
        handler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                                String picture = storePicturesUtil.bitmapToString(bitmap);
                                id = rg.getCheckedRadioButtonId();
                                String str_label;
                                if (R.id.association_apply_page_radiobutton_manager == id) {
                                    str_label = "manager";
                                } else {
                                    str_label = "member";
                                }
                                Toast.makeText(getApplicationContext(), "申请已提交，等待管理员审核，审核结果会发送至账号邮箱！（若未绑定邮箱请先绑定）", Toast.LENGTH_LONG).show();
                                String sql = "insert into apply(User_phone, Association_name, Apply_labe,img) values ('" + mEt_phone_number.getText().toString() + "'," +
                                        " '" + mEt_community_name.getText().toString() +"','"+ str_label +  "','" + picture + "');";
                                DBUtils dbUtils = new DBUtils();
                                try {
                                    t=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbUtils.update(sql);
                                        }
                                    });
                                    t.start();
                                    while (t.isAlive());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //跳转页面
                                Intent intent = null;
                                intent = new Intent(AssociationApplyActivity.this, HomePage.class);
                                startActivity(intent);
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AssociationApplyActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
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
        //获取验证码
        mBtn_get_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEt_phone_number.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "未输入手机号", Toast.LENGTH_LONG).show();
                } else if (mEt_phone_number.getText().toString().length() > 15 || mEt_phone_number.getText().toString().length() < 10) {
                    Toast.makeText(getApplicationContext(), "手机号格式不对", Toast.LENGTH_LONG).show();
                } else {
                    //如果没问题则发送验证码
                    //Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
                    String phone = mEt_phone_number.getText().toString();
                    SMSSDK.getVerificationCode("86", phone);
                }
            }
        });
        //确认申请
        mBtn_button_ensure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (mEt_community_name.getText().toString().equals("") || mEt_phone_number.getText().toString().equals("") || mEt_upload_photo_evidence.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "信息不全", Toast.LENGTH_LONG).show();
                } else if (mEt_community_name.getText().toString().length() > 20 || mEt_phone_number.getText().toString().length() > 15) {
                    Toast.makeText(getApplicationContext(), "社团名或手机号不符合规范", Toast.LENGTH_LONG).show();
                } else {
                    t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = new DBUtils();
                            rs2 = db.query("select * from User where User_phone = '" + mEt_phone_number.getText().toString() + "';");
                            try {
                                if (rs2.next()) {
                                    tmp2 = true;
                                    rs2.previous();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();
                    while(t.isAlive());
                    if (tmp2) {
                        String phone = mEt_phone_number.getText().toString();
                        String number = mEt_upload_photo_evidence.getText().toString();
                        SMSSDK.submitVerificationCode("86", phone, number);
                    } else {
                        Toast.makeText(getApplicationContext(), "该手机号并未注册", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mBtn_insert_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display();
            }
        });
    }
    private RadioGroup.OnCheckedChangeListener cBoxListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (R.id.association_apply_page_radiobutton_manager == i) {
                tv_label_tip.setText("您选择的标签：" + rb_manager.getText().toString());
                setTitle(String.valueOf(rb_manager.getText()));
            } else {
                tv_label_tip.setText("您选择的标签：" + rb_member.getText().toString());
                setTitle(String.valueOf(rb_member.getText()));
            }
        }
    };

    private void display() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_alertdiag_layout,null);
        Button btncamera = (Button) dialogView.findViewById(R.id.btncamera);
        Button btnalbum = (Button) dialogView.findViewById(R.id.btnalbum);

        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perssion_camera.launch(Manifest.permission.CAMERA);

            }
        });

        btnalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perssion_album.launch(Manifest.permission.CAMERA);

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        dialog.show();
    }

    /**
     * 开启裁剪图片
     *
     * @param sourceUri 原图片uri
     */
    private void launchImageCrop(Uri sourceUri) {
        mActLauncherCrop.launch(new CropImageResult(sourceUri, 1, 1));
    }



    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }
}