package com.example.tripandturn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, LinearLayout.OnClickListener, ViewPagerEx.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
    GoogleSignInClient mGoogleSignInClient;
    HashMap<String, Integer> file_maps;
    SliderLayout mSlider;
    private DrawerLayout drawerLayout;
    TextView userName, userEmail;
    CircleImageView profilePicture;
    NavigationView navigationView;
    View header;
    private long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        setupSlider();
        drawerLayout = findViewById(R.id.drawer_layout_customer);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        firebaseAuthenticate();
        googleAuthenticate();
        findViewById(R.id.discover_place).setOnClickListener(this);
        findViewById(R.id.book_transport).setOnClickListener(this);
        findViewById(R.id.book_package).setOnClickListener(this);
        findViewById(R.id.book_hotel).setOnClickListener(this);
        findViewById(R.id.plan_tour).setOnClickListener(this);
        findViewById(R.id.passport_assistance).setOnClickListener(this);
        findViewById(R.id.fab_call).setOnClickListener(this);

    }

    private void setupSlider() {
        mSlider = findViewById(R.id.slider);
        file_maps = new HashMap<>();
        file_maps.put("Kashmir", R.drawable.goat);
        file_maps.put("Goa", R.drawable.goa);
        file_maps.put("Forest tour", R.drawable.forest);
        file_maps.put("The palm", R.drawable.thepalm);
        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mSlider.addSlider(textSliderView);
        }
        mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
        mSlider.addOnPageChangeListener(this);
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                finish();
                startActivity(new Intent(CustomerActivity.this, CustomerActivity.class));
                break;
            case R.id.nav_place:
                startActivity(new Intent(CustomerActivity.this, DiscoverPlace.class));
                break;
            case R.id.nav_hotel:
                startActivity(new Intent(CustomerActivity.this, BookHotel.class));
                break;
            case R.id.nav_package:
                startActivity(new Intent(CustomerActivity.this, BookPackage.class));
                break;
            case R.id.nav_transport:
                startActivity(new Intent(CustomerActivity.this, BookTransport.class));
                break;
            case R.id.nav_passport:
                startActivity(new Intent(CustomerActivity.this, PassportAssistance.class));
                break;
            case R.id.nav_gift:
//                startActivity(new Intent(CustomerActivity.this, GiftVoucherActivity.class));
                break;
            case R.id.nav_enquiry:
//                startActivity(new Intent(CustomerActivity.this, EnquiryActivity.class));
                break;
            case R.id.nav_about:
//                startActivity(new Intent(CustomerActivity.this, AboutActivity.class));
                break;
            case R.id.nav_contact:
//                startActivity(new Intent(CustomerActivity.this, ContactActivity.class));
                break;
            case R.id.nav_setting:
//                startActivity(new Intent(CustomerActivity.this, SettingActivity.class));
                break;
            case R.id.nav_profile:
//                startActivity(new Intent(CustomerActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_log_out:
                new AlertDialog.Builder(CustomerActivity.this)
                        .setTitle("Log out")
                        .setMessage("Log out from Trip and Turn?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (GoogleSignIn.getLastSignedInAccount(CustomerActivity.this) != null) {
                                    signOut();
                                } else {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(CustomerActivity.this, LoginActivity.class));
                                    Toast.makeText(CustomerActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void googleAuthenticate() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            userName = header.findViewById(R.id.profile_title_customer);
            userEmail = header.findViewById(R.id.profile_subtitle_customer);
            profilePicture = header.findViewById(R.id.profile_picture_customer);

            userName.setText(personName);
            userEmail.setText(personEmail);
            Picasso.with(CustomerActivity.this)
                    .load(personPhoto)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(profilePicture);
        }
    }

    private void firebaseAuthenticate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();
            boolean emailVerified = user.isEmailVerified();

            userName = header.findViewById(R.id.profile_title);
            userEmail = header.findViewById(R.id.profile_subtitle);
            profilePicture = header.findViewById(R.id.profile_picture);

//            userName.setText(name);
//            userEmail.setText(email);
            Picasso.with(CustomerActivity.this)
                    .load(photoUrl)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(profilePicture);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut
                ()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(CustomerActivity.this, LoginActivity.class));
                        Toast.makeText(CustomerActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (back_pressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(),
                    "Press once again to exit!", Toast.LENGTH_SHORT)
                    .show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover_place:
                startActivity(new Intent(CustomerActivity.this, DiscoverPlace.class));
                break;
            case R.id.book_transport:
                startActivity(new Intent(CustomerActivity.this, BookTransport.class));
                break;
            case R.id.book_package:
                startActivity(new Intent(CustomerActivity.this, BookPackage.class));
                break;
            case R.id.book_hotel:
                startActivity(new Intent(CustomerActivity.this, BookHotel.class));
                break;
            case R.id.plan_tour:
                startActivity(new Intent(CustomerActivity.this, PlanTour.class));
                break;
            case R.id.passport_assistance:
                startActivity(new Intent(CustomerActivity.this, PassportAssistance.class));
                break;
            case R.id.fab_call:
                new AlertDialog.Builder(CustomerActivity.this)
                        .setMessage("Open contact number in dialer?")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:7069236002"));
                        startActivity(intent);
                    }
                }).show();
                break;
        }
    }
}
