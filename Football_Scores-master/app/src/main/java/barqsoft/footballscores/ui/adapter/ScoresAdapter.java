package barqsoft.footballscores.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ui.activity.MainActivity;
import barqsoft.footballscores.utility.Constants;
import barqsoft.footballscores.utility.Utility;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {

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
        mHolder.home_name.setText(cursor.getString(Constants.COL_HOME));
        mHolder.away_name.setText(cursor.getString(Constants.COL_AWAY));
        mHolder.date.setText(cursor.getString(Constants.COL_MATCH_TIME));
        mHolder.score.setText(Utility.getScores(cursor.getInt(Constants.COL_HOME_GOALS), cursor.getInt(Constants.COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(Constants.COL_MATCH_ID);

        String homeUrl = cursor.getString(Constants.COL_HOME_IMAGE_URL);
        String awayUrl = cursor.getString(Constants.COL_AWAY_IMAGE_URL);
//        Log.e("homeUrl", homeUrl + ":");
//        Log.e("awayUrl", awayUrl + ":");
        if (homeUrl == null || homeUrl.length() == 0) {
            Utility.getImageUrl(context, Constants.HOME, mHolder.home_crest, cursor.getString(Constants.COL_HOME_URL), cursor.getString(Constants.COL__ID));
        } else {
            Utility.loadImage(context, mHolder.home_crest, homeUrl);
        }
        if (awayUrl == null || awayUrl.length() == 0) {
            Utility.getImageUrl(context, Constants.AWAY, mHolder.away_crest, cursor.getString(Constants.COL_AWAY_URL), cursor.getString(Constants.COL__ID));
        } else {
            Utility.loadImage(context, mHolder.away_crest, awayUrl);
        }

        mHolder.home_name.setContentDescription(null);
        mHolder.away_name.setContentDescription(null);
        mHolder.date.setContentDescription(null);
        mHolder.score.setContentDescription(null);
        mHolder.cvMain.setContentDescription(
                context.getString(R.string.score_title,
                        cursor.getString(Constants.COL_HOME),
                        cursor.getString(Constants.COL_AWAY),
                        cursor.getString(Constants.COL_MATCH_TIME),
                        Utility.getDayName(context, cursor.getString(Constants.COL_DATE)),
                        cursor.getInt(Constants.COL_HOME_GOALS) < 0 ? 0 : cursor.getInt(Constants.COL_HOME_GOALS),
                        cursor.getInt(Constants.COL_AWAY_GOALS) < 0 ? 0 : cursor.getInt(Constants.COL_AWAY_GOALS))
        );

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));

        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        container.removeAllViews();
        if (mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");
            View v = layoutInflater.inflate(R.layout.detail_fragment, container, false);
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utility.getMatchDay(cursor.getInt(Constants.COL_MATCH_DAY),
                    cursor.getInt(Constants.COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utility.getLeague(cursor.getInt(Constants.COL_LEAGUE)));
            Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
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
