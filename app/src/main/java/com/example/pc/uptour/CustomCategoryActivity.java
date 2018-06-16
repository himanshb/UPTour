package com.example.pc.uptour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pc.uptour.adapters.MyCustomListAdapter;
import com.example.pc.uptour.classes.CityDetails;
import com.example.pc.uptour.database.DatabaseAccess;

import java.util.List;

public class CustomCategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<CityDetails> newList;
    DatabaseAccess myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_category);

        ListView customCategoryList=findViewById(R.id.customCategoryList);

        myDatabase=new DatabaseAccess(CustomCategoryActivity.this);
        myDatabase.open();

        Intent intent=getIntent();
        String tag=intent.getExtras().getString("tag");
        getSupportActionBar().setTitle(tag.toUpperCase()+" CITIES");
        newList=myDatabase.getCitiesByTags(tag);
         if (newList!=null) {
             MyCustomListAdapter mAdapter;
            mAdapter = new MyCustomListAdapter(CustomCategoryActivity.this, newList);
            customCategoryList.setAdapter(mAdapter);
       }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        customCategoryList.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CityDetails city=newList.get(i);
        String cityID=city.getCityID();
        String cityPlaceID=city.getCityPlaceID();
        Intent intent=new Intent(this,CityActivity.class);
        intent.putExtra("city_name",city.getCityName());
        intent.putExtra("city_id",cityID);
        intent.putExtra("city_place_id",cityPlaceID);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
