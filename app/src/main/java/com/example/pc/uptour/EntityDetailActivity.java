package com.example.pc.uptour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

public class EntityDetailActivity extends AppCompatActivity {

    String placeID;
    protected GeoDataClient mGeoDataClient;
    ImageView img;
    TextView tv;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_detail);
        img=findViewById(R.id.imageView3);
        //tv=findViewById(R.id.textView4);
        ratingBar=findViewById(R.id.ratingBar);
        Intent intent=this.getIntent();
        if (intent.hasExtra("hotel_place_id"))
            placeID=intent.getExtras().getString("hotel_place_id");
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this,null);
        getPlaceDetails(placeID);

    }

    protected void getPlaceDetails(final String placeID){
        mGeoDataClient.getPlaceById(placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    getPhotos(placeID);
                    ratingBar.setRating(myPlace.getRating());
                    TextView nameTextView=findViewById(R.id.nameTextView);
                    TextView addressTextView= findViewById(R.id.addressTextView);
                    TextView phoneTextView=findViewById(R.id.phoneTextView);

                    //String detail=myPlace.getName().toString()+"\n"+myPlace.getAddress().toString()+"\n"+myPlace.getPhoneNumber().toString();
                    //tv.setText(detail);

                    nameTextView.setText(myPlace.getName().toString());
                    addressTextView.setText( myPlace.getAddress().toString());
                    phoneTextView.setText(myPlace.getPhoneNumber().toString());

                    places.release();
                } else {
                    Toast.makeText(EntityDetailActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(String placeId) {
        //final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        if (photo!=null) {
                            Bitmap bitmap = photo.getBitmap();
                            img.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });
    }
}
