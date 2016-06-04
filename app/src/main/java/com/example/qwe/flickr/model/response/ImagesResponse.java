package com.example.qwe.flickr.model.response;

import com.example.qwe.flickr.model.data.Image;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImagesResponse {

    public Photos photos;

    public static class Photos{
        int page;

        int total;

        @SerializedName("photo")
        ArrayList<Image> images;

        public int getPage() {
            return page;
        }

        public int getTotal() {
            return total;
        }

        public ArrayList<Image> getImages() {
            return images;
        }

        public void setImages(ArrayList<Image> images) {
            this.images = images;
        }
    }
}
