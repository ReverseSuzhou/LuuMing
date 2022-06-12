package com.example.one.util;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.one.temp.SaveSharedPreference;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class StorePicturesUtil {

    //要连接的数据库url,注意：此处连接的应该是服务器上的MySQl的地址
    private final static String url = "jdbc:mysql://180.76.227.152:3306/test";
    //连接数据库使用的用户名
    private final static String userName = "zzuwenqu";
    //连接的数据库时使用的密码
    private final static String password = "zzu123456!";
    Connection connection=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    Thread t;

    public String bitmapToString(Bitmap bitmap){ //数据库中图片以string类型存储
        //用户在活动中上传的图片转换成String进行存储
        if(bitmap!=null){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bytes = stream.toByteArray();// 转为byte数组
//            String string=Base64.encodeToString(bytes,Base64.DEFAULT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String string = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            int n = string.length();
            System.out.println("string.length = " + n);
            return string;
        }
        else{
            return "";
        }
    }

    public Bitmap stringToBitmap(String string){ //本地显示用bitmap
        //数据库中的String类型转换成Bitmap
        if(string!=null){
            byte[] bytes= Base64.decode(string,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            System.out.println("我在这里！！！");
            return bitmap;
        }
        else {
            System.out.println("压根什么都没有");
            return null;
        }
    }

//    public int getNum(){ //帖子中的
//        int ans = 0;
//
//        try {
//            //1、加载驱动
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            System.out.println("驱动加载成功！！！（图片2）");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        try {
//            //2、获取与数据库的连接
//            connection = DriverManager.getConnection(url, userName, password);
//            System.out.println("连接数据库成功！！（图片2）");
//            //3.sql语句
//            //4.获取用于向数据库发送sql语句的ps
//            ps = connection.prepareStatement("SELECT MAX(Pic_id) FROM Pic");
//            rs = ps.executeQuery();
//            int id = 0;
//            while(rs.next()) {
//                id = rs.getInt(1)+1;
//            }
//            ans = id;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("插入图片失败");
//        }
//        finally {
//            if(connection!=null){
//                try {
//                    connection.close();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return ans;
//    }

    public void storeHeadImg(Bitmap bitmap){
        String picture = bitmapToString(bitmap);
        try {
            //1、加载驱动
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("驱动加载成功！！！（图片2）");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            //2、获取与数据库的连接
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println("连接数据库成功！！（图片2）");
            //3.sql语句
            //4.获取用于向数据库发送sql语句的ps
            SaveSharedPreference saveSharedPreference = new SaveSharedPreference();
            int userId = saveSharedPreference.getUserId(); //获取当前用户id
            String sql = "update User set img='" + picture + "' where User_id=" + userId + ";";
            ps = connection.prepareStatement(sql);
            ps.executeUpdate();
//            String findBefore = "select img from User where User_id=" + userId + ";";
//            Boolean hasChange = false;
//            int pic_id = 0;
//
//            ps = connection.prepareStatement(findBefore);
//            rs = ps.executeQuery();
//            while(rs.next()) {
//                pic_id = rs.getInt(1);
//            }
//            if (pic_id != 0)
//                hasChange = true;
//
//
//            if (hasChange){ //如果之前有头像，就删除
//                System.out.println("重复的id = " + pic_id);
//                DBUtils dbUtils = new DBUtils();
//                String sql = "delete from Pic where Pic_id=" + pic_id + ";"; //删除
//                System.out.println("sql = " + sql);
//                ps = connection.prepareStatement(sql);
//                ps.executeUpdate();
//            }
//
//            ps = connection.prepareStatement("SELECT MAX(Pic_id) FROM Pic");
//            rs = ps.executeQuery();
//            int id = 0;
//            while(rs.next()) {
//                id = rs.getInt(1)+1;
//            }
//            System.out.println("id = " + id);
//            ps = connection.prepareStatement("insert "
//                    + "into Pic values (?,?,?,?,?,?,?)"); //三个问号对应下面的三个
//            ps.setInt(1, id);  //主键，也就是编号
//            ps.setString(2, "头像"); //图片描述
//            ps.setString(3, picture); //图片
//            ps.setInt(4, saveSharedPreference.getUserId()); //用户id
//            ps.setInt(5, 0); //帖子id
//            ps.setInt(6, 1); //是头像
//            ps.setInt(7, id);  //主键，也就是编号
//            //ps.setString(3, imageString);
//            //ps.setBinaryStream(3, fis, (int) file.length());  //文件本身
//            ps.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("插入图片失败");
        }
        finally {
            if(connection!=null){
                try {
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    public ContentResolver getContentResolver() {
        throw new RuntimeException("Stub!");
    }
}

