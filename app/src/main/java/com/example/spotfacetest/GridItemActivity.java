package com.example.spotfacetest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class GridItemActivity extends AppCompatActivity {

     ImageView img ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_grid_item);
        Intent intent = getIntent();
        img = (ImageView)findViewById(R.id.img_image1);
        String url = (String)intent.getStringExtra("bmpUrl");
        DownloadPhoto dp = new DownloadPhoto(url,img);
        dp.execute();
    }
}
