package com.example.qwe.flickr.view.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qwe.flickr.R;
import com.example.qwe.flickr.util.BitmapUtil;
import com.example.qwe.flickr.view.MainActivity;
import com.example.qwe.flickr.view.image.ImageFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String PHOTO_URL = "photoUrl";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private String photoUrl;
    private double latitude;
    private double longitude;


    public static MapFragment newInstance(String photoUrl, double latitude, double longitude){
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_URL, photoUrl);
        args.putDouble(LATITUDE, latitude);
        args.putDouble(LONGITUDE, longitude);
        mapFragment.setArguments(args);
        return mapFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        photoUrl = args.getString(PHOTO_URL);
        latitude = args.getDouble(LATITUDE);
        longitude = args.getDouble(LONGITUDE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ((MainActivity) getActivity()).hideToolBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapView, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bitmap bm = BitmapUtil.roundBitmap(BitmapFactory.decodeFile(photoUrl));
        LatLng location = new LatLng(latitude, longitude);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bm))
                .anchor(0.5f, 1));
        googleMap.setOnMarkerClickListener(marker -> {
            ((MainActivity) getActivity()).showFragment(ImageFragment.newInstance(photoUrl), true);
            return false;
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
    }





}
