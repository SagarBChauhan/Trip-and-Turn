package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripandturn.DB.Login;
import com.example.tripandturn.DB.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RegisterDataActivity extends AppCompatActivity {

    EditText firstname, middlename, lastname, address, pincode, contact, email, password;
    RadioGroup group_gender;
    RadioButton male, female;
    TextView birthdate;
    Spinner country, state, city;
    Button save_data;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data);

        init();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fname=firstname.getText().toString();
                final String mname=middlename.getText().toString();
                final String lname=lastname.getText().toString();
                final String addr=address.getText().toString();
                final String pin=pincode.getText().toString();
                final String contactNo=contact.getText().toString();
                final String emailId=email.getText().toString();
                String pwd=password.getText().toString();
                String gender="NA";
                if (male.isChecked())
                {
                    gender="male";
                }
                else if (female.isChecked())
                {
                    gender="Female";
                }
                final String dob=birthdate.getText().toString();

                String country_=country.getSelectedItem().toString();
                String state_=state.getSelectedItem().toString();
                final String city_=city.getSelectedItem().toString();


                final String finalGender = gender;
                new AlertDialog.Builder(RegisterDataActivity.this)
                        .setTitle("Data Overview")
                        .setMessage("Add to Realtime DATABASE")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid=user.getUid();
                                String profile="";
                                String currentDate = new SimpleDateFormat("dd/MM/YYYY, hh:mm:ss", Locale.getDefault()).format(new Date());
                                User info=new User(
                                        fname,
                                        mname,
                                        lname,
                                        finalGender,
                                        dob,
                                        addr,
                                        city_,
                                        pin,
                                        contactNo,
                                        emailId,
                                        uid,
                                        profile,
                                        "",
                                        currentDate,
                                        currentDate
                                );
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child(mAuth.getCurrentUser().getUid())
                                        .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RegisterDataActivity.this, "Your data saved", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void init() {
        firstname=findViewById(R.id.firstname);
        middlename=findViewById(R.id.middlename);
        lastname=findViewById(R.id.lastname);
        address=findViewById(R.id.address);
        pincode=findViewById(R.id.pincode);
        contact=findViewById(R.id.contact);
        email=findViewById(R.id.email_register_data);
        password=findViewById(R.id.password_register_data);
        group_gender=findViewById(R.id.group_gender);
        male=findViewById(R.id.male);
        female=findViewById(R.id.female);
        birthdate=findViewById(R.id.birthdate);
        country=findViewById(R.id.country);
        state=findViewById(R.id.state);
        city=findViewById(R.id.city);
        save_data=findViewById(R.id.save_data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email1 = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            firstname.setText(name);
            email.setText(email1);

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
            Toast.makeText(this, "Name:"+name+"\nEmail:"+email1+"\nemailVerified:"+emailVerified, Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(RegisterDataActivity.this, Home.class));
        }
    }
}
