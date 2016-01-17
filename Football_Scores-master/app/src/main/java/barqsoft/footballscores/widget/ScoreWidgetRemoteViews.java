package barqsoft.footballscores.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;

/**
 * Created by umang on 17/01/16.
 */
public class ScoreWidgetRemoteViews extends RemoteViewsService {

    public interface ScoreQuery {
        String[] SCORE_PROJECTION = {
                DatabaseContract.scores_table.LEAGUE_COL,
                DatabaseContract.scores_table.DATE_COL,
                DatabaseContract.scores_table.TIME_COL,
                DatabaseContract.scores_table.HOME_COL,
                DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL,
                DatabaseContract.scores_table.AWAY_GOALS_COL,
                DatabaseContract.scores_table.MATCH_ID,
                DatabaseContract.scores_table.MATCH_DAY
        };
        int COL_LEAGUE = 0;
        int COL_DATE = 1;
        int COL_MATCHTIME = 2;
        int COL_HOME = 3;
        int COL_AWAY = 4;
        int COL_HOME_GOALS = 5;
        int COL_AWAY_GOALS = 6;
        int COL_MATCH_ID = 7;
        int COL_MATCH_DAY = 8;
    }


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor cursor = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
                String formatString = getString(R.string.date_format_ymd);
                SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.US);
                String todayDate = format.format(new Date());

                final long token = Binder.clearCallingIdentity();
                try {
                    cursor = getContentResolver().query(uri,
                            ScoreQuery.SCORE_PROJECTION,
                            null,
                            new String[]{todayDate},
                            null);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }
                Log.e("position", position + ":");
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item_layout);
                views.setTextViewText(R.id.home_name, cursor.getString(ScoreQuery.COL_HOME));
                views.setTextViewText(R.id.away_name, cursor.getString(ScoreQuery.COL_AWAY));
                views.setTextViewText(R.id.data_textview, cursor.getString(ScoreQuery.COL_MATCHTIME));
                views.setTextViewText(R.id.score_textview, Utility.getScores(cursor.getInt(ScoreQuery.COL_HOME_GOALS), cursor.getInt(ScoreQuery.COL_AWAY_GOALS)));
//                mHolder.match_id = cursor.getDouble(COL_ID);
                views.setImageViewResource(R.id.home_crest, Utility.getTeamCrestByTeamName(cursor.getString(ScoreQuery.COL_HOME)));
                views.setImageViewResource(R.id.away_crest, Utility.getTeamCrestByTeamName(cursor.getString(ScoreQuery.COL_AWAY)));
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item_layout);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position))
                    return cursor.getLong(ScoreQuery.COL_MATCH_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
