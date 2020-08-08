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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    private DrawerLayout drawerLayout;
    long back_pressed;
    TextView userName, userEmail;
    CircleImageView profilePicture;
    NavigationView navigationView;
    View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        firebaseAuthenticate();
        googleAuthenticate();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_place:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlaceFragment()).commit();
                break;
            case R.id.nav_hotel:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HotelFragment()).commit();
                break;
            case R.id.nav_package:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PackageFragment()).commit();
                break;
            case R.id.nav_transport:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransportFragment()).commit();
                break;
            case R.id.nav_gift:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GiftFragment()).commit();
                break;
            case R.id.nav_users:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
                break;
            case R.id.nav_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
                break;
            case R.id.nav_payments:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PaymentFragment()).commit();
                break;
            case R.id.nav_trips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TripFragment()).commit();
                break;
            case R.id.nav_package_bookings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PackageBookingFragment()).commit();
                break;
            case R.id.nav_hotel_bookings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HotelBookingFragment()).commit();
                break;
            case R.id.nav_place_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlacePhotosFragment()).commit();
                break;
            case R.id.nav_hotel_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HotelPhotosFragment()).commit();
                break;
            case R.id.nav_package_gallery:
                startActivity(new Intent(AdminActivity.this,BannerAdd.class));
                break;
            case R.id.nav_passport:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PassportFragment()).commit();
                break;
            case R.id.nav_enquiry:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EnquiryFragment()).commit();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About us", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminActivity.this, FirebasedatabaseTest.class));
                break;
            case R.id.nav_contact:
                Toast.makeText(this, "Contact us", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_details:
                startActivity(new Intent(AdminActivity.this, RegisterDataActivity.class));
                break;
            case R.id.action_profile:
                startActivity(new Intent(AdminActivity.this, Profile.class));
                break;
            case R.id.action_change_password:
                startActivity(new Intent(AdminActivity.this, ChangePassword.class));
                break;
            case R.id.action_settings:
//                startActivity(new Intent(AdminActivity.this, SettingsActivity.class));
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Log out")
                        .setMessage("Log out from Trip and Turn?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (GoogleSignIn.getLastSignedInAccount(AdminActivity.this) != null) {
                                    signOut();
                                } else {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                                    Toast.makeText(AdminActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void  firebaseAuthenticate() {
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

            userName.setText(name);
            userEmail.setText(email);
            Picasso.with(AdminActivity.this)
                    .load(photoUrl)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(profilePicture);
        }
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

            userName = header.findViewById(R.id.profile_title);
            userEmail = header.findViewById(R.id.profile_subtitle);
            profilePicture = header.findViewById(R.id.profile_picture);

            userName.setText(personName);
            userEmail.setText(personEmail);
            Picasso.with(AdminActivity.this)
                    .load(personPhoto)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(profilePicture);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                        Toast.makeText(AdminActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

}
