package com.igaltech.Picnsee;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddFace extends AsyncTask<String,Void,String> {
    private FaceServiceClient faceServiceClient;
    private FirebaseUser fUser;
    private Bitmap bmp;
    private String userName;
    private String faceId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    public AsyncResponse delegate = null;
    private String group;

    public AddFace(FirebaseUser fUser, FaceServiceClient faceServiceClient, Bitmap bmp, String userName ){
        this.bmp = bmp;
        this.faceServiceClient = faceServiceClient;
        this.fUser = fUser;
        this.userName = userName;
        this.faceId = "";
        db = FirebaseFirestore.getInstance();
        this.group = "users_new";

}

    @Override
    protected String doInBackground(String... strings)  {

        try {
            CreatePersonResult result = faceServiceClient.createPersonInLargePersonGroup(group, fUser.getPhoneNumber().replace("+",""), userName);
            FaceRectangle face = new FaceRectangle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());
            faceServiceClient.addPersonFaceInLargePersonGroup(group, result.personId, imageInputStream, null, null);
            faceServiceClient.trainLargePersonGroup(group);
            while(result.personId == null){
                try {
                    Thread.sleep(50);
                }
                catch (Exception exe)
                {
                    exe.printStackTrace();
                }
            }
            faceId = result.personId.toString();
            User user = new User(fUser.getPhoneNumber(), userName,faceId );
            CollectionReference dbUsers = db.collection("Users");
            dbUsers.add(user);

            return result.personId.toString();
        }
        catch (ClientException exe)
        {
            exe.printStackTrace();
        }
        catch (IOException exe){
            exe.printStackTrace();
        }
        return null;

    }


    @Override
    protected void onPostExecute(String result) {
        delegate.proccesAddFinish(result);

    }
    public String getFaceId(){
        return faceId;
    }
}

