package com.example.tripandturn;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PackageFragment extends Fragment {
    FrameLayout viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_package_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),PackageAdd.class));
            }
        });

        viewPager = v.findViewById(R.id.fragment_container_package);
        viewPager.animate().translationY(0).setDuration(300);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_package, new PackageViewFragment()).commit();
        }
        return v;
    }
}
