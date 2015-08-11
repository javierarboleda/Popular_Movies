package com.javierarboleda.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.javierarboleda.popularmovies.service.MovieDbService;

/**
 * Created by hype on 8/10/15.
 */
public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String voteAverage = getIntent().getStringExtra(MovieDbService.VOTE_AVERAGE);
        System.out.print("HI!");

        super.onCreate(savedInstanceState);
    }
}
