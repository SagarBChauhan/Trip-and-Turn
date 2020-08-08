package com.example.tripandturn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {
    DatabaseReference mReference;
    TextView user_count, collection_count, package_count, visit_count,title;
    long amount=0 ,Visit_count = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        user_count = v.findViewById(R.id.user_count);
        collection_count = v.findViewById(R.id.collection_count);
        package_count = v.findViewById(R.id.package_count);
        visit_count = v.findViewById(R.id.visit_count);
        title = v.findViewById(R.id.textView);

        mReference = FirebaseDatabase.getInstance().getReference();

        mReference.child("GoogleLogin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Visit_count = Long.valueOf(String.valueOf(dataSnapshot.getChildrenCount()));
                    user_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mReference.child("Login").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Visit_count=Visit_count+Long.valueOf(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        visit_count.setText(String.valueOf(Visit_count));

        mReference.child("package_booking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    package_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mReference.child("payment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
//                    title.setText(dataSnapshot.getValue().toString());
//                    for (long i=0;i<dataSnapshot.getChildrenCount();i++)
//                    {
//                        Toast.makeText(getContext(), dataSnapshot.child(String.valueOf(i)).child("amount").getValue().toString(), Toast.LENGTH_SHORT).show();
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

}
