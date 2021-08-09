package com.example.clinicapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clinicapplication.model.UploadImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import dmax.dialog.SpotsDialog;

public class EditProfileDoctorActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileDoctorActivity";
    private ImageView profileImage;
    private ImageButton selectImage;
    private Button updateProfile;
    private TextInputEditText doctorName;
    private TextInputEditText doctorEmail;
    private TextInputEditText doctorPhone;
    private TextInputEditText doctorAddress;
    final String currentDoctorUID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    private Uri uriImage;

    private StorageReference pStorageRef;
    private DatabaseReference pDatabaseRef;
    private FirebaseFirestore doctorRef;
    private StorageReference pathReference;
   // FirebaseStorage storage = FirebaseStorage.getInstance();
    static String Storage_Path = "DoctorProfile";
    private DatabaseReference currentUserImg;
    StorageReference storageReference;
    AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        doctorRef = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileImage = findViewById(R.id.image_profilee);
        selectImage = findViewById(R.id.select_image);
        updateProfile = findViewById(R.id.update);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(true).build();

        doctorName = findViewById(R.id.nameText);
        doctorPhone = findViewById(R.id.phoneText);
        //doctorEmail = findViewById(R.id.emailText);
        doctorAddress = findViewById(R.id.addressText);

        pStorageRef = FirebaseStorage.getInstance().getReference();
        pDatabaseRef = FirebaseDatabase.getInstance().getReference("DoctorProfile");

        //get the default doctor's informations from ProfileDoctorActivity
        Intent intent = getIntent(); //get the current intent
        String current_name = intent.getStringExtra("CURRENT_NAME");
        String current_phone = intent.getStringExtra("CURRENT_PHONE");
        String current_address = intent.getStringExtra("CURRENT_ADDRESS");

        //Set the default informtions in he text fields
        doctorName.setText(current_name);
        doctorPhone.setText(current_phone);
        doctorAddress.setText(current_address);


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateAddress = doctorAddress.getText().toString();
                String updateName = doctorName.getText().toString();
                //String updateEmail = doctorEmail.getText().toString();
                String updatePhone = doctorPhone.getText().toString();
                dialog.show();

                uploadProfileImage();
                updateDoctorInfos(updateName, updateAddress, updatePhone);
            }
        });


        String imageId = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        pathReference = pStorageRef.child("DoctorProfile/" + imageId +".jpg"); //Doctor photo in database
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(EditProfileDoctorActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.doctor)
                        .fit()
                        .centerCrop()
                        .into(profileImage);//Store here the imageView

//                 profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors

                Log.d("sssssssssssss",exception.getMessage());

            }
        });


    }

    /* Update the doctor info in the database */
    private void updateDoctorInfos(String name, String address, String phone) {
        String userid= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        DocumentReference documentReference = doctorRef.collection("Doctor").document("" + userid + "");
        documentReference.update("adresse", address);
        //documentReference.update("email", email);
        documentReference.update("name", name);
        documentReference.update("tel", phone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileDoctorActivity.this, "Infos Updated", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileDoctorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        Log.d("Androidview", e.getMessage());
                    }
                });
    }

    /* Used to choose a file */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /* used to get the data back */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//            uriImage = data.getData();
//            Picasso.with(this).load(uriImage).into(profileImage);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.with(this).load(uriImage).into(profileImage);
        }
    }


    /* Retrieve the extension of the file to upload */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /* Used to upload the doctor image in the DataBase */
    private void uploadProfileImage() {
        /* check if the image is not null */
        if (uriImage != null) {
            StorageReference storageReference = pStorageRef.child("DoctorProfile/"+currentDoctorUID
                    + "." + getFileExtension(uriImage));
            storageReference.putFile(uriImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    dialog.dismiss();
                    return pStorageRef.getDownloadUrl();
                }
            });
        }
    }
}