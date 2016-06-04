package com.example.qwe.flickr.view;

import android.os.Binder;


public class PresenterBinder extends Binder {

    private Object presenter;

    public PresenterBinder(Object presenter){
        this.presenter = presenter;
    }

    public <P> P getPresenter(){
        //noinspection unchecked
        return (P) presenter;
    }
}
