package com.example.pc.uptour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyFoodAdapter;
import com.example.pc.uptour.adapters.MyPlaceAdapter;

import java.util.List;

public class CityFoodActivity extends AppCompatActivity {

    String cityID;
    DatabaseAccess myDatabase;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<FoodDetails> foodList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_food);

        cityID=this.getIntent().getStringExtra("city_id");
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        foodList=myDatabase.getFood(cityID);
        if (foodList!=null) {
            recyclerView=findViewById(R.id.recycler_view_food);
            adapter=new MyFoodAdapter(foodList,this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "There are no places available right now", Toast.LENGTH_SHORT).show();
        }
    }
}
