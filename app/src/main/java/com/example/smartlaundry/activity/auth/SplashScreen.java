package com.example.smartlaundry.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.smartlaundry.R;

import maes.tech.intentanim.CustomIntent;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar splashProgress;
    int SPLASH_TIME = 3100; //This is 3 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashProgress = findViewById(R.id.splashProgress);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        splashProgress.getProgressDrawable().setColorFilter(Color.WHITE
                ,android.graphics.PorterDuff.Mode.SRC_IN);
        playProgress();
        new Handler().postDelayed(() -> {
            //Do any action here. Now we are moving to next page
            Intent mySuperIntent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(mySuperIntent);

            //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            finish();

        }, SPLASH_TIME);
    }

    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(3000)
                .start();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }
}
