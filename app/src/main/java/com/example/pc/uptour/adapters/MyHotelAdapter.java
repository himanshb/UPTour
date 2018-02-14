package com.example.pc.uptour.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.pc.uptour.EntityDetailActivity;
import com.example.pc.uptour.HotelDetails;
import com.example.pc.uptour.R;

import java.util.List;

/**
 * Created by PC on 2/12/2018.
 */

public class MyHotelAdapter extends RecyclerView.Adapter<MyHotelAdapter.ViewHolder> {
    List<HotelDetails> hotelList;
    Context context;

    public MyHotelAdapter(List<HotelDetails> hotelList, Context context) {
        this.hotelList = hotelList;
        this.context = context;
    }

    @Override
    public MyHotelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list2_layout,parent,false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHotelAdapter.ViewHolder holder, int position) {
        String hotelName=hotelList.get(position).getHotelName();
        holder.tv.setText(hotelName);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, EntityDetailActivity.class);
                intent.putExtra("hotel_place_id",hotelList.get(holder.getAdapterPosition()).getHotelPlaceID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.textView3);
            cardView=itemView.findViewById(R.id.card_view);
        }
    }
}
