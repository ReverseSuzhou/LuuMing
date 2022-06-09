package com.example.one;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



import com.example.one.Adapter.ReviewAdapter;
import com.example.one.Bean.Comment;
import com.example.one.util.StorePicturesUtil;


import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Recive extends AppCompatActivity {

    private TextView username, content, time, title;
    private ImageView back;

    private ImageView rec_collect;
    private ImageView review;
    private ImageView rec_like;
    private ImageView rec_gender;
    private String my_phone;
    private String user_phone;
    private ImageView img; //显示的图片
    private Bitmap bitmap;
    private ImageView imgHead;
    private Bitmap bitmapHead; //头像

    //private CircleImageView recive_headpic;

    private List<Comment> data = new LinkedList<>();
    //关注按钮
    private Button focus_or_not;
    private boolean iscollect = false;
    private boolean islike = false;
    private boolean isfocus = false;
    private boolean related = false;
    private boolean focused = false;
    //User current_user = BmobUser.getCurrentUser(User.class);

    String ffid;
    String fffid;

    private SwipeRefreshLayout swipe_review;
    private RecyclerView rv_review;
    private TextView error_review;

    //private ReviewAdapter reviewAdapter;

    private String id_push;
    String id_collect;
    String id_like;
    DBUtils db,d;
    ResultSet rs;
    Thread t;
    ReviewAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recive);
        
        final Intent in = getIntent();
        user_phone = in.getStringExtra("user_phone");
        SaveSharedPreference person = new SaveSharedPreference();
        my_phone = person.getPhone();
        initView();
        initData();
        getiscollect();
        getislike();
        getisfocus();
        getgender();
        getHeadImg();
        getImg();

        swipe_review.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_review.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //监听返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgHead.setOnClickListener(new View.OnClickListener() {
        //recive_headpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(Recive.this, DetailedPersonalInformationActivity.class);
                intent.putExtra("user_phone", user_phone);
                startActivity(intent);
            }
        });

        review.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomizeDialog();
            }
        }));

            rec_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(islike) {
                        try {
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db = new DBUtils();
                                    //my_user;
                                    rs = db.query("select * from likelike where User_phone = "+ my_phone + " and Forumt_id = "+ id_push + ";");
                                    try {
                                        if(rs.isBeforeFirst()) {
                                            rs.next();
                                            id_like = rs.getString("like_id");
                                            db.update("delete from likelike where like_id = " + id_like + ";");
                                        }
                                        else{
                                        }
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        while(t.isAlive());
                        try {
                            db.connection.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        islike = false;
                        rec_like.setImageResource(R.drawable.agree_black);
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("update forumt set F_likenum=F_likenum-1 where Forumt_id =" + id_push + ";" );
                            }
                        });
                        t.start();
                        while(t.isAlive());
                    }
                    else {
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                //my_user;
                                db.update("insert likelike set User_phone = " + my_phone + ",Forumt_id = " + id_push + ";");
                            }
                        });
                        t.start();
                        while(t.isAlive());
                        islike = true;
                        rec_like.setImageResource(R.drawable.agree_red);
                        try {
                            db.connection.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("update forumt set F_likenum=F_likenum+1 where Forumt_id =" + id_push + ";" );
                            }
                        });
                        t.start();
                        while(t.isAlive());
                    }
                }
            });
            rec_collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(iscollect) {
                        try {
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db = new DBUtils();
                                    //my_user;
                                    rs = db.query("select * from collect where User_phone = "+ my_phone + " and Forumt_id = "+ id_push + ";");
                                    try {
                                        if(rs.isBeforeFirst()) {
                                            rs.next();
                                            id_collect = rs.getString("collect_id");
                                            db.update("delete from collect where collect_id = " + id_collect + ";");
                                        }
                                        else{
                                        }
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        while(t.isAlive());
                        try {
                            db.connection.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        iscollect = false;
                        rec_collect.setImageResource(R.drawable.shoucang_black);
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("update forumt set F_collectnum=F_collectnum-1 where Forumt_id =" + id_push + ";" );
                            }
                        });
                        t.start();
                        while(t.isAlive());
                    }
                    else {

                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                //my_user;
                                db.update("insert collect set User_phone = " + my_phone + ",Forumt_id = " + id_push + ";");
                            }
                        });
                        t.start();
                        while(t.isAlive());
                        iscollect = true;
                        rec_collect.setImageResource(R.drawable.redstars);
                        try {
                            db.connection.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("update forumt set F_collectnum=F_collectnum+1 where Forumt_id =" + id_push + ";" );
                            }
                        });
                        t.start();
                        while(t.isAlive());
                    }
                }
            });

        focus_or_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isfocus) {
                    try {
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("delete from focus where focus_phone = " + my_phone + " and follow_phone = "+ user_phone +";");
                            }
                        });
                        t.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    while(t.isAlive());
                    try {
                        db.connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    isfocus = false;
                    focus_or_not.setText("关注");
                }
                else {
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = new DBUtils();
                            //my_user;
                            db.update("insert focus set focus_phone = " + my_phone + ",follow_phone = " + user_phone + ";");
                        }
                    });
                    t.start();
                    while(t.isAlive());
                    isfocus = true;
                    focus_or_not.setText("已关");
                    try {
                        db.connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
        try {
            Refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

        private void getislike() {
            try {
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db = new DBUtils();
                        //my_user;
                        rs = db.query("select * from likelike where User_phone = "+ my_phone + " and Forumt_id = "+ id_push + ";");
                        try {
                            if(rs.isBeforeFirst()) {
                                rec_like.setImageResource(R.drawable.agree_red);
                                islike = true;
                            }
                            else{
                                rec_like.setImageResource(R.drawable.agree_black);
                                islike = false;
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(t.isAlive() == true);
            try {
                db.connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        private void getiscollect(){

                try {
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = new DBUtils();
                            //my_user;
                            rs = db.query("select * from collect where User_phone = "+ my_phone + " and Forumt_id = "+ id_push + ";");
                            try {
                                if(rs.isBeforeFirst()) {
                                    rec_collect.setImageResource(R.drawable.redstars);
                                    iscollect = true;
                                }
                                else{
                                    rec_collect.setImageResource(R.drawable.shoucang_black);
                                    iscollect = false;
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }


                        }
                    });
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while(t.isAlive() == true);
            try {
                db.connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    private void getisfocus(){
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    //my_user;
                    rs = db.query("select * from focus where focus_phone = "+ my_phone + " and follow_phone = "+ user_phone + ";");
                    try {
                        if(rs.isBeforeFirst()) {
                            focus_or_not.setText("已关");
                            isfocus = true;
                        }
                        else{
                            focus_or_not.setText("关注");
                            isfocus = false;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(t.isAlive() == true);
        try {
            db.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void getgender(){
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    //my_user;
                    rs = db.query("select * from user where User_phone = "+ user_phone + ";");
                    try {
                        if(rs.isBeforeFirst()) {
                            while(rs.next()){
                                if(rs.getString("User_sex").equals("男"))
                                rec_gender.setImageResource(R.drawable.man);
                                else{
                                    rec_gender.setImageResource(R.drawable.girl);
                                }
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(t.isAlive() == true);
        try {
            db.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void getHeadImg(){
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    //my_user;
                    rs = db.query("select * from user where User_phone = "+ user_phone + ";");
                    try {
                        if(rs.isBeforeFirst()) {
                            while(rs.next()){
                                String picture = rs.getString("Img");
                                StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                                bitmapHead = storePicturesUtil.stringToBitmap(picture);
                                imgHead.setImageBitmap(bitmapHead);
//                                if(rs.getString("Img").equals("男"))
//                                    rec_gender.setImageResource(R.drawable.man);
//                                else{
//                                    rec_gender.setImageResource(R.drawable.girl);
//                                }
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(t.isAlive() == true);
        try {
            db.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void getImg(){
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    //my_user;
                    System.out.println("id 的值是" + id_push);
                    rs = db.query("select * from Forumt where Forumt_id=" + id_push+ ";");
                    try {
                        if(rs.isBeforeFirst()) {
                            while(rs.next()){
                                System.out.println("hhhhhhh");
                                String picture = rs.getString("Img");
                                StorePicturesUtil storePicturesUtil = new StorePicturesUtil();
                                bitmap = storePicturesUtil.stringToBitmap(picture);
                                img.setImageBitmap(bitmap);

                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(t.isAlive() == true);
        try {
            db.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initData() {

        //第二种
        Intent a = getIntent();
        String usernamea = a.getStringExtra("username");
        String contenta = a.getStringExtra("content");
        String timea = a.getStringExtra("time");
        String titlea = a.getStringExtra("title");
        username.setText(usernamea);
        content.setText(contenta);
        time.setText(timea);
        title.setText(titlea);
        id_push = a.getStringExtra("id");

    }
    private void initView() {
        username = findViewById(R.id.username);
        content = findViewById(R.id.content);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        rec_collect = findViewById(R.id.rec_collect);
//        focus_or_not = findViewById(R.id.focus_or_not);
//        recive_headpic = findViewById(R.id.recive_headpic);
        swipe_review = findViewById(R.id.swipe_review);
        rv_review = findViewById(R.id.rv_review);
        error_review = findViewById(R.id.error_review);
        review = findViewById(R.id.review);
        rec_like = findViewById(R.id.rec_like);
        focus_or_not = findViewById(R.id.focus_or_not);
        rec_gender = findViewById(R.id.gender);
        //recive_headpic = findViewById(R.id.recive_headpic);
        img = findViewById(R.id.recive_page_imageview_picture);
        imgHead = findViewById(R.id.recive_headpic);
    }
    void Refresh() throws SQLException{
        swipe_review.setRefreshing(false);
        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    SaveSharedPreference ssp = new SaveSharedPreference();
                    rs = db.query("select * from comment where Forumt_id = " + id_push+" order by Comment_time desc;");
                    try {
                        while(rs.next()){
                            Comment co = new Comment();
                            co.setComment_id(Integer.toString(rs.getInt("Comment_id")));
                            co.setUser_phone(rs.getString("User_phone"));
                            co.setForumt_id(Integer.toString(rs.getInt("Forumt_id")));
                            co.setComment_text(rs.getString("Comment_text"));
                            co.setComment_time(rs.getString("Comment_time"));
                            data.add(co);
                        };
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        db.connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(t.isAlive() == true);
        if(data.size()>0){
            swipe_review.setRefreshing(false);
            swipe_review.setVisibility(View.VISIBLE);
            adapter = new ReviewAdapter(Recive.this,data);
            rv_review.setVisibility(View.VISIBLE);
            rv_review.setLayoutManager(new LinearLayoutManager(Recive.this));
            rv_review.setAdapter(adapter);
        }
        else {
            error_review.setVisibility(View.VISIBLE);
        }
    }

    private void showCustomizeDialog() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(Recive.this);
        final View dialogView = LayoutInflater.from(Recive.this)
                .inflate(R.layout.dialog_review,null);
        customizeDialog.setTitle("评论(50字)");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText text_review =
                                (EditText) dialogView.findViewById(R.id.text_review);
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                Date PDate=new Date();
                                //String str = date.toString();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String str = format.format(PDate);
                                SaveSharedPreference ssp = new SaveSharedPreference();
                                //my_user;
                                db.update("insert into comment(User_phone,Forumt_id,Comment_text,Comment_time) values("+ssp.getPhone()+","+id_push+",\""+text_review.getText()+"\",\"" + str + "\");");
                            }
                        });
                        t.start();
                        while(t.isAlive());
                        try {
                            Refresh();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = new DBUtils();
                                db.update("update forumt set F_commentnum=F_commentnum+1 where Forumt_id =" + id_push + ";" );
                            }
                        });
                        t.start();
                        while(t.isAlive());
                    }
                });
        customizeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        customizeDialog.show();
    }
}
