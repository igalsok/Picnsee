package com.example.spotfacetest;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(ArrayList<Photo> output);
    void proccesAddFinish(String string);
}
