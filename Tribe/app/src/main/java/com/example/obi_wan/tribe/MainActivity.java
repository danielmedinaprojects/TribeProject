package com.example.obi_wan.tribe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;//the side bar or drawer of our app
    private NavigationView navigationView;//Allows view of drawer in navigation format
    private ActionBarDrawerToggle actionBarDrawerToggle;///Allows view of drawer via toggle button
    private RecyclerView postList;//User posts
    private Toolbar mToolbar;

    //Profile image view using picasso using navigation header.xml
    private CircleImageView  navigationProfileImageView;
    private TextView navigationProfileUsername;

    //Firebase
    private FirebaseAuth mAuth; //checks if user is registered with app
    private DatabaseReference userDatabaseReference;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); //gets instance of firebase authentication.
        currentUserID = mAuth.getCurrentUser().getUid();//gets current users ID
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tribe");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Navigation Header
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navigationProfileImageView = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        navigationProfileUsername = (TextView) navView.findViewById(R.id.nav_user_full_name);


        userDatabaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Username")){//checks if user has proper setup info
                        //Gets Name from Database
                        String userUsername = dataSnapshot.child("Username").getValue().toString();

                        //Sets full name on navigation view
                        navigationProfileUsername.setText(userUsername);
                    }

                    if(dataSnapshot.hasChild("profileimage")){
                        //Gets Profile Image from database
                        String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                        //Sets profile image
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(navigationProfileImageView);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "No Profile Image", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });
    }

    @Override //overrides onStart() method of FirebaseAuth
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser(); //the current user

        //If the User is not found/does not exist
        if(currentUser == null){//start if
            SendUserToLoginActivity();
        }//end if else
        else{//start else
            CheckUserExistence();
        }//end else
    }

    //Checks if user is in Tribe database
    private void CheckUserExistence() {
         final String currentUserID = mAuth.getCurrentUser().getUid();

         userDatabaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(!dataSnapshot.hasChild(currentUserID)){//if user does not exist in the database
                        SendUserToSetUpActivity();
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    //Sends User to Set Up Activity If User Has Been Authenticated But Has Not Setup Profile
    private void SendUserToSetUpActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }



    //sends user to login activity if not found
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Displays Navigation Menu/Drawer after clicked.
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){//start if
            return true;
        }//end if
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {//start method

        switch (item.getItemId()){ //Allows User To Select Navigation Menu Option i.e Settings, Logout, etc...

                case R.id.nav_tent:
                Toast.makeText(this, "View Tent", Toast.LENGTH_SHORT).show();
                SendUserToTentActivity();
                break;

                case R.id.nav_tribe:
                Toast.makeText(this, "View Tribe", Toast.LENGTH_SHORT).show();
                SendUserToViewTribeActivity();
                break;

                case R.id.nav_view_tribe_members:
                Toast.makeText(this, "Find Tribe Members", Toast.LENGTH_SHORT).show();
                SendUserToFindTribeMembersActivity();
                break;

                case R.id.nav_create_house:
                Toast.makeText(this, "Create House", Toast.LENGTH_SHORT).show();
                break;

                case R.id.nav_find_tribe:
                Toast.makeText(this, "Find Tribe", Toast.LENGTH_SHORT).show();
                SendUserToFindTribeActivity();
                break;

                case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

                case R.id.nav_settings:
                SendUserToSettingsActivity();
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

                case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

        }

    }//end method

    //sends user to view tribe activity if not found
    private void SendUserToFindTribeActivity() {
        Intent findTribeMembersIntent = new Intent(MainActivity.this, FindTribeActivity.class);
        startActivity(findTribeMembersIntent);
    }

    //sends user to view tribe activity if not found
    private void SendUserToViewTribeActivity() {
        Intent viewTribeMembersIntent = new Intent(MainActivity.this, ViewTribeActivity.class);
        startActivity(viewTribeMembersIntent);
    }

    //sends user to login activity if not found
    private void SendUserToFindTribeMembersActivity() {
        Intent findTribeMembersIntent = new Intent(MainActivity.this, FindTribeMemberActivity.class);
        startActivity(findTribeMembersIntent);
    }


    //sends user to login activity if not found
    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    //sends user to login activity if not found
    private void SendUserToTentActivity() {
        Intent tentIntent = new Intent(MainActivity.this, ViewTentActivity.class);
        startActivity(tentIntent);
    }



}
