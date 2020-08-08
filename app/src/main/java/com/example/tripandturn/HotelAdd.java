package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.regex.Pattern;

public class HotelAdd extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    int operation_status =0;
    private ImageView display_image;
    private Button save, cancel;
    private EditText name, type, address, contactNo, email, website, description;
    private ProgressBar progressBar, progressBarCircle;
    private Spinner country, state, city;
    List<String> CountryList = new ArrayList<String>();
    List<String> StateList = new ArrayList<String>();
    List<String> CityList = new ArrayList<String>();
    private Uri mImageUri;
    private ScrollView scrollViewForm;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;

    Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_add);
        getSupportActionBar().hide();

        init();
        spinnerSync();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads/hotel");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("hotel");

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
                    Toast.makeText(HotelAdd.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    addHotel();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operation_status==0) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(HotelAdd.this);
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
                }
                else {
                    finish();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addHotel() {
        final String Name = name.getText().toString().trim();
        final String Type = type.getText().toString().trim();
        final String Address = address.getText().toString().trim();
        final String City = city.getSelectedItem().toString().trim();
        final String Contact = contactNo.getText().toString().trim();
        final String Email = email.getText().toString().trim();
        final String Website = website.getText().toString().trim();
        final String Description = description.getText().toString().trim();

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
        if (mImageUri == null) {
            Snackbar.make(scrollViewForm, "No file selected", Snackbar.LENGTH_LONG)
                    .setAction("Choose File", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFileChooser();
                        }
                    })
                    .setActionTextColor(getColor(R.color.colorPrimary))
                    .show();
        }
        if (!Name.isEmpty() && !Type.isEmpty() && !Address.isEmpty() && !Contact.isEmpty() && !Email.isEmpty() && !Website.isEmpty() && !Description.isEmpty() && mImageUri != null && city.getSelectedItemPosition() != 0) {
            scrollViewForm.setAlpha(0.3f);
            progressBarCircle.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(HotelAdd.this, "picture upload successful", Toast.LENGTH_LONG).show();
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            hotel = new Hotel(Name, Type, Address, City, Contact, Email, Website, Description,String.valueOf(System.currentTimeMillis()));
                            String url = uri.toString();
                            final Upload upload = new Upload(Name, url);

                            mDatabaseReference.child(Name).setValue(hotel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);

                                    mDatabaseReference.child(Name).child("picture").setValue(upload);
                                    Snackbar.make(scrollViewForm, "Hotel added successful", Snackbar.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    progressBarCircle.setVisibility(View.GONE);
                                    scrollViewForm.setAlpha(1f);
                                    unbind();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    progressBarCircle.setVisibility(View.GONE);
                    scrollViewForm.setAlpha(1f);
                    Toast.makeText(HotelAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }

    }

    private void init() {
        display_image = findViewById(R.id.hotel_display_picture_upload);
        name = findViewById(R.id.add_hotel_name);
        type = findViewById(R.id.add_hotel_type);
        address = findViewById(R.id.add_hotel_address);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        contactNo = findViewById(R.id.add_hotel_contact);
        email = findViewById(R.id.add_hotel_email);
        website = findViewById(R.id.add_hotel_website);
        description = findViewById(R.id.add_hotel_description);
        save = findViewById(R.id.add_hotel_save);
        cancel = findViewById(R.id.add_hotel_cancel);

        progressBar = findViewById(R.id.hotel_progress_upload);
        progressBarCircle = findViewById(R.id.add_hotel_progress_circle);
        scrollViewForm = findViewById(R.id.add_hotel_scrollview_form);
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
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void unbind() {
        name.setText("");
        type.setText("");
        address.setText("");
        contactNo.setText("");
        email.setText("");
        email.setText("");
        website.setText("");
        description.setText("");
        display_image.setImageResource(R.drawable.ic_photo_150dp);
        operation_status=1;
    }

}
