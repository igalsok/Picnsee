package com.example.spotfacetest;

import android.view.View;

import java.util.ArrayList;

public class PhotoList {
    private ArrayList<Photo> photos;
    private GalleryOld gallery;

    public PhotoList(GalleryOld gallery) {
        this.photos = new ArrayList<Photo>();
        this.gallery = gallery;
    }

    public void add(Photo photo) {
        this.photos.add(photo);
        gallery.progressBar.setVisibility(View.INVISIBLE);
        gallery.setAdapter(photos);

    }
    public void setGalleryPhotoList(){
        gallery.setPhotoList(this);
    }

    public void add(int index, Photo photo) {
        this.photos.add(index, photo);
    }

    public Photo get(int index) {
        return photos.get(index);
    }

    public int size() {
        return photos.size();
    }

    public void setAdapter() {
        gallery.setAdapter(photos);
    }

}
