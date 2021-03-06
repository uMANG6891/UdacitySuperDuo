package it.jaschke.alexandria.ui.activity;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.FetchBookInfo;
import it.jaschke.alexandria.ui.adapter.Callback;
import it.jaschke.alexandria.ui.fragment.AddBookFragment;
import it.jaschke.alexandria.ui.fragment.BookDetailFragment;
import it.jaschke.alexandria.ui.fragment.ListOfBooksFragment;
import it.jaschke.alexandria.utility.Constants;
import it.jaschke.alexandria.utility.Utility;


public class MainActivity extends BaseActivity implements Callback {

    @Nullable
    @Bind(R.id.right_container)
    FrameLayout flRightContainer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReceiver;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            Fragment nextFragment;
            int startWith;
            try {
                startWith = Integer.parseInt(sp.getString("pref_startFragment", "0"));
            } catch (Exception e) {
                startWith = 0;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.EAN_IS_TABLET, IS_TABLET);
            switch (startWith) {
                default:
                case 0:
                    nextFragment = new ListOfBooksFragment();
                    break;
                case 1:
                    nextFragment = new AddBookFragment();
                    break;
            }
            nextFragment.setArguments(bundle);
            loadFragment(nextFragment);
        }

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(View view, String ean) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EAN_BOOK_ID, ean);
        bundle.putBoolean(Constants.EAN_IS_TABLET, IS_TABLET);


        if (flRightContainer != null) {
            flRightContainer.setVisibility(View.VISIBLE);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_container, fragment)
                    .commit();
        } else {
            Intent i = new Intent(this, BookDetailActivity.class);
            i.putExtras(bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View iv = view.findViewById(R.id.fullBookCover);

                Pair<View, String> ivPair = new Pair<>(iv, iv.getTransitionName());
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(this, ivPair).toBundle();
                startActivity(i, b);
            } else {
                startActivity(i);
            }
        }

    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = 0;
            if (!Utility.isNetworkAvailable(getBaseContext())) {
                message = R.string.search_status_no_network;
            } else {
                switch (Utility.getSearchStatus(getBaseContext())) {
                    case FetchBookInfo.SEARCH_STATUS_OK:
                        message = -1;
                        break;
                    case FetchBookInfo.SEARCH_STATUS_OK_EMPTY:
                        message = R.string.search_status_ok_empty;
                        break;
                    case FetchBookInfo.SEARCH_STATUS_SERVER_DOWN:
                        message = R.string.search_status_down;
                        break;
                    case FetchBookInfo.SEARCH_STATUS_SERVER_INVALID:
                        message = R.string.search_status_invalid;
                        break;
                    case FetchBookInfo.SEARCH_STATUS_UNKNOWN:
                        message = R.string.search_status_unknown;
                        break;
                    default:
                        break;
                }
            }
            if (message != -1)
                Toast.makeText(MainActivity.this, getString(message), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}