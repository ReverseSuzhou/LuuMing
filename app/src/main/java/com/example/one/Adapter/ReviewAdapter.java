package com.example.one.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.one.Bean.Comment;
import com.example.one.DBUtils;
import com.example.one.R;
import com.example.one.SaveSharedPreference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Comment> data;

    private final int N_TYPE = 0;
    private final int F_TYPE = 1;

    private int Max_num = 15;  //预加载的数据  一共15条

    private Boolean isfootview = true;  //是否有footview
    String ffid;
    DBUtils db;
    ResultSet rs;
    Thread t;
    String name;

    public ReviewAdapter(Context context, List<Comment> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review,viewGroup,false);
        View footview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.foot_item,viewGroup,false);
        if (i == F_TYPE){
            return new RecyclerViewHolder(footview,F_TYPE);
        }else {
            return new RecyclerViewHolder(view,N_TYPE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;
        if (isfootview && (getItemViewType(i))== F_TYPE){
            //底部加载提示
            recyclerViewHolder.Loading.setText("加载中...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Max_num += 8;
                    notifyDataSetChanged();
                }
            },2000);
        }else {

            //这是ord_item的内容
            final Comment post = data.get(i);
            String phone = post.getUser_phone();
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    rs = db.query("select * from user where User_phone = "+ phone + ";");
                    try {
                        rs.next();
                        name = rs.getString("User_name");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
            t.start();
            while(t.isAlive());
            recyclerViewHolder.username.setText(name);
            recyclerViewHolder.info.setText(post.getComment_text());
            recyclerViewHolder.time.setText(post.getComment_time());



//            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    int position = recyclerViewHolder.getBindingAdapterPosition();
//                    if (BmobUser.getCurrentUser(BmobUser.class) != null){
//                        Intent in = new Intent(context, Recive.class);
//                        in.putExtra("username",post.getUsername());
//                        in.putExtra("content",post.getInfo());
//                        in.putExtra("time",post.getCreatedAt());
//                        in.putExtra("title", post.getTitle());
//                        in.putExtra("user_onlyid",post.getUserOnlyId());
//                        in.putExtra("id",data.get(position).getObjectId());
//
//                        context.startActivity(in);
//                    }else {
//                        Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
//                        context.startActivity(new Intent(context, Login.class));
//                    }
//                }
//            });
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (position == Max_num - 1){
            return F_TYPE;  //底部type
        }else {
            return N_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() < Max_num){
            return data.size();
        }
        return Max_num;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView username,info,time; //ord_item的TextView
        public TextView Loading;


        public RecyclerViewHolder(View itemview, int view_type) {
            super(itemview);
            if (view_type == N_TYPE){
                username = itemview.findViewById(R.id.username_review);
                info = itemview.findViewById(R.id.info_review);
                time = itemview.findViewById(R.id.time_review);
            }else if(view_type == F_TYPE){
                Loading = itemview.findViewById(R.id.footText);
            }
        }
    }
}

