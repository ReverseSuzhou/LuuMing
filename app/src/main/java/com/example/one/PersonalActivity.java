package com.example.one;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;


import com.example.one.temp.AssociationActivity;
import com.example.one.temp.AssociationApplyActivity;
import com.example.one.temp.CollectActivity;
import com.example.one.temp.CompressImage;
import com.example.one.temp.CropImage;
import com.example.one.temp.CropImageResult;
import com.example.one.temp.DetailedPersonalInformationActivity;
import com.example.one.temp.EditorActivity;
import com.example.one.temp.HistoryRecordActivity;
import com.example.one.temp.MessageActivity;
import com.example.one.temp.MyReleaseActivity;
import com.example.one.temp.ReplacePhoneActivity;
import com.example.one.temp.SaveSharedPreference;
import com.example.one.util.StorePicturesUtil;

import java.io.IOException;
import java.sql.ResultSet;

public class PersonalActivity extends AppCompatActivity {
    //声明控件
    //底下五个
    private RadioButton mBtn_home;
    private RadioButton mBtn_association;
    private RadioButton mBtn_editor;
    private RadioButton mBtn_message;
    private RadioButton mBtn_personal;
    //上面几个
    private ImageButton mBtn_history;
    private ImageButton mBtn_collect;
    private ImageButton mBtn_replace_phone;
    private Button mBtn_modify;
    private Button mBtn_apply;
    private Button mBtn_myrelease;
    private ImageButton mBtn_userpicture;

    TextView UserName;
    TextView signature;
    Button rBt_cancellation;
    Uri photouri;
    ResultSet rs;
    String phone;
    String usernametemp;
    String signaturetemp;

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
                    bmp = CompressImage.getBitmapFormUri(PersonalActivity.this, result);
                    mBtn_userpicture.setImageBitmap(bmp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                                        storePicturesUtil.storeHeadImg(bmp);
                                        System.out.println("yesssss");
                                    }
                                }).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }});

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("nooooooo");
                }


            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_page);
        InitUpButtonAndTheirListeners();
        InitBelowButtonAndTheirListeners();
        getUserInfo();
        UserName.setText(usernametemp);


        if (signaturetemp.isEmpty())
        {
            signature.setText("\\_(2dc80_0)_/ ");
        }
        else {
            signature.setText(signaturetemp);
        }

        setHeadImage();
    }

    protected void onResume(Bundle savedInstance) {
        super.onResume();
        UserName = findViewById(R.id.personal_page_1_textview_username);
        signature = findViewById(R.id.personal_page_1_textview_signature);
        getUserInfo();
        UserName.setText(usernametemp);
        if (signaturetemp.isEmpty())
        {
            signature.setText("\\_(0_0)_/ ");
        }
        else {
            signature.setText(signaturetemp);
        }

    }

    private void getUserInfo() {
        phone = new SaveSharedPreference().getPhone();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "select User_name,U_signature from user where User_phone = '"+phone+"' ";
                DBUtils dblink= new DBUtils();
                ResultSet rest;
                rest = dblink.query(sql);
                try {
                    rest.next();
                    usernametemp = rest.getString("User_name");
                    signaturetemp = rest.getString("U_signature");
                } catch ( Exception e)  {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        while (t.isAlive()) ;
    }
    //显示头像
    private void setHeadImage (){
        SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
        int id = saveSharedPreference.getUserId(); //获取当前用户id
        System.out.println("id = " + id);
        String sql = "select * from User where User_id=" + id + ";";

        DBUtils dbUtils = new DBUtils();
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    rs = dbUtils.query(sql);
                }
            });
            t.start();
            while (t.isAlive()) ;
            if (rs.isBeforeFirst()){
                rs.next();
                String string = rs.getString("Img");
                StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                Bitmap bitmap = storePicturesUtil.stringToBitmap(string);
                mBtn_userpicture.setImageBitmap(bitmap);
            }
            else{
                System.out.println("用户未更新过头像");
            }
        } catch (Exception e) {
            System.out.println("发生了错误");
            e.printStackTrace();
        }


    }




    private void InitUpButtonAndTheirListeners() {

        mBtn_history = findViewById(R.id.personal_page_2_button_history);
        mBtn_collect = findViewById(R.id.personal_page_2_button_collect);
        mBtn_replace_phone = findViewById(R.id.personal_page_2_button_phone);
        mBtn_modify = findViewById(R.id.personal_page_button_exinfo);
        mBtn_apply = findViewById(R.id.personal_page_button_association_apply);
        mBtn_userpicture = findViewById(R.id.personal_page_1_button_userpicture);
        mBtn_myrelease = findViewById(R.id.personal_page_button_myrelease);
        UserName = findViewById(R.id.personal_page_1_textview_username);
        rBt_cancellation = findViewById(R.id.personal_page_button_cancellation);
        signature = findViewById(R.id.personal_page_1_textview_signature);
        //显示个人信息
        UserName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, DetailedPersonalInformationActivity.class);
                intent.putExtra("user_phone", new SaveSharedPreference().getPhone());
                startActivity(intent);
            }
        });



        //历史记录
        mBtn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, HistoryRecordActivity.class);
                startActivity(intent);
            }
        });
        //收藏
        mBtn_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, CollectActivity.class);
                startActivity(intent);
            }
        });
        //修改手机号
        mBtn_replace_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, ReplacePhoneActivity.class);
                startActivity(intent);
            }
        });


        //修改个人信息
        mBtn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this,ModifyPersonalActivity.class);
                startActivity(intent);
            }
        });



        //申请社团
        mBtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, AssociationApplyActivity.class);
                startActivity(intent);
            }
        });
        //我的帖子
        mBtn_myrelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, MyReleaseActivity.class);
                startActivity(intent);
            }
        });


        //更改头像
        mBtn_userpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display();
            }
        });

        rBt_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
                saveSharedPreference.close();
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void InitBelowButtonAndTheirListeners() {
        //↓↓↓↓↓↓↓↓底下五个按钮的跳转功能↓↓↓↓↓↓↓↓
//使用的时候要修改 ：1.别忘了上面的声明控件部分；2.控件部分中id后面修改成对应的id；3.OnClick中的.this前面的改成当前文件名

        //控件部分
        mBtn_home = findViewById(R.id.rb_mp);
        mBtn_association = findViewById(R.id.rb_association);
        mBtn_editor = findViewById(R.id.rb_add);
        mBtn_message = findViewById(R.id.rb_message);
        mBtn_personal = findViewById(R.id.rb_user);

        //主页面
        mBtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this,HomePage.class);
                startActivity(intent);
                finish();
            }
        });


        //社团圈
        mBtn_association.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, AssociationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //发帖子
        mBtn_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        //消息
        mBtn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });



        //个人信息
        mBtn_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(PersonalActivity.this,PersonalActivity.class);
                startActivity(intent);
                finish();
            }
        });
//↑↑↑↑↑↑↑↑底下五个按钮的跳转功能↑↑↑↑↑↑↑↑
    }

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