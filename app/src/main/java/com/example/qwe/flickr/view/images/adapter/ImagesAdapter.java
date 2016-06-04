package com.example.qwe.flickr.view.images.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qwe.flickr.R;
import com.example.qwe.flickr.model.data.Image;
import com.example.qwe.flickr.view.MainActivity;
import com.example.qwe.flickr.view.map.MapFragment;
import com.example.qwe.flickr.view.image.ImageFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FragmentActivity activity;

    ArrayList<Image> images;



    public ImagesAdapter(FragmentActivity activity){
        super();
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Image image = images.get(position);
        String url = image.isImageLoaded() ? image.getFile() : image.getUrl();
        viewHolder.ivPhoto.layout(0, 0, 0, 0);
        Glide.with(activity).load(url).into(viewHolder.ivPhoto);
        viewHolder.tvOwner.setText(image.getOwnername());
        viewHolder.tvLongitude.setText(String.valueOf(image.getLongitude()));
        viewHolder.tvLatitude.setText(String.valueOf(image.getLatitude()));
        viewHolder.tvMap.setOnClickListener(view->((MainActivity) activity).showFragment(
                MapFragment.newInstance(image.getFile(), image.getLatitude(), image.getLongitude()), true)
        );
        viewHolder.ivPhoto.setOnClickListener(view -> ((MainActivity) activity).showFragment(ImageFragment.newInstance(url), true));
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }


    public void addItems(ArrayList<Image> newItems){
        if (images == null){
            images = new ArrayList<>();
        }
        images.addAll(newItems);
    }

    public void clear(){
        if (images != null){
            images.clear();
            notifyDataSetChanged();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        @BindView(R.id.tv_owner)
        TextView tvOwner;

        @BindView(R.id.tv_longitude)
        TextView tvLongitude;

        @BindView(R.id.tv_latitude)
        TextView tvLatitude;

        @BindView(R.id.tv_map)
        TextView tvMap;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
