package com.example.obi_wan.tribe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewTentActivity extends AppCompatActivity {


    //Toolbar
    private Toolbar mToolbar;

    //Profile components
    private CircleImageView profilePicture;
    private TextView profileUsername, profileInterests, profileHobbies, profileTribe;

    //Firebase
    DatabaseReference userProfileDatabaseReference;
    FirebaseAuth mAuth;
    private String currentUsersID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tent);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tent");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Firebase References
        mAuth = FirebaseAuth.getInstance();
        currentUsersID = mAuth.getCurrentUser().getUid();
        userProfileDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUsersID);

        //Profile picture
        profilePicture = (CircleImageView) findViewById(R.id.profile_picture);

        //Profile Texts
        profileUsername = (TextView) findViewById(R.id.profile_username);
        profileInterests = (TextView) findViewById(R.id.profile_interests);
        profileHobbies = (TextView) findViewById(R.id.profile_hobbies);
        profileTribe = (TextView) findViewById(R.id.profile_tribe);


        userProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userUsername = dataSnapshot.child("Username").getValue().toString();
                    String userTribeName = dataSnapshot.child("Tribe").getValue().toString();
                    String userInterests = dataSnapshot.child("Interests").getValue().toString();
                    String userHobbies = dataSnapshot.child("Hobbies").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(profilePicture);
                    profileUsername.setText(userUsername);
                    profileTribe.setText("Tribe: " + userTribeName);
                    profileInterests.setText("Interests " + userInterests);
                    profileHobbies.setText("Hobbies " + userHobbies);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
