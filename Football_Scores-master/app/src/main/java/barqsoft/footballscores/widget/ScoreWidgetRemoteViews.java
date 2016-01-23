package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.utility.Constants;
import barqsoft.footballscores.utility.Utility;

/**
 * Created by umang on 17/01/16.
 */
public class ScoreWidgetRemoteViews extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor cursor = null;
            Context context;

            @Override
            public void onCreate() {
                context = getBaseContext();
            }

            @Override
            public void onDataSetChanged() {
                Uri uri = DatabaseContract.ScoresTable.buildScoreWithDate();
                String formatString = getString(R.string.date_format_ymd);
                SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.US);
                String todayDate = format.format(new Date());

                final long token = Binder.clearCallingIdentity();
                try {
                    cursor = getContentResolver().query(uri,
                            Constants.SCORE_PROJECTION,
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
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item_layout);
                views.setTextViewText(R.id.home_name, cursor.getString(Constants.COL_HOME));
                views.setTextViewText(R.id.away_name, cursor.getString(Constants.COL_AWAY));
                views.setTextViewText(R.id.data_textview, cursor.getString(Constants.COL_MATCH_TIME));
                views.setTextViewText(R.id.score_textview,
                        Utility.getScores(
                                cursor.getInt(Constants.COL_HOME_GOALS),
                                cursor.getInt(Constants.COL_AWAY_GOALS)));

//                views.setImageViewResource(R.id.home_crest, R.drawable.no_icon);
//                views.setImageViewResource(R.id.away_crest, R.drawable.no_icon);
//
//                Bitmap image = Utility.getImageBitmapFromUrl(context, cursor.getString(Constants.COL_HOME_IMAGE_URL));
//                if (image != null) {
//                    views.setImageViewBitmap(R.id.home_crest, image);
//                }
//                image = Utility.getImageBitmapFromUrl(context, cursor.getString(Constants.COL_AWAY_IMAGE_URL));
//                if (image != null) {
//                    views.setImageViewBitmap(R.id.away_crest, image);
//                }


                views.setContentDescription(R.id.widget_item_main,
                        getString(R.string.score_title,
                                cursor.getString(Constants.COL_HOME),
                                cursor.getString(Constants.COL_AWAY),
                                cursor.getString(Constants.COL_MATCH_TIME),
                                Utility.getDayName(getBaseContext(), System.currentTimeMillis()),
                                cursor.getInt(Constants.COL_HOME_GOALS) < 0 ? 0 : cursor.getInt(Constants.COL_HOME_GOALS),
                                cursor.getInt(Constants.COL_AWAY_GOALS) < 0 ? 0 : cursor.getInt(Constants.COL_AWAY_GOALS)));

                views.setContentDescription(R.id.home_name, null);
                views.setContentDescription(R.id.away_name, null);
                views.setContentDescription(R.id.data_textview, null);
                views.setContentDescription(R.id.score_textview, null);
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
                    return cursor.getLong(Constants.COL__ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
