package com.example.mlimagelabel;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabelerOptionsBase;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView ivPicture, logout, back;
    TextView tvResult;
    Button btnChoosePic;

    private static final String TAG = "MyTag";
    private static final int CAMERA_PERMISSION_CODE = 223;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 144;
    private static final int READ_STORAGE_PERMISSION_CODE = 144;

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;

    InputImage inputImage;
    ImageLabeler labeler;

    AlertDialog.Builder builder;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tensorflow


        firebaseAuth=FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);

        btnChoosePic = findViewById(R.id.btnChoosePicture);
        ivPicture = findViewById(R.id.ivPicture);
        tvResult = findViewById(R.id.tvResult);
        logout=findViewById(R.id.logoutImage);
        back=findViewById(R.id.back);

        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HomeActivity.class ));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Do you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
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

        //function for mobile phone camera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        try {
                            assert data != null;
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            ivPicture.setImageBitmap(photo);
                            inputImage = InputImage.fromBitmap(photo,0);
                            processImage();

                        } catch (Exception e) {
                            Log.d(TAG, "onActivityResult: " + e.getMessage());
                        }
                    }
                }
        );

        //function for phone gallery picture
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        try {
                            assert data != null;
                            inputImage = InputImage.fromFilePath(MainActivity.this, data.getData());
                            ivPicture.setImageURI(data.getData());
                            processImage();

                        } catch (Exception e) {
                            Log.d(TAG, "onActivityResult: " + e.getMessage());
                        }
                    }
                }
        );


        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Uploaded");
                String[] options = {"camera", "gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pick a option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraLauncher.launch(cameraIntent);

                        } else {
                            Intent storageIntent = new Intent();
                            storageIntent.setType("image/*");
                            storageIntent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryLauncher.launch(storageIntent);
                        }
                    }
                });
                builder.show();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }

    private void checkPermission(String permission, int requestCode) {
        //check if permission granted or not
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            //Take permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "Camera permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION_CODE);
            }
        } else if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE);
            }
        } else if (requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void processImage() {
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull List<ImageLabel> imageLabels) {
                        String result ="";
                        for (ImageLabel label : imageLabels) {
                            result = result+ "\n" +label.getText();
                        }
                        tvResult.setText(result);

                        if (result.contains("cat")||result.contains("Cat")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Warning notifications!").
                                    setMessage("Danger! Predator Detected!");

                            builder.setPositiveButton("Okay",
                                    (dialog, id) -> {
                                      dialog.cancel();
                                    });
                            AlertDialog alert11 = builder.create();
                            alert11.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d(TAG, "onFailure:" + e.getMessage());
                    }
                });
         }

}