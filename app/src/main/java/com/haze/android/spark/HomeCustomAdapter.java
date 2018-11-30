package com.haze.android.spark;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeCustomAdapter extends BaseAdapter {
    Context c;
    HomeActivity delegate;
    ArrayList<HomeSpacecraft> spacecrafts = new ArrayList<>();
    ArrayList<HomeSpacecraft> originalList = new ArrayList<>();
    public HomeCustomAdapter(Context c) {
        this.c = c;
    }
    @Override
    public int getCount() {
        return spacecrafts.size();
    }
    @Override
    public Object getItem(int position) {
        return spacecrafts.get(spacecrafts.size() - position - 1);      // Existing Code Modified To Display Recent Top
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.home_display,parent,false);
        }

        TextView tvName = (TextView)convertView.findViewById(R.id.home_name);
        TextView tvAge = (TextView)convertView.findViewById(R.id.home_age);
        TextView tvPhn = (TextView)convertView.findViewById(R.id.home_phn);
        TextView status = (TextView)convertView.findViewById(R.id.home_status);
        TextView fbLink = (TextView)convertView.findViewById(R.id.home_fbLink);
        TextView instaLink = (TextView)convertView.findViewById(R.id.home_instaLink);
        TextView snapLink = (TextView)convertView.findViewById(R.id.home_snapLink);
        TextView email = (TextView)convertView.findViewById(R.id.home_email);
        TextView city = (TextView)convertView.findViewById(R.id.home_city);
        TextView country = (TextView)convertView.findViewById(R.id.home_country);
        TextView uid = (TextView)convertView.findViewById(R.id.home_uid);
        ImageView imgProfile = (ImageView)convertView.findViewById(R.id.home_profile_pic);
        //FloatingActionButton viewFab = (FloatingActionButton)convertView.findViewById(R.id.home_view_fab);
        HomeSpacecraft s = (HomeSpacecraft) this.getItem(position);

        tvName.setText(s.getName());
        tvAge.setText(s.getAge());
        tvPhn.setText(s.getPhn());
        status.setText(s.getStatus());
        fbLink.setText(s.getFbLink());
        instaLink.setText(s.getInstaLink());
        snapLink.setText(s.getSnapLink());
        email.setText(s.getEmail());
        city.setText(s.getCity());
        country.setText(s.getCountry());
        uid.setText(s.getUid());
        Glide.with(c).load(s.getProfile_pic()).into(imgProfile);

//        viewFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        //ONITECLICK
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = position;
                delegate.displayUser(pos);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    public ArrayList<HomeSpacecraft> sortByVotes(ArrayList<HomeSpacecraft> list){
        Collections.sort(list,ageComparator);
        return list;
    }

    public static Comparator<HomeSpacecraft> ageComparator = new Comparator<HomeSpacecraft>() {
        @Override
        public int compare(HomeSpacecraft user1, HomeSpacecraft user2) {
            return (Integer.parseInt(user2.getAge()) < Integer.parseInt(user1.getAge()) ? -1 :
                    (Integer.parseInt(user2.getAge()) == Integer.parseInt(user1.getAge()) ? 0 : 1));
        }
    };

    public void update(ArrayList<HomeSpacecraft> list){
        originalList = spacecrafts;
        spacecrafts.clear();
        spacecrafts.addAll(sortByVotes(list));
        notifyDataSetChanged();
    }

}
