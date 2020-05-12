package com.example.obi_wan.tribe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //Toolbar
    private Toolbar mToolbar;

    //Firebase Reference
    DatabaseReference userSettingsReference;//Reference to database
    FirebaseAuth mAuth;//Firebase Authentication
    private String currentUsersID;

    //Settings Activity Components
    private CircleImageView userProfileImage;
    private EditText firstName, lastName, username, tribeName, interests, hobbies;
    private Button updateSettingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUsersID = mAuth.getCurrentUser().getUid();
        userSettingsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUsersID);

        //CircleImageView
        userProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        //Edit Texts
        firstName = (EditText) findViewById(R.id.settings_profile_first_name);
        lastName = (EditText) findViewById(R.id.settings_profile_last_name);
        username = (EditText) findViewById(R.id.settings_profile_username);
        tribeName = (EditText) findViewById(R.id.settings_profile_tribe);
        interests = (EditText) findViewById(R.id.settings_profile_interests);
        hobbies = (EditText) findViewById(R.id.settings_profile_hobbies);

        //Button
        updateSettingsButton = (Button) findViewById(R.id.settings_update_profile_button);

        userSettingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userFirstName = dataSnapshot.child("First Name").getValue().toString();
                    String userLastName = dataSnapshot.child("Last Name").getValue().toString();
                    String userUsername = dataSnapshot.child("Username").getValue().toString();
                    String userTribeName = dataSnapshot.child("Tribe").getValue().toString();
                    String userInterests = dataSnapshot.child("Interests").getValue().toString();
                    String userHobbies = dataSnapshot.child("Hobbies").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    username.setText(userUsername);
                    firstName.setText(userFirstName);
                    lastName.setText(userLastName);
                    tribeName.setText(userTribeName);
                    interests.setText(userInterests);
                    hobbies.setText(userHobbies);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProfileInfo();
            }
        });
    }

    //
    private void ValidateProfileInfo(){
        String settingsFirstName = firstName.getText().toString();
        String settingsLastName = lastName.getText().toString();
        String settingsUsername = username.getText().toString();
        String settingsTribeName = tribeName.getText().toString();
        String settingsInterests = interests.getText().toString();
        String settingsHobbies = hobbies.getText().toString();

        if(TextUtils.isEmpty(settingsFirstName)){
            Toast.makeText(SettingsActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(settingsLastName)){
            Toast.makeText(SettingsActivity.this, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(settingsUsername)){
            Toast.makeText(SettingsActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(settingsTribeName)){
            Toast.makeText(SettingsActivity.this, "Please Enter Tribe Name", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(settingsInterests)){
            Toast.makeText(SettingsActivity.this, "Please Enter Interests", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(settingsHobbies)){
            Toast.makeText(SettingsActivity.this, "Please Enter Hobbies", Toast.LENGTH_SHORT).show();
        } else {
            UpdateProfileInfo(settingsFirstName, settingsLastName, settingsUsername, settingsTribeName ,settingsInterests, settingsHobbies);
        }
}
    private void UpdateProfileInfo(String settingFirstName, String settingsLastName, String settingsUsername, String settingsTribeName ,String settingsInterests, String settingsHobbies){
        HashMap map = new HashMap();
        map.put("First Name", settingFirstName);
        map.put("Last Name", settingsLastName);
        map.put("Username", settingsUsername);
        map.put("Tribe", settingsTribeName);
        map.put("Interests", settingsInterests);
        map.put("Hobbies", settingsHobbies);
        userSettingsReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Settings Updated", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();
                } else{
                    Toast.makeText(SettingsActivity.this, "Error: Settings Not Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Sends User to Main Activity
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
