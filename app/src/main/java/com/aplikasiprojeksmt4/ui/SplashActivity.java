package com.aplikasiprojeksmt4.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        ImageView logo = findViewById(R.id.logo_splash);
        
        if (logo != null) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
            logo.startAnimation(anim);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                
                // Cek apakah user sudah login sebelumnya
                if (sessionManager.getUserId() != null) {
                    intent.putExtra("IS_LOGGED_IN", true);
                } else {
                    intent.putExtra("IS_LOGGED_IN", false);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
