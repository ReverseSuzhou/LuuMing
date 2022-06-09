package com.example.one.Adapter;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.one.Bean.Message;
import com.example.one.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> data;

    private final int N_TYPE = 0;
    private final int F_TYPE = 1;

    private int Max_num = 15;  //预加载的数据  一共15条

    private Boolean isfootview = true;  //是否有footview

    public MessageAdapter(Context context, List<Message> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kudos,viewGroup,false);
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
            final Message post = data.get(i);
            String s1, s2 = post.getKudos_number() + "";
            if (post.getF_title().length() <= 10) {
                s1 = post.getF_title() + "的点赞";
            }
            else {
                s1 = post.getF_title().substring(0, 12) + "...的点赞";
            }
            recyclerViewHolder.f_title.setText(s1);
            recyclerViewHolder.kudos_number.setText(s2);
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

        public TextView kudos_number,f_title; //ord_item的TextView
        public TextView Loading;


        public RecyclerViewHolder(View itemview, int view_type) {
            super(itemview);
            if (view_type == N_TYPE){
                f_title = itemview.findViewById(R.id.kudos_name);
                kudos_number = itemview.findViewById(R.id.info_kudos);
            }else if(view_type == F_TYPE){
                Loading = itemview.findViewById(R.id.footText);
            }
        }
    }
}

