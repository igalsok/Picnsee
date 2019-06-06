package com.igaltech.Picnsee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegActivity extends AppCompatActivity implements AsyncResponse  {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private EditText plnTxt_username;
    private ImageView imgView_selfie;
    private Button btn_proceed;
    private ImageButton btn_camera;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private Bitmap bmp;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private AddFace addFace;
    private String mCurrentPhotoPath;

    // Replace `<API endpoint>` with the Azure region associated with
// your subscription key. For example,
// apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    private final String apiEndpoint = "https://westeurope.api.cognitive.microsoft.com/face/v1.0";

    // Replace `<Subscription Key>` with your subscription key.
// For example, subscriptionKey = "0123456789abcdef0123456789ABCDEF"
    private final String subscriptionKey = "83a8d7db1c594534bef3dcec5eb14677";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_reg);
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://spotfacetest.appspot.com");
        db = FirebaseFirestore.getInstance();
        plnTxt_username = findViewById(R.id.plnTxt_username);
        imgView_selfie = findViewById(R.id.img_selfie);
        btn_proceed = findViewById(R.id.btn_proceed);
        btn_camera = findViewById(R.id.btn_selfie);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    addPersonToGroup();
                    uploadPhoto(bmp);
                }
               catch (Exception e){
                    e.printStackTrace();
               }


            }
        });
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.spotfacetest.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
     private void uploadPhoto(Bitmap bmp){
         if (bmp != null) {
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             byte[] data1 = baos.toByteArray();
             StorageReference imagesRef = mStorageRef.child("Faces/" + fUser.getPhoneNumber() + ".jpg");
             UploadTask uploadTask = imagesRef.putBytes(data1);
             uploadTask.addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception exception) {
                     // Handle unsuccessful uploads
                 }
             }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                     // Do what you want
                 }
             });
         }
     }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmapTmp = MediaStore.Images.Media
                            .getBitmap(RegActivity.this.getContentResolver(), Uri.fromFile(file));
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap rotatedBitmap;
                    switch(orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmapTmp, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmapTmp, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmapTmp, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmapTmp;
                }
                bmp = rotatedBitmap;
                this.imgView_selfie.setImageBitmap(rotatedBitmap);

            } catch (Exception error) {
                error.printStackTrace();
            }

        }

    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

   private void addPersonToGroup() {

    addFace = new AddFace(fUser,faceServiceClient,bmp,plnTxt_username.getText().toString());
    addFace.delegate = this;
    addFace.execute("");
   }


    @Override
    public void proccesAddFinish(String string) {
        Intent newIntent = new Intent(RegActivity.this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }
}
