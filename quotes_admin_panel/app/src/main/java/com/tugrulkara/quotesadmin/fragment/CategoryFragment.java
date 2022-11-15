package com.tugrulkara.quotesadmin.fragment;

import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesadmin.MainActivity;
import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.adapter.CategoryAdapter;
import com.tugrulkara.quotesadmin.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryFragment extends Fragment {

    private RecyclerView recycler_all_cat;
    private ArrayList<Category> catList;
    private List<Category> catListFull;
    private CategoryAdapter mAdapter;

    private FloatingActionButton cat_add;

    FirebaseFirestore firebaseFirestore;


    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        firebaseFirestore=FirebaseFirestore.getInstance();
        catList=new ArrayList<>();
        catListFull=new ArrayList<>();

        catGetData();

        recycler_all_cat=view.findViewById(R.id.recycler_all_cat);

        recycler_all_cat.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter=new CategoryAdapter(catList,catListFull);
        recycler_all_cat.setAdapter(mAdapter);

        cat_add=view.findViewById(R.id.cat_add);
        cat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCat(v);
            }
        });

        mAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                QuotesFragment quotesFragment=CategoryFragment.newInstance(catList.get(position).getCat_name());
                assert getFragmentManager() != null;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(catList.get(position).getCat_name());
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Container,quotesFragment,"Kategoriler");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    public static QuotesFragment newInstance(String data) {
        QuotesFragment quotesFragment = new QuotesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoCat", data);
        quotesFragment.setArguments(bundle);
        return quotesFragment;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Bir kategori arayın...");
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
            case R.id.action_search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void AddCat(View view){

        final android.app.Dialog dialog = new android.app.Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText cat_txt =dialog.findViewById(R.id.item_txt);
        AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);


        bt_post_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);
        ((EditText) dialog.findViewById(R.id.item_txt)).addTextChangedListener(new TextWatcher() {
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

                if (cat_txt.getText().toString().trim().length()<=0){
                    Toast.makeText(getActivity().getApplicationContext(),"Lütfen bir kategori ismi giriniz...",Toast.LENGTH_SHORT).show();
                }else{

                    UUID uuid=UUID.randomUUID();
                    String cat_name=cat_txt.getText().toString();
                    String cat_id=uuid.toString();

                    HashMap<String,Object> mData=new HashMap<>();
                    mData.put("cat_name",cat_name);
                    mData.put("cat_id",cat_id);
                    mData.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Category").document(cat_id).set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(),"Kategori ekleme başarılı...",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void catGetData(){

        firebaseFirestore.collection("Category")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    catList.clear();
                    for (DocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String cat_id = (String) data.get("cat_id");
                        String cat_name = (String) data.get("cat_name");

                        Category category=new Category();
                        category.setCat_id(cat_id);
                        category.setCat_name(cat_name);

                        catList.add(category);

                        mAdapter.notifyDataSetChanged();

                    }
                    catListFull.addAll(catList);
                }else {
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}