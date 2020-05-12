package com.example.obi_wan.tribe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText Username, FirstName, LastName;
    private Button SaveInformationButton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    //Firebase Authentication
    String currentUserID;//The user unique ID
    FirebaseAuth mAuth;//User Authentication reference
    DatabaseReference userDatabaseReference;//User database reference
    StorageReference userProfileImageReference;//Reference to profile pictures in storage.

    //Profile Picture
    final static int photoGalleryPicture = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Username = (EditText) findViewById(R.id.setup_username);
        FirstName = (EditText) findViewById(R.id.setup_first_name);
        LastName = (EditText) findViewById(R.id.setup_last_name);
        SaveInformationButton = (Button) findViewById(R.id.setup_information_save_button);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);//Loading bar for account/main activity login after setup

        //Firebase
        mAuth = FirebaseAuth.getInstance();//Gets User Authorization
        currentUserID = mAuth.getCurrentUser().getUid();//Gets current users firebase Authentication ID
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);//Gets Users Database Reference
        userProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserRegisterInformation();//Saves User Register Information
            }
        });

        //Direct user to photo gallery for profile picture selection
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoGalleryIntent = new Intent();
                photoGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                photoGalleryIntent.setType("image/*");//Directs user to photo gallery
                startActivityForResult(photoGalleryIntent, photoGalleryPicture);//You select one picture.
            }
        });

        //Adds Photo To Display
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();//Gets String value of image snapshot
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);// Added and commented out for some reason?
                    } else{
                        Toast.makeText(SetupActivity.this, "Please Pick Profile Image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Gets selected profile picture.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == photoGalleryPicture && resultCode == RESULT_OK && data != null) {
            Uri uriImage = data.getData();//gets selected gallery picture

            CropImage.activity(uriImage)//possible remove uriImage passing***
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        //Gets cropped image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                //loading bar for cropped image setup.
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Updating Profile Image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();//Cropped Image
                StorageReference filePath = userProfileImageReference.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {//Stores image in unique user's profile image storage
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){//If cropped image successfully saved to storage
                            Toast.makeText(SetupActivity.this, "Profile Image Successfully Stored in Firebase Storage", Toast.LENGTH_SHORT).show();

                            //Store cropped image to firebase database
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            userDatabaseReference.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){//if image successfully added to database
                                                Toast.makeText(SetupActivity.this, "Profile Image Successfully Stored to Firebase Database", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                                Intent setupIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(setupIntent);//Sends user back to setup activity to complete account setup .
                                            } else{
                                                loadingBar.dismiss();
                                                String errorMessage = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this,  "Error: " + errorMessage, Toast.LENGTH_SHORT);

                                            }

                                        }
                                    });
                        }
                    }
                });//Save cropped image file in firebase storage.
            }
            else{//Else if cropped image not successfully saved to Firebase storage
                loadingBar.dismiss();
                Toast.makeText(SetupActivity.this, "Error: Unsuccessful crop. Try again.", Toast.LENGTH_SHORT);
            }
        }

    }

    //Saves User Register Information about Profile Setup
    private void SaveUserRegisterInformation() {
        String username = Username.getText().toString();
        String firstName = FirstName.getText().toString();
        String lastName = LastName.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(SetupActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(firstName)){
            Toast.makeText(SetupActivity.this, "Please Enter Full Name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(lastName)){
            Toast.makeText(SetupActivity.this, "Please Enter Country", Toast.LENGTH_SHORT).show();
        }
        else{//
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please Wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();//Creates Hash Map
            //With...
            userMap.put("Username", username);//this
            userMap.put("First Name", firstName);//this
            userMap.put("Last Name", lastName);//this
            userMap.put("Occupation", "");//this
            userMap.put("Gender", "");//this
            userMap.put("DOB", "");//this
            userMap.put("Relationship Status", "");//and this
            userMap.put("Tribe", "");
            userMap.put("Interests", "");
            userMap.put("Hobbies", "");

            userDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() { //adds information to databas
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){//If Profile Created Successfully
                        Toast.makeText(SetupActivity.this, "Successfully Created Account", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                        SendUserToMainActivity();//Sends User To Main Activity
                    } else{//Else if profile not created successfully
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    
                }
            });
        }
    }

    //Sends User to Main Activity
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
