package com.sxmaps.imageload.ImageLoad;

import android.graphics.Bitmap;

/**
 * 获取图片的方式
 * Created by Administrator on 2017/12/3.
 */

public interface LoadImageMode {

    // 从网络获取图片
    Bitmap downloadBitmapFromUrl(String url,String type);

    // 从本地获取图片
    Bitmap getBitmapFromNative();
}
