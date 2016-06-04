package com.example.qwe.flickr.di.component;

import com.example.qwe.flickr.di.module.AppModule;
import com.example.qwe.flickr.di.module.ModelModule;
import com.example.qwe.flickr.di.module.PresenterModule;
import com.example.qwe.flickr.model.ModelImpl;
import com.example.qwe.flickr.presenter.ImagesPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ModelModule.class, PresenterModule.class})
public interface AppComponent {
    void inject(ModelImpl model);
    void inject(ImagesPresenter imagesPresenter);
}
