package com.tugrulkara.quotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesapp.adapter.FilterQuotesAdapter;
import com.tugrulkara.quotesapp.model.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterQuotesActivity extends AppCompatActivity {

    private RecyclerView filter_recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private List<Quote> quotesList;
    private List<Quote> quoteListFull;
    private FilterQuotesAdapter mAdapter;

    private Toolbar toolbar;

    private String infoAuth=null;
    private String infoCat=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_quotes);

        firebaseFirestore=FirebaseFirestore.getInstance();

        quotesList=new ArrayList<>();
        quoteListFull=new ArrayList<>();

        Intent intent=getIntent();
        infoAuth=intent.getStringExtra("infoAuth");
        infoCat=intent.getStringExtra("infoCat");

        toolbar = findViewById(R.id.toolbar);

        if (infoAuth != null) {
            toolbar.setTitle(infoAuth);
            authToQuotesGetData(infoAuth);
        }else{
            toolbar.setTitle(infoCat);
            catToQuotesGetData(infoCat);
        }

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        filter_recyclerView=findViewById(R.id.recycler_filter_quotes);
        filter_recyclerView.setHasFixedSize(true);
        filter_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new FilterQuotesAdapter(quotesList,quoteListFull,this);
        filter_recyclerView.setAdapter(mAdapter);

        mAdapter.setOnQuoteClickListener(new FilterQuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(FilterQuotesActivity.this, QuotesMakerActivity.class);
                intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                startActivity(intent);
            }
        });

        mAdapter.setOnQuoteMakerClickListener(new FilterQuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(FilterQuotesActivity.this, QuotesMakerActivity.class);
                intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

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

        return super.onCreateOptionsMenu(menu);
    }

    //query by author
    public void authToQuotesGetData(String info){

        firebaseFirestore.collection("Quote")
                .whereEqualTo("auth_name",info)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

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

                        mAdapter.notifyDataSetChanged();

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(FilterQuotesActivity.this,"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    //query by category
    private void catToQuotesGetData(String info){

        firebaseFirestore.collection("Quote")
                .whereEqualTo("cat_name",info)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

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

                        mAdapter.notifyDataSetChanged();

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(FilterQuotesActivity.this,"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}