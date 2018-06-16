package com.example.pc.uptour.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pc.uptour.EntityDetailActivity;
import com.example.pc.uptour.R;
import com.example.pc.uptour.classes.NearByPlace;

import java.util.List;


public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {
    private Context context;
    private List<NearByPlace> nearByPlaceList;

    public NearByAdapter(Context context, List<NearByPlace> nearByPlaceList) {
        this.context = context;
        this.nearByPlaceList = nearByPlaceList;
    }

    @Override
    public NearByAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.card_nearby,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NearByAdapter.ViewHolder holder, int position) {
        NearByPlace place=nearByPlaceList.get(position);
        holder.tv.setText(place.getPlaceName());
        //holder.tv2.setText(place.getPlaceRating());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, EntityDetailActivity.class);
                intent.putExtra("place_id",nearByPlaceList.get(holder.getAdapterPosition()).getPlaceId());
                intent.putExtra("place_type",nearByPlaceList.get(holder.getAdapterPosition()).getPlaceType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearByPlaceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv,tv2;
        public ViewHolder(View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.textView2);
           // tv2=itemView.findViewById(R.id.textView4);
        }
    }
}
