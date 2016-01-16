package it.jaschke.alexandria.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.services.BookService.SearchStatus;

/**
 * Created by umang on 15/01/16.
 */
public class Utility {

    public static void shareBook(Context con, String bookTitle) {
        con.startActivity(Intent.createChooser(createShareIntent(con, bookTitle), con.getString(R.string.share_book_title, bookTitle)));
    }

    public static Intent createShareIntent(Context con, String bookTitle) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from((Activity) con)
                .setType("text/plain")
                .setText(con.getString(R.string.share_text, bookTitle));
        return builder.getIntent();
    }

    public static void setSearchStatus(Context c, @SearchStatus int locationStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(Constants.PREF_SEARCH_STATUS, locationStatus);
        spe.commit();
    }

    @SuppressWarnings("ResourceType")
    public static
    @SearchStatus
    int getSearchStatus(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(Constants.PREF_SEARCH_STATUS, 0);
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
