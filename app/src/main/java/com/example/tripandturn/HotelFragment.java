package com.example.tripandturn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HotelFragment extends Fragment {
    FrameLayout viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hotel, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_hotel_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),HotelAdd.class));
            }
        });

        viewPager = v.findViewById(R.id.fragment_container_hotel);
        viewPager.animate().translationY(0).setDuration(300);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_hotel, new HotelViewFragment()).commit();
        }
        return v;
    }
}
