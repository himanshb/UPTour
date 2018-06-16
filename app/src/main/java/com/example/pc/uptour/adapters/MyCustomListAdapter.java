package com.example.pc.uptour.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pc.uptour.R;
import com.example.pc.uptour.classes.CityDetails;

import java.util.List;

/**
 * Created by PC on 1/19/2018.
 */

public class MyCustomListAdapter extends ArrayAdapter<String>
{
    private Activity context;
    private List<CityDetails> cityList;

    public MyCustomListAdapter(Activity context, List<CityDetails> cityList) {
        //super(context, R.layout.listview_layout,R.id.textView2,new String[cityList.size()]);
        super(context,R.layout.my_custom_list_item,R.id.cityListTextView,new String[cityList.size()]);
        this.context=context;
        this.cityList=cityList;
    }

    public int getCount() {
        return cityList.size();
    }

    public CityDetails getItem(CityDetails position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        //View rowView=inflater.inflate(R.layout.listview_layout, null,true);
        View rowView=inflater.inflate(R.layout.my_custom_list_item, parent,false);
        TextView titleText = rowView.findViewById(R.id.cityListTextView);
        titleText.setText(cityList.get(position).getCityName());
        titleText.setTextSize(24);

        return rowView;
    }
}
