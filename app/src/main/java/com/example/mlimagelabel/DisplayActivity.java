package com.example.mlimagelabel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mlimagelabel.databinding.ActivityDisplayBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DisplayActivity extends AppCompatActivity {

    ActivityDisplayBinding binding;
    StorageReference storage;
    ProgressDialog progressDialog;
    Uri imageUri;
    Bitmap bitmap;

    private FirebaseAuth firebaseAuth;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayActivity.this,HomeActivity.class ));
            }
        });


        binding.logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Do you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                startActivity(new Intent(DisplayActivity.this,LoginActivity.class));
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


        binding.btnImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(DisplayActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select image from Gallery"), 1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadtoFirebase();
            }
        });

    }

    private void uploadtoFirebase() {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("File uploaded!");
        dialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storage = FirebaseStorage.getInstance().getReference("images/"+fileName);


        storage.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot)
            {
                float percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                dialog.setMessage("Image uploading.. :"+(int)percent+ " %");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            imageUri=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(imageUri);
                bitmap= BitmapFactory.decodeStream(inputStream);
                binding.imageView.setImageBitmap(bitmap);


            }catch (Exception ex){
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




}