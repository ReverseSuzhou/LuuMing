package com.example.one.Bean;

public class Comment {
    String Comment_id;
    String User_phone;
    String Forumt_id;
    String Comment_text;
    String Comment_time;
    String Img;

    public String getComment_id() {
        return Comment_id;
    }

    public void setComment_id(String comment_id) {
        Comment_id = comment_id;
    }

    public String getUser_phone() {
        return User_phone;
    }

    public void setUser_phone(String user_phone) {
        User_phone = user_phone;
    }

    public String getForumt_id() {
        return Forumt_id;
    }

    public void setForumt_id(String forumt_id) {
        Forumt_id = forumt_id;
    }

    public String getComment_text() {
        return Comment_text;
    }

    public void setComment_text(String comment_text) {
        Comment_text = comment_text;
    }

    public String getComment_time() {
        return Comment_time;
    }

    public void setComment_time(String comment_time) {
        Comment_time = comment_time;
    }

    public String getImg(String img) {
        return img;
    }

    public void setImg(String img) {
        Img = img;
    }
}
