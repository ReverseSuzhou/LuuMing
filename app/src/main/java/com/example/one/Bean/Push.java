package com.example.one.Bean;

public class Push{
    private String Forumt_id;
    private String Forumt_date;
    private String User_phone;
    private String Forumt_title;
    private String Forumt_content;
    private int F_likenum;
    private int F_collectnum;
    private int F_commentnum;
    private String F_label;

    private String username;

    public String getUser_phone() {
        return User_phone;
    }

    public void setUser_phone(String user_phone) {
        User_phone = user_phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getForumt_title() {
        return Forumt_title;
    }

    public void setForumt_title(String forumt_title) {
        Forumt_title = forumt_title;
    }

    public String getForumt_id() {
        return Forumt_id;
    }

    public void setForumt_id(String forumt_id) {
        Forumt_id = forumt_id;
    }

    public String getForumt_date() {
        return Forumt_date;
    }

    public void setForumt_date(String forumt_date) {
        Forumt_date = forumt_date;
    }



    public String getForumt_content() {
        return Forumt_content;
    }

    public void setForumt_content(String forumt_content) {
        Forumt_content = forumt_content;
    }

    public int getF_likenum() {
        return F_likenum;
    }

    public void setF_likenum(int f_likenum) {
        F_likenum = f_likenum;
    }

    public int getF_collectnum() {
        return F_collectnum;
    }

    public void setF_collectnum(int f_collectnum) {
        F_collectnum = f_collectnum;
    }

    public int getF_commentnum() {
        return F_commentnum;
    }

    public void setF_commentnum(int f_commentnum) {
        F_commentnum = f_commentnum;
    }


    public String getF_label() {
        return F_label;
    }

    public void setF_label(String f_label) {
        F_label = f_label;
    }
}
