package com.example.qwe.flickr.model;


import com.example.qwe.flickr.model.data.Image;
import com.example.qwe.flickr.model.response.ImagesResponse;

import java.util.ArrayList;

import rx.Observable;

public interface Model {
    Observable<ImagesResponse> getRecent(int count, int page);
    ArrayList<Image> getCachedPhotos();
    Observable<ArrayList<Image>> cachePhotos(ArrayList<Image> images);
    Observable<ImagesResponse> search(String query, int count, int page);
    void clearCache();
}
