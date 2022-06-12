package com.example.one.temp;


import androidx.appcompat.app.AppCompatActivity;


import com.example.one.DBUtils;
import com.example.one.R;
import com.nex3z.flowlayout.FlowLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.TextView;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    List<String> array_his;
    FlowLayout flowLayout_his;//历史流式布局
    FlowLayout flowLayout_dis;//发现流式布局
    AutoCompleteTextView mEditSearch;//搜索输入文本框
    TextView news[] = new TextView[6];//流式布局组件
    TextView cleanHis;//清除历史信息组件
    Button cancel;//取消按钮

    TextView his_unfold;
    TextView dis_visible;
    boolean unfold = false;
    boolean invisible = false;

    private String temp;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        initView();//初始化
        initHis();
        initHotspots();
        initDropDown();
        addListener();

    }
    void initView(){
        flowLayout_his = findViewById(R.id.flowLayout_his);
        flowLayout_dis = findViewById(R.id.flowLayout_dis);
        cancel = findViewById(R.id.cancel);
        news[0] = findViewById(R.id.news_one);
        news[1] = findViewById(R.id.news_two);
        news[2] = findViewById(R.id.news_three);
        news[3] = findViewById(R.id.news_four);
        news[4] = findViewById(R.id.news_five);
        news[5] = findViewById(R.id.news_six);
        mEditSearch = this.findViewById(R.id.mEditSearch);
        cleanHis = findViewById(R.id.clean_his);
        his_unfold = findViewById(R.id.his_unfold);
        dis_visible = findViewById(R.id.dis_visible);
    }

    void initHis(){
        String sql = "select * from searchhistories where User_phone = '" + new SaveSharedPreference().getPhone() + "' order by Search_time desc;";
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                db = new DBUtils();
                rs = db.query(sql);
            }
        });
        t.start();
        while (t.isAlive()) ;
        String[] strings = new String[10];
        int count = 0;
        try {
            while (rs.next()) {
                if (count == 10) break;
                strings[count] = rs.getString("Search_text");
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        array_his = Arrays.asList(strings);
        for (int j = 0; j < count; j++) {//为历史记录添加小组件
            TextView tv = new TextView(SearchActivity.this);
            tv.setText(array_his.get(j));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            tv.setPadding(30,10,30,10);
            tv.setBackgroundResource(R.drawable.shape_flow);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditSearch.setText(tv.getText().toString().trim());
                    search();
                }
            });
            flowLayout_his.addView(tv);//普通添加
        }

    }

    void initHotspots(){
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
                    rs = db.query("select * from hotspot;");
                    try {
                        int i = 0;
                        while(rs.next() && i < 6){
                            news[i].setText(rs.getString("news"));
                            temp = rs.getString("news");
                            news[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mEditSearch.setText(temp);//更新搜索文本框内容
                                    search();
                                }
                            });
                            i++;
                        }
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
//                    for(int i = 0; i < 6; i++) {
//                        news[i].setText(hotspots.get(i));
//                        temp = hotspots.get(i);
//                        news[i].setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                mEditSearch.setText(temp);//更新搜索文本框内容
//                                search();
//                            }
//                        });
//                    }


    }
    void initDropDown() {
        final ArrayList<String> resourseData = new ArrayList<String>();//下拉提示输入信息表
        resourseData.add("aaaaaaaaaaaaaaa");
        ArrayAdapter<String> adapter_ = new ArrayAdapter<String> (this,android.R.layout.simple_dropdown_item_1line,resourseData);
        mEditSearch.setAdapter(adapter_);//添加适配器
        mEditSearch.setThreshold(1);//输入1字符后开始匹配
        mEditSearch.setOnClickListener(new View.OnClickListener() {//点击后弹出下拉框
            @Override
            public void onClick(View v) {
                mEditSearch.showDropDown();
            }
        });
    }
    void addListener(){
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {//搜索框添加监听器，手机确认键触发
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    if ("".equals(mEditSearch.getText().toString().trim())) {//如果输入为空，就搜索hint的信息
                        mEditSearch.setText(mEditSearch.getHint().toString().trim());
                        search();
                        return true;
                    }
                    // 搜索功能主体
                    else{//存在信息，进行搜索
                        search();
                        return true;
                    }
                }
                return false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {//取消按钮添加监听器
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cleanHis.setOnClickListener(new View.OnClickListener() {//取消按钮添加监听器，清空历史搜索记录
            @Override
            public void onClick(View view) {
                String sql = "delete from searchhistories where User_phone = '" + new SaveSharedPreference().getPhone() + "';";
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db = new DBUtils();
                        db.update(sql);
                    }
                });
                t.start();
                while (t.isAlive()) ;
                flowLayout_his.removeAllViews();
                initHis();
            }
        });

        his_unfold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!unfold){
                    his_unfold.setText("收起");
                    flowLayout_his.setMaxRows(7);
                    unfold = true;
                }
                else {
                    his_unfold.setText("展开");
                    flowLayout_his.setMaxRows(2);
                    unfold = false;
                }
            }
        });

        dis_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!invisible){
                    dis_visible.setText("显示");
                    flowLayout_dis.setVisibility(View.INVISIBLE);
                    invisible = true;
                }
                else {
                    dis_visible.setText("隐藏");
                    flowLayout_dis.setVisibility(View.VISIBLE);
                    invisible = false;
                }
            }
        });

    }

    void search(){//执行页面跳转进行搜索

        Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
        intent.putExtra("info", mEditSearch.getText().toString().trim());//传递搜索内容
        startActivity(intent);
        String sql = "insert into searchhistories(User_phone, Search_text) values ('" + new SaveSharedPreference().getPhone() + "', '" + mEditSearch.getText().toString() + "');";
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                db = new DBUtils();
                db.update(sql);
            }
        });
        t.start();
        while (t.isAlive());
        mEditSearch.setSelection(mEditSearch.getText().length());

        TextView tv = new TextView(this);
        tv.setText(mEditSearch.getText().toString().trim());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setPadding(30,10,30,10);
        tv.setBackgroundResource(R.drawable.shape_flow);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditSearch.setText(tv.getText().toString().trim());
                search();
            }
        });
        flowLayout_his.addView(tv,0);//执行搜索后给历史记录进行添加，添加到第0个
        //finish();




    }



}