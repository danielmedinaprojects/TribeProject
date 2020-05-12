package com.example.obi_wan.tribe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ViewTribeActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tribe);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.view_tribe_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tribe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
