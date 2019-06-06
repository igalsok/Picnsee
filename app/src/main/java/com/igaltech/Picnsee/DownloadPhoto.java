package com.igaltech.Picnsee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class DownloadPhoto  extends AsyncTask<String,Void, ArrayList<Bitmap>> {
    private FirebaseStorage storage;
    private String photoUrl;
    private ImageView view;

    public DownloadPhoto(String photoUrl, ImageView view) {
        this.photoUrl = photoUrl;
        storage = FirebaseStorage.getInstance();
        this.view = view;
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(String... strings) {
        try {
            int i = 0;
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://spotfacetest.appspot.com").child(photoUrl);
                final File localFile = File.createTempFile("images", "jpg");
                mStorageRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                String filePath = localFile.getPath();
                                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                view.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });

        } catch (Exception exe) {
            exe.printStackTrace();
        }
        return null;

    }

}
