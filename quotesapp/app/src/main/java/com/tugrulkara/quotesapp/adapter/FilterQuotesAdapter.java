package com.tugrulkara.quotesapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.model.Quote;
import com.tugrulkara.quotesapp.util.Share;
import com.tugrulkara.quotesapp.util.Snackbar;
import com.tugrulkara.quotesapp.util.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterQuotesAdapter extends RecyclerView.Adapter<FilterQuotesAdapter.PostHolder> implements Filterable {

    private FilterQuotesAdapter.OnItemClickListener quoteListener;
    private FilterQuotesAdapter.OnItemClickListener quoteMakerListener;

    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public void setOnQuoteClickListener(FilterQuotesAdapter.OnItemClickListener listener){
        quoteListener=listener;
    }

    public void setOnQuoteMakerClickListener(FilterQuotesAdapter.OnItemClickListener listener){
        quoteMakerListener=listener;
    }

    private List<Quote> quotesList;
    private List<Quote> quoteListFull;
    private ArrayList<String> favQuoteIdList;
    private Context mContext;

    public FilterQuotesAdapter(List<Quote> quotesList, List<Quote> quoteListFull, Context mContext) {
        this.quotesList = quotesList;
        this.quoteListFull = quoteListFull;
        this.mContext = mContext;
    }

    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};
    int last_position=-1;

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_quotes,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % 9]));

        if (holder.getAdapterPosition()>last_position){
            Animation animation= AnimationUtils.loadAnimation(mContext,R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            last_position=holder.getAdapterPosition();
        }

        final Quote quote = quotesList.get(position);

        favQuoteIdList=new ArrayList<>();

        holder.txt_quote.setText(quote.getQuote_txt());
        holder.txt_cat.setText(quote.getCategory());
        holder.txt_auth.setText(String.format("- %s", quote.getAuthor()));

        holder.img_copy.setOnClickListener(view -> {
            Share.copyToClipboard(mContext, quote.getQuote_txt());
            Snackbar.showText(view, R.string.copy_to_clipboard);

        });

        holder.img_share.setOnClickListener(view -> {
            Share.withText(mContext, quote.getQuote_txt());
        });


        Storage.getFavIdData(favQuoteIdList,mContext);

        if (favQuoteIdList.contains(quote.getQuote_id())){
            holder.img_fav.setImageResource(R.drawable.ic_favorite_on);
        }else {
            holder.img_fav.setImageResource(R.drawable.ic_favorite_off);
        }

        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(holder,quote,v);
            }
        });


        holder.txt_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quoteListener != null){

                    if (position != RecyclerView.NO_POSITION) {
                        quoteListener.onItemClick(position,v);
                    }
                }

            }
        });

        holder.img_quote_maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quoteMakerListener != null){

                    if (position != RecyclerView.NO_POSITION) {
                        quoteMakerListener.onItemClick(position,v);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return quotesList.size();
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        TextView txt_cat,txt_auth,txt_quote;
        ImageView img_fav,img_share,img_copy,img_quote_maker;
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
            img_quote_maker=itemView.findViewById(R.id.image_quote_maker);
        }
    }

    private void setFavorite(FilterQuotesAdapter.PostHolder holder, Quote quote, View view) {

        Storage.getFavIdData(favQuoteIdList,mContext);

        int favoriteImage = R.drawable.ic_favorite_off;

        if (favQuoteIdList.contains(quote.getQuote_id())) {
            Storage.deleteFav(quote,mContext,view);
        } else {
            Storage.addFavorite(quote,mContext,view);
            favoriteImage = R.drawable.ic_favorite_on;
        }
        holder.img_fav.setImageResource(favoriteImage);
    }

    @Override
    public Filter getFilter() {
        return quoteFilter;
    }

    private Filter quoteFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Quote> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(quoteListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Quote item : quoteListFull) {
                    if (item.getQuote_txt().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            quotesList.clear();
            quotesList.addAll((Collection<? extends Quote>) results.values);
            notifyDataSetChanged();
        }
    };
}
