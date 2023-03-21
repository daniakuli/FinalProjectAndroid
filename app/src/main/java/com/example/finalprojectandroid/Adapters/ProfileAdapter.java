package com.example.finalprojectandroid.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalprojectandroid.Interfaces.OnItemClickListener;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private List<Pictures> picturesList;

    public static OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener){
        ProfileAdapter.listener = listener;
    }

    public ProfileAdapter() {
        picturesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pictures pictures = picturesList.get(position);
        Picasso.get().load(pictures.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return picturesList.size();
    }

    public void setData(List<Pictures> picturesList) {
        this.picturesList = picturesList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);

            itemView.setOnClickListener(item -> {
                if(listener != null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(pos);
                    }
                }
            });
        }
    }
}
