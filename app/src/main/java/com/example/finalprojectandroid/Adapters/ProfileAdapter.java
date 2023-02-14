package com.example.finalprojectandroid.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.OnGetDataListener;
import com.example.finalprojectandroid.OnItemClickListener;
import com.example.finalprojectandroid.Pictures;
import com.example.finalprojectandroid.R;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private List<Pictures> mData;
    private Context context;

    public static OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public ProfileAdapter(Context context, List<Pictures> pictures) {
        this.context = context;
        mData = pictures;
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
        Pictures pictures = mData.get(position);
        Picasso.get().load(pictures.getImage()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Pictures pic, int pos) {
        mData.set(pos, pic);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.post_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            listener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
