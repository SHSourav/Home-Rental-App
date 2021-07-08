package com.example.rentahome.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentahome.Interface.ItemClickListener;
import com.example.rentahome.R;


public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView historyaddress,historyRprice,historynumber;
    public Button deletePost;
    private ItemClickListener itemClickListner;


    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyaddress=(TextView) itemView.findViewById(R.id.user_post_address);
        historyRprice=(TextView)itemView.findViewById(R.id.user_post_renting_price);
        historynumber=(TextView)itemView.findViewById(R.id.user_post_mobile);
        deletePost=(Button)itemView.findViewById(R.id.user_post_dlt_btn);

    }

    @Override
    public void onClick(View v) {
        itemClickListner.onCliclk(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.itemClickListner=listener;
    }


}
