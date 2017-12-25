package com.sxmaps.imageload.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.sxmaps.imageload.R;
import com.sxmaps.imageload.util.BitmapUtil;
import com.sxmaps.imageload.util.MD5Util;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * imageload实现类
 * Created by Administrator on 2017/12/3.
 */

public class MyImageLoad implements LoadImageProcess{

    // IO缓冲区的大小
    private static final int IO_BUFFER_SIZE = 1024*8;
    // 内存缓存
    public  LruCache<String,Bitmap> memoryCatche;
    // 硬盘缓存
    public  MyDiskLruCache mDiskLruCache;

    public  Context context;

    private static MyImageLoad myImageLoad;

    public static final String ALLCACHE="0";
    public static final String LRUCACHE="1";
    public static final String DISKCACHE="2";
    public static final String NODOUBLE="3";

    public static MyImageLoad getMyImageLoad(Context context){
        if (myImageLoad==null){
            myImageLoad = new MyImageLoad(context);
        }
        return myImageLoad;
    }

    public MyImageLoad(Context mcontext) {
        context = mcontext.getApplicationContext();
        // 获取系统可以使用的内存空间大小，单位为KB
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        // 设定内存使用大小为总内存大小的1/8
        int catchSize = maxMemory/8;

        memoryCatche = new LruCache<String, Bitmap>(catchSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量
                return bitmap.getByteCount()/1024;
            }
        };
        mDiskLruCache = new MyDiskLruCache(context);
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT+1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT*2+1;
    private static final long KEEP_ALIVE = 10L;
    private static final int IMTAGKEY = R.mipmap.ic_launcher;

    // 定义一个线程池，用于加载图片
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());


    /**
     * 加载图片
     * @param type 0:都要缓存，1：内存缓存，2：硬盘缓存，3：都不要缓存
     * @param url
     * @param im
     */
    public  void loadImage(final String type, final String url, final ImageView im){
        if (url==null){
            im.setImageResource(R.mipmap.ic_launcher);
        }
        im.setTag(IMTAGKEY,url);
        // 被执行的线程
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmap(url,type);
                LoadImageView loadImageView = new LoadImageView(url,im,bitmap);
                setImageHandler.obtainMessage(1,loadImageView).sendToTarget();
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadRunnable);
    }

    public void loadImageSize(final String type, final String url, final ImageView im, final int requestWidth, final int requestHeight){
        if (url==null){
            im.setImageResource(R.mipmap.ic_launcher);
        }
        im.setTag(IMTAGKEY,url);
        // 被执行的线程
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmap(url,type);
                bitmap = BitmapUtil.ScaleBitmap(bitmap,requestWidth,requestHeight);
                LoadImageView loadImageView = new LoadImageView(url,im,bitmap);
                setImageHandler.obtainMessage(1,loadImageView).sendToTarget();
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadRunnable);
    }

    /**
     * 异步处理UI更新
     */
   static Handler setImageHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            LoadImageView loadImageView = (LoadImageView) msg.obj;
            String url = (String) loadImageView.imageView.getTag(IMTAGKEY);
            if (loadImageView.url.equals(url)){
                // 如果是同一个ImageView
                loadImageView.imageView.setImageBitmap(loadImageView.bitmap);
            }
        }
    };

    @Override
    public void putBitmap(String type, String url, Bitmap bitmap) {
        // 添加图片到内存中
        String key  = MD5Util.hashKeyForCache(url);
        switch (type){
            case NODOUBLE:
                break;
            case LRUCACHE:
                memoryCatche.put(key,bitmap);
                break;
            default:
                break;
        }
    }

    @Override
    public Bitmap getBitmap(String url,String type) {
        Bitmap bitmap = null;
        String key = MD5Util.hashKeyForCache(url);
        bitmap = memoryCatche.get(key);

        if (bitmap!=null){
            return bitmap;
        }else {
            // 如果从内存缓存中没有获取到bitmap，就从硬盘缓存中获取
            bitmap  = mDiskLruCache.getBitmap(url,type);
            if (bitmap!=null){
                // 如果从硬盘缓存中获取到了bitmap，就把bitmap存入内存缓存中
                memoryCatche.put(key,bitmap);
                return bitmap;
            }else {
                // 硬盘缓存中也没有，就从网络中下载获取
                bitmap = LoadImageModeImp.getImageModeImp(context,mDiskLruCache).downloadBitmapFromUrl(url,type);
                return bitmap;
            }
        }
    }

    @Override
    public boolean deleteBitmap(String url) {
        String key = MD5Util.hashKeyForCache(url);
        memoryCatche.remove(key);
        return true;
    }

    /**
     * 用于handler传参用的
     */
    public static class LoadImageView{
        String url;
        Bitmap bitmap;
        ImageView imageView;

        public LoadImageView(String url, ImageView imageView,Bitmap bitmap) {
            this.url = url;
            this.bitmap = bitmap;
            this.imageView = imageView;
        }
    }

}
