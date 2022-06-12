package com.example.one.temp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.one.Adapter.HomeAdapter;
import com.example.one.Bean.Push;
import com.example.one.DBUtils;
import com.example.one.PersonalActivity;
import com.example.one.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MyReleaseActivity extends AppCompatActivity {
    //声明控件
    private ImageButton mBtn_back;
    private ImageButton mBtn_refresh;
    private SwipeRefreshLayout swipe_myrelease;
    private SwipeMenuRecyclerView rv_myrelease;
    private TextView error_myrelease;
    private List<Push> data = new LinkedList<>();
    private HomeAdapter adapter;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myrelease_page);

        //控件部分
        mBtn_back = findViewById(R.id.myrelease_page_1_button_back);
        mBtn_refresh = findViewById(R.id.myrelease_page_1_1_button_refresh);

        swipe_myrelease = findViewById(R.id.swipe_myrelease);
        rv_myrelease = findViewById(R.id.rv_myrelease);
        error_myrelease = findViewById(R.id.error_myrelease);
        rv_myrelease.setSwipeMenuCreator(swipeMenuCreator);
        rv_myrelease.setSwipeMenuItemClickListener(swipeMenuItemClickListener);
        swipe_myrelease.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_myrelease.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                intent = new Intent(MyReleaseActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });
        mBtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        try {
            Refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    private void Refresh() throws SQLException{

        swipe_myrelease.setRefreshing(false);

        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
                    rs = db.query("select * from admin_forumt where User_phone = '" + saveSharedPreference.getPhone() + "'order by Forumt_date desc;");
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
            swipe_myrelease.setRefreshing(false);
            swipe_myrelease.setVisibility(View.VISIBLE);
            adapter = new HomeAdapter(MyReleaseActivity.this,data);
            rv_myrelease.setLayoutManager(new LinearLayoutManager(MyReleaseActivity.this));
            rv_myrelease.setAdapter(adapter);
        }
        else {
            error_myrelease.setVisibility(View.VISIBLE);
        }

    }
    SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        // 创建菜单：
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem deleteItem = new SwipeMenuItem(MyReleaseActivity.this)
                    .setTextColor(Color.WHITE)
                    .setBackgroundColor(Color.RED)
                    .setText("删除")
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };
    SwipeMenuItemClickListener swipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection();//左边还是右边菜单
            final int adapterPosition = menuBridge.getAdapterPosition();//    recyclerView的Item的position。
            int position = menuBridge.getPosition();// 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {

                //删除操作
                String fid = data.get(adapterPosition).getForumt_id();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sqlsentence = "delete from forumt where forumt_id = "+fid+";";
                        DBUtils dblink = new DBUtils();
                        dblink.update(sqlsentence);
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

        }
    };
}