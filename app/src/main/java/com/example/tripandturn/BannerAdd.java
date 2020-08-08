package com.example.tripandturn;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class BannerAdd extends AppCompatActivity {

    ImageView picture;
    EditText name;
    Button save;
    ProgressBar progressCircle, progressBar;
    FirebaseStorage storage;
    FirebaseDatabase database;

    private Uri mImageUri;
    private ScrollView scrollViewForm;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;

    private static final int PICK_IMAGE_REQUEST = 1;
    long maxId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        picture = findViewById(R.id.display_picture_upload);
        name = findViewById(R.id.banner_name);
        save = findViewById(R.id.add_banner);
        progressBar = findViewById(R.id.progress_upload);
        progressCircle = findViewById(R.id.progress_circle);
        scrollViewForm = findViewById(R.id.scrollview_form);

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads/banner");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("banner");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxId = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(BannerAdd.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    addBanner();
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
            Picasso.with(this).load(mImageUri).into(picture);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void addBanner() {
        if (mImageUri == null) {
            Snackbar.make(scrollViewForm, "No file selected", Snackbar.LENGTH_LONG)
                    .setAction("Choose File", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFileChooser();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .show();
        }
        if (name.getText().toString().trim() == null) {
            Snackbar.make(scrollViewForm, "Name can not be empty", Snackbar.LENGTH_LONG).show();
            name.setError("Required");
            name.requestFocus();
        }
        if (mImageUri != null && name.getText().toString().trim() != null) {
            scrollViewForm.setAlpha(0.3f);
            progressCircle.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Banner banner = new Banner(name.getText().toString().trim(), url);
                            mDatabaseReference.child(String.valueOf(maxId + 1)).setValue(banner).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    name.setText("");
                                    picture.setImageResource(R.drawable.ic_photo_150dp);
                                    scrollViewForm.setAlpha(1f);
                                    progressCircle.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(BannerAdd.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    progressCircle.setVisibility(View.GONE);
                    scrollViewForm.setAlpha(1f);
                    Toast.makeText(BannerAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

}
