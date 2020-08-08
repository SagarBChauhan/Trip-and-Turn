package com.example.tripandturn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class PlaceFragment extends Fragment {
    FrameLayout viewPager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_place,container,false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_place_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),PlaceAdd.class));
            }
        });

        viewPager = v.findViewById(R.id.fragment_container_place);
        viewPager.animate().translationY(0).setDuration(300);

//        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation_place);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                Fragment selectedFragment = null;
//
//                switch (menuItem.getItemId()) {
//                    case R.id.nav_view:
//                        selectedFragment = new PlaceViewFragment();
//                        break;
//                    case R.id.nav_add:
//                        selectedFragment = new PlaceAddFragment();
//                        break;
//                    case R.id.nav_update:
//                        selectedFragment = new PlaceUpdateFragment();
//                        break;
//                }
//                viewPager.setAlpha(0);
//                viewPager.setTranslationY(2000);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container_place, selectedFragment).commit();
//                viewPager.animate().translationY(0).alpha(1).setDuration(300);
//
//                return true;
//            }
//        });
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_place, new PlaceViewFragment()).commit();
        }
        return v;
    }
}
