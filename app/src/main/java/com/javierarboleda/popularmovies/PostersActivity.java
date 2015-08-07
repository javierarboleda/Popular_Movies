package com.javierarboleda.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


public class PostersActivity extends AppCompatActivity {

    private Menu mMenu;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posters, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.sort_by_menu_item:
                showPopup(item);
                break;
            case R.id.menu_item_popularity:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.popularity);
                break;
            case R.id.menu_item_highest_rating:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.highest_rated);
                item.setChecked(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

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

    public void showPopup(MenuItem item) {
        final View menuItemView = findViewById(item.getItemId());

        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_sort_by, popup.getMenu());
        popup.show();
    }
}
