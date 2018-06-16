package com.example.pc.uptour;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.pc.uptour.Singleton.MySingleton;
import com.example.pc.uptour.adapters.GooglePlacesAutocompleteAdapter;
import com.example.pc.uptour.adapters.MyCustomListAdapter;
import com.example.pc.uptour.adapters.ViewPagerAdapter;
import com.example.pc.uptour.classes.CityDetails;
import com.example.pc.uptour.classes.DeviceLocation;
import com.example.pc.uptour.classes.PlaceAutocompleteDetail;
import com.example.pc.uptour.database.DatabaseAccess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public GoogleApiClient mGoogleApiClient;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    String address;
    private LocationManager locationManager;
    private LocationListener listener;

    Menu menu;
    FloatingActionButton fab;

    protected ListView listView;
    DatabaseAccess myDatabase;
    MyCustomListAdapter mAdapter;
    List<CityDetails> cityList;

    public String userName;
    //public String userId;
    public String userEmail;
    public String photoURL;

    FirebaseUser user;

    //for ViewPager Adapter
    //for viewpager
    private ViewPager viewPager;
    private LinearLayout sliderDots;
    private int dotsCount;
    private ImageView[] dots;

   /* List of Districts : Constants*/

    private TextView districtButton;
    private LinearLayout districtLinearList;
    private ListView districtListView;
    View layoutView;

    CardView business,wildlife,pilgrimage,industry;
    CardView placeTVMain,foodTVMain,hotelsTVMain;

    @Override
    public void onClick(View view) {
        //String tag= (String) view.getTag();
        Intent intent;
        switch (view.getId()) {
            case R.id.business:
              intent=new Intent(this,CustomCategoryActivity.class);
               intent.putExtra("tag","business");
               startActivity(intent);
                break;
            case R.id.wildlife:
                intent=new Intent(this,CustomCategoryActivity.class);
                intent.putExtra("tag","wildlife");
                startActivity(intent); break;
            case R.id.pilgrimage:
                intent=new Intent(this,CustomCategoryActivity.class);
                intent.putExtra("tag","pilgrimage");
                startActivity(intent);break;
            case R.id.Industry:
                intent=new Intent(this,CustomCategoryActivity.class);
                intent.putExtra("tag","industry");
                startActivity(intent); break;

            default:
                break;

        }
    }

    //timer task for viewpager to auto slide images
    public class MyTimerTask extends TimerTask
    {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (viewPager.getCurrentItem()==0)
                    {
                        viewPager.setCurrentItem(1);
                    }else if (viewPager.getCurrentItem()==dotsCount-1)
                    {
                        viewPager.setCurrentItem(0);
                    }else
                    {
                        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Open the Database
        myDatabase=new DatabaseAccess(this);
        myDatabase.open();

        business=findViewById(R.id.business);
        wildlife=findViewById(R.id.wildlife);
        pilgrimage=findViewById(R.id.pilgrimage);
        industry=findViewById(R.id.Industry);
        placeTVMain=findViewById(R.id.placesTVMain);
        foodTVMain=findViewById(R.id.foodTVMain);
        hotelsTVMain=findViewById(R.id.hotelTVMain);
        //infoTVMain=findViewById(R.id.InfoTVMain);

        business.setOnClickListener(this);
        wildlife.setOnClickListener(this);
        pilgrimage.setOnClickListener(this);
        industry.setOnClickListener(this);

        //get current address of client through volley request
        locationManager= (LocationManager) this.getSystemService(LOCATION_SERVICE);
        //requestQueue= Volley.newRequestQueue(this);
        
        //creates all api which are required
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this,null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
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
                       // Associate searchable configuration with the SearchView


                }catch (Exception e){}
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        final TextView userNameTV=hView.findViewById(R.id.userName);
        final TextView userEmailTV=hView.findViewById(R.id.userEmail);
        final ImageView userImage=hView.findViewById(R.id.myUserImage);

        //for reading the current user details
        user=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef= database.getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                           try {
                                               userName = dataSnapshot.child(user.getUid()).child("userName").getValue(String.class);
                                               userEmail = dataSnapshot.child(user.getUid()).child("userEmail").getValue(String.class);
                                               photoURL= dataSnapshot.child(user.getUid()).child("photoUrl").getValue(String.class);
/*

                                               Toast.makeText(MainActivity.this, "From Firebase: "+userName, Toast.LENGTH_SHORT).show();
                                               Toast.makeText(MainActivity.this, "From Firebase: "+userEmail, Toast.LENGTH_SHORT).show();
                                               Toast.makeText(MainActivity.this, "From Firebase: "+photoURL, Toast.LENGTH_SHORT).show();
*/


                                               userNameTV.setText(userName);
                                               userEmailTV.setText(userEmail);

                                               Glide.with(MainActivity.this)
                                                       .load(photoURL)
                                                       .override(400, 400) // resizes the image to 100x200 pixels but does not respect aspect ratio
                                                       .into(userImage);

                                           }catch (Exception ignored)
                                           {

                                           }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });



        //for implementation of View Pager
        viewPager=findViewById(R.id.myViewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.ReturnMonth();
        int month=viewPagerAdapter.getMonth();
        if (month!=0)
        {
            Integer [] images;
            images= new Integer[]{R.drawable.tajahal, R.drawable.brihadeeswarartemple, R.drawable.park};
            if (month==2)
                images=new Integer[]{R.drawable.tajahal,
                        R.drawable.month22,
                        R.drawable.month23,
                        R.drawable.month32,
                        R.drawable.brihadeeswarartemple};
            else if (month==3)
                images=new Integer[]
                        {
                                R.drawable.month31,
                                R.drawable.month32,
                                R.drawable.month33
                        };
            if (month==4)
                images=new Integer[]{
                        R.drawable.month41,
                        R.drawable.month42};
            if (month==6)
                images=new Integer[]{R.drawable.month61,
                        R.drawable.month62,
                };
            if (month==8)
                images=new Integer[]{
                        R.drawable.month81,
                        R.drawable.month82,
                        R.drawable.month83,
                };
            if (month==10)
                images=new Integer[]{R.drawable.month101,
                        R.drawable.month102,
                        R.drawable.month103,
                        R.drawable.month104,
                        R.drawable.month105,
                        R.drawable.month106
                };
            if (month==11)
                images=new Integer[]{R.drawable.month111,
                        R.drawable.month112,
                        R.drawable.month113,
                        R.drawable.month114,
                        R.drawable.month115
                };

            viewPagerAdapter.setImages(images);
            viewPagerAdapter.notifyDataSetChanged();
        }

        sliderDots=findViewById(R.id.sliderDots);
        dotsCount=viewPagerAdapter.getCount();
        dots=new ImageView[dotsCount];

        for (int i=0;i<dotsCount;i++)
        {
            dots[i]= new ImageView(MainActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDots.addView(dots[i],params);

        }



        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i=0;i<dotsCount;i++)
                {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);

        fab.setVisibility(View.INVISIBLE);

        LayoutInflater layoutInflater=getLayoutInflater();
        layoutView= layoutInflater.inflate(R.layout.custom_list_layout,null);
        districtListView=layoutView.findViewById(R.id.customDistrictList);
        myDatabase=new DatabaseAccess(MainActivity.this);
        myDatabase.open();
        cityList=myDatabase.getCities();
        districtLinearList=findViewById(R.id.displayLinearList);
        if (cityList!=null) {
            mAdapter = new MyCustomListAdapter(MainActivity.this, cityList);
            //mAdapter= new MyCustomListAdapter(MainActivity.this,R.layout.my_custom_list_item);
            districtListView.setAdapter(mAdapter);
            // Toast.makeText(this, "city List:"+ cityList.toString(), Toast.LENGTH_SHORT).show();
        }
//        listView.setOnItemClickListener(this);


       /* for displaying the list of districts*/

        districtButton=findViewById(R.id.districtButton);

        TextView customTextView= layoutView.findViewById(R.id.customTextView);

        customTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_down_animation);
                animation.setStartOffset(0);
                districtLinearList.setVisibility(View.INVISIBLE);
                districtLinearList.startAnimation(animation);
                //districtLinearList.removeView(layoutView);
                districtButton.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        });

        districtListView.setOnItemClickListener(this);

        districtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.VISIBLE);
                districtLinearList.removeAllViews();
                districtLinearList.addView(layoutView);
                districtLinearList.setVisibility(View.VISIBLE);
                districtButton.setVisibility(View.INVISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_up_animation);
                animation.setStartOffset(0);
                districtLinearList.startAnimation(animation);

            }
        });
       /* cityList=myDatabase.getCities();
        listView=findViewById(R.id.listView);
        mAdapter=new MyCustomListAdapter(this,cityList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this);*/
       CardView locationCard=findViewById(R.id.locationbased);
       locationCard.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (DeviceLocation.lat==0){
                   Toast.makeText(MainActivity.this, "GPS is fetching location!!", Toast.LENGTH_SHORT).show();
                   getLocation();
               }
           }
       });
    }




    //get the current nearby_location of client
    private void getLocation() {
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#reques if (Build.VERSION.SDK_INT < 23) {tPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 9999, listener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    Toast.makeText(this, "Fetching Location please wait...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat=location.getLatitude();
                double lng=location.getLongitude();
                DeviceLocation.lat=lat;
                DeviceLocation.lng=lng;
                String latlng=Double.toString(lat)+","+Double.toString(lng);
                Toast.makeText(MainActivity.this, latlng, Toast.LENGTH_SHORT).show();
                request(latlng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, "GPS ENABLED..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    //volley request
    private void request(String latlng) {
        String url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng+"&key=AIzaSyDGkg0JTUKZ5kdZNYwORdxiC4Vn6WjSMEo";
        JsonObjectRequest request=new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    address=response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    final String cityID=myDatabase.checkSubString(address);
                    if (cityID!=null){
                        final String cityPlaceID=myDatabase.getCityPlaceID(cityID);
                        final String cityName=myDatabase.getCityName(cityID);
                        TextView tv=MainActivity.this.findViewById(R.id.textView4);
                        tv.setText(cityName);
                        placeTVMain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(MainActivity.this,CityPlacesActivity.class);
                                //Intent intent=new Intent(this,Googlectivity.class);
                                intent.putExtra("city_id",cityID);
                                intent.putExtra("place_type","place");
                                intent.putExtra("city_name",cityName);
                                startActivity(intent);
                            }
                        });
                        foodTVMain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(MainActivity.this,CityFoodActivity.class);
                                //Intent intent=new Intent(this,Googlectivity.class);
                                intent.putExtra("city_id",cityID);
                                intent.putExtra("place_type","food");
                                intent.putExtra("city_name",cityName);
                                startActivity(intent);
                            }
                        });
                        hotelsTVMain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(MainActivity.this,CityHotelsActivity.class);
                                //Intent intent=new Intent(this,Googlectivity.class);
                                intent.putExtra("city_id",cityID);
                                intent.putExtra("place_type","hotel");
                                intent.putExtra("city_name",cityName);
                                startActivity(intent);
                            }
                        });
                      //  infoTVMain.setOnClickListener(MainActivity.this);

                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent=new Intent(MainActivity.this,CityActivity.class);
                        intent.putExtra("city_name",cityName);
                        intent.putExtra("city_id",cityID);
                        intent.putExtra("city_place_id",cityPlaceID);
                        startActivity(intent);
                            }
                        });
                    }
                    else
                        Toast.makeText(MainActivity.this, "city id is null", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
        //requestQueue.add(request);
    }

    private void getPlaceDistrict(String placeID, final Place place){
        String url="https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key=AIzaSyCV2jT3tT36I6OJuuymROZiYvplNTpMrX8";
        JsonObjectRequest request=new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray=response.getJSONObject("result").getJSONArray("address_components");
                    for (int i=0;i<jsonArray.length();i++){
                        if (jsonArray.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_2")){
                            String district=jsonArray.getJSONObject(i).getString("long_name");
                            //Toast.makeText(MainActivity.this, district, Toast.LENGTH_SHORT).show();
                            hugabuga(place,district);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                Place place= PlaceAutocomplete.getPlace(this,data);
               // Toast.makeText(this, place.getId(), Toast.LENGTH_SHORT).show();
                getPlaceDistrict(place.getId(),place);
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
        if (requestCode==2){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Intent intent=new Intent(this,EntityDetailActivity.class);
                intent.putExtra("place_id",place.getId());
                intent.putExtra("place_type",checkPlaceType(place));
                startActivity(intent);
            }
        }
    }
    //this is the main function in which place type s decided and corresponding action is performed
    private void hugabuga(Place place,String district){
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
                        intent.putExtra("city_name",place.getName().toString());
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
                String h_id=myDatabase.checkCityName(district);
                if (hotelCityID!=null||h_id!=null){
                    if (h_id!=null)
                        hotelCityID=h_id;
                    boolean check=myDatabase.insertHotel(place.getId(),place.getName().toString(),hotelCityID);
                    if (check){
                        Intent intent=new Intent(this,EntityDetailActivity.class);
                        intent.putExtra("place_id",place.getId());
                        intent.putExtra("place_type","hotel");
                        startActivity(intent);
                        Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent=new Intent(this,EntityDetailActivity.class);
                        intent.putExtra("place_id",place.getId());
                        intent.putExtra("place_type","hotel");
                        startActivity(intent);
                        //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "food":
                address=place.getAddress().toString();
                String foodCityID=myDatabase.checkSubString(address);
                String f_id=myDatabase.checkCityName(district);
                if (foodCityID!=null||f_id!=null){
                    if (f_id!=null)
                        foodCityID=f_id;
                    boolean check=myDatabase.insertFood(place.getId(),place.getName().toString(),foodCityID);
                    if (check){
                        Intent intent=new Intent(this,EntityDetailActivity.class);
                        intent.putExtra("place_id",place.getId());
                        intent.putExtra("place_type","food");
                        startActivity(intent);
                        Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Intent intent=new Intent(this,EntityDetailActivity.class);
                        intent.putExtra("place_id",place.getId());
                        intent.putExtra("place_type","food");
                        startActivity(intent);
                        //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "place":
                address=place.getAddress().toString();
                String placeCityID=myDatabase.checkSubString(address);
                String p_id=myDatabase.checkCityName(district);
                if (placeCityID!=null||p_id!=null){
                    if (p_id!=null)
                        placeCityID=p_id;
                    boolean check=myDatabase.insertPlace(place.getId(),place.getName().toString(),placeCityID);
                    if (check){
                        Intent intent=new Intent(this,EntityDetailActivity.class);
                        intent.putExtra("place_id",place.getId());
                        intent.putExtra("place_type","place");
                        startActivity(intent);
                        Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(this, EntityDetailActivity.class);
                        intent.putExtra("place_id", place.getId());
                        intent.putExtra("place_type","place");
                        startActivity(intent);
                        //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case "sublocality":
                address=place.getAddress().toString();
                //String placeID=myDatabase.checkSubString(address);
                if (address.contains("Uttar Pradesh")){
                    Intent intent = new Intent(this, EntityDetailActivity.class);
                    intent.putExtra("place_id", place.getId());
                    intent.putExtra("place_type", "extras");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "please search for tings in UP", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                address=place.getAddress().toString();
                //String placeID=myDatabase.checkSubString(address);
                if (address.contains("Uttar Pradesh")){
                    Intent intent = new Intent(this, EntityDetailActivity.class);
                    intent.putExtra("place_id", place.getId());
                    intent.putExtra("place_type", "extras");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "please search for tings in UP", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @NonNull
    private String checkPlaceType(Place myPlace){
        List<Integer> placeTypes = myPlace.getPlaceTypes();
        for (int i = 0; i < placeTypes.size(); i++) {
            if (placeTypes.get(i) == Place.TYPE_LODGING) {
                return "hotel";
            }
            if (placeTypes.get(i)==Place.TYPE_LOCALITY||placeTypes.get(i)==Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_2){
                return "city";
            }
            if (placeTypes.get(i)==Place.TYPE_SUBLOCALITY||placeTypes.get(i)==Place.TYPE_SUBLOCALITY_LEVEL_1||
                    placeTypes.get(i)==Place.TYPE_SUBLOCALITY_LEVEL_2||placeTypes.get(i)==Place.TYPE_SUBLOCALITY_LEVEL_3
                    ||placeTypes.get(i)==Place.TYPE_SUBLOCALITY_LEVEL_4||placeTypes.get(i)==Place.TYPE_SUBLOCALITY_LEVEL_5){
                return "sublocality";
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(districtButton.getVisibility()==View.INVISIBLE){
            districtLinearList.setVisibility(View.INVISIBLE);
            //districtLinearList.removeView(layoutView);
            districtButton.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_down_animation);
            animation.setStartOffset(0);
            districtLinearList.startAnimation(animation);
        }
        else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_nearby) {
            int PLACE_PICKER_REQUEST = 2;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_location) {
            if (DeviceLocation.lat!=0){
                String latlng=Double.toString(DeviceLocation.lat)+","+Double.toString(DeviceLocation.lng);
                request(latlng);
            }
            else {
                getLocation();
            }
        }
        else if (id == R.id.nav_places) {
            Intent intent=new Intent(this,ScrollingActivity.class);
            intent.putExtra("type","point_of_interest");
            intent.putExtra("place_type","places");
            startActivity(intent);
        }

        else if (id == R.id.nav_hotels) {
            Intent intent=new Intent(this,ScrollingActivity.class);
            intent.putExtra("type","lodging");
            intent.putExtra("place_type","hotels");
            startActivity(intent);
        }

        else if (id == R.id.nav_sign_out) {
            if (user!=null) {
                final AlertDialog.Builder alBuilder=new AlertDialog.Builder(this);
                alBuilder.setMessage("Do you want to sign out");
                alBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                });
                alBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alBuilder.show();
            }
            else {
                Toast.makeText(this, "Sign In first!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,LoginActivity.class));
            }
        }
        else if (id == R.id.nav_share) {
            Intent intent=new Intent(this,AboutUsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Operations when an List item is selected.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CityDetails city = cityList.get(i);
            String cityID = city.getCityID();
            String cityPlaceID = city.getCityPlaceID();
            Intent intent = new Intent(this, CityActivity.class);
            intent.putExtra("city_name", city.getCityName());
            intent.putExtra("city_id", cityID);
            intent.putExtra("city_place_id", cityPlaceID);
            startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDatabase!=null)
            myDatabase.close();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //For SearchView
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       // Inflate the search menu action bar.
       MenuInflater menuInflater = getMenuInflater();
       menuInflater.inflate(R.menu.menu, menu);

       // Get the search menu.
       MenuItem searchMenu = menu.findItem(R.id.app_bar_menu_search);

       // Get SearchView object.
       SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
       // Get SearchView autocomplete object.
       final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
       // Create a new ArrayAdapter
       searchAutoComplete.setAdapter(new GooglePlacesAutocompleteAdapter(this,R.layout.auto_complete_layout));
       searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               PlaceAutocompleteDetail detail=(PlaceAutocompleteDetail)adapterView.getItemAtPosition(i);
               placeDetail(detail.getPlaceID());
           }
       });
       return super.onCreateOptionsMenu(menu);
    }

    private void placeDetail(final String placeID) {
       mGeoDataClient.getPlaceById(placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            if (task.isSuccessful()) {
                PlaceBufferResponse places = task.getResult();
                Place myPlace = places.get(0);
                getPlaceDistrict(placeID,myPlace);
            } else {
                Toast.makeText(MainActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

}
