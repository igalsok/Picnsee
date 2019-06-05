package com.example.spotfacetest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckIfRegistered extends AsyncTask<String,Void, Boolean> implements CheckResponse{
    private FirebaseFirestore db;
    public boolean isRegistered = false;
    public CheckResponse delegate = null;
    private MainActivity act;
    private String phone;
    public CheckIfRegistered(String phone , MainActivity act){
        this.phone = phone;
        this.act = act;
        this.db = FirebaseFirestore.getInstance();
    }


    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            db.collection("Users").whereEqualTo("phoneNumber", phone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().getDocuments().isEmpty())
                    {
                        isRegistered = true;
                    }
                }
            });
        }
        catch (Exception exe){

        }
        return false;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        delegate.proccesFinish(result);
    }

    @Override
    public void proccesFinish(Boolean bool) {
        act.isRegistered = bool;

    }
}
