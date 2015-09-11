package com.javierarboleda.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.util.MovieDbUtil;

/**
 * Created by Javier Arboleda on 8/10/15.
 *
 * Launch activity for posters UI
 *
 * Code for two pane master/detail UI taken from Udacity Sunshine App example
 *
 */
public class PostersActivity extends AppCompatActivity implements PostersFragment.Callback {

    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posters);

        // let's remove that pesky shadow below actionbar, eh
        getSupportActionBar().setElevation(0);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(),
                                DETAIL_FRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    /**
     * Callback function from PostersFragment to handle master/detail tablet layout.
     * In the case that we are using tablet layout, new DetailFragment will be created or
     * replaced with fragment transaction. Otherwise, create new DetailActivity intent.
     */
    @Override
    public boolean onItemSelected(Movie movie, String apiKey) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDbUtil.MOVIE, movie);
            args.putString(MovieDbUtil.API_KEY_PARAM, apiKey);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = createDetailActivityIntent(movie, apiKey);
            startActivity(intent);
        }

        return mTwoPane;
    }

    /**
     * Helper method for creating a DetailActivity intent
     */
    private Intent createDetailActivityIntent(Movie movie, String apiKey) {

        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(MovieDbUtil.MOVIE, movie);
        intent.putExtra(MovieDbUtil.API_KEY_PARAM, apiKey);

        return intent;
    }
}
