package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BookPackage3 extends AppCompatActivity {

    TextView amount, account_no, bank_ifsc;
    ProgressBar progressCircle;
    Spinner bank_name;
    private ScrollView scrollViewForm;
    long maxId = 0,maxId2 = 0;
    Button done;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabaseReference1;
    private DatabaseReference mDatabaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_package3);

        init();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completePayment();
            }
        });
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            amount.setText(preferences.getString("amount", null));
        }
        mDatabaseReference1 = FirebaseDatabase.getInstance().getReference("payment");
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("package_booking");

        mDatabaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxId = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxId2 = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void completePayment() {
        final String Amount = amount.getText().toString().trim();
        String AccountNo = account_no.getText().toString().trim();
        String Bank_ifsc = bank_ifsc.getText().toString().trim();
        String Bank_name = bank_name.getSelectedItem().toString().trim();
        if (Amount.isEmpty()) {
//            amount.requestFocus();
//            amount.setError("Please provide cost");
        }
        if (AccountNo.isEmpty()) {
            account_no.requestFocus();
            account_no.setError("Please provide Account no");
        } else if (Bank_ifsc.isEmpty()) {
            bank_ifsc.requestFocus();
            bank_ifsc.setError("Please provide name");
        } else if (bank_name.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select bank", Toast.LENGTH_SHORT).show();
        } else {
            scrollViewForm.setAlpha(0.3f);
            progressCircle.setVisibility(View.VISIBLE);
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
            if (preferences.contains("reference")) {
                final String Package_id = preferences.getString("reference", null);
                Payment payment = new Payment(Amount, "Net Banking", AccountNo, Bank_name, Bank_ifsc, "10000155552552", "Bank of Baroda", "BOG34433", String.valueOf(System.currentTimeMillis()));
                mDatabaseReference1.child(String.valueOf(maxId + 1)).setValue(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 500);


                        String uid="0";
                        mAuth = FirebaseAuth.getInstance();
                        // Configure sign-in to request the user's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        mGoogleSignInClient = GoogleSignIn.getClient(BookPackage3.this, gso);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(BookPackage3.this);
                        if (currentUser!=null)
                        {
                            uid=currentUser.getUid();

                        }
                        else if (account!=null){
                            uid=account.getId();
                        }

                        PackageBook packageBook = new PackageBook(uid,Package_id,Amount,String.valueOf(maxId+1),String.valueOf(System.currentTimeMillis()));
                        mDatabaseReference2.child(String.valueOf(maxId2 + 1)).setValue(packageBook).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                }, 500);

                                Toast.makeText(BookPackage3.this,"Booking successful",Toast.LENGTH_LONG);
                                finish();
                            }
                        });
                    }
                });
            }
        }

    }

    private void init() {
        amount = findViewById(R.id.amount);
        account_no = findViewById(R.id.account_no);
        bank_ifsc = findViewById(R.id.ifsc_code);
        bank_name = findViewById(R.id.bank_name);
        done = findViewById(R.id.done);
        progressCircle = findViewById(R.id.progress_circle);
        scrollViewForm = findViewById(R.id.scrollview_form);
    }
}
