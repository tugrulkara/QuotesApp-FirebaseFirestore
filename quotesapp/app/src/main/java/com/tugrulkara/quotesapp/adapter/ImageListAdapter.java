package com.tugrulkara.quotesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tugrulkara.quotesapp.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.PostHolder> {

    private ImageListAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public void setOnItemClickListener(ImageListAdapter.OnItemClickListener listener){
        mListener=listener;
    }


    ArrayList<String> array_image;

    public ImageListAdapter(ArrayList<String> array_image) {
        this.array_image = array_image;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {


        if (array_image.size()<=0){
            Toast.makeText(holder.itemView.getContext(),"Veriler Yüklenemedi! İnternet bağlantınızı kontrol edin veya uygulamayı yeniden başlatın!",Toast.LENGTH_LONG).show();
        }else {
            Picasso.get().load(array_image.get(position)).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null){

                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position,v);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return array_image.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.image_item);
        }
    }
}
