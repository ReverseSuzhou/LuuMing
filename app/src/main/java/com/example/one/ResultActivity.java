package com.example.one;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private ImageButton mBtn_search;
    private SwipeRefreshLayout swipe_result;
    private RecyclerView rv_result;
    private TextView error_result;
    private List<Push> data = new LinkedList<>();
    private HomeAdapter adapter;
    private TextView mEditSearch;
    private String info;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);

        //控件
        mBtn_back = findViewById(R.id.result_page_1_1_imageview_back);
        mBtn_search = findViewById(R.id.result_page_1_2_1_imagebutton_search);
        mEditSearch = findViewById(R.id.result_page_1_2_1_edittext_mEditSearch);
        swipe_result = findViewById(R.id.swipe_result);
        rv_result= findViewById(R.id.rv_result);
        error_result= findViewById(R.id.error_result);

        swipe_result.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_result.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        Intent intent = getIntent();
        info = intent.getStringExtra("info");
        mEditSearch.setText(info);
        //返回
        mBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ResultActivity.this,HomePage.class);
                startActivity(intent);
            }
        });
        //搜索
        mBtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(ResultActivity.this,ResultActivity.class);
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

        swipe_result.setRefreshing(false);
        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    String temp = "select * from admin_forumt where Forumt_content like '%"+info+"%';";
                    rs = db.query(temp);
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
            swipe_result.setRefreshing(false);
            swipe_result.setVisibility(View.VISIBLE);
            adapter = new HomeAdapter(ResultActivity.this,data);
            rv_result.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
            rv_result.setAdapter(adapter);
        }
        else {
            error_result.setVisibility(View.VISIBLE);
        }

    }
}