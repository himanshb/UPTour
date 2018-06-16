package com.example.pc.uptour;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.uptour.classes.DeviceLocation;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EntityDetailActivity extends AppCompatActivity implements View.OnClickListener {

    String placeID;
    String address;
    String placeType;
    protected GeoDataClient mGeoDataClient;
    ImageView img;
    RatingBar ratingBar;
    ProgressDialog dialog;
    AlertDialog.Builder dialogRepot;
    FloatingActionButton floatingActionButton;
    Button commentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = findViewById(R.id.imageView3);
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading please wait...");
        dialog.setCancelable(false);
        dialog.show();

        dialogRepot = new AlertDialog.Builder(this);
        dialogRepot.setMessage("Do you want to report this place ?");
        dialogRepot.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PlaceReport report=PlaceReport.create(placeID,"Wrong place image");
                PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(EntityDetailActivity.this, null);
                mPlaceDetectionClient.reportDeviceAtPlace(report);
                Toast.makeText(EntityDetailActivity.this, "Report sent", Toast.LENGTH_SHORT).show();
            }
        });
        dialogRepot.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });



        //tv=findViewById(R.id.textView4);
        ratingBar = findViewById(R.id.ratingBar);
        Intent intent = this.getIntent();
        if (intent.hasExtra("place_id") && intent.hasExtra("place_type")) {
            placeID = intent.getExtras().getString("place_id");
            placeType = intent.getExtras().getString("place_type");
        }


        commentButton = findViewById(R.id.buttonComments);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentComments = new Intent(EntityDetailActivity.this, CommentsActivity.class);
                intentComments.putExtra("place_id", placeID);
                intentComments.putExtra("place_type", placeType);
                startActivity(intentComments);
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            commentButton.setVisibility(View.INVISIBLE);
        }

        floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton.bringToFront();
        floatingActionButton.setOnClickListener(this);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        getPlaceDetails(placeID);

    }

    protected void getPlaceDetails(final String placeID) {
        mGeoDataClient.getPlaceById(placeID).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EntityDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
        mGeoDataClient.getPlaceById(placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    try {
                        PlaceBufferResponse places = task.getResult();
                        Place myPlace = places.get(0);
                        address = myPlace.getAddress().toString();
                        getPhotos(placeID);
                        ratingBar.setRating(myPlace.getRating());
                        /*if (placeType.equals("hotel")){
                            TextView addrText=findViewById(R.id.addressText);
                            int price=myPlace.getPriceLevel();
                            addrText.setText("Price : "+price);
                        }*/
                        TextView nameTextView = findViewById(R.id.nameTextView);
                        TextView addressTextView = findViewById(R.id.addressTextView);
                        final TextView phoneTextView = findViewById(R.id.phoneTextView);
                        phoneTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phoneTextView.getText().toString()));
                                if (ActivityCompat.checkSelfPermission(EntityDetailActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                EntityDetailActivity.this.startActivity(callIntent);
                            }
                        });
                        //String detail=myPlace.getName().toString()+"\n"+myPlace.getAddress().toString()+"\n"+myPlace.getPhoneNumber().toString();
                        //tv.setText(detail);
                        nameTextView.setText(myPlace.getName().toString());
                        addressTextView.setText(myPlace.getAddress().toString());
                        phoneTextView.setText(myPlace.getPhoneNumber().toString());
                        //places.release();
                    }
                    catch (IllegalStateException e){
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(EntityDetailActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(String placeId) {
        //final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(this,new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                try {/*
                    int size=photoMetadataBuffer.getCount();
                    //for loop will be used for viewPager in future.
                    for (int i=0;i<size;i++){
                    }*/
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);

                    // Get the attribution text.
                    //CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(EntityDetailActivity.this, new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            img.setImageBitmap(photo.getBitmap());
                        }
                    });
                    photoMetadataBuffer.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (DeviceLocation.lng!=0) {
            String uri = "https://www.google.com/maps/dir/?api=1&origin=" + DeviceLocation.lat+","+DeviceLocation.lng + "&destination="+address;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
        /*if (DeviceLocation.lng!=0)
            Toast.makeText(this, DeviceLocation.lat+","+DeviceLocation.lng, Toast.LENGTH_SHORT).show();
         Toast.makeText(this, "Thanks for rating ^_^", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.report:
                dialogRepot.show();
                break;
        }
        return true;
    }
}

