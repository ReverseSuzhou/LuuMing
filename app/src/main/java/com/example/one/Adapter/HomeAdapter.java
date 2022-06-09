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


import com.example.one.Bean.Push;
import com.example.one.DBUtils;
import com.example.one.R;
import com.example.one.Recive;
import com.example.one.SaveSharedPreference;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;



public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Push> data;

    private final int N_TYPE = 0;
    private final int F_TYPE = 1;

    private int Max_num = 15;  //预加载的数据  一共15条

    private Boolean isfootview = true;  //是否有footview
    String ffid;

    public HomeAdapter(Context context, List<Push> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home,viewGroup,false);
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
            final Push post = data.get(i);

            recyclerViewHolder.username.setText(post.getUsername());
            recyclerViewHolder.title.setText(post.getForumt_title());
            recyclerViewHolder.info.setText(post.getForumt_content());
            recyclerViewHolder.time.setText(post.getForumt_date());
            recyclerViewHolder.likenum.setText(Integer.toString(post.getF_likenum()));
            recyclerViewHolder.collectnum.setText(Integer.toString(post.getF_collectnum()));
            recyclerViewHolder.commentnum.setText(Integer.toString(post.getF_commentnum()));
            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = recyclerViewHolder.getBindingAdapterPosition();

                        Intent in = new Intent(context, Recive.class);
                        in.putExtra("username",post.getUsername());
                        in.putExtra("content",post.getForumt_content());
                        in.putExtra("time",post.getForumt_date());
                        in.putExtra("title", post.getForumt_title());
                        in.putExtra("user_phone",post.getUser_phone());
                        in.putExtra("id",data.get(position).getForumt_id());


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String phone = new SaveSharedPreference().getPhone();
                            String idnum = data.get(position).getForumt_id();
                            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Timestamp time = new Timestamp(System.currentTimeMillis());
                            DBUtils db = new DBUtils();
                            ResultSet rst;

                            Boolean exit=false;
                            String sqlqens="select * from historyrecord where User_phone = '"+phone+"' and Forumt_id = "+idnum+";";
                            try {
                                rst = db.query(sqlqens);
                                if (rst.next()) {
                                    exit = true;
                                }
                                else {
                                    exit = false;
                                }
                            } catch (Exception e)  {
                                e.printStackTrace();
                            }

                            if (exit == true) {
                                String sqlset = "update historyrecord set History_time = \""+time+"\" where User_phone = '"+phone+"' and Forumt_id = "+idnum+";";
                                DBUtils dbl = new DBUtils();
                                dbl.update(sqlset);
                            }
                            else {
                                DBUtils dblink = new DBUtils();
                                String sql = "insert into historyrecord values( '"+ phone+"', "+idnum+", \""+time+"\");";
                                dblink.update(sql);
                            }

                        }
                    }).start();
                        context.startActivity(in);
                }
            });
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

        public TextView username,info,time,title,likenum,collectnum,commentnum; //ord_item的TextView
        public TextView Loading;
        public ImageView gender;

        public RecyclerViewHolder(View itemview, int view_type) {
            super(itemview);
            if (view_type == N_TYPE){
                username = itemview.findViewById(R.id.username);
                info = itemview.findViewById(R.id.info);
                time = itemview.findViewById(R.id.time);
                gender = itemview.findViewById(R.id.gender);
                title = itemview.findViewById(R.id.title);
                likenum = itemview.findViewById(R.id.likenum);
                collectnum = itemview.findViewById(R.id.collectnum);
                commentnum = itemview.findViewById(R.id.commentnum);
            }else if(view_type == F_TYPE){
                Loading = itemview.findViewById(R.id.footText);
            }
        }
    }
}

