package it.jaschke.alexandria.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.ui.fragment.BookDetailFragment;
import it.jaschke.alexandria.ui.fragment.ListOfBooks;
import it.jaschke.alexandria.utility.Utility;


public class MainActivity extends BaseActivity implements Callback {

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
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Fragment nextFragment = new ListOfBooks();
        loadFragment(nextFragment);

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
    public void onItemSelected(String ean) {
        Bundle bundle = new Bundle();
        bundle.putString(BookDetailActivity.EAN_BOOK_ID, ean);
        bundle.putBoolean(BookDetailActivity.EAN_IS_TABLET, IS_TABLET);


        if (findViewById(R.id.right_container) != null) {
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_container, fragment)
                    .commit();
        } else {
            Intent i = new Intent(this, BookDetailActivity.class);
            i.putExtras(bundle);
            startActivity(i);
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
                    case BookService.SEARCH_STATUS_OK:
                        message = -1;
                        break;
                    case BookService.SEARCH_STATUS_OK_EMPTY:
                        message = R.string.search_status_ok_empty;
                        break;
                    case BookService.SEARCH_STATUS_SERVER_DOWN:
                        message = R.string.search_status_down;
                        break;
                    case BookService.SEARCH_STATUS_SERVER_INVALID:
                        message = R.string.search_status_invalid;
                        break;
                    case BookService.SEARCH_STATUS_UNKNOWN:
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