package com.haze.android.spark;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static com.haze.android.spark.SignUpActivity.FB_STORAGE_PATH;
import static com.haze.android.spark.SignUpActivity.REQUEST_CODE;

public class EditActivity extends AppCompatActivity {

    TextView editName,editStatus,editAge,editCity,editWhats,editFb,editInsta,editSnap;
    Button save;
    Spinner citizenship;
    private String name,status,age,city,countryUser,whats,fb,insta,snap,profile_pic;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Boolean saved = false;
    ImageView editProfileImg;
    private Uri imgUri;
    Boolean isPhotoAdded = false;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        name = sharedPreferences.getString("userName","");
        status = sharedPreferences.getString("userStatus","");
        age = sharedPreferences.getString("userAge","");
        city = sharedPreferences.getString("userCity","");
        countryUser = sharedPreferences.getString("userCountry","");
        whats = sharedPreferences.getString("userPhn","");
        fb = sharedPreferences.getString("userFbLink","");
        insta = sharedPreferences.getString("userInstaLink","");
        snap = sharedPreferences.getString("userSnapLink","");

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

        String compareValue = countryUser;

        citizenship = (Spinner)findViewById(R.id.edit_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);

        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            citizenship.setSelection(spinnerPosition);
        }

        editName = (TextView)findViewById(R.id.edit_name);
        editStatus = (TextView)findViewById(R.id.edit_status);
        editAge = (TextView)findViewById(R.id.edit_age);
        editCity = (TextView)findViewById(R.id.edit_city);
        editWhats = (TextView)findViewById(R.id.edit_whatsapp);
        editFb = (TextView)findViewById(R.id.edit_fb);
        editInsta = (TextView)findViewById(R.id.edit_insta);
        editSnap = (TextView)findViewById(R.id.edit_snap);
        save = (Button)findViewById(R.id.saveEdtpro);
        editProfileImg = (ImageView)findViewById(R.id.edit_profileImg);
        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBrowse_Click();
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
                Picasso.with(getApplicationContext()).load(profile_pic).into(editProfileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditActivity.this, "Failed to Retrieve Profile Pic!", Toast.LENGTH_SHORT).show();
            }
        });

        editName.setText(name);
        editStatus.setText(status);
        editAge.setText(age);
        editCity.setText(city);
        editWhats.setText(whats);
        editFb.setText(fb);
        editInsta.setText(insta);
        editSnap.setText(snap);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (TextUtils.isEmpty(editName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editAge.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter the Age", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (Integer.parseInt(editAge.getText().toString()) < 16) {
                        Toast.makeText(getApplicationContext(), "You Must be Above 16 to Use this App", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editAge.getText().toString().length()!=2) {
                        Toast.makeText(getApplicationContext(), "Enter Correct Age!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editWhats.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Correct Phn Number!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editStatus.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Status!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editFb.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter FB Profile Link!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editInsta.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Insta Username!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editSnap.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter Snapchat Username!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(editCity.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Enter city!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    InputStream is = null;
                    try {
                        is = EditActivity.this.getAssets().open("load_one.json");
                        byte[] buffer = new byte[is.available()];
                        is.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LottieAnimationView animation = (LottieAnimationView) getLayoutInflater().inflate(R.layout.loading_one,null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    builder.setView(animation);
                    alertDialog = builder.create();
                    alertDialog.show();

                    DatabaseReference picDataRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference proF = picDataRef
                            .child("Activity")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("profile_pic");
                    proF.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            profile_pic = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   if(isPhotoAdded){
                       final StorageReference mStorageRef;
                       mStorageRef = FirebaseStorage.getInstance().getReference();
                       final DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();
                       StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "."+getImageExt(imgUri));
                       ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               updateData(taskSnapshot.getDownloadUrl().toString());
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(EditActivity.this, e+"", Toast.LENGTH_SHORT).show();
                               saved =false;
                           }
                       });
                   }else{
                       updateData("");
                   }


                }

        });
    }//************************ONCREATE*************************

    public void updateData(String profileImageUrl){
        try {
            HashMap<String, Object> result = new HashMap<>();
            result.put("name", editName.getText().toString());
            result.put("status", editStatus.getText().toString());
            result.put("age", editAge.getText().toString());
            result.put("phn", editWhats.getText().toString());
            result.put("fbLink", editFb.getText().toString());
            result.put("instaLink", editInsta.getText().toString());
            result.put("snapLink", editSnap.getText().toString());
            result.put("city", editCity.getText().toString());
            result.put("country", citizenship.getSelectedItem().toString());
            if(profileImageUrl != ""){
                result.put("profile_pic",profileImageUrl);
            }

            Toast.makeText(EditActivity.this, saved+" ONE", Toast.LENGTH_SHORT).show();//***************************
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(profile_pic);
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditActivity.this, "Pic Deted Successfully", Toast.LENGTH_SHORT).show();
                            Toast.makeText(EditActivity.this, saved+" TWO", Toast.LENGTH_SHORT).show();//*****************
                            saved=true;
                            Toast.makeText(EditActivity.this, saved+" THREE", Toast.LENGTH_SHORT).show();//********************
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditActivity.this, saved+" FOUR", Toast.LENGTH_SHORT).show();//************************
                            Toast.makeText(EditActivity.this, e+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                    saved=true;
                    Toast.makeText(EditActivity.this, saved+" FIVE", Toast.LENGTH_SHORT).show();//**************************
                    Toast.makeText(EditActivity.this, "Profile Upted Successfully", Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditActivity.this, saved+" SIX", Toast.LENGTH_SHORT).show();//******************************
                    Toast.makeText(EditActivity.this, e+"", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(EditActivity.this, saved+" SEVEN", Toast.LENGTH_SHORT).show();//*********************************
            Toast.makeText(EditActivity.this, e+"", Toast.LENGTH_SHORT).show();
            saved=false;
        }
        if (saved){
            editor.putString("userName",editName.getText().toString());
            editor.putString("userAge",editAge.getText().toString());
            editor.putString("userPhn",editWhats.getText().toString());
            editor.putString("userFbLink",editFb.getText().toString());
            editor.putString("userInstaLink",editInsta.getText().toString());
            editor.putString("userSnapLink",editSnap.getText().toString());
            editor.putString("userStatus",editStatus.getText().toString());
            editor.putString("userCity",editCity.getText().toString());
            editor.putString("userCountry",citizenship.getSelectedItem().toString());
            editor.commit();
            alertDialog.dismiss();
            Toast.makeText(EditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            finish();
        }else if (!saved){
            alertDialog.dismiss();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.idLayout),"Failed to Update Profile!",Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    public void btnBrowse_Click(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                editProfileImg.setImageBitmap(bm);
                isPhotoAdded = true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
