package barqsoft.footballscores.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ui.fragment.PagerFragment;

public class MainActivity extends AppCompatActivity {
    public static int selected_match_id;
    public static int CURRENT_FRAGMENT = 2;
    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Pager_Current", my_main.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match", selected_match_id);
        getSupportFragmentManager().putFragment(outState, "my_main", my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        CURRENT_FRAGMENT = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, "my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
