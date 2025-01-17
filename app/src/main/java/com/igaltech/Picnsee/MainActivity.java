package com.igaltech.Picnsee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements QueryResponse,FinishResponse {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private Button btn_logout;
    private ImageButton btn_cam;
    private FirebaseAuth mAuth;
    private GridView grid;
    private Uri[] images;
    private SwipeRefreshLayout refresh;
    private String mCurrentPhotoPath;
    public Boolean isRegistered;
    private RecyclerView recyclerView;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_main);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent newIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(newIntent);
                finish();
            }
        });

        btn_cam = findViewById(R.id.btn_cam);
        btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        if (currentUser != null) {

        } else {
            Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        this.recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SampleRecycler());
        if(mAuth.getCurrentUser() != null) {
            Gallery gal = new Gallery(mAuth.getCurrentUser().getPhoneNumber());
            gal.delegate = this;
            gal.execute();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =  new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(new Date());
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
        try {
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
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo is uploading", Toast.LENGTH_SHORT).show();
            try {
                MainActivity.myThread thread = new MainActivity.myThread(mAuth.getCurrentUser().getPhoneNumber());
                thread.start();
            } catch (Exception exe) {
                exe.printStackTrace();
            }

        }
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void processFinish(Query output) {
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(output, Note.class)
                .build();
        NoteAdapter adp = new NoteAdapter(options, this);
        RecyclerView rView = recyclerView;
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new GridLayoutManager(this,3));
        rView.setAdapter(adp);
        adp.startListening();
    }

    @Override
    public void processFinish(boolean output) {
        Toast.makeText(this, "Photo uploaded Successfully", Toast.LENGTH_SHORT).show();
    }


    public class myThread extends Thread {
        private String phoneNumber;

        public myThread(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        public void run() {
            try {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmapTmp = MediaStore.Images.Media
                        .getBitmap(MainActivity.this.getContentResolver(), Uri.fromFile(file));
                ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap;
                switch (orientation) {
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
                Bitmap thumbnail = rotatedBitmap.copy(rotatedBitmap.getConfig(), true);

                FaceRecognition reco = new FaceRecognition(phoneNumber, rotatedBitmap, ThumbnailUtils.extractThumbnail(thumbnail, 300, 300));
                reco.delegate = MainActivity.this;
                reco.execute();
            } catch (Exception error) {
                error.printStackTrace();
            }


        }
    }

}





