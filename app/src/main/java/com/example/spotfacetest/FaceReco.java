package com.example.spotfacetest;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Candidate;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class FaceReco extends Thread {
    // Replace `<API endpoint>` with the Azure region associated with
// your subscription key. For example,
// apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    private final String apiEndpoint = "https://westeurope.api.cognitive.microsoft.com/face/v1.0";

    // Replace `<Subscription Key>` with your subscription key.
// For example, subscriptionKey = "0123456789abcdef0123456789ABCDEF"
    private final String subscriptionKey = "83a8d7db1c594534bef3dcec5eb14677";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private FirebaseFirestore db;
    private UUID photoId;
    private String phoneNumber;
    final private String group = "users_new";
    private byte[] data1;
    private Uri photoUri;
    private Uri thumbnailUri;

    public FaceReco(String phoneNumber, byte[] data1, UUID photoId, Uri photoUri, Uri thumbnailUri){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        db = FirebaseFirestore.getInstance();
        this.phoneNumber = phoneNumber;
        this.data1 = data1;
        this.photoId = photoId;
        this.photoUri = photoUri;
        this.thumbnailUri = thumbnailUri;
    }
    @Override
    public void run(){
        try {
                            try {
                                ByteArrayInputStream bis = new ByteArrayInputStream(data1);
                                Face[] faces = faceServiceClient.detect(
                                        bis,
                                        true,
                                        true,
                                        new FaceServiceClient.FaceAttributeType[]{
                                                FaceServiceClient.FaceAttributeType.Age,
                                                FaceServiceClient.FaceAttributeType.Gender,
                                                FaceServiceClient.FaceAttributeType.Smile,
                                                FaceServiceClient.FaceAttributeType.FacialHair,
                                                FaceServiceClient.FaceAttributeType.Emotion,
                                                FaceServiceClient.FaceAttributeType.HeadPose,
                                                FaceServiceClient.FaceAttributeType.Accessories,
                                                FaceServiceClient.FaceAttributeType.Hair
                                        });
                                UUID[] faceIds = new UUID[faces.length];
                                for (int i = 0; i < faceIds.length; i++) {
                                    faceIds[i] = faces[i].faceId;
                                }
                                IdentifyResult[] results = faceServiceClient.identityInLargePersonGroup(group, faceIds, 10);
                                for (IdentifyResult result : results) {
                                    Candidate result2 = result.candidates.get(0);
                                    UserPhoto userPhoto = new UserPhoto(result2.personId.toString(), "Photos/" + phoneNumber + "/" + photoId + ".jpg", "Thumbnails/" + phoneNumber + "/" + photoId + ".jpg",photoUri.toString(),thumbnailUri.toString());
                                    CollectionReference dbUsers = db.collection("UserPhotos");
                                    dbUsers.add(userPhoto);
                                }
                            } catch (Exception exe) {
                                exe.printStackTrace();
                            }

        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

}