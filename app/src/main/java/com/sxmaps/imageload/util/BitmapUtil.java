package com.sxmaps.imageload.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * bitmap处理工具类
 * Created by Administrator on 2017/12/6.
 */

public class BitmapUtil {

    /**
     * 改变bitmap的大小
     * @param bitmap
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    public static Bitmap ScaleBitmap(Bitmap bitmap,int requestWidth,int requestHeight){
        int width= bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (float) requestWidth/(float) width;
        float scaleHeight = (float) requestHeight/(float) height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return bitmap;
    }

    /**
     * 通过本地图片bitmap进行缩略处理
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    public static Bitmap optionsBitmapResource(Context context,int id, int requestWidth, int requestHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options,requestWidth,requestHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),id,options);
        return bitmap;
    }

    /**
     * 直接对bitmap进行缩放处理
     * @param bitmap
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    public static Bitmap optionsBitmap(Bitmap bitmap, int requestWidth, int requestHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options,requestWidth,requestHeight);
        options.inJustDecodeBounds = false;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] bytes = stream.toByteArray();
        Bitmap newbitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        return newbitmap;
    }

    /**
     * 通过流获取bitmap进行缩略处理
     * @param is
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    public static Bitmap optionsBitmapIS(InputStream is, int requestWidth, int requestHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options,requestWidth,requestHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    /**
     * 处理缩略的大小
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        int width = options.outWidth;
        int height = options.outHeight;
        int simpleSize = 1;
        if (height>reqHeight||width>reqWidth){
            int heightRoate = Math.round(height/reqHeight);
            int widthRoate = Math.round(width/reqWidth);
            simpleSize = heightRoate>widthRoate?heightRoate:widthRoate;
        }
        return simpleSize;
    }
}
