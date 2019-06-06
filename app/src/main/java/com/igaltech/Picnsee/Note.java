package com.igaltech.Picnsee;


import com.google.firebase.Timestamp;

public class Note {

    private String thumbnailUrl, faceId, photoUrl,photo_location,thumbnail_location;
    private Timestamp time;

    public Note(){

    }
    public Note(String url){
        this.thumbnailUrl = url;
    }

    public String getUrl() {
        return thumbnailUrl;
    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoto_location() {
        return photo_location;
    }

    public void setPhoto_location(String photo_Location) {
        this.photo_location = photo_Location;
    }

    public String getThumbnail_location() {
        return thumbnail_location;
    }

    public void setThumbnail_location(String thumbnail_location) {
        this.thumbnail_location = thumbnail_location;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
