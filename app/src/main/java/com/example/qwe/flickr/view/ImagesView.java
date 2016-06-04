package com.example.qwe.flickr.view;

import com.example.qwe.flickr.model.data.Image;

import java.util.ArrayList;

public interface ImagesView {

    void showProgressBar();
    void hideProgressBar();
    void showSmallProgressBar();
    void hideSmallProgressBar();
    void showError(String error);
    void showPhotos(ArrayList<Image> images);
    void clearPhotos();

}
