package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Resister extends AppCompatActivity {

    private EditText txt_fullName,txt_email,txt_phone,txt_password,txt_confirmPassword;
    private Button btn_register;
    //private RadioButton radioGenderMale, radioGenderFemale;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "SignUp_Form";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_resister);


            getSupportActionBar().setTitle("SignUp Form");

            txt_fullName=(EditText)findViewById(R.id.fullName);
            txt_email=(EditText)findViewById(R.id.email);
            txt_phone=(EditText)findViewById(R.id.phoneNumber);
            txt_password=(EditText)findViewById(R.id.Password);
            txt_confirmPassword=(EditText)findViewById(R.id.confirmPassword);
            btn_register=(Button)findViewById(R.id.register);

            progressBar=(ProgressBar)findViewById(R.id.progressBar);



            databaseReference=firebaseDatabase.getInstance().getReference("User");

            firebaseAuth=FirebaseAuth.getInstance();

            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                }
            });


            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String fullName = txt_fullName.getText ().toString ();
                    String email = txt_email.getText ().toString ().trim ();
                    String phoneNumber = txt_phone.getText ().toString ().trim ();
                    String password = txt_password.getText ().toString ().trim ();
                    String confirmPassword = txt_confirmPassword.getText ().toString ().trim ();


                    if (TextUtils.isEmpty (email)) {
                        Toast.makeText (Resister.this, "Please Enter Email", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    if (TextUtils.isEmpty (password)) {
                        Toast.makeText (Resister.this, "Please Enter password", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    if (TextUtils.isEmpty (fullName)) {
                        Toast.makeText (Resister.this, "Please Enter Fullname", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    if (TextUtils.isEmpty (confirmPassword)) {
                        Toast.makeText (Resister.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    if (TextUtils.isEmpty (phoneNumber)) {
                        Toast.makeText (Resister.this, "Please Enter phone Number", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    if (password.length () < 8) {
                        Toast.makeText (Resister.this, "password too short", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    progressBar.setVisibility (View.VISIBLE);
                    if (password.equals((confirmPassword))) {

                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Resister.this, new OnCompleteListener<AuthResult> () {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            User information = new User();
                                            information.setEmail(email);
                                            information.setFullName (fullName);
                                            information.setPhoneNumber ( phoneNumber);




                                            FirebaseDatabase.getInstance().getReference("User" )
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    startActivity((new Intent(getApplicationContext(), MainActivity.class)));
                                                    Toast.makeText(Resister.this, "Register Successful", Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                        } else {

                                            Toast.makeText(Resister.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                });

                    }
                }
            });

    }

}



