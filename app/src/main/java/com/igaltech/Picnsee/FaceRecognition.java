package com.igaltech.Picnsee;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import java.io.ByteArrayOutputStream;
import java.util.UUID;


public class FaceRecognition extends AsyncTask<String, Void, Void> {
    // Replace `<API endpoint>` with the Azure region associated with
// your subscription key. For example,
// apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    private final String apiEndpoint = "https://westeurope.api.cognitive.microsoft.com/face/v1.0";

    // Replace `<Subscription Key>` with your subscription key.
// For example, subscriptionKey = "0123456789abcdef0123456789ABCDEF"
    private final String subscriptionKey = "83a8d7db1c594534bef3dcec5eb14677";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private Bitmap bmp;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private String phoneNumber;
    private byte[] data1;
    private UUID photoId;
    private String group;
    private Bitmap thumbnail;
    private Uri photoUri = null;
    private Uri thumbNailUri = null;
    public FinishResponse delegate = null;


    public FaceRecognition(String phoneNumber, Bitmap bmp, Bitmap thumbnail) {
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://spotfacetest.appspot.com");
        this.phoneNumber = phoneNumber;
        this.group = "users_new";
        this.bmp = bmp.copy(bmp.getConfig(), true);
        this.thumbnail = thumbnail.copy(thumbnail.getConfig(), true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    @Override
    protected Void doInBackground(String... strings) {
        this.photoId = UUID.randomUUID();
        try {
            final StorageReference imagesRef = mStorageRef.child("Photos").child(phoneNumber).child(photoId + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            data1 = baos.toByteArray();
            imagesRef.putBytes(data1).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    setPhotoUri(uri);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

            final StorageReference imagesRef2 = mStorageRef.child("Thumbnails").child(phoneNumber).child(photoId + ".jpg");
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, baos2);
            byte[] data = baos2.toByteArray();
            imagesRef2.putBytes(data).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagesRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    setThumbnailUri(uri);
                                }
                            });
                        }
                    });
        } catch (Exception exe) {
            exe.printStackTrace();
        }
        while(photoUri== null || thumbNailUri== null ){
            try{
                Thread.sleep(50);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }
    private void setPhotoUri(Uri uri){
        photoUri = uri;
    }
    private void setThumbnailUri(Uri uri){
        thumbNailUri = uri;
    }

    @Override
    protected void onPostExecute(Void vo) {

        FaceReco reco = new FaceReco(phoneNumber, data1, photoId, photoUri,thumbNailUri, delegate);
        reco.execute();

    }

}
