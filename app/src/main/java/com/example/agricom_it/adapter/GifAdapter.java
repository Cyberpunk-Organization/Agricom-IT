package com.example.agricom_it.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agricom_it.model.GifItem;

import java.util.List;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifViewHolder> {
    private List<GifItem> gifs;
    private OnGifClickListener listener;

    public interface OnGifClickListener {
        void onGifClick(String gifUrl);
    }

    public GifAdapter(List<GifItem> gifs, OnGifClickListener listener) {
        this.gifs = gifs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 300));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new GifViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(GifViewHolder holder, int position) {
        GifItem gif = gifs.get(position);
        Glide.with(holder.image.getContext()).asGif().load(gif.url).into(holder.image);
        holder.image.setOnClickListener(v -> listener.onGifClick(gif.url));
    }
    @Override
    public int getItemCount() {
        return gifs.size();
    }
    public static class GifViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public GifViewHolder(ImageView iv) {
            super(iv);
            image = iv;
        }
    }
}