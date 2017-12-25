package com.sxmaps.imageload.ImageLoad;

import android.graphics.Bitmap;

/**
 * 缓存的一些流程操作
 * Created by Administrator on 2017/12/6.
 */

public interface LoadImageProcess {
    // 添加图片
    void putBitmap( String type,String url,  Bitmap bitmap);
    // 获取图片
    Bitmap getBitmap(String url,String type);
    // 删除图片
    boolean deleteBitmap(String url);
}
