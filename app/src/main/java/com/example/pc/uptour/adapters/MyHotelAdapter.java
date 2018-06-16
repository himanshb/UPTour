package com.example.pc.uptour.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pc.uptour.EntityDetailActivity;
import com.example.pc.uptour.classes.HotelDetails;
import com.example.pc.uptour.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.List;


public class MyHotelAdapter extends RecyclerView.Adapter<MyHotelAdapter.ViewHolder> {
    private List<HotelDetails> hotelList;
    private Context context;
    private GeoDataClient mGeoDataClient;

    public MyHotelAdapter(List<HotelDetails> hotelList, Context context) {
        this.hotelList = hotelList;
        this.context = context;
        mGeoDataClient = Places.getGeoDataClient(context,null);
    }

    @Override
    public MyHotelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list2_layout,parent,false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHotelAdapter.ViewHolder holder, int position) {
        //holder.setIsRecyclable(false);
        int adapterPos=holder.getAdapterPosition();
        if (adapterPos<0){
            adapterPos*=-1;
        }
        String hotelName = hotelList.get(adapterPos).getHotelName();
        holder.tv.setText(hotelName);

        if (hotelList.get(adapterPos).getHotelImg()==null){
            getPhotos(hotelList.get(adapterPos).getHotelPlaceID(), holder,adapterPos);
        }
        else
            holder.img.setImageBitmap(hotelList.get(adapterPos).getHotelImg());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EntityDetailActivity.class);
                intent.putExtra("place_id", hotelList.get(holder.getAdapterPosition()).getHotelPlaceID());
                intent.putExtra("place_type", "hotel");
                context.startActivity(intent);
            }
        });
    }

    private void getPhotos(String placeId,final MyHotelAdapter.ViewHolder holder,final int position ) {
        //final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener((Activity)context,new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                try{
                  PlacePhotoMetadataResponse photos = task.getResult();
                  // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                  PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                  // Get the first photo in the list.

              try {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                   // CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener((Activity)context, new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            holder.img.setImageBitmap(bitmap);
                            hotelList.get(position).setHotelImg(bitmap);
                        }
                    });
                    photoMetadataBuffer.release();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }}catch (Exception e)
                {
                    Log.d("info","API Exception");
                }
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
        ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.textView3);
            cardView=itemView.findViewById(R.id.card_view);
            img=itemView.findViewById(R.id.imageView4);
        }
    }
}
