package com.example.doanmp3.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.doanmp3.Context.Data.AudioThumbnail;
import com.example.doanmp3.Interface.ItemClick;
import com.example.doanmp3.Interface.ItemConfig;
import com.example.doanmp3.Interface.OptionClick;
import com.example.doanmp3.Models.Song;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.Tools;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SongSelectedAdapter extends RecyclerView.Adapter<SongSelectedAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> songs;
    ItemClick itemClick;
    OptionClick optionClick;
    Boolean haveConfig;
    ItemConfig configure;
    int itemSelected;
    Handler handler;


    ArrayList<Bitmap> bitmaps;

    public SongSelectedAdapter(Context context, ArrayList<Song> songs, ItemClick itemClick, OptionClick optionClick) {
        this.context = context;
        this.songs = songs;
        this.itemClick = itemClick;
        this.optionClick = optionClick;
        this.haveConfig = false;
        itemSelected = -1;
        bitmaps = new ArrayList<>();
        handler = new Handler();
    }

    public SongSelectedAdapter(Context context, ArrayList<Song> songs, ItemClick itemClick, OptionClick optionClick, ItemConfig configure) {
        this.context = context;
        this.songs = songs;
        this.itemClick = itemClick;
        this.optionClick = optionClick;
        this.configure = configure;
        this.haveConfig = false;
        itemSelected = -1;
        bitmaps = new ArrayList<>();
        handler = new Handler();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_song_selected, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song == null) {
            return;
        }

        // Config Item
        if (haveConfig) {
            configure.CustomItem(holder.itemView, position);
        }

        // UI
        holder.tvSongName.setText(song.getName());
        holder.tvSingersName.setText(song.getAllSingerNames());

        if (song.isAudio()) {
            int resId;
            try {
                resId = Integer.parseInt(song.getThumbnail());
            } catch (Exception e) {
                resId = AudioThumbnail.getRandomThumbnail();
            }
            Glide.with(context).asBitmap().load(resId).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap bitmapBlurred = Tools.blurBitmap(context, resource, 25f);
                    bitmaps.add(bitmapBlurred);
                    holder.thumbnail.setImageBitmap(resource);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        } else {
            Glide.with(context).asBitmap().load(song.getThumbnail()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    holder.thumbnail.setImageBitmap(resource);
                    Bitmap bitmapBlurred = Tools.blurBitmap(context, resource, 25f);
                    bitmaps.add(bitmapBlurred);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });

        }


        // Event
        holder.btnOptions.setOnClickListener(v -> optionClick.onOptionClick(position));
        holder.itemView.setOnClickListener(v -> {
            changeItemSelected(position);
            itemClick.onItemClick(position);
        });

        if (itemSelected == position) {
            holder.itemView.setBackgroundResource(R.drawable.custom_selected_item_recycleview);
        } else {
            holder.itemView.setBackgroundResource(R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView thumbnail;
        TextView tvSongName, tvSingersName;
        MaterialButton btnOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail_item_song_selected);
            tvSongName = itemView.findViewById(R.id.tv_name_song_item_song);
            tvSingersName = itemView.findViewById(R.id.tv_name_singers_item_song_selected);
            btnOptions = itemView.findViewById(R.id.options_item_song_selected);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeItemSelected(int newIndex) {
        int oldSelectedIndex = itemSelected;
        itemSelected = newIndex;
        notifyItemChanged(oldSelectedIndex);
        notifyItemChanged(itemSelected);
    }


    public ArrayList<Bitmap> getBackgroundDrawables() {
        return bitmaps;
    }
}
