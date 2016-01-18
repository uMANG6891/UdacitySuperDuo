package barqsoft.footballscores.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ui.activity.MainActivity;
import barqsoft.footballscores.utility.Utility;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCH_DAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCH_TIME = 2;
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    Context context;
    LayoutInflater layoutInflater;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCH_TIME));
        mHolder.score.setText(Utility.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utility.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utility.getTeamCrestByTeamName(cursor.getString(COL_AWAY)));

        mHolder.home_name.setContentDescription(null);
        mHolder.away_name.setContentDescription(null);
        mHolder.date.setContentDescription(null);
        mHolder.score.setContentDescription(null);
        mHolder.cvMain.setContentDescription(
                context.getString(R.string.score_title,
                        cursor.getString(COL_HOME),
                        cursor.getString(COL_AWAY),
                        cursor.getString(COL_MATCH_TIME),
                        Utility.getDayName(context, System.currentTimeMillis() + ((cursor.getPosition() - 2) * 86400000)),
                        cursor.getInt(COL_HOME_GOALS) < 0 ? 0 : cursor.getInt(COL_HOME_GOALS),
                        cursor.getInt(COL_AWAY_GOALS) < 0 ? 0 : cursor.getInt(COL_AWAY_GOALS))
        );

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));

        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        container.removeAllViews();
        if (mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");
            View v = layoutInflater.inflate(R.layout.detail_fragment, container, false);
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utility.getMatchDay(cursor.getInt(COL_MATCH_DAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utility.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
            container.addView(v);
        }

        mHolder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder selected = (ViewHolder) v.getTag();
                if (detail_match_id == selected.match_id) {
                    detail_match_id = 0;
                    MainActivity.selected_match_id = 0;
                } else {
                    detail_match_id = selected.match_id;
                    MainActivity.selected_match_id = (int) selected.match_id;
                }
                notifyDataSetChanged();
            }
        });

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
