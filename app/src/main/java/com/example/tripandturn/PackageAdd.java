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
import com.google.firebase.database.ChildEventListener;
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

public class PackageAdd extends AppCompatActivity {


    private ProgressBar progressBar, progressBarCircle;
    private ScrollView scrollViewForm;
    private ImageView display_image;
    private EditText name, type, cost, persons, days, nights, description;
    private Spinner country, state, city, place;
    private Button save, cancel;

    List<String> CountryList = new ArrayList<String>();
    List<String> StateList = new ArrayList<String>();
    List<String> CityList = new ArrayList<String>();
    List<String> PlaceList = new ArrayList<String>();

    int operation_status = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;

    Package aPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_add);
        getSupportActionBar().hide();

        init();
        spinnerSync();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads/package");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("package");

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
                    Toast.makeText(PackageAdd.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    addPackage();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operation_status == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PackageAdd.this);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addPackage() {
        final String Name = name.getText().toString().trim();
        final String Type = type.getText().toString().trim();
        final String Cost = cost.getText().toString().trim();
        final String City = city.getSelectedItem().toString().trim();
        final String Place = place.getSelectedItem().toString().trim();
        final String Persons = persons.getText().toString().trim();
        final String Days = days.getText().toString().trim();
        final String Nights = nights.getText().toString().trim();
        final String Description = description.getText().toString().trim();

        if (Description.isEmpty()) {
            description.requestFocus();
            description.setError("Please provide description");
        }
        if (Nights.isEmpty()) {
            nights.requestFocus();
            nights.setError("Please provide nights");
        }
        if (Days.isEmpty()) {
            days.requestFocus();
            days.setError("Please provide days");
        }
        if (Persons.isEmpty()) {
            persons.requestFocus();
            persons.setError("Please provide  number of persons");
        }
        if (Cost.isEmpty()) {
            cost.requestFocus();
            cost.setError("Please provide cost");
        }
        if (city.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select city", Toast.LENGTH_SHORT).show();
        }
        if (place.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select place", Toast.LENGTH_SHORT).show();
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
        if (!Name.isEmpty() && !Type.isEmpty() && !Type.isEmpty() && !Cost.isEmpty() && !Persons.isEmpty() && !Days.isEmpty() && !Nights.isEmpty() && !Description.isEmpty() && mImageUri != null && city.getSelectedItemPosition() != 0) {
            scrollViewForm.setAlpha(0.3f);
            progressBar.setVisibility(View.VISIBLE);
            progressBarCircle.setVisibility(View.VISIBLE);
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(PackageAdd.this, "picture upload successful", Toast.LENGTH_LONG).show();
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            aPackage = new Package(Name, Type, Cost, Place, Persons, Days, Nights, Description, String.valueOf(System.currentTimeMillis()));
                            String url = uri.toString();
                            final Upload upload = new Upload(Name, url);

                            mDatabaseReference.child(Name).setValue(aPackage).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    Snackbar.make(scrollViewForm, "Package added successful", Snackbar.LENGTH_LONG).show();
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
                    Toast.makeText(PackageAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        progressBar = findViewById(R.id.package_progress_upload);
        progressBarCircle = findViewById(R.id.add_package_progress_circle);
        scrollViewForm = findViewById(R.id.add_package_scrollview_form);

        display_image = findViewById(R.id.package_display_picture_upload);
        name = findViewById(R.id.add_package_name);
        type = findViewById(R.id.add_package_type);
        cost = findViewById(R.id.add_package_cost);
        persons = findViewById(R.id.add_package_persons);
        days = findViewById(R.id.add_package_days);
        nights = findViewById(R.id.add_package_nights);
        description = findViewById(R.id.add_package_description);

        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        place = findViewById(R.id.place);

        save = findViewById(R.id.add_package_save);
        cancel = findViewById(R.id.add_package_cancel);

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

        //Handle City select event And Fetch Place accordingly
        final ArrayAdapter<String> PlaceAdepter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PlaceList);
        PlaceAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        place.setAdapter(PlaceAdepter);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                PlaceList.clear();
                PlaceList.add(0, "Select place");
                FirebaseDatabase.getInstance().getReference().child("place").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.child("city").getValue().toString().equals(parent.getItemAtPosition(position)))
                        {
                            PlaceList.add(dataSnapshot.getKey().toString());
                        }
                        PlaceAdepter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        cost.setText("");
        persons.setText("");
        days.setText("");
        nights.setText("");
        description.setText("");
        display_image.setImageResource(R.drawable.ic_photo_150dp);
        operation_status = 1;
    }
}
