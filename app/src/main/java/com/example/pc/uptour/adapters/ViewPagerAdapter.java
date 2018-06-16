package com.example.pc.uptour.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pc.uptour.R;

import java.util.Calendar;

/**
 * Created by apoorv on 2/5/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer [] images={R.drawable.tajahal, R.drawable.brihadeeswarartemple,R.drawable.park};

    int month=0;

    public int getMonth() {
        return month;
    }

    public void setImages(Integer[] images) {
        this.images = images;
    }

    public ViewPagerAdapter(Context context)
    {
        this.context=context;
    }

    //function to find the month of the year
    public void ReturnMonth(){
        Calendar calendar= Calendar.getInstance();
        month=calendar.MONTH;

    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.custom_layout,null);
        ImageView imageView= view.findViewById(R.id.userImage);
        imageView.setImageResource(images[position]);

        ViewPager viewPager=(ViewPager)container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager=(ViewPager)container;
        View view=(View)object;

        viewPager.removeView(view);
    }
}
