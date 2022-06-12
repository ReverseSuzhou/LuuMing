package com.example.one.temp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * 裁剪图片
 */
public class CropImage extends ActivityResultContract<CropImageResult, Uri> {

    //裁剪后输出的图片文件Uri
    private Uri mUriOutput;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, CropImageResult input) {

        //把CropImageResult转换成裁剪图片的意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        String mimeType = context.getContentResolver().getType(input.getUri());
        String imageName = System.currentTimeMillis() +
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) + "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            mUriOutput = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            mUriOutput = Uri.fromFile(new File(context.getExternalCacheDir().getAbsolutePath(), imageName));
        }
        context.grantUriPermission(context.getPackageName(), mUriOutput, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //去除默认的人脸识别，否则和剪裁匡重叠
        intent.putExtra("noFaceDetection", true);
        intent.setDataAndType(input.getUri(), mimeType);
        //crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("crop", "true");
        intent.putExtra("output", mUriOutput);
        //返回格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", false);

        //配置裁剪图片的宽高比例
        if (input.getAspectX() != 0 && input.getAspectY() != 0) {
            intent.putExtra("aspectX", input.getAspectX());
            intent.putExtra("aspectY", input.getAspectY());
        }
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        return mUriOutput;
    }
}