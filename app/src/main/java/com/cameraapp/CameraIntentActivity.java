package com.cameraapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CameraIntentActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE = -1;
    public static final int REQ_CAMERA = 10001;
    private Uri imageUri;
    Button btnOpCam;
    ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_intent);

        btnOpCam=findViewById(R.id.btn_op_cam);
        ivPic=findViewById(R.id.iv_pic);

        btnOpCam.setOnClickListener(view -> {

            if (checkPermission()) {
//                activityResultLaunch.launch(getPickImageChooserIntent());
                startCamera();
            }else
                requestPermission();
        });
    }


    public void startCamera(){
        String folderName="DefaultCam";
        File photo = FileHelper.getOutputMediaFile(getApplicationContext(),folderName,"jpg");

        /*File photo=getExternalCacheDir();
        photo = new File(photo.getPath(), "FirstImg.png");*/

        if(photo != null) {
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",photo);
            startCamera(imageUri);
        }
    }

    public void startCamera(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQ_CAMERA);

        /*if (intent.resolveActivity(this.getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQ_CAMERA);
        }*/
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{CAMERA,WRITE_EXTERNAL_STORAGE},
                100);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }*/

                ivPic.setImageBitmap(bitmap);
            } catch (IOException e) {e.printStackTrace();}

        }
    }






    public Intent getPickImageChooserIntent() {

        File photo=new File(getExternalCacheDir().getPath(), "FirstImg.png");
        imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",photo);

        List<Intent> allIntents = new ArrayList();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (imageUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        /*Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }*/

        Intent chooserIntent = Intent.createChooser(new Intent(), "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case TAKE_PICTURE:
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                getContentResolver().notifyChange(imageUri, null);
                                ImageView imageView = findViewById(R.id.iv_pic);
                                try {
                                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    imageView.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                                    Log.e("Camera", e.toString());
                                }
                            }
                    }
                }
            });

}