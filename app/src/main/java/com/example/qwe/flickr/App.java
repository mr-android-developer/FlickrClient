package com.example.qwe.flickr;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.qwe.flickr.di.component.AppComponent;
import com.example.qwe.flickr.di.component.DaggerAppComponent;
import com.example.qwe.flickr.di.module.AppModule;
import com.example.qwe.flickr.presenter.NetworkState;

public class App extends Application implements NetworkState {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent appComponent(){
        return appComponent;
    }

    @Override
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
