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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

public class TransportUpdateActivity extends AppCompatActivity {


    private ProgressBar progressBar, progressBarCircle;
    private ScrollView scrollViewForm;
    private ImageView display_image;
    private EditText company_name, name, model, type, cost;
    private Button save, cancel;

    int operation_status = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;
    String Reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_update);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update details");
        init();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("reference", Context.MODE_PRIVATE);
        if (preferences.contains("reference")) {
            Reference = preferences.getString("reference", null);
            mStorageReference = FirebaseStorage.getInstance().getReference("uploads/transport");
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("transport").child(Reference);
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
                    Toast.makeText(TransportUpdateActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updateTransport();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operation_status == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransportUpdateActivity.this);
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
                if (dataSnapshot.hasChild("name")) {
                    company_name.setText(dataSnapshot.child("companyName").getValue().toString());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    model.setText(dataSnapshot.child("model").getValue().toString());
                    type.setText(dataSnapshot.child("type").getValue().toString());
                    cost.setText(dataSnapshot.child("cost").getValue().toString());
                    if (dataSnapshot.hasChild("picture")) {
                        Picasso.with(TransportUpdateActivity.this)
                                .load(dataSnapshot.child("picture").child("mImageUrl").getValue().toString())
                                .placeholder(R.mipmap.ic_launcher_foreground)
                                .fit()
                                .centerCrop()
                                .into(display_image);
                    }
                } else {
                    Toast.makeText(TransportUpdateActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateTransport() {
        final String CompanyName = company_name.getText().toString().trim();
        final String Name = name.getText().toString().trim();
        final String Model = model.getText().toString().trim();
        final String Type = type.getText().toString().trim();
        final String Cost = cost.getText().toString().trim();

        if (Cost.isEmpty()) {
            cost.requestFocus();
            cost.setError("Please provide cost");
        }
        if (Type.isEmpty()) {
            type.requestFocus();
            type.setError("Please provide type");
        }
        if (Model.isEmpty()) {
            model.requestFocus();
            model.setError("Please provide name");
        }
        if (Name.isEmpty()) {
            name.requestFocus();
            name.setError("Please provide name");
        }
        if (CompanyName.isEmpty()) {
            company_name.requestFocus();
            company_name.setError("Please provide name");
        }
        if (!CompanyName.isEmpty() && !Name.isEmpty() && !Model.isEmpty() && !Type.isEmpty() && !Cost.isEmpty() ) {
            scrollViewForm.setAlpha(0.3f);
            progressBarCircle.setVisibility(View.VISIBLE);

            mDatabaseReference.child("companyName").setValue(Name);
            mDatabaseReference.child("name").setValue(Name);
            mDatabaseReference.child("model").setValue(Model);
            mDatabaseReference.child("type").setValue(Type);
            mDatabaseReference.child("cost").setValue(Cost);
            mDatabaseReference.child("lastUpdateTime")
                    .setValue(String.valueOf(System.currentTimeMillis())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    Toast.makeText(TransportUpdateActivity.this, "Transport update successful", Toast.LENGTH_SHORT).show();
                    progressBarCircle.setVisibility(View.GONE);
                    scrollViewForm.setAlpha(1f);
                }
            });
        }

    }

    private void init() {
        progressBar = findViewById(R.id.progress_upload);
        progressBarCircle = findViewById(R.id.progress_circle);
        company_name = findViewById(R.id.company_name);
        name = findViewById(R.id.transport_name);
        model = findViewById(R.id.model);
        type = findViewById(R.id.type);
        cost = findViewById(R.id.cost);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        display_image = findViewById(R.id.display_picture_upload);
        scrollViewForm = findViewById(R.id.scrollview_form);
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
                    Toast.makeText(TransportUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
