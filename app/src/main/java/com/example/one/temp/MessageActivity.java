package com.example.one.temp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.one.Adapter.HomeAdapter;
import com.example.one.Adapter.MessageAdapter;
import com.example.one.Adapter.NoticeAdapter;
import com.example.one.Adapter.ReviewAdapter;
import com.example.one.Bean.Comment;
import com.example.one.Bean.Message;
import com.example.one.Bean.Notice;
import com.example.one.Bean.Push;
import com.example.one.DBUtils;
import com.example.one.HomePage;
import com.example.one.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private ImageButton mBtn_refresh;
    private RadioGroup radioGroup;
    private RadioButton rBtn_notice;
    private RadioButton rBtn_kudos;
    private RadioButton rBtn_focus;
    private RadioButton rBtn_comments;
    private SwipeRefreshLayout swipe_message;
    private RecyclerView rv_message;
    private TextView error_message;
    private List<Message> messages = new LinkedList<>();
    private List<Push> data = new LinkedList<>();
    private List<Comment> comments = new LinkedList<>();
    private List<Notice> notices = new LinkedList<>();
    private MessageAdapter Madapter;
    private HomeAdapter Hadapter;
    private ReviewAdapter Radapter;
    private NoticeAdapter Nadapter;
    private String[] sql = new String[4];
    private int select = 1;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        //控件部分
        radioGroup = findViewById(R.id.message_page_relativelayout_2_2);
        mBtn_back = findViewById(R.id.message_page_1_button_back);
        mBtn_refresh = findViewById(R.id.message_page_1_button_refresh);
        rBtn_notice = findViewById(R.id.message_page_2_2_button_notification);
        rBtn_kudos = findViewById(R.id.message_page_2_2_button_praise);
        rBtn_focus = findViewById(R.id.message_page_2_2_button_concern);
        rBtn_comments = findViewById(R.id.message_page_2_2_button_comment);

        SaveSharedPreference user = new SaveSharedPreference();
        sql[0] = "select * from notice order by Notice_time desc;";
        sql[1] = "select * from user_forumt left join admin_forumt on user_forumt.Forumt_id = admin_forumt.Forumt_id where user_forumt.User_phone = '" + user.getPhone() + "' order by Forumt_date desc;";
        sql[2] = "select * from focus left join admin_forumt on follow_phone = User_phone where focus_phone = '" + user.getPhone() + "' order by Forumt_date desc limit 10;";
        sql[3] = "select * from comment left join user_forumt on user_forumt.Forumt_id = comment.Forumt_id where user_forumt.User_phone = '" + user.getPhone() + "' order by Comment_time desc;";

        swipe_message = findViewById(R.id.swipe_message);
        rv_message = findViewById(R.id.rv_message);
        error_message = findViewById(R.id.error_message);
        Refresh();
        swipe_message.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_message.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //返回
        mBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(MessageActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        mBtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Refresh();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                SaveSharedPreference user = new SaveSharedPreference();
                if (checkedId == rBtn_notice.getId()) {
                    select = 1;
                }
                else if (checkedId == rBtn_kudos.getId()) {
                    select = 2;
                }
                else if (checkedId == rBtn_focus.getId()) {
                    select = 3;
                }
                else if (checkedId == rBtn_comments.getId()) {
                    select = 4;
                }
                Refresh();
            }
        });
    }
    private void Refresh() {
        swipe_message.setRefreshing(false);
        messages.clear();
        comments.clear();
        data.clear();
        notices.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    rs = db.query(sql[select - 1]);
                    try {
                        if (!rs.isBeforeFirst()) return ;
                        switch (select) {
                            case 1:
                                while (rs.next()) {
                                    Notice notice = new Notice();
                                    notice.setNotice_title(rs.getString("Notice_title"));
                                    notice.setNotice_content(rs.getString("Notice_content"));
                                    notice.setNotice_time(rs.getString("Notice_time"));
                                    notices.add(notice);
                                }
                                break;
                            case 2:
                                while (rs.next()) {
                                    Message message = new Message();
                                    message.setF_title(rs.getString("F_title"));
                                    message.setKudos_number(rs.getInt("F_likenum"));
                                    messages.add(message);
                                }
                                break;
                            case 3:
                                while (rs.next()) {
                                    Push push = new Push();
                                    push.setForumt_id(rs.getString("Forumt_id"));
                                    push.setForumt_date(rs.getString("Forumt_date"));
                                    push.setForumt_title(rs.getString("F_title"));
                                    push.setForumt_content(rs.getString("Forumt_content"));
                                    push.setF_likenum(rs.getInt("F_likenum"));
                                    push.setF_collectnum(rs.getInt("F_collectnum"));
                                    push.setF_commentnum(rs.getInt("F_commentnum"));
                                    push.setUsername(rs.getString("User_name"));
                                    push.setUser_phone(rs.getString("User_phone"));
                                    data.add(push);
                                }
                                break;
                            case 4:
                                while (rs.next()) {
                                    Comment comment = new Comment();
                                    comment.setComment_id(Integer.toString(rs.getInt("Comment_id")));
                                    comment.setUser_phone(rs.getString("comment.User_phone"));
                                    comment.setForumt_id(Integer.toString(rs.getInt("user_forumt.Forumt_id")));
                                    comment.setComment_text(rs.getString("Comment_text"));
                                    comment.setComment_time(rs.getString("Comment_time"));
                                    comments.add(comment);
                                }
                                break;
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
        while (t.isAlive() == true);
        switch (select) {
            case 1:
                if (notices.size() > 0) {
                    swipe_message.setRefreshing(false);
                    swipe_message.setVisibility(View.VISIBLE);
                    Nadapter = new NoticeAdapter(MessageActivity.this,notices);
                    rv_message.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    rv_message.setAdapter(Nadapter);
                }
                break;
            case 2:
                if (messages.size() > 0) {
                    swipe_message.setRefreshing(false);
                    swipe_message.setVisibility(View.VISIBLE);
                    Madapter = new MessageAdapter(MessageActivity.this,messages);
                    rv_message.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    rv_message.setAdapter(Madapter);
                }
                break;
            case 3:
                if(data.size()>0){
                    swipe_message.setRefreshing(false);
                    swipe_message.setVisibility(View.VISIBLE);
                    Hadapter = new HomeAdapter(MessageActivity.this,data);
                    rv_message.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    rv_message.setAdapter(Hadapter);
                }
                else {
                    error_message.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if (comments.size() > 0) {
                    swipe_message.setRefreshing(false);
                    swipe_message.setVisibility(View.VISIBLE);
                    Radapter = new ReviewAdapter(MessageActivity.this,comments);
                    rv_message.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    rv_message.setAdapter(Radapter);
                }
                break;
        }
    }
}