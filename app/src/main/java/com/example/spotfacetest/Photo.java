package com.example.spotfacetest;

import android.graphics.Bitmap;

public class Photo {
    public String url;
    public Bitmap photo;


    public Photo(String url, Bitmap photo){
        this.url = url;
        this.photo = photo;
    }
}
