package com.haze.android.spark;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    TextView nameEdit,ageEdit,phoneEdit,fbEdit,instaEdit,snapEdit,emailEdit,passwordEdit,alreadyRegistered,cityEditTxt;
    ImageView profileImg;
    Button signUp;
    Spinner citizenship;
    RadioGroup genderGroup;
    String genderStr = "male";

    public static final String FB_STORAGE_PATH = "image/";
    public static final int REQUEST_CODE = 1234;
    private Uri imgUri;

    private FirebaseAuth auth;
    DatabaseReference db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        nameEdit = (EditText) findViewById(R.id.nameSign);
        ageEdit = (EditText) findViewById(R.id.ageSign);
        phoneEdit = (EditText) findViewById(R.id.phnSign);
        fbEdit = (EditText) findViewById(R.id.fbSign);
        instaEdit = (EditText) findViewById(R.id.instaSign);
        snapEdit = (EditText) findViewById(R.id.snapSign);
        emailEdit = (EditText) findViewById(R.id.emailSign);
        passwordEdit = (EditText) findViewById(R.id.passwordSign);
        cityEditTxt = (EditText) findViewById(R.id.city_editTxt);
        alreadyRegistered = (TextView)findViewById(R.id.loginMe);
        signUp = (Button)findViewById(R.id.signUpBtn);
        profileImg = (ImageView)findViewById(R.id.profile_image);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBrowse_Click();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpload_click();
            }
        });
        genderGroup = (RadioGroup)findViewById(R.id.gender_radio_sign);
        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.man_radio_sign:
                        genderStr = "male";
                        break;
                    case R.id.woman_radio_sign:
                        genderStr = "female";
                        break;
                }
            }
        });


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

        citizenship = (Spinner)findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);

    }

    public void btnBrowse_Click(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @SuppressWarnings("VisibleForTests")
    public void btnUpload_click(){

        if(imgUri != null){
            String name = nameEdit.getText().toString().trim();
            String age = ageEdit.getText().toString().trim();
            String phn = phoneEdit.getText().toString().trim();
            String fb = fbEdit.getText().toString().trim();
            String insta = instaEdit.getText().toString().trim();
            String snap = snapEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            String city = cityEditTxt.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(age)) {
                Toast.makeText(getApplicationContext(), "Enter the Age", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Integer.parseInt(age) < 16) {
                Toast.makeText(getApplicationContext(), "You Must be Above 16 to Use this App", Toast.LENGTH_SHORT).show();
                return;
            }

            if (age.length()!=2) {
                Toast.makeText(getApplicationContext(), "Enter Correct Age!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phn)) {
                Toast.makeText(getApplicationContext(), "Enter Correct Phn Number!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(fb)) {
                Toast.makeText(getApplicationContext(), "Enter FB Profile Link!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(insta)) {
                Toast.makeText(getApplicationContext(), "Enter Insta Username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(snap)) {
                Toast.makeText(getApplicationContext(), "Enter Snapchat Username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(city)) {
                Toast.makeText(getApplicationContext(), "Enter city!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkEmailExists()){

                if (checkIfEmailVerified()){
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                }else if (!checkIfEmailVerified()){
                    //remove !t
                    createNewAcc();
                }

            }else if (!checkEmailExists()){
                createNewAcc();
            }

            InputStream is = null;
            try {
                is = SignUpActivity.this.getAssets().open("load_one.json");
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LottieAnimationView animation = (LottieAnimationView) getLayoutInflater().inflate(R.layout.loading_one,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setView(animation);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            StorageReference mStorageRef;
                            mStorageRef = FirebaseStorage.getInstance().getReference();
                            final DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();
                            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "."+getImageExt(imgUri));
                            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    alertDialog.dismiss();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Authentication failed" ,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        //GET DATA
                                        String name = nameEdit.getText().toString().trim();
                                        String age = ageEdit.getText().toString().trim();
                                        String phn = phoneEdit.getText().toString().trim();
                                        String fb = fbEdit.getText().toString().trim();
                                        String insta = instaEdit.getText().toString().trim();
                                        String snap = snapEdit.getText().toString().trim();
                                        String email = emailEdit.getText().toString().trim();
                                        String city = cityEditTxt.getText().toString().trim();
                                        String country = citizenship.getSelectedItem().toString();

                                        //SET DATA
                                        HomeSpacecraft s = new HomeSpacecraft();
                                        s.setName(name);
                                        s.setAge(age);
                                        s.setPhn(phn);
                                        s.setFbLink(fb);
                                        s.setInstaLink(insta);
                                        s.setSnapLink(snap);
                                        s.setEmail(email);
                                        s.setCoins("50");
                                        s.setStatus("Hey there am using SPARK!");
                                        s.setProfile_pic(taskSnapshot.getDownloadUrl().toString());
                                        s.setCity(city);
                                        s.setCountry(country);
                                        s.setGender(genderStr);
                                        s.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
                                        Boolean saved;
                                        try{
                                            db = FirebaseDatabase.getInstance().getReference();
                                            db.child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(s);
                                            saved = true;
                                        }catch(Exception e){
                                            saved = false;
                                        }
                                        if(saved){
                                            editor.putString("userName",nameEdit.getText().toString());
                                            editor.putString("userAge",ageEdit.getText().toString());
                                            editor.putString("userPhn",phoneEdit.getText().toString());
                                            editor.putString("userFbLink",fbEdit.getText().toString());
                                            editor.putString("userInstaLink",instaEdit.getText().toString());
                                            editor.putString("userSnapLink",snapEdit.getText().toString());
                                            editor.putString("userEmail",emailEdit.getText().toString());
                                            editor.putString("userCountry",citizenship.getSelectedItem().toString());
                                            editor.putString("userStatus","Hey there am using SPARK!");
                                            editor.putString("userCity",cityEditTxt.getText().toString());
                                            editor.putString("userGender",genderStr);
                                            editor.putString("userLogin","1");
                                            editor.putString("emailLogin","");
                                            editor.putString("passwordLogin","");
                                            editor.commit();
                                            Toast.makeText(SignUpActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                            finish();
                                        }else if(!saved){
                                            Toast.makeText(SignUpActivity.this, "Authentication failed" , Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
        }else{
            Toast.makeText(this, "Select Profile Pic", Toast.LENGTH_SHORT).show();
        }
    }

    public void createNewAcc(){

    }

    public boolean checkEmailExists(){

        final boolean[] check = new boolean[1];
        auth.fetchProvidersForEmail(emailEdit.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                check[0] = !task.getResult().getProviders().isEmpty();

                // check[0] = true - Registered(Exists) ; false - Not Registered(Not Exists)
            }
        });

        return check[0];
    }

    public void sendEmailVerification(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"Please check your email for verification",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean checkIfEmailVerified() {

        boolean verified;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            verified = true;
        }else {
            verified = false;
        }
        return verified;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                profileImg.setImageBitmap(bm);
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
