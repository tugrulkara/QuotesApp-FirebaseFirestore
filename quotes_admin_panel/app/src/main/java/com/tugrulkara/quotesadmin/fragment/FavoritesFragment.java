package com.tugrulkara.quotesadmin.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.adapter.FavoritesAdapter;
import com.tugrulkara.quotesadmin.model.Quote;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class FavoritesFragment extends Fragment {

    private RecyclerView recycler_all_fav;
    private ArrayList<Quote> favQuoteList;

    SQLiteDatabase database;
    private FavoritesAdapter mAdapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=this.getActivity().openOrCreateDatabase("quotes", MODE_PRIVATE,null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fovorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favQuoteList=new ArrayList<>();

        getFavData();

        recycler_all_fav=view.findViewById(R.id.recycler_all_fav);

        recycler_all_fav.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter=new FavoritesAdapter(favQuoteList);
        recycler_all_fav.setAdapter(mAdapter);

    }

    private void getFavData(){

        try {

            Cursor cursor=database.rawQuery("SELECT * FROM quotes ORDER BY id DESC",null);
            int quoteIx=cursor.getColumnIndex("quote_name");
            int authIx=cursor.getColumnIndex("auth_name");
            int catIx=cursor.getColumnIndex("cat_name");
            int uuidIx=cursor.getColumnIndex("uuid");

            favQuoteList.clear();

            while (cursor.moveToNext()){

                Quote quote=new Quote();

                quote.setQuote_txt(cursor.getString(quoteIx));
                quote.setAuthor(cursor.getString(authIx));
                quote.setCategory(cursor.getString(catIx));
                quote.setQuote_id(cursor.getString(uuidIx));

                favQuoteList.add(quote);

            }
            cursor.close();

        }catch (Exception e){


        }

    }

}