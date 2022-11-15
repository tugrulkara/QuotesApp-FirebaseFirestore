package com.tugrulkara.quotesadmin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.model.Author;
import com.tugrulkara.quotesadmin.util.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuthorsAdapter extends RecyclerView.Adapter<AuthorsAdapter.PostHolder> implements Filterable {

    private AuthorsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public void setOnItemClickListener(AuthorsAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    private Context mContext;
    private ArrayList<Author> authList;
    private List<Author> authListFull;
    int last_position=-1;

    public AuthorsAdapter(ArrayList<Author> authList,List<Author> authListFull) {
        this.authList = authList;
        this.authListFull=authListFull;
    }

    @NonNull
    @Override
    public AuthorsAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        AuthorsAdapter.PostHolder aph = new AuthorsAdapter.PostHolder(v,mListener);
        return aph;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        mContext = holder.itemView.getContext();

        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % 9]));

        if (holder.getAdapterPosition()>last_position){
            Animation animation= AnimationUtils.loadAnimation(mContext,R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            last_position=holder.getAdapterPosition();
        }

        holder.txt_item.setText(authList.get(position).getAuth_name());

        holder.popup_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext.getApplicationContext(), holder.itemView);
                //popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) getActivity().getApplicationContext());
                popup.inflate(R.menu.popup_cat_menu);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId()==R.id.cat_edit){

                            Storage.EditAuth(position,authList,mContext);
                            return true;
                        }else {
                            Storage.DeleteAuth(position,authList,mContext);
                            return true;
                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return authList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        TextView txt_item;
        CardView cardView;
        ImageView popup_item;

        public PostHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);

            txt_item=itemView.findViewById(R.id.text_item);
            cardView=itemView.findViewById(R.id.card_item_row);
            popup_item=itemView.findViewById(R.id.popup_item);

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

    @Override
    public Filter getFilter() {
        return authFilter;
    }

    private Filter authFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Author> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(authListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Author item : authListFull) {
                    if (item.getAuth_name().toLowerCase().contains(filterPattern)) {
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
            authList.clear();
            authList.addAll((Collection<? extends Author>) results.values);
            notifyDataSetChanged();
        }
    };
}
