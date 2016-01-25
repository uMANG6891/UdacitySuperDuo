package it.jaschke.alexandria.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.ui.fragment.AboutFragment;
import it.jaschke.alexandria.ui.fragment.AddBookFragment;
import it.jaschke.alexandria.ui.fragment.ListOfBooksFragment;

/**
 * Created by umang on 19/01/16.
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.right_container)
    FrameLayout flRightContainer;

    @Bind(R.id.coord_main)
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
                finish();
            }
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment nextFragment;
        switch (item.getItemId()) {
            default:
            case R.id.navigation_item_library:
                nextFragment = new ListOfBooksFragment();
                loadFragment(nextFragment);
                if (flRightContainer != null) {
                    flRightContainer.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.navigation_item_library_add:
                nextFragment = new AddBookFragment();
                loadFragment(nextFragment);
                if (flRightContainer != null) {
                    flRightContainer.setVisibility(View.GONE);
                }
                break;
            case R.id.navigation_item_about:
                nextFragment = new AboutFragment();
                loadFragment(nextFragment);
                break;
            case R.id.navigation_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void loadFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack(String.valueOf(getTitle()))
                .commit();
    }

    public void showEndlessSnackBar(int message) {
        snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public void hideEndlessSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                else
                    drawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return false;
        }
    }


}
