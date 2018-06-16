package com.example.pc.uptour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.uptour.database.DatabaseAccess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class CityActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    CardView placeButton,hotelButton,foodButton,itemButton;
    ImageView img;
    String cityID,cityPlaceID;
    String cityName;
    //FloatingActionButton infoButton;
    CardView infoButton;
    AlertDialog.Builder dialogMsg;
    DatabaseAccess myDatabase;

    TextView cityNameTV;
    TextView cityDescription;


    protected GeoDataClient mGeoDataClient;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this,null);



        placeButton=findViewById(R.id.button);
        placeButton.setOnClickListener(this);
        hotelButton=findViewById(R.id.button2);
        hotelButton.setOnClickListener(this);
        foodButton=findViewById(R.id.button3);
        foodButton.setOnClickListener(this);
       // itemButton=findViewById(R.id.infoButton);
      //  itemButton.setOnClickListener(this);
        img=findViewById(R.id.imageView2);
       // infoButton=findViewById(R.id.infoButton);

        cityName=this.getIntent().getExtras().getString("city_name");
        cityID=this.getIntent().getStringExtra("city_id");
        cityPlaceID=this.getIntent().getStringExtra("city_place_id");
        if (cityPlaceID!=null){
            //getPlaceDetails(cityPlaceID);
            getPhotos(cityPlaceID);
        }
        getSupportActionBar().setTitle(cityName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //for setting Description

        cityNameTV=findViewById(R.id.cityName);
        cityDescription=findViewById(R.id.cityDescription);

        cityNameTV.setText(cityName);
        cityDescription.setText(getDescription(cityID));


        //Toast.makeText(this, cityID+" "+cityPlaceID, Toast.LENGTH_SHORT).show();

      /*  infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
   */ }



    private String getDescription(String cityID) {
        dialogMsg=new AlertDialog.Builder(this);
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        String description=myDatabase.getCityDescription(cityID);
        myDatabase.close();
        if (!description.equals("")) {
           /* dialogMsg.setTitle(myDatabase.getCityName(cityID));
            dialogMsg.setMessage(description);
            dialogMsg.setPositiveButton("Okay",null);
            dialogMsg.show();*/
           return description;
        }else
            return "Not Available";

    }

    @Override
    public void onClick(View view) {
        if (view==placeButton){
            Intent intent=new Intent(this,CityPlacesActivity.class);
            //Intent intent=new Intent(this,Googlectivity.class);
            intent.putExtra("city_id",cityID);
            intent.putExtra("place_type","place");
            intent.putExtra("city_name",cityName);
            startActivity(intent);
        }
        if (view==hotelButton){
            Intent intent=new Intent(this,CityHotelsActivity.class);
            //Intent intent=new Intent(this,Googlectivity.class);
            intent.putExtra("city_id",cityID);
            intent.putExtra("place_type","hotel");
            intent.putExtra("city_name",cityName);
            startActivity(intent);
        }
        if (view==foodButton){
            Intent intent=new Intent(this,CityFoodActivity.class);
            //Intent intent=new Intent(this,Googlectivity.class);
            intent.putExtra("city_id",cityID);
            intent.putExtra("place_type","food");
            intent.putExtra("city_name",cityName);
            startActivity(intent);
        }
        if (view==itemButton){
            getDescription(cityID);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    /*protected void getPlaceDetails(String placeID){
        mGeoDataClient.getPlaceById(placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    places.release();
                } else {
                    Toast.makeText(CityActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
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
                try {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    //CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(CityActivity.this, new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            img.setImageBitmap(photo.getBitmap());
                        }
                    });
                    photoMetadataBuffer.release();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDatabase!=null){
            myDatabase.close();
        }
    }

}
