package com.haze.android.spark;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.R.attr.animation;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName,profileStatus,profileAge,
            profileLikes,profileCoins,profileCityCountry,
                profileWhatsapp,profileFb,profileInsta,profileSnap;
    String name,status,age,prolikes,coins,city,country,whats,fb,insta,snap;
    Button editProfile,instaView,fbView,whatsView,snapView;
    ImageView profilePic,profilePicBck;
    LottieAnimationView animation;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        instaView = (Button)findViewById(R.id.instaView);
        fbView = (Button)findViewById(R.id.fbView);
        snapView = (Button)findViewById(R.id.snapchatView);
        whatsView = (Button)findViewById(R.id.whatsappView);
        profileName = (TextView)findViewById(R.id.profile_name);
        profileStatus = (TextView)findViewById(R.id.profile_status);
        profileAge = (TextView)findViewById(R.id.profile_age);
        profileLikes = (TextView)findViewById(R.id.profile_votes);
        profileCoins = (TextView)findViewById(R.id.profile_coins);
        profileCityCountry = (TextView)findViewById(R.id.profile_city_country);
        profileWhatsapp = (TextView)findViewById(R.id.profile_whatsapp);
        profileFb = (TextView)findViewById(R.id.profile_fb);
        profileInsta = (TextView)findViewById(R.id.profile_insta);
        profileSnap = (TextView)findViewById(R.id.profile_snap);
        editProfile = (Button)findViewById(R.id.editProfile);
        profilePic = (ImageView) findViewById(R.id.profile_pic_image);
        profilePicBck = (ImageView) findViewById(R.id.profile_pic_bck);
        animation = (LottieAnimationView)findViewById(R.id.profile_premiumView);

        sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        name = sharedPreferences.getString("userName","");
        status = sharedPreferences.getString("userStatus","");
        age = sharedPreferences.getString("userAge","");
        city = sharedPreferences.getString("userCity","");
        country = sharedPreferences.getString("userCountry","");
        whats = sharedPreferences.getString("userPhn","");
        fb = sharedPreferences.getString("userFbLink","");
        insta = sharedPreferences.getString("userInstaLink","");
        snap = sharedPreferences.getString("userSnapLink","");

        profileName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setClipboard(ProfileActivity.this,FirebaseAuth.getInstance().getCurrentUser().getUid());
                return true;
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("Activity")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("votes");
        try {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    prolikes = dataSnapshot.getChildrenCount()+"";
                    profileLikes.setText(prolikes);
                    if (Integer.parseInt(prolikes) > 999){
                        animation.setVisibility(View.VISIBLE);
                    }else if (Integer.parseInt(prolikes) <= 999){
                        animation.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(ProfileActivity.this, e+"", Toast.LENGTH_SHORT).show();
        }

        DatabaseReference coinsDb = FirebaseDatabase.getInstance().getReference()
                .child("Activity")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("coins");
        try {
            coinsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    coins = dataSnapshot.getValue(String.class);
                    profileCoins.setText(coins);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference img = ref
                .child("Activity")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("profile_pic");
        img.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profile_pic = dataSnapshot.getValue(String.class)+"";
                Picasso.with(getApplicationContext()).load(profile_pic).into(profilePic);
                Picasso.with(getApplicationContext()).load(profile_pic).into(profilePicBck);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to Retrieve Profile Pic!", Toast.LENGTH_SHORT).show();
            }
        });

        profileName.setText(name);
        profileStatus.setText(status);
        profileAge.setText(age);
        profileCityCountry.setText(city+", "+country);
        profileWhatsapp.setText(whats);
        profileFb.setText(fb);
        profileInsta.setText(insta);
        profileSnap.setText(snap);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,EditActivity.class));
                finish();
            }
        });

        instaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDetail("Instagram Username",insta);
            }
        });
        fbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDetail("Facebook Link",fb);
            }
        });
        snapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDetail("Snapchat Username",snap);
            }
        });
        whatsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDetail("WhatsApp Number",whats);
            }
        });

    }//*********************ONCREATE**************************

    public void viewDetail(String plat,String det){
        TextView platTxt,detTxt;
        RelativeLayout closeRel;
        final ImageView profilePicDialog;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.view_details);
        platTxt = (TextView)dialog.findViewById(R.id.nameWhatsapp);
        detTxt = (TextView)dialog.findViewById(R.id.numberWhatsapp);
        closeRel = (RelativeLayout)dialog.findViewById(R.id.whatsapp_dialog_close);
        profilePicDialog = (ImageView)dialog.findViewById(R.id.viewProfilePic);
        platTxt.setText(plat);
        detTxt.setText(det);
        closeRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference img = ref
                .child("Activity")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("profile_pic");
        img.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profile_pic = dataSnapshot.getValue(String.class)+"";
                Picasso.with(getApplicationContext()).load(profile_pic).into(profilePicDialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to Retrieve Profile Pic!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void setClipboard(Context context, String text) {

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "User key copied!", Toast.LENGTH_SHORT).show();

    }

}
