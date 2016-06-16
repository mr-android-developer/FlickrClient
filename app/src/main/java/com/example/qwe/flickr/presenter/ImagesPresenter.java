package com.example.qwe.flickr.presenter;


import android.os.Bundle;

import com.example.qwe.flickr.App;
import com.example.qwe.flickr.model.Model;
import com.example.qwe.flickr.model.data.Image;
import com.example.qwe.flickr.model.response.ImagesResponse;
import com.example.qwe.flickr.view.ImagesView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscriber;

public class ImagesPresenter {

    public static final int PAGE_SIZE = 50;

    enum PhotoSource {
        RECENT,
        SEARCH,
        CACHE
    }

    enum LoadStatus {
        LOADING_NEW_DATA,
        LOADING_MORE_DATA,
        CACHING_DATA,
        LOADING_COMPLETE
    }

    @Inject
    Model model;

    @Inject
    NetworkState networkState;

    private LoadStatus loadStatus = LoadStatus.LOADING_COMPLETE;

    /**
     * Все загруженные фотографии
     */
    private ArrayList<Image> images;


    private ImagesView view;

    /**
     * Количество фотографий которые модель нашла по запросу
     */
    private int total;

    /**
     * Источник фотографий(Запрос свежих, запрос по поиску, локальная БД)
     */
    private PhotoSource photosSource;

    /**
     * Сохраняем искомый текст каждый раз, что бы подгружать данные при прокрутке списка
     */
    private String query;


    public ImagesPresenter() {
        App.appComponent().inject(this);
    }

    /**
     * Фрагмент/активити созданны
     */
    public void onCreate(Bundle savedInstanceState) {
        if (images == null) {
            if (networkState.isConnected()) {
                if (savedInstanceState == null) {
                    model.clearCache();
                }
                photosSource = PhotoSource.RECENT;
                loadPhotos(LoadStatus.LOADING_NEW_DATA);
            } else {
                photosSource = PhotoSource.CACHE;
                images = model.getCachedPhotos();
            }
        }
    }


    /**
     * Привязка view
     */
    public void bindView(ImagesView imagesView){
        view = imagesView;
        if (images != null){
            view.showPhotos(images);
        }
        showStatus();
    }

    public void onToolBarReady(){
        showStatus();
    }

    private void showStatus(){
        if (view != null) {
            switch (loadStatus) {
                case LOADING_NEW_DATA:
                    view.onLoadingImages();
                    break;
                case LOADING_MORE_DATA:
                    view.onMoreLoadingImages();
                    break;
                case CACHING_DATA:
                    view.onCachingImages();
                    break;
            }
        }
    }

    /**
     * Вызывается каждый раз при скроллинге, если неебходимо запрашиваем еще порцию фотографий
     *
     * @param visibleItemCount - кол-во видимых элементов
     * @param firstVisibleItem - позиция первого видимого элемента
     */
    public void onScrolled(int visibleItemCount, int firstVisibleItem) {
        int visibleThreshold = 5;
        if (loadStatus == LoadStatus.LOADING_COMPLETE && images != null && (images.size() - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            view.onMoreLoadingImages();
            switch (photosSource) {
                case RECENT:
                    loadPhotos(LoadStatus.LOADING_MORE_DATA);
                    break;
                case SEARCH:
                    search(query, LoadStatus.LOADING_MORE_DATA);
                    break;
            }
        }
    }


    /**
     * Пользователь запросил обновление данных
     */
    public void onRefresh() {
        if (networkState.isConnected()) {
            photosSource = PhotoSource.RECENT;
            images.clear();
            model.clearCache();
            view.clearPhotos();
            loadPhotos(LoadStatus.LOADING_NEW_DATA);
        } else {
            view.onError("Нет соединения");
        }
    }

    /**
     * Запрос свежих фотографий
     */
    private void loadPhotos(LoadStatus newLoadStatus) {
        if (loadStatus == LoadStatus.LOADING_COMPLETE && haveMorePhotos()) {
            loadStatus = newLoadStatus;
            model.getRecent(PAGE_SIZE, getNextPageNum())
                    .doOnSubscribe(this::showStatus)
                    .switchMap(photosResponse -> {
                        ImagesResponse.Photos photos = photosResponse.photos;
                        onNetworkLoadCompleted(photos.getImages(), photos.getTotal());
                        onStartCaching();
                        return model.cachePhotos(photosResponse.photos.getImages());
                    })
                    //.doOnSubscribe(this::onStartCaching)
                    .subscribe(getCacheSubscriber());
        }
    }


    /**
     * Вызывается каждый раз когда пользователь нажал кнопку поиска
     *
     * @param query - искомый текст
     */
    public void onSearch(String query) {
        if (networkState.isConnected()) {
            this.query = query;
            images.clear();
            model.clearCache();
            view.clearPhotos();
            search(query, LoadStatus.LOADING_NEW_DATA);
        } else {
            view.onError("Нет соединения");
        }
    }

    /**
     * Отправляем модели запрос на поиск фотографий
     *
     * @param query - искомы текст
     */
    private void search(String query, LoadStatus newLoadStatus) {
        if (loadStatus == LoadStatus.LOADING_COMPLETE && haveMorePhotos()) {
            loadStatus = newLoadStatus;
            model.search(query, PAGE_SIZE, getNextPageNum())
                    .doOnSubscribe(this::showStatus)
                    .switchMap(photosResponse -> {
                        ImagesResponse.Photos photos = photosResponse.photos;
                        onNetworkLoadCompleted(photos.getImages(), photos.getTotal());
                        onStartCaching();
                        return model.cachePhotos(photosResponse.photos.getImages());
                    })
                    //.doOnSubscribe(this::onStartCaching)
                    .subscribe(getCacheSubscriber());
        }
    }

    private Subscriber<ArrayList<Image>> getCacheSubscriber() {
        return new Subscriber<ArrayList<Image>>() {
            @Override
            public void onCompleted() {
                loadStatus = LoadStatus.LOADING_COMPLETE;
                if (view != null) {
                    view.onComplete();
                }
            }

            @Override
            public void onError(Throwable t) {
                loadStatus = LoadStatus.LOADING_COMPLETE;
                if (view != null) {
                    view.onError(t.getMessage());
                }
            }

            @Override
            public void onNext(ArrayList<Image> images) {

            }
        };
    }

    private int getNextPageNum() {
        return images != null ? (images.size() / PAGE_SIZE) + 1 : 1;
    }

    private boolean haveMorePhotos() {
        return images == null || images.size() == 0 || images.size() < total || (photosSource == PhotoSource.CACHE);
    }

    private void onStartCaching() {
        loadStatus = LoadStatus.CACHING_DATA;
        showStatus();
    }

    private void onNetworkLoadCompleted(ArrayList<Image> responseImages, int responseTotal) {
        if (images == null) images = new ArrayList<>();
        images.addAll(responseImages);
        total = responseTotal;
        if (view != null) {
            view.onComplete();
            view.showPhotos(images);
        }
    }


    public void unbindView() {
        view = null;
    }

}
