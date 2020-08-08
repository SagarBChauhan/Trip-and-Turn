package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripandturn.DB.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText emailid, password;
    Button register;
    ProgressBar progress_signup;
    TextView signin;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailid.getText().toString().trim();
                String pwd=password.getText().toString().trim();

                if (email.isEmpty())
                {
                    emailid.setError("Please enter email");
                    emailid.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailid.setError("Please enter valid email");
                    emailid.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(pwd.length()<6)
                {
                    password.setError("Password must be at lest 6 character long");
                    password.requestFocus();
                }
                else if(pwd.isEmpty() && email.isEmpty())
                {
                    emailid.setError("Please enter email");
                    password.setError("Please enter password");
                    emailid.requestFocus();
                }
                else if(!(pwd.isEmpty() &&email.isEmpty()))
                {
                    createAccount(email,pwd);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });
    }

    private void init() {
        emailid=findViewById(R.id.email_register);
        password=findViewById(R.id.password_register);
        register=findViewById(R.id.register_register);
        signin=findViewById(R.id.signin_register);
        progress_signup=findViewById(R.id.progress_signup);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void createAccount(final String email, final String password)
    {
        register.setVisibility(View.GONE);
        progress_signup.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private String TAG;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress_signup.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "User registered successfully in auth", Toast.LENGTH_SHORT).show();

                            Login info=new Login(mAuth.getCurrentUser().getUid(),email,password.toString(),"Customer","1", convertDate(String.valueOf(System.currentTimeMillis()), "dd/MM/yyyy hh:mm:ss"));
//                            FirebaseDatabase.getInstance().getReference("Login")
//                                    .child(mAuth.getCurrentUser().getUid())
//                                    .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    updateUI(user);
//                                }
//                            });
                        }
                        else
                        {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();
            if (user.isEmailVerified()) {
                FirebaseDatabase.getInstance().getReference("Login").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Type").getValue().toString().trim().equals("Admin")) {
                            finish();
                            startActivity(new Intent(MainActivity.this, AdminActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Customer")) {
                            finish();
                            startActivity(new Intent(MainActivity.this, CustomerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Employee")) {
                            finish();
                            startActivity(new Intent(MainActivity.this, EmployeeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Guide")) {
                            Toast.makeText(MainActivity.this, "Hello Guide", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "User type unknown", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(MainActivity.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
    }
    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

}
