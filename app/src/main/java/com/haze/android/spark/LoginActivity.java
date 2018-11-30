package com.haze.android.spark;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

import static com.haze.android.spark.R.drawable.view;

public class LoginActivity extends AppCompatActivity {

    Context c;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    TextView btnReset,btnSignup;
    private Button btnLogin;
    CheckBox remember;
    DatabaseReference mDatabaseReference;
    String emailLogin,passwordLogin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    Boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // set the view now
        setContentView(R.layout.activity_login);
        remember = (CheckBox) findViewById(R.id.rememberLogin);
        inputEmail = (EditText) findViewById(R.id.emailLogin);
        inputPassword = (EditText) findViewById(R.id.passwordLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (TextView) findViewById(R.id.register);
        btnLogin = (Button) findViewById(R.id.loginLogin);
        btnReset = (TextView) findViewById(R.id.forgotLogin);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        emailLogin = sharedPreferences.getString("emailLogin","");
        passwordLogin = sharedPreferences.getString("passwordLogin","");

        inputEmail.setText(emailLogin);
        inputPassword.setText(passwordLogin);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputEmail.getText().toString().isEmpty()){
                    Toast.makeText(c, "Enter the email", Toast.LENGTH_SHORT).show();
                }else{
                    String emailReset = inputEmail.getText().toString();
                    new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.email).setTitle("Reset password email")
                            .setMessage("Send reset password link to "+emailReset)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    auth.sendPasswordResetEmail(inputEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(c, "Check email for link", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(c, "Email is not registered!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("Change Email", null).show();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                InputStream is = null;
                try {
                    is = c.getAssets().open("load_one.json");
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LottieAnimationView animation = (LottieAnimationView) getLayoutInflater().inflate(R.layout.loading_one,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setView(animation);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    alertDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
                                } else {

                                    if (remember.isChecked()){
                                        editor.putString("emailLogin",inputEmail.getText().toString());
                                        editor.putString("passwordLogin",inputPassword.getText().toString());
                                        editor.commit();
                                    }else {
                                        editor.putString("emailLogin","");
                                        editor.putString("passwordLogin","");
                                        editor.commit();
                                    }

                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    final DatabaseReference name = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("name");
                                    final DatabaseReference age = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("age");
                                    final DatabaseReference phn = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("phn");
                                    final DatabaseReference fbLink = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("fbLink");
                                    final DatabaseReference instaLink = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("instaLink");
                                    final DatabaseReference snap = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("snapLink");
                                    final DatabaseReference country = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("country");
                                    final DatabaseReference status = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("status");
                                    final DatabaseReference city = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("city");
                                    final DatabaseReference gender = ref
                                            .child("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("gender");

                                    try {

                                        name.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);
                                                editor.putString("userName",name);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        age.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);
                                                editor.putString("userAge",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        phn.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userPhn",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        fbLink.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userFbLink",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        instaLink.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userInstaLink",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        snap.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userSnapLink",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        country.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userCountry",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        status.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userStatus",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        city.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userCity",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        gender.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.getValue(String.class);

                                                editor.putString("userGender",name);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });

                                        saved = true;

                                    }catch (Exception e){
                                        Toast.makeText(c, e+"", Toast.LENGTH_SHORT).show();
                                        saved = false;
                                    }

                                    editor.putString("userEmail",inputEmail.getText().toString());
                                    editor.putString("userLogin","1");
                                    editor.commit();

                                    if (saved){
                                        alertDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                        finish();
                                    }else if (!saved){
                                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
            }
        });
    }

}



//                                                    editor.putString("userName",nameEdit.getText().toString());
//                                                    editor.putString("userAge",ageEdit.getText().toString());
//                                                    editor.putString("userPhn",phoneEdit.getText().toString());
//                                                    editor.putString("userFbLink",fbEdit.getText().toString());
//                                                    editor.putString("userInstaLink",instaEdit.getText().toString());
//                                                    editor.putString("userSnapLink",snapEdit.getText().toString());
//                                                    editor.putString("userEmail",emailEdit.getText().toString());
//                                                    editor.putString("userCountry",citizenship.getSelectedItem().toString());
//                                                    editor.putString("userStatus","Hey there am using SPARK!");
//                                                    editor.putString("userCity",cityEditTxt.getText().toString());
//                                                    editor.putString("userCoins","50");
//                                                    editor.putString("userGender",genderStr);
//                                                    editor.putString("userLogin","1");
//                                                    editor.commit();





//    Toast toast = Toast.makeText(LoginActivity.this, "Hello "+name+"\nLogin Successful", Toast.LENGTH_LONG);
//    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//                                                    if( v != null) v.setGravity(Gravity.CENTER);
//                                                            toast.show();
//                                                            editor.putString("userEmail",inputEmail.getText().toString());
//                                                            editor.putString("userName",name);
//                                                            editor.putString("userAge","");
//                                                            editor.putString("userPhn","");
//                                                            editor.putString("userFbLink","");
//                                                            editor.putString("userInstaLink","");
//                                                            editor.putString("userSnapLink","");
//                                                            editor.putString("userCountry","");
//                                                            editor.putString("userStatus","");
//                                                            editor.putString("userCity","");
//                                                            editor.putString("userCoins","");
//                                                            editor.putString("userLogin","1");
//                                                            editor.commit();