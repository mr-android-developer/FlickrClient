package com.example.qwe.flickr.di.module;

import android.util.Log;

import com.example.qwe.flickr.Constants;
import com.example.qwe.flickr.model.api.Api;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class ModelModule {

    @Singleton
    @Provides
    @Named(Constants.UI_THREAD)
    Scheduler provideSchedulerUI(){
        return AndroidSchedulers.mainThread();
    }

    @Singleton
    @Provides
    @Named(Constants.IO_THREAD)
    Scheduler provideSchedulerIO(){
        return Schedulers.io();
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @Singleton
    Api provideApi(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class);
    }

}
