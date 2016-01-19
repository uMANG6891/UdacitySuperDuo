package barqsoft.footballscores.data;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import barqsoft.footballscores.R;

public class ScoreHttpClient extends AsyncHttpClient {

    private static AsyncHttpClient client;

    public ScoreHttpClient(Context context) {
        client = new AsyncHttpClient();
        client.addHeader("X-Auth-Token", context.getString(R.string.api_key));
    }

    @Override
    public RequestHandle get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return client.get(url, params, responseHandler);
    }
}