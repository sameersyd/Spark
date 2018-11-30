package com.haze.android.spark;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class HomeFirebaseHelper {

    HomeCustomAdapter adapter;
    DatabaseReference db;
    Context c;
    ArrayList<HomeSpacecraft> spacecrafts = new ArrayList<>();

    //PASS DATABASE REFRENCE

    public HomeFirebaseHelper(DatabaseReference db) {
        this.db = db;
    }

    //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot) {
        spacecrafts.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            HomeSpacecraft spacecraft = ds.getValue(HomeSpacecraft.class);
            spacecrafts.add(spacecraft);
        }
        adapter.spacecrafts = this.spacecrafts;
        adapter.notifyDataSetChanged();
//        adapter.update(spacecrafts);
    }

    //READ THEN RETURN ARRAYLIST
    public ArrayList<HomeSpacecraft> retrieve() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return spacecrafts;
    }

    private void fetchFilterData(DataSnapshot dataSnapshot,String age,String country,String acc,String gender){
        spacecrafts.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            HomeSpacecraft spacecraft = ds.getValue(HomeSpacecraft.class);
            spacecraft.votes = ds.child("votes").getChildrenCount();

            int accVotes = 0;
            if (acc.equals("Popular")){
                accVotes = 0;
            }
            if (acc.equals("Un - Popular")){
                accVotes = 1;
            }
            if (acc.equals("Both")){
                accVotes = 2;
            }
            int userVotes = Integer.parseInt(String.valueOf(spacecraft.votes));
            String[] ages = age.split("-");

            final int initialAge = Integer.parseInt(ages[0]);
            final int finalAge = Integer.parseInt(ages[1]);
            int userAge = Integer.parseInt(spacecraft.getAge());
            if (userAge >= initialAge && userAge <= finalAge) {
                if (gender.equals(spacecraft.getGender())){

                    if (country.equals(spacecraft.getCountry())){
                        switch (accVotes){
                            case 0:
                                if (userVotes > 1000){
                                    spacecrafts.add(spacecraft);
                                }
                                break;
                            case 1:
                                if (userVotes < 1000){
                                    spacecrafts.add(spacecraft);
                                }
                                break;
                            case 2:
                                spacecrafts.add(spacecraft);
                                break;
                        }
                    }

                }
            }
        }
//        adapter.update(spacecrafts);
    }

    public ArrayList<HomeSpacecraft> retrieveFilter(final String age, final String country, final String acc,final String gender) {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchFilterData(dataSnapshot,age,country,acc,gender);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchFilterData(dataSnapshot,age,country,acc,gender);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return spacecrafts;
    }

}



//    DatabaseReference name = ref
//            .child("Activity")
//            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//            .child("name");
//                                                name.addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//        name = dataSnapshot.getValue(String.class);
//        }
//
//@Override
//public void onCancelled(DatabaseError databaseError) {
//
//        }
//        });