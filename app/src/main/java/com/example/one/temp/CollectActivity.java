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

public class CollectActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private SwipeRefreshLayout swipe_collect;
    private RecyclerView rv_collect;
    private TextView error_collect;
    private List<Push> data = new LinkedList<>();
    private HomeAdapter adapter;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_page);

        //控件部分
        mBtn_back = findViewById(R.id.collect_page_1_button_back);
        swipe_collect = findViewById(R.id.swipe_collect);
        rv_collect = findViewById(R.id.rv_collect);
        error_collect = findViewById(R.id.error_collect);

        swipe_collect.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_collect.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                intent = new Intent(CollectActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });
        try {
            Refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void Refresh() throws SQLException{

        swipe_collect.setRefreshing(false);

        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    rs = db.query("select * from admin_forumt, collect where collect.User_phone = '"+ new SaveSharedPreference().getPhone()
                    +"' and collect.Forumt_id = admin_forumt.Forumt_id order by Forumt_date desc;");
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
            swipe_collect.setRefreshing(false);
            swipe_collect.setVisibility(View.VISIBLE);
            adapter = new HomeAdapter(CollectActivity.this,data);
            rv_collect.setLayoutManager(new LinearLayoutManager(CollectActivity.this));
            rv_collect.setAdapter(adapter);
        }
        else {
            error_collect.setVisibility(View.VISIBLE);
        }

    }
}