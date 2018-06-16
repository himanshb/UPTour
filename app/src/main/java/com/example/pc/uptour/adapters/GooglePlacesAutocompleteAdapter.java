package com.example.pc.uptour.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pc.uptour.R;
import com.example.pc.uptour.Singleton.MySingleton;
import com.example.pc.uptour.classes.PlaceAutocompleteDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *Created by PC on 2/17/2018.
 */

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<PlaceAutocompleteDetail> implements Filterable {
    private ArrayList<PlaceAutocompleteDetail> resultList;
    private ArrayList<PlaceAutocompleteDetail> result;
    private Context context;

    public GooglePlacesAutocompleteAdapter(Context context, int resourceID) {
        super(context,resourceID);
        this.context=context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public PlaceAutocompleteDetail getItem(int index) {
        return resultList.get(index);
    }

   @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.auto_complete_layout, parent, false);
            view.setBackgroundResource(R.color.cardview_light_background);
        }

        PlaceAutocompleteDetail detail=getItem(position);
        assert detail != null;
        TextView name = view.findViewById(R.id.textView);
        TextView address=view.findViewById(R.id.textView2);
        name.setText(detail.getPlaceName());
        address.setText(detail.getPlaceAddress());
        return view;
    }

    private ArrayList<PlaceAutocompleteDetail> autocomplete(String input){
        String URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyCV2jT3tT36I6OJuuymROZiYvplNTpMrX8&components=country:in&input=";
        String url= URL +input;
        JsonObjectRequest request=new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("predictions");
                    result=new ArrayList<>(jsonArray.length());
                    for (int i=0;i<jsonArray.length();i++){
                        String name=jsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text");
                        String id=jsonArray.getJSONObject(i).getString("place_id");
                        String address=jsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text");
                        PlaceAutocompleteDetail detail=new PlaceAutocompleteDetail(name,id,address);
                       // Toast.makeText(context, jsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"), Toast.LENGTH_SHORT).show();
                        result.add(detail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
        return result;
    }
}