package com.example.tripandturn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isFirstStart())
        {
            startMainActivity();
        }
//        setContentView(R.layout.activity_intro);

//        setFadeAnimation();
//        setZoomAnimation();
//        setFlowAnimation();
//        setSlideOverAnimation();
        setDepthAnimation();

        addSlide(AppIntro2Fragment.newInstance("Welcome to","Trip and Turn tours and travels",
                R.drawable.papper_plane_round_icon, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Book packages","Tour packages from all around world",
                R.drawable.ic_layers_white_intro, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Book hotels","at reasonable price",
                R.drawable.ic_hotel_white_intro, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Book Transports","at reasonable price",
                R.drawable.ic_directions_car_white_intro, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Passport Assistance","at reasonable price",
                R.drawable.ic_assignment_ind_white_intro, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Eran gift vouchers","on booking of any service",
                R.drawable.ic_card_giftcard_white_intro, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));


        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    private boolean isFirstStart()
    {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("Intro", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Flag",true);
    }

    private void setStartStatus(boolean status)
    {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("Intro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Flag",status);
        editor.commit();
    }

    @Override
    public void onDonePressed(Fragment fragment) {
        super.onDonePressed(fragment);
        startMainActivity();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startMainActivity();
    }
    public void startMainActivity(){
        setStartStatus(false);

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
