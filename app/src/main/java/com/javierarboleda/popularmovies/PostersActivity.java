package com.javierarboleda.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class PostersActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posters);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_posters, new PostersFragment())
                    .commit();
        }

        // let's remove that pesky shadow below actionbar, eh
        getSupportActionBar().setElevation(0);
    }



}
