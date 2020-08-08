package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class HotelDetailsActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseStorage storage;
    ImageView picture;
    TextView title, name, type, city, address, contact, email, website, description, lastUpdate;
    String Reff, pictureUri;
    Button update, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);
        final ActionBar actionBar = getSupportActionBar();

        init();
        clickIntents();
        storage = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("hotel");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            Reff = preferences.getString("reference", null);
            actionBar.setTitle(Reff);
            reference = reference.child(Reff);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        title.setText(dataSnapshot.child("name").getValue().toString());
                        name.setText(dataSnapshot.child("name").getValue().toString());
                        type.setText(dataSnapshot.child("type").getValue().toString());
                        city.setText(dataSnapshot.child("city").getValue().toString());
                        address.setText(dataSnapshot.child("address").getValue().toString());
                        contact.setText(dataSnapshot.child("contact").getValue().toString());
                        email.setText(dataSnapshot.child("email").getValue().toString());
                        website.setText(dataSnapshot.child("website").getValue().toString());
                        description.setText(dataSnapshot.child("description").getValue().toString());
                        lastUpdate.setText(convertDate(dataSnapshot.child("lastUpdateTime").getValue().toString(), "dd/MM/yyyy hh:mm:ss"));
                        if (dataSnapshot.hasChild("picture")) {
                            pictureUri = dataSnapshot.child("picture").child("mImageUrl").getValue().toString();
                            Picasso.with(HotelDetailsActivity.this)
                                    .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                                    .placeholder(R.mipmap.ic_launcher_foreground)
                                    .fit()
                                    .centerCrop()
                                    .into(picture);
                        }
                    } else {
                        Toast.makeText(HotelDetailsActivity.this, "Data may be removed", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(HotelDetailsActivity.this, HotelUpdateActivity.class).putExtra("reference", Reff));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HotelDetailsActivity.this)
                        .setTitle("Remove hotel")
                        .setMessage("Do you really want to remove this hotel")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(HotelDetailsActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(HotelDetailsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
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
        picture = findViewById(R.id.details_picture);
        title = findViewById(R.id.details_title);
        name = findViewById(R.id.details_name);
        type = findViewById(R.id.details_type);
        city = findViewById(R.id.details_city);
        address = findViewById(R.id.details_address);
        contact = findViewById(R.id.details_contact);
        email = findViewById(R.id.details_email);
        website = findViewById(R.id.details_website);
        description = findViewById(R.id.details_description);
        lastUpdate = findViewById(R.id.details_update_time);
        update = findViewById(R.id.details_update);
        delete = findViewById(R.id.details_delete);
    }

    private void clickIntents() {
        findViewById(R.id.open_website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HotelDetailsActivity.this)
                        .setMessage("Open website?")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Open website", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri webpage;
                        if (!website.getText().toString().trim().startsWith("http://") && !website.getText().toString().trim().startsWith("https://")) {
                            webpage = Uri.parse("http://" + website.getText().toString().trim());
                        } else {
                            webpage = Uri.parse(website.getText().toString().trim());
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(browserIntent);
                    }
                }).show();

            }
        });
        findViewById(R.id.open_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HotelDetailsActivity.this)
                        .setMessage("Open mail?")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String subject = "Feedback/Support";
                        String body = "Hello " + name.getText().toString().trim() + ",\n \tMy self Admin of Trip and turn tours and travels. \n\n Regards,\n  Admin@tripandturn";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:" + email.getText().toString().trim() + "?subject=" + subject + "&body=" + body);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }).show();
            }
        });
        findViewById(R.id.open_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HotelDetailsActivity.this)
                        .setMessage("Open contact number in dialer?")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + contact.getText().toString().trim()));
                        startActivity(intent);
                    }
                }).show();
            }
        });

    }

}
