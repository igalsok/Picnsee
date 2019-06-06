package com.igaltech.Picnsee;

import com.google.firebase.firestore.Query;

public interface QueryResponse {
    void processFinish(Query output);
}
