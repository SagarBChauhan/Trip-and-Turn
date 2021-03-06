package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class TransportDetailsActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseStorage storage;
    ImageView picture;
    TextView company_name, name, model, type, cost, lastUpdate;
    String Reference, pictureUri;
    Button update, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_details);
        final ActionBar actionBar = getSupportActionBar();

        init();
        storage = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("transport");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            Reference = preferences.getString("reference", null);
            actionBar.setTitle(Reference);
            reference = reference.child(Reference);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        company_name.setText(dataSnapshot.child("companyName").getValue().toString());
                        name.setText(dataSnapshot.child("name").getValue().toString());
                        type.setText(dataSnapshot.child("type").getValue().toString());
                        model.setText(dataSnapshot.child("model").getValue().toString());
                        cost.setText(dataSnapshot.child("cost").getValue().toString());
                        lastUpdate.setText(convertDate(dataSnapshot.child("lastUpdateTime").getValue().toString(), "dd/MM/yyyy hh:mm:ss"));
                        if (dataSnapshot.hasChild("picture")) {
                            pictureUri = dataSnapshot.child("picture").child("mImageUrl").getValue().toString();
                            Picasso.with(TransportDetailsActivity.this)
                                    .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                                    .placeholder(R.mipmap.ic_launcher_foreground)
                                    .fit()
                                    .centerCrop()
                                    .into(picture);
                        }
                    } else {
                        Toast.makeText(TransportDetailsActivity.this, "Data may be removed", Toast.LENGTH_SHORT).show();
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransportDetailsActivity.this, TransportUpdateActivity.class));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TransportDetailsActivity.this)
                        .setTitle("Remove transport")
                        .setMessage("Do you really want to remove this transport?")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(TransportDetailsActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(TransportDetailsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
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
    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    private void init() {
        company_name=findViewById(R.id.details_company_name);
        name=findViewById(R.id.details_name);
        model=findViewById(R.id.details_model);
        type=findViewById(R.id.details_type);
        cost=findViewById(R.id.details_cost);
        lastUpdate=findViewById(R.id.details_update_time);
        update=findViewById(R.id.details_update);
        delete=findViewById(R.id.details_delete);
        picture=findViewById(R.id.details_picture);
    }
}
