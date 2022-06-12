package com.example.one.temp;

import android.net.Uri;

/**
 * 裁剪图片配置类
 */
public class CropImageResult {

    private Uri uri;
    //裁剪框横向比例数值
    private int aspectX;
    //裁剪框纵向比例数值
    private int aspectY;

    public CropImageResult(Uri uri, int aspectX, int aspectY) {
        this.uri = uri;
        this.aspectX = aspectX;
        this.aspectY = aspectY;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {
        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        this.aspectY = aspectY;
    }
}
