package com.example.obi_wan.tribe;

import android.content.Context;

import com.squareup.picasso.Picasso;

public class FindTribeMembers {

    public String profileimage, Username, Tribe;

    //Default Constructor
    public FindTribeMembers(){

    }

    public FindTribeMembers(String profileimage, String Username, String Tribe){
        this.profileimage = profileimage;
        this.Username = Username;
        this.Tribe = Tribe;

    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getTribe() {
        return Tribe;
    }

    public void setTribe(String tribe) {
        Tribe = tribe;
    }
}
