package com.example.qwe.flickr.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.annimon.stream.Stream;
import com.example.qwe.flickr.App;
import com.example.qwe.flickr.Constants;
import com.example.qwe.flickr.model.api.Api;
import com.example.qwe.flickr.model.data.Image;
import com.example.qwe.flickr.model.response.ImagesResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.Scheduler;

public class ModelImpl implements Model {

    private final Observable.Transformer schedulersTransformer;

    @Inject
    @Named(Constants.UI_THREAD)
    Scheduler uiThread;

    @Inject
    @Named(Constants.IO_THREAD)
    Scheduler ioThread;

    @Inject
    Api api;

    @Inject
    RealmConfiguration realmConfiguration;

    @Inject
    Context context;

    public ModelImpl() {
        App.appComponent().inject(this);
        schedulersTransformer = o -> ((Observable) o).subscribeOn(ioThread)
                .observeOn(uiThread)
                .unsubscribeOn(ioThread);
    }

    @Override
    public Observable<ImagesResponse> getRecent(int count, int page) {
        return api.getRecent(count, page)
                .compose(applySchedulers());
    }

    @Override
    public Observable<ImagesResponse> search(String query, int count, int page) {
        return api.search(query, count, page)
                .compose(applySchedulers());
    }

    @Override
    public ArrayList<Image> getCachedPhotos() {
        Realm realm = Realm.getInstance(realmConfiguration);
        ArrayList<Image> images = new ArrayList<>(realm.copyFromRealm(realm.allObjects(Image.class)));
        realm.close();
        return images;
    }


    @Override
    public void clearCache() {
        long tic = System.currentTimeMillis();
        /*File dir = new File(context.getFilesDir().getPath() + "/images");
        if (dir.exists()) {
            dir.deleteOnExit();
        }*/
        Realm realm = Realm.getInstance(realmConfiguration);
        RealmResults<Image> realmResult = realm.where(Image.class).equalTo("imageLoaded", true).findAll();
        Stream.of(realmResult).forEach(photo -> new File(photo.getFile()).deleteOnExit());
        realm.beginTransaction();
        realm.allObjects(Image.class).deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
        long toc = System.currentTimeMillis();
        Log.d(Constants.LOG_TAG, "tic=" + tic + " toc=" + toc + " time=" + (toc - tic));
    }


    @Override
    @SuppressWarnings("unchecked")
    public Observable<ArrayList<Image>> cachePhotos(ArrayList<Image> images) {
        return Observable.create((subscriber) -> {
            Realm realm = Realm.getInstance(realmConfiguration);
            try {
                realm.beginTransaction();
                Stream.of(images).forEach(this::saveImage);
                ArrayList<Image> cachedImages = new ArrayList<>(realm.copyToRealm(images));
                subscriber.onNext(cachedImages);
                realm.commitTransaction();
                subscriber.onCompleted();
            } catch (Throwable t) {
                subscriber.onError(t);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }).map(savedPhotos -> (ArrayList<Image>) savedPhotos) // Пришлось обернуть в map, иначе Transformer не применить
                .compose(applySchedulers());
    }

    private void saveImage(Image image) {
        OutputStream out = null;
        try {
            File dir = new File(context.getFilesDir() + "/images");
            if (!dir.exists()) {
                boolean mkdir = dir.mkdir();
                Log.d(Constants.LOG_TAG, "mkdir=" + mkdir);
            }
            File file = new File(dir.getPath() + "/" + image.getFileName());
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(image.getUrl()).openStream());
            out = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image.setFile(file.getPath());
            image.setImageLoaded(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }
}
