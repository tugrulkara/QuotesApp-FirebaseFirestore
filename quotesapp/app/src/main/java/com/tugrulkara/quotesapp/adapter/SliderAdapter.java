package com.tugrulkara.quotesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.model.Quote;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {


    private SliderAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public void setOnItemClickListener(SliderAdapter.OnItemClickListener listener){
        mListener=listener;
    }


    //private String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

    private int[] slider_images={R.drawable.slider_1,R.drawable.slider_2,R.drawable.slider_3};


    List<Quote> sliderList;

    public SliderAdapter(List<Quote> sliderList){

        this.sliderList = sliderList;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {

        viewHolder.text_continue.setVisibility(View.GONE);

        viewHolder.cardView.setBackgroundResource(slider_images[position % 3]);
        //viewHolder.cardView.setCardBackgroundColor(Color.parseColor(colors[position % 9]));
        viewHolder.textView.setText(sliderList.get(position).getQuote_txt()+"\n"+"-"+sliderList.get(position).getAuthor());

        if (viewHolder.textView.getText().toString().trim().length()>=180){

            viewHolder.text_continue.setVisibility(View.VISIBLE);

            viewHolder.text_continue.setText("Alıntıyı Görüntüle");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null){

                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position,v);
                    }
                }
            }
        });


    }

    @Override
    public int getCount() {
        return sliderList.size();
    }

    public class Holder extends  SliderViewAdapter.ViewHolder{

        TextView textView,text_continue;
        CardView cardView;

        public Holder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.slider_text_item);
            cardView=itemView.findViewById(R.id.slider_card_item_row);
            text_continue=itemView.findViewById(R.id.text_continue);

        }
    }

}