package com.haze.android.spark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CoinsActivity extends AppCompatActivity {

    Button popular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins);

        popular = (Button)findViewById(R.id.coins_popular);
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popular.setVisibility(View.GONE);
                DatabaseReference db;
                db = FirebaseDatabase.getInstance().getReference();
                HomeSpacecraft sp = new HomeSpacecraft();
                sp.setName(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
                Boolean saved = null;

                try {
                    for(int i =0;i < 50;i++){
                        db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("votes").push().setValue(sp);
                    }
                    saved = true;
                }catch (Exception e){
                    Toast.makeText(CoinsActivity.this, e+"", Toast.LENGTH_SHORT).show();
                    saved = false;
                }
                if (saved){
                    Toast.makeText(CoinsActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                }else if (!saved){
                    Toast.makeText(CoinsActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                }
                popular.setVisibility(View.VISIBLE);

            }
        });

    }

}
