package com.example.spotfacetest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Gallery extends AsyncTask<String,Void,Query> {

    private FirebaseFirestore db;
    private String phoneNumber;
    private Query query;
    public QueryResponse delegate = null;

    public Gallery(String phoneNumber){
        db = FirebaseFirestore.getInstance();
        this.phoneNumber = phoneNumber;

    }
    private void setQuery(Query query){
        this.query=query;
    }

    @Override
    protected Query doInBackground(String... strings) {
        try {
            db.collection("Users").whereEqualTo("phoneNumber", phoneNumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    User user = null;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        user = doc.toObject(User.class);
                    }
                    try {
                        if(user!=null) {
                            Query query = db.collection("UserPhotos").whereEqualTo("faceId", user.faceId).orderBy("time", Query.Direction.DESCENDING);
                            setQuery(query);
                        }


                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }
            });
        } catch (Exception exe) {
            exe.printStackTrace();
        }
        while(query==null){
            try{
                Thread.sleep(30);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return query;
    }
    @Override
    protected void onPostExecute(Query result) {
        delegate.processFinish(result);

    }
}
