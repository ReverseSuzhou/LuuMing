package com.example.one.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.one.Adapter.HomeAdapter;
import com.example.one.Bean.Push;
import com.example.one.DBUtils;
import com.example.one.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Fragment_HomeFour extends Fragment{
    private SwipeRefreshLayout swipe_hometabfour;
    private RecyclerView rv_hometabfour;
    private TextView error_hometabfour;
    List<Push> data = new LinkedList<>();
    private HomeAdapter adapter;
    DBUtils db;
    ResultSet rs;
    Thread t;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hometabfour,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        initView();
        Refresh();



        swipe_hometabfour.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        swipe_hometabfour.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
    }
    private void Refresh() {
        swipe_hometabfour.setRefreshing(false);
        data.clear();
        try {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db = new DBUtils();
                    rs = db.query("select * from admin_forumt where F_lable = 'sport' order by Forumt_date desc ;");
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
            swipe_hometabfour.setRefreshing(false);
            swipe_hometabfour.setVisibility(View.VISIBLE);
            adapter = new HomeAdapter(getActivity(),data);
            rv_hometabfour.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_hometabfour.setAdapter(adapter);
        }
        else {
            error_hometabfour.setVisibility(View.VISIBLE);
        }
    }


    private void initView() {
        error_hometabfour = getActivity().findViewById(R.id.error_hometabfour);
        rv_hometabfour = getActivity().findViewById(R.id.rv_hometabfour);
        swipe_hometabfour = getActivity().findViewById(R.id.swipe_hometabfour);
    }
}
