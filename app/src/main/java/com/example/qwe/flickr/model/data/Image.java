package com.example.qwe.flickr.model.data;

import io.realm.RealmObject;

public class Image extends RealmObject{
    long id;
    int farm;
    String server;
    String secret;
    String ownername;
    double latitude;
    double longitude;
    boolean imageLoaded;
    String file;

    public String getUrl(){
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    public String getFile(){
        return file;
    }

    public String getFileName(){
        return farm + "_" + server + "_" + id + "_" + secret + ".jpg";
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public String getOwnername() {
        return ownername;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
