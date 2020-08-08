package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Profile extends AppCompatActivity {

    private static final int CHOOSE_IMAGE =101 ;
    ImageView profile;
    EditText name;
    ProgressBar progressBar;
    Uri uriProfilePicture;
    String profilePictureUploadUrl;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        init();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        findViewById(R.id.save_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }

    private void saveUserInfo() {
        String diplayName=name.getText().toString();

        if (diplayName.isEmpty())
        {
            name.setError("name Required");
            name.requestFocus();
            return;
        }
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null && profilePictureUploadUrl!=null)
        {
            UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder()
                    .setDisplayName(diplayName)
                    .setPhotoUri(Uri.parse(profilePictureUploadUrl))
                    .build();
            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void showImageChooser() {
        Intent i= new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i.createChooser(i,"Select new profile picture"),CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            uriProfilePicture=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfilePicture);
                profile.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void  loadUserInformation()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null)
        {
            if (user.getPhotoUrl()!=null)
            {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profile);
//                String photoURL=user.getPhotoUrl().toString();
            }
            if (user.getDisplayName()!=null)
            {
                String displayName=user.getDisplayName();
                name.setText(displayName);
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profilePictureRef= FirebaseStorage.getInstance().getReference("profilePictures/"+System.currentTimeMillis()+".jpg");
        if (uriProfilePicture != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            profilePictureRef.putFile(uriProfilePicture)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profilePictureUploadUrl=taskSnapshot.getStorage().getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
            });
        }
    }

    private void init() {
        profile=findViewById(R.id.profile_picture_profile);
        name=findViewById(R.id.name_profile);
        progressBar=findViewById(R.id.progress_profile_picture_upload);
    }
}
