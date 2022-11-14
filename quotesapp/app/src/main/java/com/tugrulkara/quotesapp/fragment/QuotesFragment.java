package com.tugrulkara.quotesapp.fragment;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesapp.FilterQuotesActivity;
import com.tugrulkara.quotesapp.MainActivity;
import com.tugrulkara.quotesapp.QuotesMakerActivity;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.adapter.QuotesAdapter;
import com.tugrulkara.quotesapp.model.Quote;
import com.tugrulkara.quotesapp.util.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuotesFragment extends Fragment {

    private RecyclerView recycler_quotes;
    private FloatingActionButton scrollUp;
    private List<Quote> quotesList;
    private List<Quote> quoteListFull;

    FirebaseFirestore firebaseFirestore;
    QuotesAdapter mAdapter;

    //private InterstitialAd mInterstitialAd;
    private boolean mInterstitialLoaded=true;//appodeal

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

        Appodeal.initialize(getActivity(), "ee3effe77385b6d328109a32393480096104d245dff12b24", Appodeal.INTERSTITIAL, true);
        Appodeal.isLoaded(Appodeal.INTERSTITIAL);
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                // Called when interstitial is loaded
                loadAppodeal();
            }
            @Override
            public void onInterstitialFailedToLoad() {
                // Called when interstitial failed to load
            }
            @Override
            public void onInterstitialShown() {
                // Called when interstitial is shown
            }
            @Override
            public void onInterstitialShowFailed() {
                // Called when interstitial show failed
            }
            @Override
            public void onInterstitialClicked() {
                // Called when interstitial is clicked
            }
            @Override
            public void onInterstitialClosed() {
                // Called when interstitial is closed
            }
            @Override
            public void onInterstitialExpired()  {
                // Called when interstitial is expired
            }
        });

        /*AdRequest adRequest = new AdRequest.Builder().build();

        //Geçiş_Test: ca-app-pub-3940256099942544/1033173712

        InterstitialAd.load(getActivity(),getString(R.string.testGecis), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                //Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                //Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });*/


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
                if (mInterstitialLoaded) {
                    //mInterstitialAd.show(getActivity());
                    Appodeal.show(getActivity(), Appodeal.INTERSTITIAL);
                    mInterstitialLoaded=false;
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                    startActivity(intent);
                } else {
                    //Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                    startActivity(intent);
                }

            }
        });

        mAdapter.setOnQuoteMakerClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (mInterstitialLoaded) {
                    //mInterstitialAd.show(getActivity());
                    Appodeal.show(getActivity(), Appodeal.INTERSTITIAL);
                    mInterstitialLoaded=false;
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                    startActivity(intent);
                } else {
                    //Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",quotesList.get(position).getQuote_txt()+"\n"+"\n"+"-"+quotesList.get(position).getAuthor());
                    startActivity(intent);
                }

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

    private void loadAppodeal(){
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (!mInterstitialLoaded){
                            mInterstitialLoaded=true;
                        }
                        //Toast.makeText(getActivity(), String.valueOf(isShown), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 120, 120, TimeUnit.SECONDS);
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm,String title) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.addToBackStack(null);
        ft.commit();
        setToolbarTitle(title);
    }

    public void setToolbarTitle(String Title) {

        if (((MainActivity)getActivity()).getSupportActionBar() != null) {

            ((MainActivity)getActivity()).getSupportActionBar().setTitle(Title);
        }
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