package com.igaltech.Picnsee;

import com.google.firebase.Timestamp;

public class UserPhoto {
    public String faceId;
    public String photo_location;
    public String thumbnail_location;
    public Timestamp time ;
    public String photoUrl;
    public String thumbnailUrl;
    public UserPhoto(String faceId, String photo_location,String thumbnail_location, String photoUrl, String thumbnailUrl)
    {
        this.faceId = faceId;
        this.photo_location = photo_location;
        time = Timestamp.now();
        this.thumbnail_location = thumbnail_location;
        this.photoUrl = photoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
    public UserPhoto() {

    }
}
