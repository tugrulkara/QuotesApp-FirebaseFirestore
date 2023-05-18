package com.tugrulkara.quotesapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.PostHolder> {

    private CategoryAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    private ArrayList<Category> catList;

    public CategoryAdapter(ArrayList<Category> catList) {
        this.catList = catList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        CategoryAdapter.PostHolder cph=new CategoryAdapter.PostHolder(v,mListener);
        return cph;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % 9]));

        holder.txt_item.setText(catList.get(position).getCat_name());
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        TextView txt_item;
        CardView cardView;

        public PostHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            cardView=itemView.findViewById(R.id.card_item_row);
            txt_item=itemView.findViewById(R.id.text_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null){

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,v);
                        }
                    }
                }
            });
        }
    }
}
