package com.example.one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;



import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.one.Fragment.Fragment_HomeFour;
import com.example.one.Fragment.Fragment_HomeThree;
import com.example.one.Fragment.Fragment_HomeTwo;
import com.example.one.Fragment.Fragment_Homeone;
import com.example.one.temp.AssociationActivity;
import com.example.one.temp.EditorActivity;
import com.example.one.temp.MessageActivity;
import com.example.one.temp.SaveSharedPreference;
import com.example.one.temp.SearchActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class HomePage extends AppCompatActivity {
    //声明控件
    private RadioButton mBtn_home;
    private RadioButton mBtn_association;
    private RadioButton mBtn_editor;
    private RadioButton mBtn_message;
    private RadioButton mBtn_personal;
    private TextView tv_search;

//    private Button hotspot;
//    private Button reco;
//    private Button study;
//    private Button sport;

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        //搜索
        tv_search = findViewById(R.id.main_page_1_1_autocompletetextview_mEditSearch);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(HomePage.this, SearchActivity.class);
                startActivity(intent);
            }
        });


//↓↓↓↓↓↓↓↓底下五个按钮的跳转功能↓↓↓↓↓↓↓↓
//使用的时候要修改 ：1.别忘了上面的声明控件部分；2.控件部分中id后面修改成对应的id；3.OnClick中的.this前面的改成当前文件名

        //控件部分
        mBtn_home = findViewById(R.id.rb_mp);
        mBtn_association = findViewById(R.id.rb_association);
        mBtn_editor = findViewById(R.id.rb_add);
        mBtn_message = findViewById(R.id.rb_message);
        mBtn_personal = findViewById(R.id.rb_user);

//        hotspot = findViewById(R.id.main_page_2_button_HotSpot);
//        reco = findViewById(R.id.main_page_2_button_Recommend);
//        study = findViewById(R.id.main_page_2_button_Study);
//        sport = findViewById(R.id.main_page_2_button_Sport);

        initView();
        viewPager.setOffscreenPageLimit(5);

        initTab();

        //主页面
        mBtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(HomePage.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });


//
//        //社团圈
//        mBtn_association.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = null;
//                intent = new Intent(HomePage.this, AssociationActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        //发帖子
//        mBtn_editor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = null;
//                intent = new Intent(HomePage.this, EditorActivity.class);
//                startActivity(intent);
//            }
//        });
//        //消息
//        mBtn_message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = null;
//                intent = new Intent(HomePage.this, MessageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//

        //个人信息
        mBtn_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(HomePage.this, PersonalActivity.class);
                intent.putExtra("username", new SaveSharedPreference().getUsername());
                startActivity(intent);
                finish();
            }
        });

    }
    private void initView() {
        smartTabLayout = findViewById(R.id.hometab);
        viewPager = findViewById(R.id.homevp);
    }


    private void initTab() {
        FragmentPagerItemAdapter ad = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("热点", Fragment_Homeone.class).add("推荐",Fragment_HomeTwo.class)
                        .add("学习", Fragment_HomeThree.class).add("运动", Fragment_HomeFour.class)
                        .create());
        viewPager.setAdapter(ad);
        smartTabLayout.setViewPager(viewPager);
        smartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}