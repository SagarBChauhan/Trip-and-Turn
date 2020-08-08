package com.example.tripandturn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseDelete extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_delete);

        mDatabaseReference=FirebaseDatabase.getInstance().getReference("City");
        mDatabaseReference.removeValue();

    }
}
