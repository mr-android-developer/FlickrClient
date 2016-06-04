package com.example.qwe.flickr.view.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.qwe.flickr.R;
import com.example.qwe.flickr.view.MainActivity;

import uk.co.senab.photoview.PhotoView;


/**
 * Выводит 1 фотографию
 */
public class ImageFragment extends Fragment {

    String url;

    public static ImageFragment newInstance(String url){
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        ((MainActivity) getActivity()).hideToolBar();
        PhotoView pvImage = (PhotoView) view.findViewById(R.id.pvImage);
        pvImage.setMinimumScale(1.0f);
        Glide.with(getActivity()).load(url).into(pvImage);
        return view;
    }
}
