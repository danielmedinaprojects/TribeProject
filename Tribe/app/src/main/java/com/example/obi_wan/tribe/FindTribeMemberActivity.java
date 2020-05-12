package com.example.obi_wan.tribe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindTribeMemberActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton searchButton;
    private EditText searchTextInput;
    private RecyclerView SearchResultList;
    private DatabaseReference allUsersDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tribe_member);

        //Firebase
        allUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.find_tribe_members_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Tribe Members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Components
        searchButton = (ImageButton) findViewById(R.id.find_tribe_member_search_button);
        searchTextInput = (EditText) findViewById(R.id.find_tribe_member_search_box);

        SearchResultList = (RecyclerView) findViewById(R.id.tribeMembersResultList);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textInput = searchTextInput.getText().toString();

                SearchTribeMember(textInput);
            }
        });
    }


    //Searches for Tribe Member
    private void SearchTribeMember(String searchInput){

        Toast.makeText(FindTribeMemberActivity.this, "Searching...", Toast.LENGTH_LONG ).show();

        Query searchTribeMembersQuery = allUsersDatabaseReference.orderByChild("Username").startAt(searchInput).endAt(searchInput + "\uf8ff");




        FirebaseRecyclerAdapter<FindTribeMembers, FindTribeMembersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindTribeMembers, FindTribeMembersViewHolder>(
                        FindTribeMembers.class,
                        R.layout.all_tribe_members_display_layout,
                        FindTribeMembersViewHolder.class,
                        searchTribeMembersQuery
        ) {
            @Override
            protected void populateViewHolder(FindTribeMembersViewHolder viewHolder, FindTribeMembers model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTribe(model.getTribe());
                viewHolder.setProfileimage(model.getProfileimage());
            }
        };
        SearchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindTribeMembersViewHolder extends RecyclerView.ViewHolder {//Start Inner Class

        View mView;

        public FindTribeMembersViewHolder(View itemView){
            super(itemView);
            this.mView = itemView;
        }

        public void setProfileimage(String profileimage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.members_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setUsername(String username){
            TextView myUsername = (TextView) mView.findViewById(R.id.all_members_username);
            myUsername.setText(username);
        }

        public void setTribe(String tribe){
            TextView myTribe = (TextView) mView.findViewById(R.id.all_members_Tribe);
            myTribe.setText(tribe);
        }

    }//End Inner Class
}
