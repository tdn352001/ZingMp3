package com.example.doanmp3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmp3.Activity.DanhSachBaiHatActivity;
import com.example.doanmp3.Activity.PlayNhacActivity;
import com.example.doanmp3.Model.BaiHat;
import com.example.doanmp3.Model.ModelAudio;
import com.example.doanmp3.R;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    Context context;
    ArrayList<ModelAudio> audios;
    ArrayList<BaiHat> arrayList;

    public AudioAdapter(Context context, ArrayList<ModelAudio> audios) {
        this.context = context;
        this.audios = audios;
        arrayList = new ArrayList<>();
        if (audios.size() > 0) {
            for (int i = 0; i < audios.size(); i++) {
                arrayList.add(audios.get(i).convertBaiHat());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dong_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(audios.get(position).getaudioTitle());
        holder.artist.setText(audios.get(position).getaudioArtist());

    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, artist;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayNhacActivity.class);
                intent.putExtra("mangbaihat", arrayList);
                intent.putExtra("position", getPosition());
                intent.putExtra("audio", true);
                DanhSachBaiHatActivity.category = "Playlist";
                DanhSachBaiHatActivity.TenCategoty = "Bài hát trên thiết bị";
                context.startActivity(intent);

            });

        }
    }

}
