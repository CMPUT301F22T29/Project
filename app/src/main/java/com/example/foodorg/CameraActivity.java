package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
/**
 * CameraActivity is the activity the user access to store pictures for recipe
 * <ul>
 *     <li>Camera button</li>
 *     <li>Gallery button</li>
 *     <li>Return to RecipeActivity Button</li>
 *
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class CameraActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage;
    Button cameraBtn,galleryBtn,returnRecipeBtn;
    String currentPhotoPath,recipeID;
    StorageReference storageReference;

    private static final int REQEST_CODE = 22;
    //ImageView picture;

    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_page);

        selectedImage = findViewById(R.id.displayImageView);
        returnRecipeBtn = findViewById(R.id.returnButtonRecipe);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);

        storageReference = FirebaseStorage.getInstance().getReference();

        returnRecipeBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * returns to recipe
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CameraActivity.this,RecipeActivity.class);
                startActivity(i);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * camera of phone open to take picture for recipe
             * @param v
             */
            @Override
            public void onClick(View v) {
                askCameraPermissions();

            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * gallery opens to select picture for recipe
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        showImage();

    }
    /**
     * what activity to perform on result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            recipeID = getIntent().getExtras().getString("recipe_id");
            selectedImage.setImageBitmap(photo);
            handleUpload(photo,recipeID);
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recipeID = getIntent().getExtras().getString("recipe_id");
                Uri contentUri = data.getData();
                String imageFileName = recipeID;
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                selectedImage.setImageURI(contentUri);
                uploadImageToFirebase(imageFileName, contentUri);

            }

        }
    }
    /**
     * what permission to ask user when granted
     */

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQEST_CODE);
        }

    }

    /**
     * Ask user for permission to use camera
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQEST_CODE);
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * To show image on the activity if previous image exists of that recipe
     */

    private void showImage() {
        recipeID= getIntent().getExtras().getString("recipe_id");
        //recipeImage = (ImageView)findViewById(R.id.ingredientRecipeImageView);
        selectedImage = findViewById(R.id.displayImageView);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("recipe_images/"+recipeID);
        try{
            final File localFile = File.createTempFile(recipeID,"jpeg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            selectedImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(IngredientOfRecipeActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles the upload for camera picture
     * @param bitmap
     * @param name
     */
    private void handleUpload(Bitmap bitmap,String name) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);


        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("recipe_images/" + name.toString());

        storageReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CameraActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });
    }
    /**
     * handles upload for gallery picture
     * @param name
     * @param contentUri
     */

    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("recipe_images/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

                Toast.makeText(CameraActivity.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CameraActivity.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
