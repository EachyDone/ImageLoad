package com.sxmaps.imageload;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sxmaps.imageload.LoadImage.LoadImageActivity;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one);
       findViewById(R.id.goto_load_image).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               LoadImageActivity.startActivity(MainActivity.this);
           }
       });
    }


}
