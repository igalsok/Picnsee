package com.example.spotfacetest;

import com.google.firebase.firestore.Query;

public interface QueryResponse {
    void processFinish(Query output);
}
