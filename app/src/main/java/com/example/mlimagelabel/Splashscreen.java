package com.example.mlimagelabel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class Splashscreen extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        lottieAnimationView=findViewById(R.id.splash);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    super.run();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }super.run();
            }
        };
        timer.start();

        lottieAnimationView=findViewById(R.id.splash);

        lottieAnimationView.animate().translationY(-1400).setDuration(1500).setStartDelay(1000);


    }
}
