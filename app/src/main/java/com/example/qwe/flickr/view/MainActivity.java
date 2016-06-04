package com.example.qwe.flickr.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.example.qwe.flickr.R;
import com.example.qwe.flickr.view.images.ImagesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fragment_container)
    FrameLayout container;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null){
            showFragment(new ImagesFragment(), false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void showFragment(Fragment fragment, boolean backStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (backStack){
            ft.addToBackStack(null);
        }
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    public void hideToolBar(){
        toolbar.setVisibility(View.GONE);
    }

    public void showToolBar(){
        toolbar.setVisibility(View.VISIBLE);
    }
}
