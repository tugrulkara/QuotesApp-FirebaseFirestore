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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesadmin.MainActivity;
import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.adapter.AuthorsAdapter;
import com.tugrulkara.quotesadmin.model.Author;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AuthorsFragment extends Fragment {

    private RecyclerView recycler_all_author;
    private ArrayList<Author> authList;
    private List<Author> authListFull;
    private AuthorsAdapter mAdapter;

    private FloatingActionButton auth_add;

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

        recycler_all_author.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter=new AuthorsAdapter(authList,authListFull);
        recycler_all_author.setAdapter(mAdapter);


        auth_add=view.findViewById(R.id.auth_add);
        auth_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAuth(v);
            }
        });

        mAdapter.setOnItemClickListener(new AuthorsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                QuotesFragment quotesFragment=AuthorsFragment.newInstance(authList.get(position).getAuth_name());
                assert getFragmentManager() != null;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(authList.get(position).getAuth_name());
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Container,quotesFragment,"Yazarlar-Kişiler");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void AddAuth(View view){

        final android.app.Dialog dialog = new android.app.Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText auth_txt =dialog.findViewById(R.id.item_txt);
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

                if (auth_txt.getText().toString().trim().length()<=0){
                    Toast.makeText(getActivity().getApplicationContext(),"Lütfen bir yazar ismi giriniz...",Toast.LENGTH_SHORT).show();
                }else{

                    UUID uuid=UUID.randomUUID();
                    String auth_name=auth_txt.getText().toString();
                    String auth_id=uuid.toString();

                    HashMap<String,Object> mData=new HashMap<>();
                    mData.put("auth_name",auth_name);
                    mData.put("auth_id",auth_id);
                    mData.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Author").document(auth_id).set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(),"Yazar ekleme başarılı...",Toast.LENGTH_SHORT).show();

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

    private void authGetData(){

        firebaseFirestore.collection("Author")
                .orderBy("auth_name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){
                    authList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()){

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
                    Toast.makeText(getActivity(),"Veriler Yüklenemedi",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}