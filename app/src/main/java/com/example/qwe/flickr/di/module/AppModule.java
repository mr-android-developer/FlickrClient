package com.example.qwe.flickr.di.module;

import android.content.Context;

import com.example.qwe.flickr.App;
import com.example.qwe.flickr.presenter.NetworkState;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;

@Module
public class AppModule {

    App app;

    public AppModule(App app){
        this.app = app;
    }

    @Provides
    @Singleton
    Context provideContext(){
        return app;
    }

    @Provides
    @Singleton
    RealmConfiguration provideRealmConfiguration(Context context){
        return new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @Provides
    @Singleton
    NetworkState provideNetworkState(){
        return app;
    }
}
