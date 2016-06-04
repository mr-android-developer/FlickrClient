package com.example.qwe.flickr.model.api;

import com.example.qwe.flickr.Constants;
import com.example.qwe.flickr.model.response.ImagesResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {

    @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=owner_name,geo&api_key=" + Constants.API_KEY)
    Observable<ImagesResponse> getRecent(
            @Query("per_page") int count,
            @Query("page") int page
    );

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1&extras=owner_name,geo&api_key=" + Constants.API_KEY)
    Observable<ImagesResponse> search(
            @Query("text") String query,
            @Query("per_page") int count,
            @Query("page") int page
    );


}
