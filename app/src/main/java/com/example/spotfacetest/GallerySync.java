package com.example.spotfacetest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GallerySync extends AsyncTask<String,Void,String> {

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    public ProgressBar progressBar;
    private GalleryOld gallery;

    public GallerySync(FirebaseUser user,ProgressBar progressBar , GalleryOld gallery){

        this.currentUser = user;
        this.progressBar = progressBar;
        db = FirebaseFirestore.getInstance();
        this.gallery = gallery;
    }

    public void initPhotos(){
        final ArrayList<String> imageList = new ArrayList<String>();
        try {
            db.collection("Users").whereEqualTo("phoneNumber", currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    User user = null;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        user = doc.toObject(User.class);
                    }
                    try {
                        db.collection("UserPhotos").whereEqualTo("faceId", user.faceId).orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        UserPhoto photo = document.toObject(UserPhoto.class);
                                        imageList.add(photo.thumbnail_location);
                                    }
                                    if(imageList.size() == 0){
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                    DownloadThumbnailList downloadPhotoList = new DownloadThumbnailList(imageList,gallery);
                                    downloadPhotoList.execute("init");

                                } else {

                                }
                            }
                        });
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }
            });
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }
    public void updatePhotos() {
        final ArrayList<String> imageList = new ArrayList<String>();
        try {
            db.collection("Users").whereEqualTo("phoneNumber", currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    User user = null;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        user = doc.toObject(User.class);
                    }
                    try {
                        db.collection("UserPhotos").whereEqualTo("faceId", user.faceId).orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        UserPhoto photo = document.toObject(UserPhoto.class);
                                        imageList.add(photo.thumbnail_location);
                                    }
                                    DownloadThumbnailList downloadPhotoList = new DownloadThumbnailList(imageList,gallery);
                                    downloadPhotoList.execute("update");

                                } else {

                                }
                            }
                        });
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }
            });
        } catch (Exception exe) {
            exe.printStackTrace();
        }


    }

    @Override
    protected String doInBackground(String... strings) {
        if(strings[0].equals("init")){
            initPhotos();
        }
        else if(strings[0].equals("update")) {
            updatePhotos();
        }
        return strings[0];
    }
}
