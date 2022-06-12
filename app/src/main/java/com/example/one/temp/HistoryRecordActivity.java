package com.example.one.temp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.one.Adapter.HomeAdapter;
import com.example.one.Bean.Push;
import com.example.one.DBUtils;
import com.example.one.PersonalActivity;
import com.example.one.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class HistoryRecordActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private ImageButton mBtn_delete;
    private SwipeRefreshLayout swipe_his_rec;
    private RecyclerView rv_his_rec;
    private TextView error_his_rec;
    private List<Push> data = new LinkedList<>();
    private HomeAdapter adapter;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_record_page);

        //控件部分
        mBtn_back = findViewById(R.id.history_record_page_1_1_button_back);
        mBtn_delete = findViewById(R.id.history_record_page_1_1_button_refresh);
        swipe_his_rec = findViewById(R.id.swipe_his_rec);
        rv_his_rec = findViewById(R.id.rv_his_rec);
        error_his_rec = findViewById(R.id.error_his_rec);
        swipe_his_rec.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_his_rec.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //返回
        mBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(HistoryRecordActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });
        try {
            Refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //
        mBtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phone = new SaveSharedPreference().getPhone();
                        String sql = "delete from historyrecord where User_phone = '"+phone+"' ;";
                        DBUtils dblinke = new DBUtils();
                        dblinke.update(sql);
                    }
                });
                thread.start();
                while(thread.isAlive()) ;
                try {
                    Refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
    private void Refresh() throws SQLException {

        swipe_his_rec.setRefreshing(false);
        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    rs = db.query("select * from admin_forumt, historyrecord where historyrecord.User_phone = '"+
                            new SaveSharedPreference().getPhone() + "' and historyrecord.Forumt_id = admin_forumt.Forumt_id order by History_time desc;");
                    try {
                        while(rs.next()){
                            Push po = new Push();
                            po.setForumt_id(rs.getString("Forumt_id"));
                            po.setForumt_date(rs.getString("Forumt_date"));
                            po.setForumt_title(rs.getString("F_title"));
                            po.setForumt_content(rs.getString("Forumt_content"));
                            po.setF_likenum(rs.getInt("F_likenum"));
                            po.setF_collectnum(rs.getInt("F_collectnum"));
                            po.setF_commentnum(rs.getInt("F_commentnum"));
                            po.setUsername(rs.getString("User_name"));
                            po.setUser_phone(rs.getString("User_phone"));
                            data.add(po);
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
            swipe_his_rec.setRefreshing(false);
            swipe_his_rec.setVisibility(View.VISIBLE);
            adapter = new HomeAdapter(HistoryRecordActivity.this,data);
            rv_his_rec.setLayoutManager(new LinearLayoutManager(HistoryRecordActivity.this));
            rv_his_rec.setAdapter(adapter);
        }
        else {
            error_his_rec.setVisibility(View.VISIBLE);
        }

    }
}