package com.example.spotfacetest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class GalleryOld implements AsyncResponse {

    private GridView grid;
    private Context context;
    private FirebaseUser currentUser;
    private ArrayList<Photo> images;
    public ProgressBar progressBar;
    public SwipeRefreshLayout refresh;
    private PhotoList photoList;



    public GalleryOld(GridView grid1, Context context1, FirebaseUser user, ProgressBar progressBar) {
        this.grid = grid1;
        this.context = context1;
        this.currentUser = user;
        this.progressBar = progressBar;
        images = new ArrayList<Photo>();
    }
    public void execute(String action){
        GallerySync sync = new GallerySync(currentUser,progressBar,this);
        sync.execute(action);
    }
    public void setAdapter(ArrayList<Photo> images1){
            setImageArr(images1);
            GalleryAdapter adapter = new GalleryAdapter(images, GalleryOld.this,context);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context.getApplicationContext(), GridItemActivity.class);
                    intent.putExtra("bmpUrl", images.get(i).url.replace("Thumbnails","Photos"));
                    context.startActivity(intent);
                }


            });
            if (refresh != null) {
                refresh.setRefreshing(false);

            }
        }




    private void setImageArr(ArrayList<Photo> images) {
        this.images = images;
    }
    public void setPhotoList(PhotoList photoList){
        this.photoList = photoList;
    }
    public PhotoList getPhotoList(){
        return this.photoList;
    }


    @Override
    public void processFinish(ArrayList<Photo> output) {


    }

    @Override
    public void proccesAddFinish(String string) {

    }


}
