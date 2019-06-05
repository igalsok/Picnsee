package com.example.spotfacetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class DownloadThumbnailList extends AsyncTask<String, Void, String> {
    private FirebaseStorage storage;
    private ArrayList<String> photos;
    private boolean isFinished = false;
    private GalleryOld gallery;
    private PhotoList images;
    private boolean isUpdated = false;


    public DownloadThumbnailList(ArrayList<String> strings, GalleryOld gallery) {
        storage = FirebaseStorage.getInstance();
        this.photos = strings;
        this.gallery = gallery;
        this.images = new PhotoList(gallery);
    }


    @Override
    protected String doInBackground(String... strings) {
        if (strings[0].equals("init")) {
                initDownload();
        }
        if (strings[0].equals("update")) {
            updateDownload();
        }
        return strings[0];
    }

    private boolean isFinished() {
        return this.isFinished;
    }

    private void initDownload() {
        try {
            for (final String urlString : photos) {
                isFinished = false;
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://spotfacetest.appspot.com").child(urlString);
                final File localFile = File.createTempFile("images", "jpg");
                mStorageRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                String filePath = localFile.getPath();
                                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                images.add(new Photo(urlString, bitmap));
                                isFinished = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });
                while (!isFinished) {
                    try {
                        Thread.sleep(5);
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }

            }
        } catch (Exception exe) {
            exe.printStackTrace();
        }
        while (images.size() != photos.size()) {
            try {
                Thread.sleep(5);
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
        images.setGalleryPhotoList();
        System.out.println(images.size());


    }

    private void updateDownload() {

        if (gallery.getPhotoList() == null) {
            System.out.println("gallery.getPhotoList() == null");
            gallery.refresh.setRefreshing(false);
        } else {
            if (photos.size() == gallery.getPhotoList().size()) {
                System.out.println("photos.size() == gallery.getPhotoList().size()");
                gallery.refresh.setRefreshing(false);
            }
            else {
                try {
                    System.out.println("else");
                    images = gallery.getPhotoList();
                    int oldSize =  gallery.getPhotoList().size();
                    for (int i = 0; i < photos.size() -oldSize; i++) {
                        final int j = i;
                        final String urlString = photos.get(i);
                        isFinished = false;
                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://spotfacetest.appspot.com").child(urlString);
                        final File localFile = File.createTempFile("images", "jpg");
                        mStorageRef.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        String filePath = localFile.getPath();
                                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                        images.add(j, new Photo(urlString, bitmap));
                                        isFinished = true;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                exception.printStackTrace();
                            }
                        });
                        while (!isFinished) {
                            try {
                                Thread.sleep(50);
                            } catch (Exception exe) {
                                exe.printStackTrace();
                            }
                        }

                    }
                } catch (Exception exe) {
                    exe.printStackTrace();
                }
                while (images.size() != photos.size()) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }
                isUpdated = true;


            }

        }
    }

    @Override
    protected void onPostExecute(String result) {

        if (result.equals("update")) {
            if(isUpdated)
            images.setAdapter();
        }
    }

}
