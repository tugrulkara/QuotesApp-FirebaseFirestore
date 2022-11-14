package com.tugrulkara.quotesapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.tugrulkara.quotesapp.util.Share;
import com.tugrulkara.quotesapp.util.Snackbar;

import java.util.Random;

public class SliderDetailActivity extends AppCompatActivity {

    private TextView text_quote_slider,text_author_slider;
    private ImageView image_quote_maker_slider,image_share_slider,image_copy_to_clipboard_slider;
    private CardView cardView;
    private Toolbar toolbar;

    private String id;

    private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_detail);

        text_quote_slider=findViewById(R.id.text_quote_slider);
        text_author_slider=findViewById(R.id.text_author_slider);
        image_quote_maker_slider=findViewById(R.id.image_quote_maker_slider);
        image_share_slider=findViewById(R.id.image_share_slider);
        image_copy_to_clipboard_slider=findViewById(R.id.image_copy_to_clipboard_slider);
        cardView=findViewById(R.id.card_quote_home_slider);

        toolbar = findViewById(R.id.toolbar);

        Random random=new Random();
        int x = random.nextInt(8);
        cardView.setCardBackgroundColor(Color.parseColor(colors[x]));

        Intent intent=getIntent();
        String quote=intent.getStringExtra("quote");
        String auth=intent.getStringExtra("auth");
        id=intent.getStringExtra("id");

        toolbar.setTitle(auth);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        text_quote_slider.setText(quote);
        text_author_slider.setText(auth);


        text_quote_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SliderDetailActivity.this, QuotesMakerActivity.class);
                intent.putExtra("info",quote+"\n"+"\n"+"-"+auth);
                startActivity(intent);

            }
        });

        image_quote_maker_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SliderDetailActivity.this, QuotesMakerActivity.class);
                intent.putExtra("info",quote+"\n"+"\n"+"-"+auth);
                startActivity(intent);

            }
        });


        image_copy_to_clipboard_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.copyToClipboard(SliderDetailActivity.this, quote);
                Snackbar.showText(v, R.string.copy_to_clipboard);
            }
        });

        image_share_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.withText(SliderDetailActivity.this, quote);
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

}