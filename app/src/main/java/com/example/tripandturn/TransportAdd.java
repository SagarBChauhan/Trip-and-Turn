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
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class TransportAdd extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_add);
        getSupportActionBar().hide();

        init();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads/transport");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("transport");

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
                    Toast.makeText(TransportAdd.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    addTransport();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (operation_status == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransportAdd.this);
                    builder.setTitle("Exit without saving data")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getFragmentManager().popBackStackImmediate();
                                    finish();
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
    private void addTransport() {
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
        if (!CompanyName.isEmpty() &&!Name.isEmpty() && !Model.isEmpty() && !Type.isEmpty() && !Cost.isEmpty() && mImageUri != null ) {
            scrollViewForm.setAlpha(0.3f);
            progressBar.setVisibility(View.VISIBLE);
            progressBarCircle.setVisibility(View.VISIBLE);
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(TransportAdd.this, "picture upload successful", Toast.LENGTH_LONG).show();
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Transport transport  = new Transport(CompanyName,Name,Model,Type,Cost,String.valueOf(System.currentTimeMillis()));
                            String url = uri.toString();
                            final Upload upload = new Upload(Name, url);

                            mDatabaseReference.child(CompanyName+" "+Name+" "+Model).setValue(transport).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);

                                    mDatabaseReference.child(CompanyName+" "+Name+" "+Model).child("picture").setValue(upload);
                                    Snackbar.make(scrollViewForm, "Transport added successful", Snackbar.LENGTH_LONG).show();
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
                    Toast.makeText(TransportAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        model.setText("");
        company_name.setText("");
        display_image.setImageResource(R.drawable.ic_photo_150dp);
        operation_status = 1;
    }
}
