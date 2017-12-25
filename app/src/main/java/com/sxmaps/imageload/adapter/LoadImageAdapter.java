package com.sxmaps.imageload.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sxmaps.imageload.ImageLoad.MyImageLoad;
import com.sxmaps.imageload.R;

/**
 * 加载图片的adapter
 * Created by Administrator on 2017/12/3.
 */

public class LoadImageAdapter extends BaseAdapter{

    private Context context;
    private String[] list;

    public LoadImageAdapter(Context context, String[] list) {
        MyImageLoad.getMyImageLoad(context);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String url = list[position];
        MyHolder holder;
        if (convertView==null){
            holder = new MyHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.image_load_item,null);
            holder.im_item = (ImageView) convertView.findViewById(R.id.im_item);
            MyImageLoad.getMyImageLoad(context).loadImage(MyImageLoad.NODOUBLE,url,holder.im_item);
            convertView.setTag(holder);
        }else {
            holder = (MyHolder) convertView.getTag();
        }
        return convertView;
    }

    public class MyHolder{
        ImageView im_item;
    }
}
