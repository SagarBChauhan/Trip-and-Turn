package com.example.tripandturn;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlaceViewFragment extends Fragment {

    ArrayList<String> mArrayList=new ArrayList<>();
    ArrayList<String> NameList=new ArrayList<>();
    ArrayList<String> TypeList=new ArrayList<>();
    ArrayList<String> PictureList=new ArrayList<>();

    ListView listView;
    SearchView searchView;
    DatabaseReference mReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v= inflater.inflate(R.layout.fragment_place_view,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Places");

        listView=v.findViewById(R.id.PlaceList);
        searchView=v.findViewById(R.id.searchView);

        mReference=FirebaseDatabase.getInstance().getReference().child("place");
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,mArrayList);

        listView.setAdapter(adapter);
        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String Name=dataSnapshot.getKey().toString();
//                String Name=dataSnapshot.child("name").getValue().toString();
//                String Type=dataSnapshot.child("type").getValue().toString();
                String City=dataSnapshot.child("city").getValue().toString();
                if (dataSnapshot.hasChild("picture")) {
                    String Picture = dataSnapshot.child("picture").child("mImageUrl").getValue().toString();
                    PictureList.add(Picture);
                }
                String myChildValue=dataSnapshot.toString();


                NameList.add(Name);
//                TypeList.add(Type);

                mArrayList.add(Name);
//                mArrayList.add(Name+", "+City);

//                listAdapter=new ListAdapter(getContex t(), Name,Type,images);
//                listView.setAdapter(listAdapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                String[] value=parent.getItemAtPosition(position).toString().split(",");
//                Toast.makeText(getContext(), mReference.toString()+"/"+value[0], Toast.LENGTH_SHORT).show();
                getContext().getSharedPreferences("reference", Context.MODE_PRIVATE).edit().putString("reference",parent.getItemAtPosition(position).toString().trim()).commit();
                startActivity(new Intent(getContext(),PlaceDetailsActivity.class).putExtra("reference",parent.getItemAtPosition(position).toString().trim()));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mArrayList.contains(query))
                {
                    adapter.getFilter().filter(query);
                }
                else {
                    Toast.makeText(getContext(), "No match found..", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return v;
    }
}
