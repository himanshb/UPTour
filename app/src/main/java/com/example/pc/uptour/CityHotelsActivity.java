package com.example.pc.uptour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyHotelAdapter;
import com.example.pc.uptour.classes.HotelDetails;
import com.example.pc.uptour.database.DatabaseAccess;
import java.util.List;

public class CityHotelsActivity extends AppCompatActivity {

    String cityID;
    DatabaseAccess myDatabase;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<HotelDetails> hotelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hotels");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityID=this.getIntent().getStringExtra("city_id");
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        hotelList=myDatabase.getHotels(cityID);
        if (hotelList!=null) {
            recyclerView=findViewById(R.id.recycler_view);
            adapter=new MyHotelAdapter(hotelList,this);
            recyclerView.setAdapter(adapter);
            //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            Toast.makeText(this, "There are no hotels available right now", Toast.LENGTH_SHORT).show();
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
