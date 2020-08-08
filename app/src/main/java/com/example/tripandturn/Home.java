package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    Button logout, verify_email;
    TextView username;
    View home_layout;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home_layout=findViewById(R.id.home_layout);
        init();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        verify_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Home.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
//                        Snackbar.make(home_layout,"Verification email sent!",Snackbar.LENGTH_INDEFINITE).show();
//                        FirebaseAuth.getInstance().signOut();
                        finish();
//                        startActivity(new Intent(Home.this,LoginActivity.class));
                        Toast.makeText(Home.this, "Open mail and click on link to verify email and login again!", Toast.LENGTH_LONG).show();

                    }
                });
//                startActivity(new Intent(Home.this,VerifyEmail.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(Home.this,MainActivity.class));
            }
        });
//        register_data.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Home.this,RegisterDataActivity.class));
//            }
//        });

    }

    private void init() {
        ImageView slide=findViewById(R.id.slideshow1);
        logout=findViewById(R.id.logout);
        verify_email=findViewById(R.id.verify);
        username=findViewById(R.id.username);
//        register_data=findViewById(R.id.registerdata);
        AnimationDrawable animationDrawable= (AnimationDrawable) slide.getDrawable();
        animationDrawable.start();
    }

    @Override
    protected void onStart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
            username.setText(email);
//            Toast.makeText(this, "Name:"+name+"\nEmail:"+email+"\nemailVerified:"+emailVerified, Toast.LENGTH_SHORT).show();

            if(emailVerified) {
                finish();
                Toast.makeText(this, "verified", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Home.this, AdminActivity.class));
            }
            else
            {
                Toast.makeText(this, "Not verified yet", Toast.LENGTH_SHORT).show();
            }
        }
        super.onStart();
    }
}
