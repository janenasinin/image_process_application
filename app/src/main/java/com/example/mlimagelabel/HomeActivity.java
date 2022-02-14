package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    LinearLayout imageCard, storageCard, profileCard;
    ImageView logout;


    AlertDialog.Builder builder;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //firebase Cloud messaging
        Intent intentBackgroundService = new Intent(this, NotificationActivity.class);
        startService(intentBackgroundService);

        builder = new AlertDialog.Builder(this);
        firebaseAuth=FirebaseAuth.getInstance();


        imageCard=findViewById(R.id.layoutImages);
        storageCard=findViewById(R.id.layoutStorage);
        profileCard=findViewById(R.id.layoutProfile);
        logout=findViewById(R.id.logoutImage);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Do you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert= builder.create();
                alert.setTitle("Logout");
                alert.show();

            }
        });

        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Image Clicked!");
                startActivity(new Intent(HomeActivity.this,MainActivity.class ));
            }
        });

        storageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Storage Clicked!");
                startActivity(new Intent(HomeActivity.this,DisplayActivity.class ));
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Profile Clicked!");
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class ));
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}