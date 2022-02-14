package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccount, forgotPassword, googleSignup;
    EditText inputEmail, inputPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createNewAccount = findViewById(R.id.createNewAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        forgotPassword = findViewById(R.id.forgotPassword);
        googleSignup = findViewById(R.id.googleLogo);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perforLogin();
            }
        });

        googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GoogleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void perforLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct email!");
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError("Enter correct password!");
        } else {
            progressDialog.setMessage("Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    assert user != null;
                    if (user.isEmailVerified()) {
                        //redirect to user profile
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else
                    {
                        progressDialog.dismiss();
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check email to verify email", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}