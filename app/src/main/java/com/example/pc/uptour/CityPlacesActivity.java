package com.example.pc.uptour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyPlaceAdapter;
import com.example.pc.uptour.classes.PlaceDetails;
import com.example.pc.uptour.database.DatabaseAccess;

import java.util.List;

public class CityPlacesActivity extends AppCompatActivity {


    String cityID;
    DatabaseAccess myDatabase;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<PlaceDetails> placeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_places);

        getSupportActionBar().setTitle("Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityID=this.getIntent().getStringExtra("city_id");
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        placeList=myDatabase.getPlaces(cityID);
        if (placeList!=null) {
            recyclerView=findViewById(R.id.recycler_view_place);
            adapter=new MyPlaceAdapter(placeList,this);
            //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "There are no places available right now", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDatabase!=null){
            myDatabase.close();
        }
    }
}
