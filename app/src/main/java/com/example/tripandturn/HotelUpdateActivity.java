package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotelUpdateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    int operation_status = 0;
    private ImageView display_image;
    private Button save, cancel;
    private EditText name, type, address, contactNo, email, website, description;
    private ProgressBar progressBar, progressBarCircle;
    private Spinner country, state, city;
    private ScrollView scrollViewForm;

    List<String> CountryList = new ArrayList<String>();
    List<String> StateList = new ArrayList<String>();
    List<String> CityList = new ArrayList<String>();

    private Uri mImageUri;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;

    String Reference, City;
    Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_update);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update details");

        init();
        spinnerSync();
        spinnerToggle();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            Reference = preferences.getString("reference", null);
            mStorageReference = FirebaseStorage.getInstance().getReference("uploads/hotel");
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("hotel").child(Reference);
            fetchData();
        } else {
            Toast.makeText(this, "no reference found please try again", Toast.LENGTH_SHORT).show();
        }

        display_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(HotelUpdateActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updateHotel();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operation_status == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HotelUpdateActivity.this);
                    builder.setTitle("Exit without saving data")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getFragmentManager().popBackStackImmediate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                } else {
                    finish();
                }
            }
        });
    }

    private void fetchData() {
        //Get Data to controls
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                type.setText(dataSnapshot.child("type").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
                City = dataSnapshot.child("city").getValue().toString();
                contactNo.setText(dataSnapshot.child("contact").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                website.setText(dataSnapshot.child("website").getValue().toString());
                description.setText(dataSnapshot.child("description").getValue().toString());
                if (dataSnapshot.hasChild("picture")) {
                    Picasso.with(HotelUpdateActivity.this)
                            .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                            .placeholder(R.mipmap.ic_launcher_foreground)
                            .fit()
                            .centerCrop()
                            .into(display_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateHotel() {
        final String Name = name.getText().toString().trim();
        final String Type = type.getText().toString().trim();
        final String Address = address.getText().toString().trim();
        final String Contact = contactNo.getText().toString().trim();
        final String Email = email.getText().toString().trim();
        final String Website = website.getText().toString().trim();
        final String Description = description.getText().toString().trim();
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Description.isEmpty()) {
            description.requestFocus();
            description.setError("Please provide description");
        }
        if (Website.isEmpty()) {
            website.requestFocus();
            website.setError("Please provide website");
        } else if (!Patterns.WEB_URL.matcher(Website).matches()) {
            website.requestFocus();
            website.setError("Please provide valid website");
        }
        if (Email.isEmpty()) {
            email.requestFocus();
            email.setError("Please provide email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.requestFocus();
            email.setError("Please provide valid email");
        }
        if (Contact.isEmpty()) {
            contactNo.requestFocus();
            contactNo.setError("Please provide contact number");
        } else if (!Patterns.PHONE.matcher(Contact).matches()) {
            contactNo.requestFocus();
            contactNo.setError("Please provide valid contact number");
        }
        if (Address.isEmpty()) {
            address.requestFocus();
            address.setError("Please provide address");
        }
        if (city.getSelectedItemPosition() == 0) {
            city.requestFocus();
            Toast.makeText(this, "Please select city", Toast.LENGTH_SHORT).show();
        }
        if (Type.isEmpty()) {
            type.requestFocus();
            type.setError("Please provide type");
        }
        if (Name.isEmpty()) {
            name.requestFocus();
            name.setError("Please provide name");
        }
        if (!Name.isEmpty() && !Type.isEmpty() && !Address.isEmpty() && !Contact.isEmpty() && !Email.isEmpty() && !Website.isEmpty() && !Description.isEmpty() && city.getSelectedItemPosition() != 0) {
            scrollViewForm.setAlpha(0.3f);
            progressBarCircle.setVisibility(View.VISIBLE);

            mDatabaseReference.child("name").setValue(Name);
            mDatabaseReference.child("type").setValue(Type);
            mDatabaseReference.child("address").setValue(Address);
            mDatabaseReference.child("city").setValue(City);
            mDatabaseReference.child("contact").setValue(Contact);
            mDatabaseReference.child("email").setValue(Email);
            mDatabaseReference.child("website").setValue(Website);
            mDatabaseReference.child("description").setValue(Description).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    Toast.makeText(HotelUpdateActivity.this, "Hotel update successful", Toast.LENGTH_SHORT).show();
                    progressBarCircle.setVisibility(View.GONE);
                    scrollViewForm.setAlpha(1f);
                }
            });
        }

    }

    private void uploadFile() {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            progressBar.setVisibility(View.VISIBLE);
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Upload upload = new Upload(name.getText().toString().trim(), url);
                            mDatabaseReference.child("picture").setValue(upload);
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(scrollViewForm, "Picture updated successfully", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HotelUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }
    private void init() {
        display_image = findViewById(R.id.hotel_display_picture_upload);
        name = findViewById(R.id.update_hotel_name);
        type = findViewById(R.id.update_hotel_type);
        address = findViewById(R.id.update_hotel_address);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        contactNo = findViewById(R.id.update_hotel_contact);
        email = findViewById(R.id.update_hotel_email);
        website = findViewById(R.id.update_hotel_website);
        description = findViewById(R.id.update_hotel_description);
        save = findViewById(R.id.update_hotel_save);
        cancel = findViewById(R.id.update_hotel_cancel);

        progressBar = findViewById(R.id.hotel_progress_upload);
        progressBarCircle = findViewById(R.id.update_hotel_progress_circle);
        scrollViewForm = findViewById(R.id.update_hotel_scrollview_form);
    }
    private void spinnerSync() {
        //Fetch Country
        final ArrayAdapter<String> CountryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CountryList);
        CountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(CountryAdapter);
        FirebaseDatabase.getInstance().getReference("Country").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long length = dataSnapshot.getChildrenCount();
                CountryList.clear();
                CountryList.add(0, "Select country");
                for (long i = 0; i < length; i++) {
                    CountryList.add(dataSnapshot.child(String.valueOf(i)).child("Name").getValue().toString());
                    CountryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Handle Country select event And Fetch State accordingly
        final ArrayAdapter<String> StateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StateList);
        StateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        state.setAdapter(StateAdapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FirebaseDatabase.getInstance().getReference("Country").child(String.valueOf(position - 1)).child("State").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long length = dataSnapshot.getChildrenCount();
                        StateList.clear();
                        StateList.add(0, "Select state");
                        for (long i = 0; i < length; i++) {
                            StateList.add(dataSnapshot.child(String.valueOf(i)).child("Name").getValue().toString());
                            StateAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Handle State select event And Fetch City accordingly
        final ArrayAdapter<String> CityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CityList);
        CityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(CityAdapter);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FirebaseDatabase.getInstance().getReference("Country").child(String.valueOf(country.getSelectedItemPosition() - 1)).child("State").child(String.valueOf(position - 1)).child("City").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long length = dataSnapshot.getChildrenCount();
                        CityList.clear();
                        CityList.add(0, "Select city");
                        for (long i = 0; i < length; i++) {
                            CityList.add(dataSnapshot.child(String.valueOf(i)).child("Name").getValue().toString());
                            CityAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void spinnerToggle(){
        final TableRow sp1,sp2,sp3,sp4;
        sp1=findViewById(R.id.sp1);
        sp2=findViewById(R.id.sp2);
        sp3=findViewById(R.id.sp3);
        findViewById(R.id.sp_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView=findViewById(R.id.sp_toggle_text);

                if (sp1.getVisibility()==View.GONE)
                {
                    textView.setText("Hide place");
                    sp1.setVisibility(View.VISIBLE);
                    sp2.setVisibility(View.VISIBLE);
                    sp3.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setText("Show place");
                    sp1.setVisibility(View.GONE);
                    sp2.setVisibility(View.GONE);
                    sp3.setVisibility(View.GONE);
                }

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select new profile picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(display_image);
            uploadFile();
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}
