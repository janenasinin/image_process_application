package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity {

    EditText inputEmail;
    Button btnReset;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        inputEmail=findViewById(R.id.inputEmail);
        btnReset=findViewById(R.id.btnReset);
        auth = FirebaseAuth.getInstance();


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email= inputEmail.getText().toString().trim();

        if(email.isEmpty()){
            inputEmail.setError("Email required");
            inputEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Valid registered email required");
            inputEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if ((task.isSuccessful())){
                    Toast.makeText(ForgotPassword.this, "Check your email to reset password", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ForgotPassword.this,"Please retry, Something wrong happened", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}