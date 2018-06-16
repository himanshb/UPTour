package com.example.pc.uptour;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pc.uptour.adapters.NearByAdapter;
import com.example.pc.uptour.classes.DeviceLocation;
import com.example.pc.uptour.classes.NearByPlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ DeviceLocation.lat+
            ","+DeviceLocation.lng+"&radius=500&types=";
    private final String key="&key=AIzaSyCgPIRwbTGC_rNFeyqAB1ztaufqe8aG7Js";
    private String type,placeType;
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.recycler_view);

        appBarLayout=findViewById(R.id.app_bar);
        type=this.getIntent().getExtras().getString("type");
        placeType=this.getIntent().getExtras().getString("place_type");

        url=url+type+key;
        if (type.equals("lodging")){
            appBarLayout.setBackground(this.getDrawable(R.drawable.hotel));
            getSupportActionBar().setTitle("Hotels");
        }
        if (type.equals("point_of_interest")){
            appBarLayout.setBackground(this.getDrawable(R.drawable.point_of_interest));
            getSupportActionBar().setTitle("Places Nearby");
        }

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request=new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<NearByPlace> nearByPlaces=new ArrayList<>();
                    JSONArray jsonArray=response.getJSONArray("results");
                    /*Toast.makeText(ScrollingActivity.this, ""+jsonArray.length(), Toast.LENGTH_SHORT).show();*/
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject obj=jsonArray.getJSONObject(i);
                        String placeName=obj.getString("name");
                        String placeId=obj.getString("place_id");
                        NearByPlace place=new NearByPlace(placeName,placeId);
                        place.setPlaceType(placeType);
                        nearByPlaces.add(place);
                    }

                    //setting the adapter
                    adapter=new NearByAdapter(ScrollingActivity.this,nearByPlaces);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ScrollingActivity.this));
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
