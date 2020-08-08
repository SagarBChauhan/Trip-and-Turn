package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class PlaceUpdateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView display_image;
    private Button upload, show;
    private Button save, cancel;
    private EditText name, type, description;
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
    String Reference, City;
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_update);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update details");

        init();
        spinnerSync();
        spinnerToggle();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            //Storage and Database location
            Reference = preferences.getString("reference", null);
            mStorageReference = FirebaseStorage.getInstance().getReference("uploads/place");
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("place").child(Reference);
            fetchData();
        } else {
            Toast.makeText(this, "no reference found please try again", Toast.LENGTH_SHORT).show();
        }


        //open image picker + upload on select new image
        display_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(PlaceUpdateActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    openFileChooser();
                }
            }
        });

        //update details
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(PlaceUpdateActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updatePlace();
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
//                city.setSelection(1);
                City = dataSnapshot.child("city").getValue().toString();
                type.setText(dataSnapshot.child("type").getValue().toString());
                description.setText(dataSnapshot.child("description").getValue().toString());
                if (dataSnapshot.hasChild("picture")) {
                    Picasso.with(PlaceUpdateActivity.this)
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
    private void updatePlace() {
        final String Name = name.getText().toString().trim();
        final String Type = type.getText().toString().trim();
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
        if (Name.isEmpty()) {
            name.requestFocus();
            name.setError("Please provide place name");
        }
        if (Type.isEmpty()) {
            type.requestFocus();
            type.setError("Please provide place type");
        }
        if (Description.isEmpty()) {
            description.requestFocus();
            description.setError("Please provide place description");
        }
        if (!Name.isEmpty() && !Type.isEmpty() && !Description.isEmpty()) {
            scrollViewForm.setAlpha(0.3f);
            progressBarCircle.setVisibility(View.VISIBLE);

//            place = new Place(Name, Type, City, Description);
            mDatabaseReference.child("name").setValue(Name);
            mDatabaseReference.child("type").setValue(Type);
            mDatabaseReference.child("city").setValue(City);
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

                    Toast.makeText(PlaceUpdateActivity.this, "Place update successful", Toast.LENGTH_SHORT).show();
                    progressBarCircle.setVisibility(View.GONE);
                    scrollViewForm.setAlpha(1f);
                }
            });
        }
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
//            display_image.setImageURI(mImageUri);
            uploadFile();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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
                            String uploadId = mDatabaseReference.push().getKey();
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
                    Toast.makeText(PlaceUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        display_image = findViewById(R.id.place_display_picture_upload);
        name = findViewById(R.id.update_place_name);
        save = findViewById(R.id.update_place_save);
        cancel = findViewById(R.id.update_place_cancel);
        type = findViewById(R.id.update_place_type);
        description = findViewById(R.id.update_place_description);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        progressBar = findViewById(R.id.place_progress_upload);
        progressBarCircle = findViewById(R.id.update_place_progress_circle);
        scrollViewForm = findViewById(R.id.update_place_scrollview_form);
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

    private void spinnerToggle() {
        final TableRow sp1, sp2, sp3, sp4;
        sp1 = findViewById(R.id.sp1);
        sp2 = findViewById(R.id.sp2);
        sp3 = findViewById(R.id.sp3);
        findViewById(R.id.sp_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.sp_toggle_text);

                if (sp1.getVisibility() == View.GONE) {
                    textView.setText("Hide place");
                    sp1.setVisibility(View.VISIBLE);
                    sp2.setVisibility(View.VISIBLE);
                    sp3.setVisibility(View.VISIBLE);
                } else {
                    textView.setText("Show place");
                    sp1.setVisibility(View.GONE);
                    sp2.setVisibility(View.GONE);
                    sp3.setVisibility(View.GONE);
                }

            }
        });
    }

}
