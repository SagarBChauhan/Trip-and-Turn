package com.example.tripandturn;

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

public class GiftFragment extends Fragment {
    FrameLayout viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gift, container, false);

        viewPager = v.findViewById(R.id.fragment_container_gift);

        viewPager.animate().translationY(0).setDuration(300);
        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation_gift);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_view:
                        selectedFragment = new GiftViewFragment();
                        break;
                    case R.id.nav_add:
                        selectedFragment = new GiftAddFragment();
                        break;
                    case R.id.nav_update:
                        selectedFragment = new GiftUpdateFragment();
                        break;
                }
                viewPager.setAlpha(0);
                viewPager.setTranslationY(2000);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container_gift, selectedFragment).commit();
                viewPager.animate().translationY(0).alpha(1).setDuration(300);

                return true;
            }
        });
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_gift, new GiftViewFragment()).commit();
        }
        return v;
    }
}