package com.sxmaps.imageload.ImageLoad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.sxmaps.imageload.util.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 硬盘缓存的实现
 * Created by Administrator on 2017/12/3.
 */

public class MyDiskLruCache implements LoadImageProcess{

    private DiskLruCache diskLruCache;

    // 定义硬盘缓存大小为100MB
    private int diskCache = 1024*1024*100;

    private Context context;

    public MyDiskLruCache(Context context) {
        this.context = context;
        try {
        // 定义一个图片缓存的文件
        File diskFile = getDiskCacheDir(context,"JHImage");
        if (!diskFile.exists()){
            // 如果文件夹不存在,就创建一个文件夹
            diskFile.mkdirs();
        }
            diskLruCache = DiskLruCache.open(diskFile,getAppVersion(context),1,diskCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取缓存地址
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    //获取应用的版本号
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 添加图片到缓存,写入图片时需要获取DiskLruCache的Editor
     * 使用Editor获取OutputStream进行写入操作
     * @param url
     * @param bitmap 传入的bitmap
     */
    @Override
    public void putBitmap(String type, String url, Bitmap bitmap) {
        if (url==null||bitmap==null||
                MyImageLoad.NODOUBLE.equals(type)||MyImageLoad.LRUCACHE.equals(type)){
            return;
        }
        if (diskLruCache!=null){
            String key = MD5Util.hashKeyForCache(url);
            OutputStream ops = null;
            try {
                DiskLruCache.Snapshot snapshot  = diskLruCache.get(key);
                if (snapshot==null){
                    DiskLruCache.Editor editor = diskLruCache.edit(key);
                    if (editor!=null){
                        ops = editor.newOutputStream(0);
                        // bitmap的格式
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,ops);
                        editor.commit();
                        diskLruCache.flush();
                    }
                }else {
                    snapshot.getInputStream(0).close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (ops!=null){
                    try {
                        ops.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取缓存图片，获取图片时需要获取DiskLruCache.Snapshot
     * 根据Snapshot获取输出流InputStream进行读取操作
     * @param url
     * @return
     */
    @Override
    public Bitmap getBitmap(String url,String type) {
        if (url==null){
            return null;
        }
        InputStream is = null;
        String key = MD5Util.hashKeyForCache(url);
        try {
            if (diskLruCache!=null){
                DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                if (snapshot!=null){
                    is = snapshot.getInputStream(0);
                    if (is!=null){
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        if (bitmap!=null){
                            return bitmap;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteBitmap(String url) {
        if (url==null){
            return false;
        }
        String key = MD5Util.hashKeyForCache(url);
        if (diskLruCache!=null){
            try {
                boolean isRemove = diskLruCache.remove(key);
                return isRemove;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
