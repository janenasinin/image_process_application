package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {
    ImageView back, logout;
    TextView name, email;

    private FirebaseAuth firebaseAuth;

    DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        back=findViewById(R.id.back);
        logout=findViewById(R.id.logoutImage);
        name=findViewById(R.id.userName);
        email=findViewById(R.id.userEmail);

        firebaseDatabase = FirebaseDatabase.getInstance( ).getReference("User Info")
                .child(FirebaseAuth.getInstance( ).getCurrentUser( ).getUid( ));

        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String username = snapshot.child("name").getValue(String.class);
                String useremail = snapshot.child("email").getValue(String.class);
                name.setText(username);
                email.setText(useremail);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class ));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
            }
        });

    }
}