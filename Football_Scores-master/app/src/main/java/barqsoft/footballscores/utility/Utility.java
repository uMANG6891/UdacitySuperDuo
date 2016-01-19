package barqsoft.footballscores.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.DatabaseContract.ScoresTable;
import barqsoft.footballscores.data.ScoreHttpClient;
import barqsoft.footballscores.service.MyFetchService.ScoreStatus;
import cz.msebera.android.httpclient.Header;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    public static String getLeague(int league_num) {
        switch (league_num) {
            case SERIE_A:
                return "Seria A";
            case PREMIER_LEGAUE:
                return "Premier League";
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case BUNDESLIGA:
                return "Bundesliga";
            default:
                return "Not known League Please report";
        }
    }

    public static String getMatchDay(int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return "Group Stages, Matchday : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if (match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }

    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.US);
        return sdf.format(date);
    }


    public static String getDayName(Context context, String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = sdf.parse(dateString);
            return getDayName(context, date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("error", e.getLocalizedMessage());
        }
        return null;
    }

    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Date todayDate = new Date();
        String inputStr = getDateString(new Date(dateInMillis));
        String todayStr = getDateString(todayDate);

        Calendar calTomorrow = Calendar.getInstance(),
                calYesterday = Calendar.getInstance();

        calTomorrow.setTime(todayDate);
        calYesterday.setTime(todayDate);

        calTomorrow.add(Calendar.DATE, 1);
        calYesterday.add(Calendar.DATE, -1);

        if (inputStr.equals(todayStr)) {
            return context.getString(R.string.today);
        } else if (inputStr.equals(getDateString(calTomorrow.getTime()))) {
            return context.getString(R.string.tomorrow);
        } else if (inputStr.equals(getDateString(calYesterday.getTime()))) {
            return context.getString(R.string.yesterday);
        } else {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            return dayFormat.format(dateInMillis);
        }
    }

    public static void setScoreStatus(Context c, @ScoreStatus int locationStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(Constants.PREF_SCORE_STATUS, locationStatus);
        spe.apply();
    }

    @SuppressWarnings("ResourceType")
    public static
    @ScoreStatus
    int getScoreStatus(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(Constants.PREF_SCORE_STATUS, 0);
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static void getImageUrl(final Context context, final String homeOrAway, final ImageView ivCrest, String url, final String _id) {
        ScoreHttpClient client = new ScoreHttpClient(context);
        client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String crestUrl = null;
                        if (responseBody != null)
                            crestUrl = getCrestUrl(new String(responseBody));
                        if (crestUrl != null) {
                            // replace svg url with png if found
                            if (crestUrl.endsWith(".svg")) {
                                String join = getSeparatorString(crestUrl);
                                if (join != null) {
                                    String[] a = crestUrl.split(join);
                                    String[] b = crestUrl.split("/");
                                    crestUrl = a[0] + join + "thumb/" + a[1] + "/100px-" + b[b.length - 1] + ".png";
                                }
                            }
                        }
                        Log.e("url", crestUrl + ":");

                        // load image to imageView
                        if (crestUrl != null)
                            loadImage(context, ivCrest, crestUrl);
                        else
                            loadImageWithError(ivCrest);

                        // save image url to database
                        ContentValues values = new ContentValues();
                        if (homeOrAway.equals(Constants.HOME))
                            values.put(ScoresTable.HOME_IMAGE_URL_COL, crestUrl);
                        else if (homeOrAway.equals(Constants.AWAY))
                            values.put(ScoresTable.AWAY_IMAGE_URL_COL, crestUrl);
                        if (values.size() > 0)
                            context.getContentResolver().update(DatabaseContract.BASE_CONTENT_URI,
                                    values,
                                    ScoresTable._ID + " = ?",
                                    new String[]{_id});
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {
                        loadImageWithError(ivCrest);
                    }
                }

        );
    }

    public static void loadImage(Context context, ImageView ivCrest, String crestUrl) {
        Ion.with(context)
                .load(crestUrl)
                .withBitmap()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.no_icon)
                .intoImageView(ivCrest);
    }

    private static void loadImageWithError(ImageView crest) {
        crest.setImageResource(R.drawable.no_icon);
    }

    private static String getCrestUrl(String s) {
        try {
            JSONObject jo = new JSONObject(s);
            return jo.getString("crestUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSeparatorString(String url) {
        String pattern = "(/wikipedia/../)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(0);
        } else {
            return null;
        }
    }

    public static Bitmap getImageBitmapFromUrl(Context context, String url) {
        if (url != null && url.length() != 0) try {
            return Ion.with(context)
                    .load(url)
                    .withBitmap()
                    .asBitmap()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
