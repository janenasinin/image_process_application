package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveaccount;
    EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveaccount=findViewById(R.id.alreadyHaveaccount);

        inputName=findViewById(R.id.inputName);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConfirmPassword=findViewById(R.id.inputConfirmPassword);
        progressDialog=new ProgressDialog(this);
        btnRegister=findViewById(R.id.btnRegister);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
            }
        });

    }

    private void PerforAuth() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern))
        {
            inputEmail.setError("Enter correct email!");
        }else if (name.isEmpty()){
            inputName.setError("Name cannot be empty");
        }
        else if(password.isEmpty() || password.length()<6 ){
            inputPassword.setError("Password cannot be empty / less than 6 characters");
        }else if (!password.equals(confirmPassword)){
            inputConfirmPassword.setError("Password doesn't match");
        }
        else{
            progressDialog.setMessage("Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {


                    if(task.isSuccessful()){
                        User user = new User(email,name);
                        progressDialog.dismiss();

                        FirebaseDatabase.getInstance( ).getReference("User Info")
                                .child(FirebaseAuth.getInstance( ).getCurrentUser( ).getUid( ))
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    sendUserToNextActivity();
                                    Toast.makeText(RegisterActivity.this, "Registration Complete!", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}