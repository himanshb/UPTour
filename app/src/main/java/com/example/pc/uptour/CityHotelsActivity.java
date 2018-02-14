package com.example.pc.uptour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyHotelAdapter;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cityID=this.getIntent().getStringExtra("city_id");
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        hotelList=myDatabase.getHotels(cityID);
        if (hotelList!=null) {
            recyclerView=findViewById(R.id.recycler_view);
            adapter=new MyHotelAdapter(hotelList,this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "There are no hotels available right now", Toast.LENGTH_SHORT).show();
        }
    }

}
