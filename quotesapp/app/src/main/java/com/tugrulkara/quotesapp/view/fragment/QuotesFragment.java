package com.tugrulkara.quotesapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesapp.view.FilterQuotesActivity;
import com.tugrulkara.quotesapp.view.QuotesMakerActivity;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.adapter.QuotesAdapter;
import com.tugrulkara.quotesapp.model.Quote;
import com.tugrulkara.quotesapp.util.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuotesFragment extends Fragment {

    private RecyclerView recycler_quotes;
    private FloatingActionButton scrollUp;
    private List<Quote> quotesList;
    private List<Quote> quoteListFull;

    FirebaseFirestore firebaseFirestore;
    QuotesAdapter mAdapter;

    public QuotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore=FirebaseFirestore.getInstance();

        quotesList=new ArrayList<>();
        quoteListFull=new ArrayList<>();

        quotesGetData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        scrollUp=view.findViewById(R.id.fab_up);

        recycler_quotes=view.findViewById(R.id.recycler_all_quotes);
        recycler_quotes.setHasFixedSize(true);
        recycler_quotes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mAdapter=new QuotesAdapter(quotesList,quoteListFull,getActivity());
        recycler_quotes.setAdapter(mAdapter);


        mAdapter.setOnAuthClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoAuth",quotesList.get(position).getAuthor());
                startActivity(intent);
            }
        });

        mAdapter.setOnCatClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoCat",quotesList.get(position).getCategory());
                startActivity(intent);
            }
        });

        mAdapter.setOnQuoteClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                startActivity(intent);
            }
        });

        mAdapter.setOnQuoteMakerClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                startActivity(intent);
            }
        });

        scrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_quotes.smoothScrollToPosition(0);
            }
        });

        recycler_quotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) { // scrolling down
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollUp.setVisibility(View.GONE);
                        }
                    }, 4000); // delay of 2 seconds before hiding the fab

                } else if (dy < 0) { // scrolling up

                    scrollUp.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // No scrolling
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollUp.setVisibility(View.GONE);
                        }
                    }, 4000); // delay of 2 seconds before hiding the fab
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        inflater.inflate(R.menu.menu_quote_maker, menu);
        inflater.inflate(R.menu.menu_all_quotes, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Bir alıntı arayın...");

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today_quote:
                Dialog.quoteOfTheDay(getActivity());
                break;
            case R.id.action_quote_maker:
                Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //extract data from the firestore database
    private void quotesGetData(){

        firebaseFirestore.collection("Quote")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    quotesList.clear();
                    quoteListFull.clear();

                    for (QueryDocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String quote_name = (String) data.get("quote_name");
                        String quote_id = (String) data.get("quote_id");
                        String auth_name = (String) data.get("auth_name");
                        String cat_name = (String) data.get("cat_name");

                        Quote quote=new Quote();

                        quote.setQuote_id(quote_id);
                        quote.setQuote_txt(quote_name);
                        quote.setAuthor(auth_name);
                        quote.setCategory(cat_name);

                        quotesList.add(quote);

                        recycler_quotes.getRecycledViewPool().clear();
                        mAdapter.notifyDataSetChanged();

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(getActivity(),"Veriler yüklenemedi lütfen uygulamayı yeniden başlatın!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}