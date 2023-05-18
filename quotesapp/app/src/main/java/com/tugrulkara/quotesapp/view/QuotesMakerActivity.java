package com.tugrulkara.quotesapp.view;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.tugrulkara.quotesapp.util.FileSaveHelper.isSdkHigherThan28;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.adapter.ImageListAdapter;
import com.tugrulkara.quotesapp.base.BaseActivity;
import com.tugrulkara.quotesapp.util.FileSaveHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class QuotesMakerActivity extends BaseActivity {

    private PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;
    private ImageView txt_select,btn_save,btn_camera,btn_gallery;
    private RecyclerView recyclerView;
    private ArrayList<String> array_image;
    private ImageListAdapter mAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar;
    private FileSaveHelper mSaveFileHelper;
    private Uri mSaveImageUri;
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_maker);

        firebaseFirestore=FirebaseFirestore.getInstance();

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        Picasso.get().load(R.drawable.image_1).into(mPhotoEditorView.getSource());
        //mPhotoEditorView.getSource().setImageResource(R.drawable.image_1);
        txt_select=findViewById(R.id.txtSelect);
        btn_save=findViewById(R.id.imgSave);
        btn_gallery=findViewById(R.id.imgGallery);
        //btn_camera=findViewById(R.id.imgCamera);
        array_image = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Alıntı Oluşturucu");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mSaveFileHelper = new FileSaveHelper(this);

        Intent intent=getIntent();
        String quote_text=intent.getStringExtra("info");

        //imageList();
        getImageList();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mAdapter=new ImageListAdapter(array_image);
        recyclerView.setAdapter(mAdapter);


        Typeface mTextTypeFace = Typeface.createFromAsset(getAssets(), "OpenSans-ExtraBold.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextTypeFace)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        txt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(QuotesMakerActivity.this);
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);
                    styleBuilder.withTextFont(mTextTypeFace);
                    mPhotoEditor.addText(inputText, styleBuilder);

                    //mTxtCurrentTool.setText(R.string.label_text);
                });

            }
        });

        try {
            if (quote_text!=null || !quote_text.matches("")){
                btn_gallery.setVisibility(View.GONE);
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(Color.WHITE);
                styleBuilder.withTextFont(mTextTypeFace);
                mPhotoEditor.addText(quote_text, styleBuilder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

       /* btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });*/

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
            }
        });

        mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {

                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.show(QuotesMakerActivity.this, text, colorCode);
                textEditorDialogFragment.setOnTextEditorListener((inputText, newColorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(newColorCode);

                    mPhotoEditor.editText(rootView, inputText, styleBuilder);
                    //mTxtCurrentTool.setText(R.string.label_text);
                });

            }

            @Override
            public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {

            }

            @Override
            public void onStopViewChangeListener(ViewType viewType) {

            }
        });

        mAdapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                //mPhotoEditorView.getSource().setImageResource(array_image.get(position));
                try {
                    Picasso.get().load(array_image.get(position)).into(mPhotoEditorView.getSource());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    private void getImageList(){

        firebaseFirestore.collection("QuoteImageList")
                .orderBy("popular", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    array_image.clear();

                    for (QueryDocumentSnapshot snapshot:task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String image=(String) data.get("image");
                        array_image.add(image);

                        mAdapter.notifyDataSetChanged();
                    }
                    //Picasso.get().load(array_image.get(0)).into(mPhotoEditorView.getSource());

                }else {
                    Toast.makeText(QuotesMakerActivity.this,"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void saveImage() {
        final String fileName = System.currentTimeMillis() + ".png";
        final boolean hasStoragePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        if (hasStoragePermission || isSdkHigherThan28()) {
            showLoading("Saving...");
            mSaveFileHelper.createFile(fileName, (fileCreated, filePath, error, uri) -> {
                if (fileCreated) {
                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build();

                    mPhotoEditor.saveAsFile(filePath, saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(getContentResolver());
                            hideLoading();
                            showSnackbar("Image Saved Successfully");
                            mSaveImageUri = uri;
                            mPhotoEditorView.getSource().setImageURI(mSaveImageUri);
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            hideLoading();
                            showSnackbar("Failed to save Image");
                        }
                    });

                } else {
                    hideLoading();
                    showSnackbar(error);
                }
            });
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}