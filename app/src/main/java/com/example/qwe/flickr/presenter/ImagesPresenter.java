package com.example.qwe.flickr.presenter;


import android.os.Bundle;

import com.example.qwe.flickr.App;
import com.example.qwe.flickr.model.Model;
import com.example.qwe.flickr.model.data.Image;
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

    @Inject
    Model model;

    @Inject
    NetworkState networkState;

    private boolean loading;

    private boolean loadingMore;

    /**
     * Все загруженные фотографии
     */
    private ArrayList<Image> images;

    /**
     * Порция подгруженных фотографий,
     */
    private ArrayList<Image> newImages;

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

    private Subscriber<ArrayList<Image>> subscriber;

    public ImagesPresenter(){
        App.appComponent().inject(this);
    }

    /**
     * Фрагмент/активити созданны
     */
    public void onCreate(Bundle savedInstanceState){
        if (images == null && !loading){
            if (networkState.isConnected()) {
                if (savedInstanceState == null){
                    model.clearCache();
                }
                loadPhotos(loadingMore);
                photosSource = PhotoSource.RECENT;
            } else {
                images = model.getCachedPhotos();
                photosSource = PhotoSource.CACHE;
            }
        }
    }

    /**
     * Привязка view
     */
    public void onCreateView(ImagesView imagesView){
        view = imagesView;
        if (loading){
            if (loadingMore) {
                view.showSmallProgressBar();
                view.showPhotos(images);
            } else {
                view.showProgressBar();
            }
        } else {
            view.showPhotos(images);
        }
    }

    /**
     * Вызывается каждый раз при скроллинге, если неебходимо запрашиваем еще порцию фотографий
     * @param visibleItemCount - кол-во видимых элементов
     * @param firstVisibleItem - позиция первого видимого элемента
     */
    public void onScrolled(int visibleItemCount, int firstVisibleItem){
        int visibleThreshold = 5;
        if (!loading && images != null && (images.size() - visibleItemCount) <= (firstVisibleItem + visibleThreshold)){
            view.showSmallProgressBar();
            switch (photosSource) {
                case RECENT :
                    loadPhotos(true);
                    break;
                case SEARCH:
                    search(query, true);
                    break;
            }
        }
    }


    /**
     * Пользователь запросил обновление данных
     */
    public void onRefresh(){
        if (networkState.isConnected()) {
            photosSource = PhotoSource.RECENT;
            images.clear();
            model.clearCache();
            view.clearPhotos();
            loadPhotos(false);
        } else {
            view.showError("Нет соединения");
        }
    }

    /**
     * Запрос свежих фотографий
     * @param loadMore - покзывать ли индикатор загрузки
     */
    private void loadPhotos(boolean loadMore){
        if (!loading && haveMorePhotos()){
            unSubscribe();
            loadingMore = loadMore;
            subscriber = getSubscriber();
            model.getRecent(PAGE_SIZE, getNextPageNum())
                    .doOnSubscribe(() -> onLoad(loadMore))
                    .switchMap(photosResponse -> {
                        if (images == null){
                            images = new ArrayList<>();
                        }
                        newImages = photosResponse.photos.getImages();
                        images.addAll(newImages);
                        total = photosResponse.photos.getTotal();
                        return model.cachePhotos(photosResponse.photos.getImages());
                    })
                    .subscribe(subscriber);
        }
    }

    private void unSubscribe(){
        if (subscriber != null && !subscriber.isUnsubscribed()){
            subscriber.unsubscribe();
        }
    }

    /**
     * Вызывается каждый раз когда пользователь нажал кнопку поиска
     * @param query - искомый текст
     */
    public void onSearch(String query){
        if (networkState.isConnected()) {
            this.query = query;
            images.clear();
            model.clearCache();
            view.clearPhotos();
            search(query, false);
        } else {
            view.showError("Нет соединения");
        }
    }

    /**
     * Отправляем модели запрос на поиск фотографий
     * @param query - искомы текст
     * @param loadMore - показывать ли индикатор загрузки
     */
    private void search(String query, boolean loadMore){
        if (!loading && haveMorePhotos()){
            unSubscribe();
            subscriber = getSubscriber();
            model.search(query, PAGE_SIZE, getNextPageNum())
                    .doOnSubscribe(() -> onLoad(loadMore))
                    .switchMap(photosResponse -> {
                        if (images == null){
                            images = new ArrayList<>();
                        }
                        newImages = photosResponse.photos.getImages();
                        images.addAll(newImages);
                        total = photosResponse.photos.getTotal();
                        return model.cachePhotos(photosResponse.photos.getImages());
                    })
                    .subscribe(subscriber);
        }
    }

    private Subscriber<ArrayList<Image>> getSubscriber(){
        return new Subscriber<ArrayList<Image>>() {
            @Override
            public void onCompleted() {
                onStopLoad();
            }

            @Override
            public void onError(Throwable t) {
                onErrorLoad(t);
            }

            @Override
            public void onNext(ArrayList<Image> cachedImages) {
                view.showPhotos(newImages);
            }
        };
    }


    private int getNextPageNum(){
        return images != null ? (images.size() / PAGE_SIZE) + 1 : 1;
    }

    private boolean haveMorePhotos(){
        return images == null || images.size() == 0 || images.size() < total || (photosSource == PhotoSource.CACHE);
    }

    private void onLoad(boolean loadMore){
        loading = true;
        if (view != null) {
            view.hideProgressBar();
            if (!loadMore) {
                view.showProgressBar();
            } else {
                view.showSmallProgressBar();
            }
        }
    }

    private void onStopLoad(){
        loading = false;
        if (view != null) {
            view.hideProgressBar();
            view.hideSmallProgressBar();
        }
    }

    private void onErrorLoad(Throwable t){
        onStopLoad();
        if (view != null){
            view.showError(t.getMessage());
        }
    }

    public void unbindView(){
        view = null;
    }

}
