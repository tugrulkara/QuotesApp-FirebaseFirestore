package com.tugrulkara.quotesapp.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tugrulkara.quotesapp.FilterQuotesActivity;
import com.tugrulkara.quotesapp.MainActivity;
import com.tugrulkara.quotesapp.QuotesMakerActivity;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.SliderDetailActivity;
import com.tugrulkara.quotesapp.adapter.CategoryAdapter;
import com.tugrulkara.quotesapp.adapter.SliderAdapter;
import com.tugrulkara.quotesapp.model.Category;
import com.tugrulkara.quotesapp.model.Quote;
import com.tugrulkara.quotesapp.util.Share;
import com.tugrulkara.quotesapp.util.Snackbar;
import com.tugrulkara.quotesapp.util.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomePageFragment extends Fragment {

    //not active

    private ArrayList<Category> catList;
    private CategoryAdapter mAdapter;
    private List<Quote> randomQuotesList;
    private ArrayList<String> favQuoteIdList;

    private List<Quote> sliderList;

    private RecyclerView recyclerView;
    private TextView txt_quote,txt_auth,txt_cat,recentAll;
    private ImageView random_btn;
    private CardView cardView;
    private ImageView image_favorite_home,image_share_home,image_copy_to_clipboard_home,image_quote_maker_home;

    private FirebaseFirestore firebaseFirestore;
    private Context mContext;
    private LinearLayout linearLayout;
    private InterstitialAd mInterstitialAd;
    private Quote quote;

    private FragmentManager fragmentManager;

    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    private SliderView sliderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mContext=getContext().getApplicationContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        fragmentManager = getFragmentManager();

        favQuoteIdList=new ArrayList<>();
        catList=new ArrayList<>();
        randomQuotesList=new ArrayList<>();

        sliderList=new ArrayList<>();

        txt_auth=view.findViewById(R.id.text_author_home);
        txt_cat=view.findViewById(R.id.text_category_home);
        txt_quote=view.findViewById(R.id.text_quote_home);
        random_btn=view.findViewById(R.id.random_btn);
        cardView=view.findViewById(R.id.card_quote_home_page);
        image_favorite_home=view.findViewById(R.id.image_favorite_home);
        image_share_home=view.findViewById(R.id.image_share_home);
        image_copy_to_clipboard_home=view.findViewById(R.id.image_copy_to_clipboard_home);
        image_quote_maker_home=view.findViewById(R.id.image_quote_maker_home);
        linearLayout=view.findViewById(R.id.random_lyt);
        recentAll=view.findViewById(R.id.recentAll);

        MobileAds.initialize(getActivity());

        AdRequest adRequestIn = new AdRequest.Builder().build();

        InterstitialAd.load(getActivity(),getString(R.string.testGecis), adRequestIn, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });

        sliderView = view.findViewById(R.id.slider);

        quotesGetData();
        catGetData();
        getSliderData(getActivity().getApplicationContext());

        SliderAdapter sliderAdapter = new SliderAdapter(sliderList);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        recyclerView=view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter=new CategoryAdapter(catList);
        recyclerView.setAdapter(mAdapter);

        sliderAdapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getContext(), SliderDetailActivity.class);
                intent.putExtra("quote",sliderList.get(position).getQuote_txt());
                intent.putExtra("auth",sliderList.get(position).getAuthor());
                intent.putExtra("id",sliderList.get(position).getQuote_id());
                startActivity(intent);
            }
        });

        recentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity=new MainActivity();
                CategoryFragment categoryFragment=new CategoryFragment();
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.category_nav_bottom);
                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
            }
        });

        mAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoCat",catList.get(position).getCat_name());
                startActivity(intent);
            }
        });

        random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (randomQuotesList.size()>0){
                        LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_bottom_to_top_slide);
                        linearLayout.setLayoutAnimation(anim);
                        randomQuote(randomQuotesList);
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }
            }
        });

        txt_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",txt_quote.getText().toString()+"\n"+"\n"+"-"+txt_auth.getText().toString());
                    startActivity(intent);
                } else {
                    //Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",txt_quote.getText().toString()+"\n"+"\n"+"-"+txt_auth.getText().toString());
                    startActivity(intent);
                }

            }
        });

        image_quote_maker_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",txt_quote.getText().toString()+"\n"+"\n"+"-"+txt_auth.getText().toString());
                    startActivity(intent);
                } else {
                    //Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                    intent.putExtra("info",txt_quote.getText().toString()+"\n"+"\n"+"-"+txt_auth.getText().toString());
                    startActivity(intent);
                }

            }
        });

        image_favorite_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (randomQuotesList.size()>0){
                        setFavorite(quote,v);
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }

            }
        });

        image_copy_to_clipboard_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.copyToClipboard(getActivity(), txt_quote.getText().toString());
                Snackbar.showText(view, R.string.copy_to_clipboard);
            }
        });

        image_share_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.withText(getActivity(), txt_quote.getText().toString());
            }
        });

        txt_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoAuth",txt_auth.getText().toString());
                startActivity(intent);
            }
        });

        txt_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoCat",txt_cat.getText().toString());
                startActivity(intent);
            }
        });

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        //ft.addToBackStack(null);
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {

        if (((MainActivity)getActivity()).getSupportActionBar() != null) {

            ((MainActivity)getActivity()).getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_quote_maker, menu);
        //inflater.inflate(R.menu.menu_all_quotes, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_today_quote:

                Dialog.quoteOfTheDay(getActivity());
                break;*/

            case R.id.action_quote_maker:

                Intent intent=new Intent(getActivity().getApplicationContext(), QuotesMakerActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void randomQuote(List<Quote> randomQuotesList){

        try {
            Random random=new Random();
            int x = random.nextInt(8);
            cardView.setCardBackgroundColor(Color.parseColor(colors[x]));

            if (randomQuotesList.size() > 0){

                Random random1=new Random();
                int y= random1.nextInt(randomQuotesList.size());
                quote=randomQuotesList.get(y);

                txt_quote.setText(quote.getQuote_txt());
                txt_cat.setText(quote.getCategory());
                txt_auth.setText(quote.getAuthor());

                System.out.println(randomQuotesList.size());
                System.out.println(y);

                Storage.getFavIdData(favQuoteIdList,mContext);

                if (favQuoteIdList.contains(quote.getQuote_id())){
                    image_favorite_home.setImageResource(R.drawable.ic_favorite_on);
                }else {
                    image_favorite_home.setImageResource(R.drawable.ic_favorite_off);
                }

            }else {
                Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
        }

    }

    private void setFavorite(Quote quote, View view) {

        Storage.getFavIdData(favQuoteIdList,getActivity().getApplicationContext());

        int favoriteImage = R.drawable.ic_favorite_off;

        if (favQuoteIdList.contains(quote.getQuote_id())) {
            Storage.deleteFav(quote,getActivity(),view);
        } else {
            Storage.addFavorite(quote,getActivity(),view);
            favoriteImage = R.drawable.ic_favorite_on;
        }
        image_favorite_home.setImageResource(favoriteImage);
    }

    private void quotesGetData(){

       firebaseFirestore.collection("Quote").orderBy("timestamp", Query.Direction.DESCENDING)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                       if (task.isSuccessful()) {

                           randomQuotesList.clear();

                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Map<String, Object> data = document.getData();

                               String quote_name = (String) data.get("quote_name");
                               String quote_id = (String) data.get("quote_id");
                               String auth_name = (String) data.get("auth_name");
                               String cat_name = (String) data.get("cat_name");

                               Quote quote=new Quote();

                               quote.setQuote_id(quote_id);
                               quote.setQuote_txt(quote_name);
                               quote.setAuthor(auth_name);
                               quote.setCategory(cat_name);

                               randomQuotesList.add(quote);

                           }
                           randomQuote(randomQuotesList);
                       } else {
                           Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                       }

                   }
               });
    }

    private void catGetData(){

        firebaseFirestore.collection("Category")
                .orderBy("popular", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    catList.clear();

                    for (QueryDocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String cat_id = (String) data.get("cat_id");
                        String cat_name = (String) data.get("cat_name");

                        Category category=new Category();
                        category.setCat_id(cat_id);
                        category.setCat_name(cat_name);

                        if (catList.size()<=3){
                            catList.add(category);
                        }

                        mAdapter.notifyDataSetChanged();

                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getSliderData(Context mContext){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("slider", MODE_PRIVATE,null);

        try {

            Cursor cursor=database.rawQuery("SELECT * FROM slider ORDER BY id DESC",null);
            int quoteIx=cursor.getColumnIndex("quote_name");
            int authIx=cursor.getColumnIndex("auth_name");
            int uuidIx=cursor.getColumnIndex("uuid");

            if (cursor!=null){

                //System.out.println("Slider verisi var");

                sliderList.clear();

                while (cursor.moveToNext()){

                    Quote quote=new Quote();

                    quote.setQuote_txt(cursor.getString(quoteIx));
                    quote.setAuthor(cursor.getString(authIx));
                    quote.setQuote_id(cursor.getString(uuidIx));

                    sliderList.add(quote);

                }
                cursor.close();
            }

        }catch (Exception e){

            if (sliderList.size()<=0){

                Quote quote1=new Quote();
                quote1.setQuote_id("ff9fd71b-da15-46c0-b74b-ef855c312809");
                quote1.setQuote_txt("Der misin ki bir gün;\n" +
                        "\"İnşallah çok bekletmedim seni...\"");
                quote1.setAuthor("Cahit Zarifoğlu");

                Quote quote2=new Quote();
                quote2.setQuote_id("dba1e0b4-879e-48ec-89d9-eb1df4758642");
                quote2.setQuote_txt("Herkesin, diğer insanların hayatlarını nasıl yönetmesi gerektiğine dair net bir fikri var, ancak kendi hayatına dair hiçbir fikri yok.");
                quote2.setAuthor("Paulo Coelho");

                Quote quote3=new Quote();
                quote3.setQuote_id("fe6357b7-c0ea-4a3b-bb4e-ce94fbd5b2ed");
                quote3.setQuote_txt("Göğün yıldızları arasından yoksul, düzeni bozulmuş bir evren görür gibi oluyorum. Bu yüzden huzursuzum.");
                quote3.setAuthor("Victor Hugo");

                sliderList.add(quote1);
                sliderList.add(quote2);
                sliderList.add(quote3);

                //System.out.println("SliderListe veri eklendi");
            }

        }

    }

}