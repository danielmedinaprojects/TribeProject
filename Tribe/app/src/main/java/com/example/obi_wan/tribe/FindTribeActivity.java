package com.example.obi_wan.tribe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class FindTribeActivity extends AppCompatActivity {

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tribe);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.find_tribe_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Tribe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
