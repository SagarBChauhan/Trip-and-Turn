package com.example.tripandturn;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BookPackage2 extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseStorage storage;
    ImageView picture;
    TextView title, name, type, place, cost, persons,days,nights, description, lastUpdate;
    String Reference, pictureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_package2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE).edit().putString("amount",cost.getText().toString().trim()).commit();

                startActivity(new Intent(BookPackage2.this,BookPackage3.class));
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        storage = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("package");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            Reference = preferences.getString("reference", null);
            actionBar.setTitle(Reference);
            reference = reference.child(Reference);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        title.setText(dataSnapshot.child("name").getValue().toString());
                        name.setText(dataSnapshot.child("name").getValue().toString());
                        type.setText(dataSnapshot.child("type").getValue().toString());
                        place.setText(dataSnapshot.child("place").getValue().toString());
                        cost.setText(dataSnapshot.child("cost").getValue().toString());
                        persons.setText(dataSnapshot.child("persons").getValue().toString());
                        days.setText(dataSnapshot.child("days").getValue().toString());
                        nights.setText(dataSnapshot.child("nights").getValue().toString());
                        description.setText(dataSnapshot.child("description").getValue().toString());
                        lastUpdate.setText(convertDate(dataSnapshot.child("lastUpdateTime").getValue().toString(), "dd/MM/yyyy hh:mm:ss"));
                        if (dataSnapshot.hasChild("picture")) {
                            pictureUri = dataSnapshot.child("picture").child("mImageUrl").getValue().toString();
                            Picasso.with(getApplicationContext())
                                    .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                                    .placeholder(R.mipmap.ic_launcher_foreground)
                                    .fit()
                                    .centerCrop()
                                    .into(picture);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Data may be removed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "no reference found please try again", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, BookPackage2.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
    private void init() {
        picture = findViewById(R.id.details_picture);
        title = findViewById(R.id.details_title);
        name = findViewById(R.id.details_name);
        type = findViewById(R.id.details_type);
        place = findViewById(R.id.details_place);
        cost = findViewById(R.id.details_cost);
        persons = findViewById(R.id.details_persons);
        days = findViewById(R.id.details_days);
        nights= findViewById(R.id.details_nights);
        description = findViewById(R.id.details_description);
        lastUpdate = findViewById(R.id.details_update_time);

    }
}
