package com.tugrulkara.quotesadmin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.model.Quote;
import com.tugrulkara.quotesadmin.util.Share;
import com.tugrulkara.quotesadmin.util.Snackbar;
import com.tugrulkara.quotesadmin.util.Storage;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.PostHolder>{


    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    private ArrayList<Quote> favQuoteList;
    private ArrayList<String> favQuoteIdlist;
    private Context mContext;

    public FavoritesAdapter(ArrayList<Quote> favQuoteList) {
        this.favQuoteList = favQuoteList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_quotes,parent,false);
        FavoritesAdapter.PostHolder fap=new FavoritesAdapter.PostHolder(v);
        return fap;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        mContext = holder.itemView.getContext();

        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % 9]));

        holder.img_popup.setVisibility(View.INVISIBLE);

        favQuoteIdlist=new ArrayList<>();

        final Quote quote = favQuoteList.get(holder.getAdapterPosition());

        holder.txt_quote.setText(quote.getQuote_txt());
        holder.txt_cat.setText(quote.getCategory());
        holder.txt_auth.setText(String.format("- %s", quote.getAuthor()));

        holder.img_copy.setOnClickListener(view -> {
            Share.copyToClipboard(mContext, quote.getQuote_txt());
            Snackbar.showText(view, R.string.copy_to_clipboard);
            //Toast.makeText(mContext,"Kopyalandı",Toast.LENGTH_SHORT).show();
        });

        holder.img_share.setOnClickListener(view -> Share.withText(mContext, quote.getQuote_txt()));

        Storage.getFavIdData(favQuoteIdlist,mContext);

        if (favQuoteIdlist.contains(quote.getQuote_id())){
            holder.img_fav.setImageResource(R.drawable.ic_favorite_on);
        }

        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(holder,quote,v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favQuoteList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        TextView txt_cat,txt_auth,txt_quote;
        ImageView img_fav,img_share,img_copy,img_popup;
        CardView cardView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.card_quote);

            txt_auth=itemView.findViewById(R.id.text_author);
            txt_cat=itemView.findViewById(R.id.text_category);
            txt_quote=itemView.findViewById(R.id.text_quote);
            img_copy=itemView.findViewById(R.id.image_copy_to_clipboard);
            img_fav=itemView.findViewById(R.id.image_favorite);
            img_share=itemView.findViewById(R.id.image_share);
            img_popup=itemView.findViewById(R.id.popup_quote);

        }
    }

    private void setFavorite(FavoritesAdapter.PostHolder holder, Quote quote,View view) {

        Storage.getFavIdData(favQuoteIdlist,mContext);

        int favoriteImage = R.drawable.ic_favorite_off;

        if (favQuoteIdlist.contains(quote.getQuote_id())) {
            Storage.deleteFav(quote,mContext,view);
        } else {
            Storage.addFavorite(quote,mContext,view);
            favoriteImage = R.drawable.ic_favorite_on;
        }
        holder.img_fav.setImageResource(favoriteImage);
    }

}
