package com.example.qwe.flickr.view;

import com.example.qwe.flickr.model.data.Image;

import java.util.ArrayList;

public interface ImagesView {
    void showPhotos(ArrayList<Image> images);
    void clearPhotos();
    void onLoadingImages();
    void onMoreLoadingImages();
    void onCachingImages();
    void onComplete();
    void onError(String error);
}
