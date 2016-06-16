package com.example.qwe.flickr.view.images;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qwe.flickr.R;
import com.example.qwe.flickr.model.data.Image;
import com.example.qwe.flickr.presenter.ImagesPresenter;
import com.example.qwe.flickr.view.ImagesView;
import com.example.qwe.flickr.view.MainActivity;
import com.example.qwe.flickr.view.PresenterBinder;
import com.example.qwe.flickr.view.images.adapter.ImagesAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesFragment extends Fragment implements ImagesView {

    public static final String PHOTOS_PRESENTER = "photosPresenter";

    ImagesPresenter presenter;

    @BindView(R.id.rv_images)
    RecyclerView rcImages;

    ProgressDialog progressDialog;

    ImagesAdapter imagesAdapter;

    MenuItem miProgressBar;

    MenuItem miRefresh;

    MenuItem miSearch;

    boolean menuReady = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            IBinder binder = BundleCompat.getBinder(savedInstanceState, PHOTOS_PRESENTER);
            if (binder instanceof PresenterBinder) {
                presenter = ((PresenterBinder) binder).getPresenter();
            } else {
                presenter = new ImagesPresenter();
            }
        } else {
            presenter = new ImagesPresenter();
        }
        imagesAdapter = new ImagesAdapter(getActivity());
        presenter.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.images_fragment, container, false);
        ((MainActivity) getActivity()).showToolBar();
        ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        final LinearLayoutManager lm = new LinearLayoutManager(getContext());
        rcImages.setLayoutManager(lm);
        //presenter.onCreateView(this);
        rcImages.setAdapter(imagesAdapter);
        rcImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                presenter.onScrolled(recyclerView.getChildCount(), lm.findFirstVisibleItemPosition());
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.bindView(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        miSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        miRefresh = menu.findItem(R.id.action_refresh);
        miRefresh.setOnMenuItemClickListener(item -> {
            presenter.onRefresh();
            return true;
        });
        miProgressBar = menu.findItem(R.id.action_progress_bar);
        menuReady = true;
        presenter.onToolBarReady();
    }



    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleCompat.putBinder(outState, PHOTOS_PRESENTER, new PresenterBinder(presenter));
    }

    @Override
    public void onError(String error) {
        hideProgressBar();
        hideSmallProgressBar();
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPhotos(ArrayList<Image> images) {
        imagesAdapter.addItems(images);
        imagesAdapter.notifyItemInserted(imagesAdapter.getItemCount() - images.size());
    }

    @Override
    public void clearPhotos() {
        imagesAdapter.clear();
    }


    @Override
    public void onLoadingImages() {
        showProgressBar();
        activity().showTitle(R.string.loading);
    }

    @Override
    public void onCachingImages() {
        showSmallProgressBar();
        activity().showTitle(R.string.caching);
    }

    @Override
    public void onComplete() {
        hideProgressBar();
        hideSmallProgressBar();
        activity().hideTitle();
    }

    @Override
    public void onMoreLoadingImages() {
        showSmallProgressBar();
        activity().showTitle(R.string.loading);
    }

    public void showProgressBar() {
        progressDialog.show();
    }

    public void hideProgressBar() {
        progressDialog.dismiss();
    }

    public void showSmallProgressBar() {
        if (menuReady) {
            miProgressBar.setVisible(true);
            miRefresh.setVisible(false);
            miSearch.setVisible(false);
        }
    }

    public void hideSmallProgressBar() {
        if (menuReady) {
            miProgressBar.setVisible(false);
            miRefresh.setVisible(true);
            miSearch.setVisible(true);
        }
    }

    private MainActivity activity(){
        return (MainActivity) getActivity();
    }
}
