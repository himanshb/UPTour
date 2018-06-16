package com.example.pc.uptour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.pc.uptour.adapters.MyFoodAdapter;
import com.example.pc.uptour.classes.FoodDetails;
import com.example.pc.uptour.database.DatabaseAccess;

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

        getSupportActionBar().setTitle("Food");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityID=this.getIntent().getStringExtra("city_id");
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        foodList=myDatabase.getFood(cityID);

        if (foodList!=null) {
            recyclerView=findViewById(R.id.recycler_view_food);
            adapter=new MyFoodAdapter(foodList,this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
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
