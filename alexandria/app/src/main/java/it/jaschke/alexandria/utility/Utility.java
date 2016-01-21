package it.jaschke.alexandria.utility;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.data.FetchBookInfo.SearchStatus;
import it.jaschke.alexandria.utility.Constants.Book;

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
        spe.apply();
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


    public static void deleteBook(Context context, String ISBNNumber) {
        context.getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ISBNNumber)), null, null);
    }

    private static void writeBackBook(Context context, String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values = new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        context.getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI, values);
    }

    private static void writeBackAuthors(Context context, String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.AuthorEntry._ID, ean);
            values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            context.getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    private static void writeBackCategories(Context context, String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.CategoryEntry._ID, ean);
            values.put(AlexandriaContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            context.getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    public static void saveBook(Context context, ArrayMap<String, String> bookInfo) {
        try {
            writeBackBook(context,
                    bookInfo.get(Book.EAN),
                    bookInfo.get(Book.TITLE),
                    bookInfo.get(Book.SUBTITLE),
                    bookInfo.get(Book.DESC),
                    bookInfo.get(Book.IMG_URL));

            if (bookInfo.containsKey(Book.AUTHORS))
                writeBackAuthors(context,
                        bookInfo.get(Book.EAN),
                        new JSONArray(bookInfo.get(Book.AUTHORS)));

            if (bookInfo.containsKey(Book.CATEGORIES))
                writeBackCategories(context,
                        bookInfo.get(Book.EAN),
                        new JSONArray(bookInfo.get(Book.CATEGORIES)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
