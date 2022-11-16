package com.tugrulkara.quotesadmin.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesadmin.MainActivity;
import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.adapter.QuotesAdapter;
import com.tugrulkara.quotesadmin.model.Quote;
import com.tugrulkara.quotesadmin.util.Dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuotesFragment extends Fragment {

    private RecyclerView recycler_quotes;
    private FloatingActionButton fab_add;
    private FloatingActionButton scrollUp;
    private ArrayList<Quote> quotesList;
    private List<Quote> quoteListFull;

    private ArrayList<String> cat_name_list;
    private ArrayList<String> cat_id_list;
    private ArrayList<String> auth_name_list;
    private ArrayList<String> auth_id_list;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    QuotesAdapter mAdapter;

    private String cate_name="";
    private String cate_id="";
    private String author_name="";
    private String author_id="";

    String infoAuth=null;
    String infoCat=null;

    TextView textView;

    public QuotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        quotesList=new ArrayList<>();
        quoteListFull=new ArrayList<>();


        cat_name_list=new ArrayList<>();
        cat_id_list=new ArrayList<>();
        auth_name_list=new ArrayList<>();
        auth_id_list=new ArrayList<>();

        if (getArguments() != null) {

            //infoAuth = String.valueOf(getArguments().getSerializable("infoAuth"));
            //infoCat = String.valueOf(getArguments().getSerializable("infoCat"));

            infoAuth=getArguments().getString("infoAuth");
            infoCat=getArguments().getString("infoCat");

            if (infoAuth != null) {
                authToQuotesGetData(infoAuth);
            }else{
                catToQuotesGetData(infoCat);
            }

        }else {
            quotesGetData();
        }

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

        recycler_quotes=view.findViewById(R.id.recycler_all_quotes);
        recycler_quotes.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mAdapter=new QuotesAdapter(quotesList,quoteListFull);
        recycler_quotes.setAdapter(mAdapter);
        textView=view.findViewById(R.id.alertPanel);

        fab_add=view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuote(v);
            }
        });

        scrollUp=view.findViewById(R.id.fab_up);

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


        mAdapter.setOnItemClickListener(new QuotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), view);
                //popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) getActivity().getApplicationContext());
                popup.inflate(R.menu.popup_cat_menu);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId()==R.id.cat_edit){
                            EditQuote(position);
                            return true;
                        }else {

                            DeleteQuote(position);
                            return true;
                        }

                    }
                });
            }
        });

        if (infoAuth==null && infoCat==null){

            mAdapter.setOnAuthClickListener(new QuotesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {

                    QuotesFragment quotesFragment=AuthorsFragment.newInstance(quotesList.get(position).getAuthor());
                    assert getFragmentManager() != null;
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(quotesList.get(position).getAuthor());
                    FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.Container,quotesFragment, "Alıntılar-Sözler");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            mAdapter.setOnCatClickListener(new QuotesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {

                    QuotesFragment quotesFragment=CategoryFragment.newInstance(quotesList.get(position).getCategory());
                    assert getFragmentManager() != null;
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(quotesList.get(position).getCategory());
                    FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.Container,quotesFragment,"Alıntılar-Sözler");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void AddQuote(View view){

        final android.app.Dialog dialog = new android.app.Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_quote);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        EditText quote_txt =dialog.findViewById(R.id.quote_txt);
        TextView selected_cat_txt =dialog.findViewById(R.id.selected_cat_txt);
        TextView selected_auth_txt =dialog.findViewById(R.id.selected_auth_txt);
        AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);

        GetDataCat();
        GetDataAuth();

        selected_cat_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Ekle");
                builder.setSingleChoiceItems(cat_name_list.toArray(new String[cat_name_list.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cate_name=cat_name_list.get(i).toString();
                        cate_id=cat_id_list.get(i).toString();

                    }
                }).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected_cat_txt.setText(cate_name);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        selected_auth_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Ekle");
                builder.setSingleChoiceItems(auth_name_list.toArray(new String[auth_name_list.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //((EditText) v).setText(array[i]);
                        author_name=auth_name_list.get(i).toString();
                        author_id=auth_id_list.get(i).toString();

                    }
                }).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_auth_txt.setText(author_name);
                        dialog.dismiss();
                    }
                }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        bt_post_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);
        ((EditText) dialog.findViewById(R.id.quote_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                post_submit.setEnabled(!s.toString().trim().isEmpty());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        post_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cate_name.matches("") || author_name.matches("")){
                    Toast.makeText(getActivity(),"Lütfen bir kategori veya yazar seçiniz...",Toast.LENGTH_SHORT).show();

                }else{

                    if (quote_txt.getText().toString().trim().length()<=0){
                        Toast.makeText(getActivity(),"Lütfen bir alıntı giriniz...",Toast.LENGTH_SHORT).show();
                    }else{
                        UUID uuid=UUID.randomUUID();
                        String quote_name=quote_txt.getText().toString();
                        String quote_id=uuid.toString();


                        HashMap<String,Object> mData=new HashMap<>();
                        mData.put("quote_name",quote_name);
                        mData.put("quote_id",quote_id);
                        mData.put("auth_id",author_id);
                        mData.put("cat_id",cate_id);
                        mData.put("auth_name",author_name);
                        mData.put("cat_name",cate_name);
                        mData.put("user_id",firebaseUser.getUid());
                        mData.put("timestamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Quote").document(quote_id).set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                quote_txt.setText("");
                                dialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(),"Alıntı ekleme başarılı...",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void EditQuote(int position){

        final android.app.Dialog dialog = new android.app.Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_quote);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        EditText quote_txt =dialog.findViewById(R.id.quote_txt);
        TextView selected_cat_txt =dialog.findViewById(R.id.selected_cat_txt);
        TextView selected_auth_txt =dialog.findViewById(R.id.selected_auth_txt);
        AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);

        quote_txt.setText(quotesList.get(position).getQuote_txt());

        GetDataCat();
        GetDataAuth();

        selected_cat_txt.setText(quotesList.get(position).getCategory());
        selected_auth_txt.setText(quotesList.get(position).getAuthor());

        cate_name=quotesList.get(position).getCategory();
        author_name=quotesList.get(position).getAuthor();

        selected_cat_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Ekle");
                builder.setSingleChoiceItems(cat_name_list.toArray(new String[cat_name_list.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cate_name=cat_name_list.get(i).toString();
                        cate_id=cat_id_list.get(i).toString();

                    }
                }).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_cat_txt.setText(cate_name);
                        dialog.dismiss();
                    }
                }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        selected_auth_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Ekle");
                builder.setSingleChoiceItems(auth_name_list.toArray(new String[auth_name_list.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //((EditText) v).setText(array[i]);
                        author_name=auth_name_list.get(i).toString();
                        author_id=auth_id_list.get(i).toString();

                    }
                }).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_auth_txt.setText(author_name);
                        dialog.dismiss();
                    }
                }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        bt_post_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);

        post_submit.setEnabled(true);

        post_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cate_name.matches("") || author_name.matches("")){
                    Toast.makeText(getActivity(),"Lütfen bir kategori veya yazar seçiniz...",Toast.LENGTH_SHORT).show();

                }else {
                    if (quote_txt.getText().toString().trim().length()<=0){
                        Toast.makeText(getActivity(),"Lütfen bir alıntı giriniz...",Toast.LENGTH_SHORT).show();
                    }else{

                        String quote_name=quote_txt.getText().toString();
                        String quote_id= quotesList.get(position).getQuote_id();

                        HashMap<String,Object> mData=new HashMap<>();
                        mData.put("quote_name",quote_name);
                        //mData.put("quote_id",quote_id);
                        mData.put("auth_id",author_id);
                        mData.put("cat_id",cate_id);
                        mData.put("auth_name",author_name);
                        mData.put("cat_name",cate_name);
                        mData.put("user_id",firebaseUser.getUid());
                        //mData.put("timestamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Quote").document(quote_id).update(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                quote_txt.setText("");
                                dialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(),"Alıntı düzenleme başarılı...",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }


    private void DeleteQuote(int position){

        firebaseFirestore.collection("Quote")
                .document(quotesList.get(position).getQuote_id())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(),"Alıntı silme başarılı...",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

            }
        });

    }



    private void GetDataCat(){

        firebaseFirestore.collection("Category")
                .orderBy("cat_name",Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    cat_name_list.clear();
                    for (DocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        if (data != null) {

                            String cat_name = String.valueOf(data.get("cat_name"));
                            String cat_id=String.valueOf(data.get("cat_id"));

                            cat_name_list.add(cat_name);
                            cat_id_list.add(cat_id);

                        }

                    }
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GetDataAuth(){

        firebaseFirestore.collection("Author").orderBy("auth_name", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    auth_name_list.clear();

                    for (DocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        if (data != null) {

                            String auth_name = String.valueOf(data.get("auth_name"));
                            String auth_id=String.valueOf(data.get("auth_id"));

                            auth_name_list.add(auth_name);
                            auth_id_list.add(auth_id);
                        }

                    }
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void quotesGetData(){

        firebaseFirestore.collection("Quote")
                .orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){

                    quotesList.clear();
                    quoteListFull.clear();

                    for (DocumentSnapshot snapshot : value.getDocuments()){

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

                        textView.setVisibility(View.GONE);

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void authToQuotesGetData(String info){

        firebaseFirestore.collection("Quote")
                .whereEqualTo("auth_name",info)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    quotesList.clear();
                    quoteListFull.clear();

                    for (DocumentSnapshot snapshot : task.getResult()){

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

                        textView.setVisibility(View.GONE);

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void catToQuotesGetData(String info){

        firebaseFirestore.collection("Quote")
                .whereEqualTo("cat_name",info)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    quotesList.clear();
                    quoteListFull.clear();

                    for (DocumentSnapshot snapshot : task.getResult()){

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

                        textView.setVisibility(View.GONE);

                    }
                    quoteListFull.addAll(quotesList);
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}