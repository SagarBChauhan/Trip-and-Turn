package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "loginUpActivity";
    EditText emailid, password;
    Button signin;
    TextView signup;
    ProgressBar prgress_signin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    long back_pressed;
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailid.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                if (email.isEmpty()) {
                    emailid.setError("Please enter email");
                    emailid.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailid.setError("Please enter valid email");
                    emailid.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter password");
                    password.requestFocus();
                } else if (pwd.length() < 6) {
                    password.setError("Password must be at lest 6 character long");
                    password.requestFocus();
                } else if (pwd.isEmpty() && email.isEmpty()) {
                    emailid.setError("Please enter email");
                    password.setError("Please enter password");
                    emailid.requestFocus();
                } else if (!(pwd.isEmpty() && email.isEmpty())) {
                    signIn(email, pwd);
                } else {
                    Toast.makeText(LoginActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateGoogleUI(account);


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateGoogleUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            updateGoogleUI(null);
        }
    }

    private void updateGoogleUI(GoogleSignInAccount account) {
        if (account != null) {
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();

            GoogleLogin login = new GoogleLogin(personId, personName, personGivenName, personFamilyName, personEmail, String.valueOf(personPhoto), "Customer");
            FirebaseDatabase.getInstance().getReference("GoogleLogin")
                    .child(personId).setValue(login)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(LoginActivity.this, CustomerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            Toast.makeText(LoginActivity.this, "Signed in google", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    public void signIn(final String email, final String password) {
        signin.setVisibility(View.GONE);
        prgress_signin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    private String TAG;

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        prgress_signin.setVisibility(View.GONE);
                        signin.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference("Login")
                                    .child(mAuth.getCurrentUser().getUid()).child("LastUpdateTime")
                                    .setValue(convertDate(String.valueOf(System.currentTimeMillis()), "dd/MM/yyyy hh:mm:ss"))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                        }
                                    });

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Customer")) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, CustomerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Employee")) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, EmployeeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else if (dataSnapshot.child("Type").getValue().toString().trim().equals("Guide")) {
                            Toast.makeText(LoginActivity.this, "Hello Guide", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "User type unknown", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(LoginActivity.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(),
                    "Press once again to exit!", Toast.LENGTH_SHORT)
                    .show();
        }
        back_pressed = System.currentTimeMillis();
    }
    public void init() {
        emailid = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signin = findViewById(R.id.signin_login);
        signup = findViewById(R.id.signup_login);
        prgress_signin = findViewById(R.id.progress_signin);
        signInButton = findViewById(R.id.sign_in_button);
    }
}
