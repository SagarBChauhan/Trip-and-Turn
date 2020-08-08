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

public class PaymentFragment extends Fragment {
    FrameLayout viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_payment,container,false);

        viewPager = v.findViewById(R.id.fragment_container_payment);

        viewPager.animate().translationY(0).setDuration(300);
        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation_payment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_payment_pending:
                        selectedFragment = new PaymentPendingFragment();
                        break;
                    case R.id.nav_payment_history:
                        selectedFragment = new PaymentHistoryFragment();
                        break;
                }
                viewPager.setAlpha(0);
                viewPager.setTranslationY(2000);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container_payment, selectedFragment).commit();
                viewPager.animate().translationY(0).alpha(1).setDuration(300);

                return true;
            }
        });
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_payment, new PaymentPendingFragment()).commit();
        }
        return v;
    }
}
