package com.example.qwe.flickr.di.module;

import com.example.qwe.flickr.model.Model;
import com.example.qwe.flickr.model.ModelImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {


    @Singleton
    @Provides
    Model providesModel(){
        return new ModelImpl();
    }
}
