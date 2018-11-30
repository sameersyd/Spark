package com.haze.android.spark;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GridView gv;
    DatabaseReference db;
    HomeFirebaseHelper helper;
    HomeCustomAdapter adapter;
    FirebaseAuth auth;
    String loginStr,countryUser;
    ImageView navImg,navBckImg;
    AlertDialog alertDialog;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    ArrayList<HomeSpacecraft> spacecrafts = new ArrayList<>();

    private String userName,userEmail,coins;
    TextView navUserName,navUserEmail,coins_toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ImageView filter_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        Thread thread = new Thread(){
            public void run(){

                sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                if (auth.getCurrentUser() == null) {
                    auth.signOut();
                    editor.putString("userLogin","0");
                    editor.commit();
                }
                userEmail = sharedPreferences.getString("userEmail","");
                userName = sharedPreferences.getString("userName","");
                loginStr = sharedPreferences.getString("userLogin","null");
                countryUser = sharedPreferences.getString("userCountry",null);

                if (Integer.parseInt(loginStr)==1){

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference img = ref
                            .child("Activity")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("profile_pic");
                    img.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String profile_pic = dataSnapshot.getValue(String.class) + "";
                            Picasso.with(getApplicationContext()).load(profile_pic).into(navImg);
                            Picasso.with(getApplicationContext()).load(profile_pic).into(navBckImg);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Toast.makeText(this, e.getMessage().toString()+"", Toast.LENGTH_SHORT).show();
        }

        navUserEmail = (TextView)headerView.findViewById(R.id.navEmail);
        navUserName = (TextView) headerView.findViewById(R.id.navName);
        navImg = (ImageView)headerView.findViewById(R.id.nav_profile_image);
        navBckImg = (ImageView)headerView.findViewById(R.id.navBackImg);
        coins_toolbar = (TextView)findViewById(R.id.coins_toolbar);
        filter_toolbar = (ImageView) findViewById(R.id.filter_toolbar);
        filter_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFilter();
            }
        });

        if(Integer.parseInt(loginStr)==1){
            navUserEmail.setText(userEmail);
            navUserName.setText(userName);

            DatabaseReference coinsRef = ref
                    .child("Activity")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("coins");
            coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    coins = dataSnapshot.getValue(String.class);
                    coins_toolbar.setText(coins+"");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else if(Integer.parseInt(loginStr)==0){
            navUserEmail.setText("hazecorp.co");
            navUserName.setText("Haze Corp");
        }else {
            navUserEmail.setText("");
            navUserName.setText("");
        }

        if (Integer.parseInt(loginStr)==1){

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference img = ref
                    .child("Activity")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("profile_pic");
            img.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profile_pic = dataSnapshot.getValue(String.class) + "";
                    Picasso.with(getApplicationContext()).load(profile_pic).into(navImg);
                    Picasso.with(getApplicationContext()).load(profile_pic).into(navBckImg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        LottieAnimationView animation = (LottieAnimationView) getLayoutInflater().inflate(R.layout.loading_one,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setView(animation);
        alertDialog = builder.create();
        alertDialog.show();

        gv = (GridView) findViewById(R.id.homeList);
        //INITIALIZE FIREBASE DB
        db = FirebaseDatabase.getInstance().getReference();
        helper = new HomeFirebaseHelper(db);
        //ADAPTER
        adapter = new HomeCustomAdapter(this);
        adapter.delegate = this;
        gv.setAdapter(adapter);
        retrieve();
        alertDialog.show();

        InputStream is = null;
        try {
            is = HomeActivity.this.getAssets().open("load_one.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing! Please Wait...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                retrieve();

                if (auth.getCurrentUser() == null) {
                    auth.signOut();
                    editor.putString("userLogin","0");
                    editor.commit();
                }

                userEmail = sharedPreferences.getString("userEmail","");
                userName = sharedPreferences.getString("userName","");
                loginStr = sharedPreferences.getString("userLogin","null");

                DatabaseReference coinsRef = ref
                        .child("Activity")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("coins");
                coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        coins = dataSnapshot.getValue(String.class);
                        coins_toolbar.setText(coins+"");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(Integer.parseInt(loginStr)==1){
                    navUserEmail.setText(userEmail);
                    navUserName.setText(userName);
                }else if(Integer.parseInt(loginStr)==0){
                    navUserEmail.setText("hazecorp.co");
                    navUserName.setText("Haze Corp");
                }else {
                    navUserEmail.setText("");
                    navUserName.setText("");
                }

                if (Integer.parseInt(loginStr)==1){

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference img = ref
                            .child("Activity")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("profile_pic");
                    img.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String profile_pic = dataSnapshot.getValue(String.class) + "";
                            Picasso.with(getApplicationContext()).load(profile_pic).into(navImg);
                            Picasso.with(getApplicationContext()).load(profile_pic).into(navBckImg);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                            startActivity(intent);
                            finish();
                            System.exit(0);
                        }
                    }).setNegativeButton("No", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_home) {

             Toast.makeText(this, "You are already in the Home :)", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile) {

            if (Integer.parseInt(loginStr)==1){
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }else {
                new AlertDialog.Builder(HomeActivity.this).setIcon(R.drawable.login).setTitle("Login Required")
                        .setMessage("You have to login!")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Later", null).show();
            }

        } else if (id == R.id.nav_coins) {

            if (Integer.parseInt(loginStr)==1){

                startActivity(new Intent(HomeActivity.this,CoinsActivity.class));

            }else {
                new AlertDialog.Builder(HomeActivity.this).setIcon(R.drawable.login).setTitle("Login Required")
                        .setMessage("You have to login!")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Later", null).show();
            }

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_sign_out) {

            if (Integer.parseInt(loginStr)==1){

                new AlertDialog.Builder(this).setIcon(R.drawable.logoutblack).setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("No", null).show();

            }else {
                new AlertDialog.Builder(HomeActivity.this).setIcon(R.drawable.login).setTitle("Login Required")
                        .setMessage("You have to login!")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Later", null).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayUser(int pos) {
        Intent i = new Intent(HomeActivity.this, UserDetailedActivity.class);
        i.putExtra("position",pos);
        startActivity(i);
    }

    public void displayFilter(){
        final Dialog filter = new Dialog(this);
        filter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filter.setContentView(R.layout.filter);
        final TextView age,reset;
        RadioGroup genderFilter;
        SeekBar ageSeek;
        Button apply;
        ImageView cancel;
        final String[] agePass = new String[1];
        final String[] genderPass = new String[1];
        final String[] countryPass = new String[1];
        final String[] accPass = new String[1];

        reset = (TextView)filter.findViewById(R.id.filter_reset);
        genderFilter = (RadioGroup)filter.findViewById(R.id.filter_gender_radio);
        cancel = (ImageView)filter.findViewById(R.id.filter_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.dismiss();
            }
        });
        age = (TextView)filter.findViewById(R.id.filter_age);
        apply = (Button)filter.findViewById(R.id.filter_saveBtn);
        ageSeek = (SeekBar)filter.findViewById(R.id.filter_seekbar_age);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Refreshing! Please Wait...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                retrieve();

            }
        });
        genderPass[0]="male";
        genderFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.filter_man_radio:
                        genderPass[0]="male";
                        break;
                    case R.id.filter_woman_radio:
                        genderPass[0]="female";
                        break;
                }
            }
        });
        agePass[0] = "16-23";
        ageSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i){
                    case 0:
                        age.setText("16 - 23");
                        agePass[0] ="16-23";
                        break;
                    case 1:
                        age.setText("24 - 30");
                        agePass[0] ="24-30";
                        break;
                    case 2:
                        age.setText("31 - 40");
                        agePass[0] ="31-40";
                        break;
                    case 3:
                        age.setText("41 - 50");
                        agePass[0] ="41-50";
                        break;
                    case 4:
                        age.setText("50+");
                        agePass[0] ="51-99";
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String[] arraySpinner = new String[] {"Popular", "Un - Popular", "Both"};
        final Spinner s = (Spinner) filter.findViewById(R.id.filter_acc);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(spinnerAdapter);

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        final Spinner citizenship = (Spinner)filter.findViewById(R.id.filter_country);
        ArrayAdapter<String> adapterCiti = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapterCiti);

        String compareValue = countryUser;
        if (compareValue != null) {
            int spinnerPosition = adapterCiti.getPosition(compareValue);
            citizenship.setSelection(spinnerPosition);
        }

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countryPass[0] = citizenship.getSelectedItem().toString();
                accPass[0] = s.getSelectedItem().toString();
                filter.dismiss();

                Snackbar.make(view, "Applying Filter! Please Wait...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                db = FirebaseDatabase.getInstance().getReference();
                helper = new HomeFirebaseHelper(db);
                //ADAPTER

                adapter = new HomeCustomAdapter(HomeActivity.this);
                adapter.delegate = HomeActivity.this;
                gv.setAdapter(adapter);
                retrieveFilter(agePass[0],countryPass[0],accPass[0],genderPass[0]);
                alertDialog.show();

            }
        });

        filter.show();
    }

    private void fetchData(DataSnapshot dataSnapshot) {

        spacecrafts.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            HomeSpacecraft spacecraft = ds.getValue(HomeSpacecraft.class);
            spacecraft.votes = ds.child("votes").getChildrenCount();
            spacecrafts.add(spacecraft);
        }
        adapter.spacecrafts = this.spacecrafts;
        ((GlobalContext)  getApplicationContext()).globaUserList = this.spacecrafts;
        adapter.notifyDataSetChanged();
        gv.invalidate();
        alertDialog.dismiss();

    }
    //READ THEN RETURN ARRAYLIST
    public void retrieve() {

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
        adapter.spacecrafts = this.spacecrafts;
        ((GlobalContext)  getApplicationContext()).globaUserList = this.spacecrafts;
        adapter.notifyDataSetChanged();
        gv.invalidate();
        alertDialog.dismiss();
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
