package com.tugrulkara.quotesapp.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.tugrulkara.quotesapp.adapter.AuthorsAdapter;
import com.tugrulkara.quotesapp.model.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AuthorsFragment extends Fragment {

    private RecyclerView recycler_all_author;
    private ArrayList<Author> authList;
    private List<Author> authListFull;
    private AuthorsAdapter mAdapter;

    FirebaseFirestore firebaseFirestore;


    public AuthorsFragment() {
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
        return inflater.inflate(R.layout.fragment_authors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);


        firebaseFirestore=FirebaseFirestore.getInstance();
        authList=new ArrayList<>();
        authListFull=new ArrayList<>();

        authGetData();

        recycler_all_author=view.findViewById(R.id.recycler_all_author);
        recycler_all_author.setHasFixedSize(true);
        recycler_all_author.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter=new AuthorsAdapter(authList,authListFull);
        recycler_all_author.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AuthorsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(getActivity(), FilterQuotesActivity.class);
                intent.putExtra("infoAuth",authList.get(position).getAuth_name());
                startActivity(intent);
            }
        });

    }

    public static QuotesFragment newInstance(String data) {
        QuotesFragment quotesFragment = new QuotesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoAuth", data);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Bir yazar arayın...");

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


    private void authGetData(){

        firebaseFirestore.collection("Author")
                .orderBy("auth_name", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    authList.clear();
                    authListFull.clear();

                    for (QueryDocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String auth_id = (String) data.get("auth_id");
                        String auth_name = (String) data.get("auth_name");

                        Author author=new Author();
                        author.setAuth_id(auth_id);
                        author.setAuth_name(auth_name);

                        authList.add(author);

                        mAdapter.notifyDataSetChanged();

                    }
                    authListFull.addAll(authList);
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}