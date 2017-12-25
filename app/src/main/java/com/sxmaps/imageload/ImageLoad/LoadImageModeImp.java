package com.sxmaps.imageload.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/12/3.
 */

public class LoadImageModeImp implements LoadImageMode{

    private Context context;
    private static LoadImageModeImp imageModeImp;
    private MyDiskLruCache diskLruCache;

    public static LoadImageModeImp getImageModeImp(Context context,MyDiskLruCache diskLruCache){
        if (imageModeImp==null){
            imageModeImp = new LoadImageModeImp(context,diskLruCache);
        }
        return imageModeImp;
    }



    public LoadImageModeImp(Context context,MyDiskLruCache diskLruCache) {
        this.context = context.getApplicationContext();
        this.diskLruCache =diskLruCache;
    }

    @Override
    public Bitmap getBitmapFromNative() {
        return null;
    }

    @Override
    public Bitmap downloadBitmapFromUrl(String url,String type) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url1 = new URL(url);
            urlConnection = (HttpURLConnection) url1.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
//            bitmap = BitmapUtil.ScaleBitmap(in,200,300);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG,"error in downloadBitmap");
            e.printStackTrace();
        }finally {
            if(urlConnection != null&&in!=null){
                urlConnection.disconnect();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (url!=null&&bitmap!=null){
            MyImageLoad.getMyImageLoad(context).putBitmap(type,url,bitmap);
            diskLruCache.putBitmap(type,url,bitmap);
        }
        return bitmap;
    }
}
