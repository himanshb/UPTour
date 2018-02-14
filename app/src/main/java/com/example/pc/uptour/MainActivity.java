package com.example.pc.uptour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    protected GeoDataClient mGeoDataClient;

    protected ListView listView;
    DatabaseAccess myDatabase;
    MyCustomListAdapter mAdapter;
    List<CityDetails> cityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //creates all api which are required
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this,null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int REQUEST_CODE = 1;
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setCountry("IN")
                            .build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(MainActivity.this);
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myDatabase=new DatabaseAccess(this);
        myDatabase.open();
        cityList=myDatabase.getCities();
        listView=findViewById(R.id.listView);
        mAdapter=new MyCustomListAdapter(this,cityList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                Place place=PlaceAutocomplete.getPlace(this,data);
                String placeType=checkPlaceType(place);
                switch (placeType){
                    case "city":
                        String cityID=myDatabase.checkCityNameForInsert(place.getName().toString());
                        if (cityID!=null&&!cityID.equals("false")){
                            myDatabase.insertInCities(place.getId(),place.getName().toString());
                            String cityPlaceID=place.getId();
                            Intent intent=new Intent(this,CityActivity.class);
                            intent.putExtra("city_name",place.getName());
                            intent.putExtra("city_id",cityID);
                            intent.putExtra("city_place_id",cityPlaceID);
                            startActivity(intent);
                        }
                        if (cityID!=null&&cityID.equals("false")){
                            cityID=myDatabase.checkCityName(place.getName().toString());
                            if (cityID!=null){
                                String cityPlaceID=place.getId();
                                Intent intent=new Intent(this,CityActivity.class);
                                intent.putExtra("city_id",cityID);
                                intent.putExtra("city_place_id",cityPlaceID);
                                startActivity(intent);
                            }
                        }
                        if (cityID==null){
                            Toast.makeText(this, "Please select another city in UP", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "hotel":
                        String address=place.getAddress().toString();
                        String hotelCityID=myDatabase.checkSubString(address);
                        if (hotelCityID!=null){
                            boolean check=myDatabase.insertHotel(place.getId(),place.getName().toString(),hotelCityID);
                            if (check){
                                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "food":
                        address=place.getAddress().toString();
                        String foodCityID=myDatabase.checkSubString(address);
                        if (foodCityID!=null){
                            boolean check=myDatabase.insertFood(place.getId(),place.getName().toString(),foodCityID);
                            if (check){
                                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "place":
                        address=place.getAddress().toString();
                        String placeCityID=myDatabase.checkSubString(address);
                        if (placeCityID!=null){
                            boolean check=myDatabase.insertPlace(place.getId(),place.getName().toString(),placeCityID);
                            if (check){
                                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                //getDetailsByPlaceID(place.getId());
            }
            else if (resultCode==PlaceAutocomplete.RESULT_ERROR){
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatus()+"", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private String checkPlaceType(Place myPlace){
        List<Integer> placeTypes = myPlace.getPlaceTypes();
        for (int i = 0; i < placeTypes.size(); i++) {
            if (placeTypes.get(i) == Place.TYPE_LODGING) {
                return "hotel";
            }
            if (placeTypes.get(i)==Place.TYPE_LOCALITY){
                return "city";
            }
            if (placeTypes.get(i)==Place.TYPE_RESTAURANT||placeTypes.get(i)==Place.TYPE_FOOD){
                return "food";
            }
            if (placeTypes.get(i)==Place.TYPE_PLACE_OF_WORSHIP||placeTypes.get(i)==Place.TYPE_POINT_OF_INTEREST){
                return "place";
            }
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CityDetails city=cityList.get(i);
        String cityID=city.getCityID();
        String cityPlaceID=city.getCityPlaceID();
        Intent intent=new Intent(this,CityActivity.class);
        intent.putExtra("city_name",city.getCityName());
        intent.putExtra("city_id",cityID);
        intent.putExtra("city_place_id",cityPlaceID);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDatabase.close();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
