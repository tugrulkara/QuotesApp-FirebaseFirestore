package com.tugrulkara.quotesapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesapp.FilterQuotesActivity;
import com.tugrulkara.quotesapp.MainActivity;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.adapter.CategoryAdapter;
import com.tugrulkara.quotesapp.model.Category;

import java.util.ArrayList;
import java.util.Map;

public class CategoryFragment extends Fragment {

    private RecyclerView recycler_all_cat;
    private ArrayList<Category> catList;
    private CategoryAdapter mAdapter;

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

        firebaseFirestore=FirebaseFirestore.getInstance();
        catList=new ArrayList<>();

        catGetData();

        recycler_all_cat=view.findViewById(R.id.recycler_all_cat);
        recycler_all_cat.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter=new CategoryAdapter(catList);
        recycler_all_cat.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoCat",catList.get(position).getCat_name());
                startActivity(intent);
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

    //extract data from the firestore database
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

                        catList.add(category);

                        mAdapter.notifyDataSetChanged();

                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}