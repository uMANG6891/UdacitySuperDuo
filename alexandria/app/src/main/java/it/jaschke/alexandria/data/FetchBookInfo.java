package it.jaschke.alexandria.data;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.util.ArrayMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cz.msebera.android.httpclient.Header;
import it.jaschke.alexandria.utility.Constants;
import it.jaschke.alexandria.utility.Utility;

/**
 * Created by umang on 20/01/16.
 */
public class FetchBookInfo {

    public static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    public static final String QUERY_PARAM = "q";

    public static void load(final Context context, final String ISBNNumber, final GetBookInfo callback) {
        final String ISBN_PARAM = "isbn:" + ISBNNumber;

        RequestParams params = new RequestParams();
        params.add(QUERY_PARAM, ISBN_PARAM);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BOOK_BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) {
                    Utility.setSearchStatus(context, SEARCH_STATUS_SERVER_DOWN);
                    callback.onComplete(null);
                    return;
                }
                ArrayMap<String, String> map = new ArrayMap<>();
                map.put(Constants.Book.EAN, ISBNNumber);
                makeBookInfoArrayMap(context, map, new String(responseBody));
                callback.onComplete(map);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utility.setSearchStatus(context, SEARCH_STATUS_UNKNOWN);
                callback.onComplete(null);
            }
        });
    }

    private static void makeBookInfoArrayMap(Context context, ArrayMap<String, String> map, String JSONData) {
        try {
            JSONObject bookJson = new JSONObject(JSONData);
            JSONArray bookArray;
            if (bookJson.has(Constants.Book.ITEMS)) {
                bookArray = bookJson.getJSONArray(Constants.Book.ITEMS);
                Utility.setSearchStatus(context, SEARCH_STATUS_OK);

                JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(Constants.Book.VOLUME_INFO);
                String title = bookInfo.getString(Constants.Book.TITLE);

                String subtitle = "";
                if (bookInfo.has(Constants.Book.SUBTITLE)) {
                    subtitle = bookInfo.getString(Constants.Book.SUBTITLE);
                }

                String desc = "";
                if (bookInfo.has(Constants.Book.DESC)) {
                    desc = bookInfo.getString(Constants.Book.DESC);
                }

                String imgUrl = "";
                if (bookInfo.has(Constants.Book.IMG_URL_PATH) && bookInfo.getJSONObject(Constants.Book.IMG_URL_PATH).has(Constants.Book.IMG_URL)) {
                    imgUrl = bookInfo.getJSONObject(Constants.Book.IMG_URL_PATH).getString(Constants.Book.IMG_URL);
                }

                map.put(Constants.Book.TITLE, title);
                map.put(Constants.Book.SUBTITLE, subtitle);
                if (bookInfo.has(Constants.Book.AUTHORS))
                    map.put(Constants.Book.AUTHORS, bookInfo.getJSONArray(Constants.Book.AUTHORS).toString());
                map.put(Constants.Book.DESC, desc);
                if (bookInfo.has(Constants.Book.CATEGORIES))
                    map.put(Constants.Book.CATEGORIES, bookInfo.getJSONArray(Constants.Book.CATEGORIES).toString());
                map.put(Constants.Book.IMG_URL, imgUrl);

//                        ((GetBookInfo) getBaseContext()).onComplete(map);

//                writeBackBook(ean, title, subtitle, desc, imgUrl);
//
//                if (bookInfo.has(Constants.Book.AUTHORS)) {
//                    writeBackAuthors(ean, bookInfo.getJSONArray(Constants.Book.AUTHORS));
//                }
//                if (bookInfo.has(Constants.Book.CATEGORIES)) {
//                    writeBackCategories(ean, bookInfo.getJSONArray(Constants.Book.CATEGORIES));
//                }
            } else {
                Utility.setSearchStatus(context, SEARCH_STATUS_OK_EMPTY);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Utility.setSearchStatus(context, SEARCH_STATUS_SERVER_INVALID);
        }
    }

    public interface GetBookInfo {
        void onComplete(ArrayMap<String, String> map);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SEARCH_STATUS_OK, SEARCH_STATUS_OK_EMPTY, SEARCH_STATUS_SERVER_DOWN, SEARCH_STATUS_SERVER_INVALID, SEARCH_STATUS_UNKNOWN})
    public @interface SearchStatus {}

    public static final int SEARCH_STATUS_OK = 0;
    public static final int SEARCH_STATUS_OK_EMPTY = 1;
    public static final int SEARCH_STATUS_SERVER_DOWN = 2;
    public static final int SEARCH_STATUS_SERVER_INVALID = 3;
    public static final int SEARCH_STATUS_UNKNOWN = 4;
}
