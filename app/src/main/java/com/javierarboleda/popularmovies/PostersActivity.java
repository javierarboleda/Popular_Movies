package com.javierarboleda.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


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
//        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // let's remove that pesky shadow below actionbar, eh
        getSupportActionBar().setElevation(0);
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return super.onOptionsItemSelected(item);
//    }

    public boolean sortByRadioButtonClicked(MenuItem item) {
        // Is the button now checked?
        boolean checked = item.isChecked();
//
//        if (checked) {
//            return false;
//        }

        // Check which radio button was clicked
        switch(item.getItemId()) {
            case R.id.menu_item_popularity:
                item.setChecked(true);
                break;
            case R.id.menu_item_highest_rating:
                item.setChecked(true);
                break;
        }
        invalidateOptionsMenu();
        return true;
    }


}
