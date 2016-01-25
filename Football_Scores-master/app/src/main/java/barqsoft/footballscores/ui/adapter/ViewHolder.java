package barqsoft.footballscores.ui.adapter;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder {
    @Bind(R.id.scores_list_main)
    CardView cvMain;
    @Bind(R.id.home_name)
    TextView home_name;
    @Bind(R.id.away_name)
    TextView away_name;
    @Bind(R.id.score_textview)
    TextView score;
    @Bind(R.id.data_textview)
    TextView date;
    @Bind(R.id.home_crest)
    ImageView home_crest;
    @Bind(R.id.away_crest)
    ImageView away_crest;
    public double match_id;

    public ViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
