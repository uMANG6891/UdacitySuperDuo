package barqsoft.footballscores.utility;

import android.widget.ImageView;

import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by umang on 18/01/16.
 */
public class Constants {
    public static final String PREF_SCORE_STATUS = "pref_score_status";

    public static final String HOME = "home";
    public static final String AWAY = "away";

    public static final String[] SCORE_PROJECTION = {
            DatabaseContract.ScoresTable._ID,
            DatabaseContract.ScoresTable.LEAGUE_COL,
            DatabaseContract.ScoresTable.DATE_COL,
            DatabaseContract.ScoresTable.TIME_COL,
            DatabaseContract.ScoresTable.HOME_COL,
            DatabaseContract.ScoresTable.AWAY_COL,
            DatabaseContract.ScoresTable.HOME_GOALS_COL,
            DatabaseContract.ScoresTable.AWAY_GOALS_COL,
            DatabaseContract.ScoresTable.MATCH_ID,
            DatabaseContract.ScoresTable.MATCH_DAY,
            DatabaseContract.ScoresTable.HOME_URL_COL,
            DatabaseContract.ScoresTable.AWAY_URL_COL,
            DatabaseContract.ScoresTable.HOME_IMAGE_URL_COL,
            DatabaseContract.ScoresTable.AWAY_IMAGE_URL_COL
    };

    public static final int COL__ID = 0;
    public static final int COL_LEAGUE = 1;
    public static final int COL_DATE = 2;
    public static final int COL_MATCH_TIME = 3;
    public static final int COL_HOME = 4;
    public static final int COL_AWAY = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_MATCH_ID = 8;
    public static final int COL_MATCH_DAY = 9;
    public static final int COL_HOME_URL = 10;
    public static final int COL_AWAY_URL = 11;
    public static final int COL_HOME_IMAGE_URL = 12;
    public static final int COL_AWAY_IMAGE_URL = 13;
}
