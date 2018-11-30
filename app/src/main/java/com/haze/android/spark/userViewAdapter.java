package com.haze.android.spark;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.haze.android.spark.R.id.coins_toolbar;

public class userViewAdapter extends RecyclerView.Adapter<userViewHolder> {
//    HomeActivity

    ArrayList<HomeSpacecraft> userDetailList = new ArrayList<>();


    @Override
    public userViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_detailed_layout,parent,false);
        return new userViewHolder(view,parent.getContext());
    }

    @Override
    public void onBindViewHolder(userViewHolder holder, int position) {
       holder.setDetails(userDetailList.get(position));

    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }
}


class userViewHolder extends  RecyclerView.ViewHolder {

    View itemView;
    Context c;
    FloatingActionButton instaFab,facebookFab,snapchatFab,whatsappFab,likeUserFab;
    TextView name_age,city_country,status_text,fabVotes;
    ImageView profile_pic;
    String coinsStr,coins,userLogin,instaKey,facebookKey,snapchatKey,whatsappKey,name,uid;
    long votes = 0;
    LottieAnimationView animation;
    DatabaseReference ref;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public userViewHolder(final View itemView, final Context context) {

        super(itemView);
        this.itemView = itemView;
        this.c = context;

        sharedPreferences = c.getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userLogin = sharedPreferences.getString("userLogin",null);

        if(Integer.parseInt(userLogin) == 1){

            ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference coinsRef = ref
                    .child("Activity")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("coins");
            coinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    coins = dataSnapshot.getValue(String.class);
                    coinsStr = coins;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        profile_pic = (ImageView) itemView.findViewById(R.id.dialog_profile_pic);
        name_age = (TextView)itemView.findViewById(R.id.dialog_name_age);
        city_country = (TextView)itemView.findViewById(R.id.dialog_city_country);
        status_text = (TextView)itemView.findViewById(R.id.dialog_status);
        instaFab = (FloatingActionButton)itemView.findViewById(R.id.insta_fab);
        facebookFab = (FloatingActionButton)itemView.findViewById(R.id.facebook_fab);
        snapchatFab = (FloatingActionButton)itemView.findViewById(R.id.snapchat_fab);
        whatsappFab = (FloatingActionButton)itemView.findViewById(R.id.whatsapp_fab);
        likeUserFab = (FloatingActionButton)itemView.findViewById(R.id.likeUser_fab);
        animation = (LottieAnimationView)itemView.findViewById(R.id.premiumView);
        fabVotes = (TextView)itemView.findViewById(R.id.fabVotesTxt);

        instaFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(userLogin) == 1){

                    String checkEq = FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
                    if (checkEq.equals(uid)){
                        Toast.makeText(context, "You can't view your own account!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (votes > 1000){

                        if (Integer.parseInt(coinsStr)>=15){
                            new AlertDialog.Builder(c).setIcon(R.drawable.coins).setTitle("Popular Account")
                                    .setMessage("View Instagram account Using 15 Coins")
                                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeSpacecraft s = new HomeSpacecraft();
                                            Boolean saved;
                                            s.setCoins((Integer.parseInt(coinsStr)-15)+"");
                                            try {
                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("coins", (Integer.parseInt(coinsStr)-15)+"");
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result);
                                                saved = true;
                                            }catch (Exception e){
                                                Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                                saved=false;
                                            }
                                            if (saved){
                                                coinsStr = (Integer.parseInt(coinsStr) - 15)+"";
                                                instagramMethod();
                                            }else if (!saved){
                                                Toast.makeText(c, "Sorry! Failed to View Instagram account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }else{
                            Toast.makeText(c, "Not Enough Coins! Recharge to View Instagram account", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        instagramMethod();
                    }

                }else if (Integer.parseInt(userLogin)==0){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }else if (userLogin == null){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }

            }
        });

        facebookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(userLogin) == 1){

                    String checkEq = FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
                    if (checkEq.equals(uid)){
                        Toast.makeText(context, "You can't view your own account!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (votes > 1000){

                        if (Integer.parseInt(coinsStr)>=15){
                            new AlertDialog.Builder(c).setIcon(R.drawable.coins).setTitle("Popular Account")
                                    .setMessage("View Facebook account using 15 coins")
                                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeSpacecraft s = new HomeSpacecraft();
                                            Boolean saved;
                                            s.setCoins((Integer.parseInt(coinsStr)-15)+"");
                                            try {
                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("coins", (Integer.parseInt(coinsStr)-15)+"");
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result);
                                                saved = true;
                                            }catch (Exception e){
                                                Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                                saved=false;
                                            }
                                            if (saved){
                                                coinsStr = (Integer.parseInt(coinsStr) - 15)+"";
                                                facebookMethod();
                                            }else if (!saved){
                                                Toast.makeText(c, "Sorry! Failed to View Facebook Account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }else{
                            Toast.makeText(c, "Not Enough Coins! Recharge to View Facebook Account", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        facebookMethod();
                    }

                }else if (Integer.parseInt(userLogin)==0){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }else if (userLogin == null){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }


            }
        });

        snapchatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(userLogin) == 1){

                    String checkEq = FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
                    if (checkEq.equals(uid)){
                        Toast.makeText(context, "You can't view your own account!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (votes > 1000){

                        if (Integer.parseInt(coinsStr)>=15){
                            new AlertDialog.Builder(c).setIcon(R.drawable.coins).setTitle("Popular Account")
                                    .setMessage("View snapchat account using 15 coins")
                                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeSpacecraft s = new HomeSpacecraft();
                                            Boolean saved;
                                            s.setCoins((Integer.parseInt(coinsStr)-15)+"");
                                            try {
                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("coins", (Integer.parseInt(coinsStr)-15)+"");
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result);
                                                saved = true;
                                            }catch (Exception e){
                                                Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                                saved=false;
                                            }
                                            if (saved){
                                                coinsStr = (Integer.parseInt(coinsStr) - 15)+"";
                                                snapChatMethod();
                                            }else if (!saved){
                                                Toast.makeText(c, "Sorry! Failed to View Snapchat Account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }else{
                            Toast.makeText(c, "Not Enough Coins! Recharge to View Snapchat Account", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        snapChatMethod();
                    }

                }else if (Integer.parseInt(userLogin)==0){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }else if (userLogin == null){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }

            }
        });
        whatsappFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(userLogin) == 1){

                    String checkEq = FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
                    if (checkEq.equals(uid)){
                        Toast.makeText(context, "You can't view your own account!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (votes > 1000){

                        if (Integer.parseInt(coinsStr)>=15){
                            new AlertDialog.Builder(c).setIcon(R.drawable.coins).setTitle("Popular Account")
                                    .setMessage("View WhatsApp Number Using 15 Coins")
                                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeSpacecraft s = new HomeSpacecraft();
                                            Boolean saved = null;
                                            s.setCoins((Integer.parseInt(coinsStr)-15)+"");
                                            try {
                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("coins", (Integer.parseInt(coinsStr)-15)+"");
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result);
                                                saved = true;
                                            }catch (Exception e){
                                                Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                                saved =false;
                                            }
                                            if (saved){
                                                coinsStr = (Integer.parseInt(coinsStr) - 15)+"";
                                                whatsAppMethod();
                                            }else if (!saved){
                                                Toast.makeText(c, "Sorry! Failed to View WhatsApp Number", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }else{
                            Toast.makeText(c, "Not Enough Coins! Recharge to View WhatsApp Number", Toast.LENGTH_SHORT).show();
                        }

                    }else{

                        if (Integer.parseInt(coinsStr)>=5){
                            new AlertDialog.Builder(c).setIcon(R.drawable.coins).setTitle("WhatsApp Number")
                                    .setMessage("View WhatsApp Number Using 5 Coins")
                                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeSpacecraft s = new HomeSpacecraft();
                                            Boolean saved = null;
                                            s.setCoins((Integer.parseInt(coinsStr)-5)+"");
                                            try {
                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("coins", (Integer.parseInt(coinsStr)-5)+"");
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result);
                                                saved = true;
                                            }catch (Exception e){
                                                Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                                saved =false;
                                            }
                                            if (saved){
                                                coinsStr = (Integer.parseInt(coinsStr) - 5)+"";
                                                whatsAppMethod();
                                            }else if (!saved){
                                                Toast.makeText(c, "Sorry! Failed to View WhatsApp Number", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }else{
                            Toast.makeText(c, "Not Enough Coins! Recharge to View WhatsApp Number", Toast.LENGTH_SHORT).show();
                        }

                    }

                }else if (Integer.parseInt(userLogin)==0){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }else if (userLogin == null){

                    new AlertDialog.Builder(c).setIcon(R.drawable.login).setTitle("Logged Out")
                            .setMessage("You have to login to View!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    c.startActivity(new Intent(c,LoginActivity.class));
                                    //c.finish();
                                }
                            }).setNegativeButton("Later", null).show();

                }
            }
        });
        likeUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, votes+"", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void whatsAppMethod(){
        Intent whats  = new Intent(c,SocialActivity.class);
        whats.putExtra("socialName",name);
        whats.putExtra("socialPlatform","WhatsApp");
        whats.putExtra("socialDetail",whatsappKey);
        whats.putExtra("socialBlue","Save to contacts");
        whats.putExtra("socialGreen","Copy number");
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference();
        HomeSpacecraft sp = new HomeSpacecraft();
        sp.setName(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
        db.child("Activity").child(uid).child("votes").push().setValue(sp);
        c.startActivity(whats);
    }

    public void snapChatMethod(){
        Intent snap  = new Intent(c,SocialActivity.class);
        snap.putExtra("socialName",name);
        snap.putExtra("socialPlatform","Snapchat");
        snap.putExtra("socialDetail",snapchatKey);
        snap.putExtra("socialBlue","Open in Snapchat");
        snap.putExtra("socialGreen","Copy username");
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference();
        HomeSpacecraft sp = new HomeSpacecraft();
        sp.setName(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
        db.child("Activity").child(uid).child("votes").push().setValue(sp);
        c.startActivity(snap);
    }

    public void facebookMethod(){
        Intent fb  = new Intent(c,SocialActivity.class);
        fb.putExtra("socialName",name);
        fb.putExtra("socialPlatform","Facebook");
        fb.putExtra("socialDetail",facebookKey);
        fb.putExtra("socialBlue","Open in Facebook");
        fb.putExtra("socialGreen","Copy link");
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference();
        HomeSpacecraft sp = new HomeSpacecraft();
        sp.setName(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
        db.child("Activity").child(uid).child("votes").push().setValue(sp);
        c.startActivity(fb);
    }

    public void instagramMethod(){
        Intent insta  = new Intent(c,SocialActivity.class);
        insta.putExtra("socialName",name);
        insta.putExtra("socialPlatform","Instagram");
        insta.putExtra("socialDetail",instaKey);
        insta.putExtra("socialBlue","Open in Instagram");
        insta.putExtra("socialGreen","Copy username");
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference();
        HomeSpacecraft sp = new HomeSpacecraft();
        sp.setName(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
        db.child("Activity").child(uid).child("votes").push().setValue(sp);
        c.startActivity(insta);
    }

    public void setDetails(HomeSpacecraft user){
        name_age.setText(user.getName()+", "+user.getAge());
        city_country.setText(user.getCity()+", "+user.getCountry());
        status_text.setText(user.getStatus());
        votes = user.votes;
        name = user.getName();
        uid = user.getUid();
        instaKey = user.getInstaLink();
        snapchatKey = user.getSnapLink();
        facebookKey = user.getFbLink();
        whatsappKey = user.getPhn();
        if (votes < 999){
            animation.setVisibility(View.GONE);
            fabVotes.setText("+"+votes);
        }else if (votes >= 1000 && votes <= 1000000){
            String count =(votes / 1000) + "";
            fabVotes.setText("+"+count+ "K");
        }
        Glide.with(c).load(user.getProfile_pic()).into(profile_pic);
    }

}

