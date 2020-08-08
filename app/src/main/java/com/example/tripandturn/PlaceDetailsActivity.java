package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PlaceDetailsActivity extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseStorage storage;
    Button update, delete;
    ImageView picture;
    TextView name, type, description, city, title;
    String placeReff, pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        final ActionBar actionBar = getSupportActionBar();
        init();

        Intent intent = getIntent();
        storage = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("place");

        SharedPreferences preferences=getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference"))
        {
            placeReff = preferences.getString("reference",null);
            actionBar.setTitle(placeReff);
            reference = reference.child(placeReff);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        title.setText(dataSnapshot.child("name").getValue().toString());
                        name.setText(dataSnapshot.child("name").getValue().toString());
                        type.setText(dataSnapshot.child("type").getValue().toString());
                        city.setText(dataSnapshot.child("city").getValue().toString());
                        description.setText(dataSnapshot.child("description").getValue().toString());
//                picture.setImageURI(Uri.parse(dataSnapshot.child("picture").child("mImageUrl").getValue().toString()));
                        if (dataSnapshot.hasChild("picture")) {
                            pictureUri = dataSnapshot.child("picture").child("mImageUrl").getValue().toString();
                            Picasso.with(PlaceDetailsActivity.this)
                                    .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                                    .placeholder(R.mipmap.ic_launcher_foreground)
                                    .fit()
                                    .centerCrop()
                                    .into(picture);
                        }
                    } else {
                        Toast.makeText(PlaceDetailsActivity.this, "Data may be removed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "no reference found please try again", Toast.LENGTH_SHORT).show();
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceDetailsActivity.this, PlaceUpdateActivity.class).putExtra("placeReference", placeReff));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PlaceDetailsActivity.this)
                        .setTitle("Remove place")
                        .setMessage("Do you really want to remove this place")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(PlaceDetailsActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageReference imageRef = storage.getReferenceFromUrl(pictureUri);
                                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PlaceDetailsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }).show();
            }
        });
    }

    private void init() {
        picture = findViewById(R.id.place_details_picture);
        title = findViewById(R.id.place_details_title);
        name = findViewById(R.id.place_details_name);
        type = findViewById(R.id.place_details_type);
        city = findViewById(R.id.place_details_city);
        description = findViewById(R.id.place_details_description);
        update = findViewById(R.id.place_details_update);
        delete = findViewById(R.id.place_details_delete);
    }
}
