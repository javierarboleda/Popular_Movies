package com.javierarboleda.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioButton;


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

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

//        // let's remove that pesky shadow below actionbar, eh
//        getSupportActionBar().setElevation(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort_by_menu_item) {
            showPopup(item);
        }


        return super.onOptionsItemSelected(item);
    }

    public void onSortByOptionClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            return;
        }

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.menu_item_popularity:
                ((RadioButton) view).setChecked(true);
                break;
            case R.id.menu_item_highest_rating:
                ((RadioButton) view).setChecked(true);
                break;
        }
    }

    public void showPopup(MenuItem item) {
        final View menuItemView = findViewById(item.getItemId());

        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_sort_by, popup.getMenu());
        popup.show();
    }
}
