package barqsoft.footballscores.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.ScoresTable;
import barqsoft.footballscores.service.MyFetchService;
import barqsoft.footballscores.ui.activity.MainActivity;
import barqsoft.footballscores.ui.adapter.ScoresAdapter;
import barqsoft.footballscores.utility.Constants;
import barqsoft.footballscores.utility.Utility;

/**
 * A placeholder fragment containing a simple emptyView.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    public String fragmentDate;

    private final String SAVE_STATE_DATE = "save_state_date";

    TextView tvEmptyList;

    public MainScreenFragment() {
    }

    public void setFragmentDate(String date) {
        fragmentDate = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        mAdapter.detail_match_id = MainActivity.selected_match_id;

        score_list.setAdapter(mAdapter);

        tvEmptyList = (TextView) rootView.findViewById(R.id.empty_text_view);
        score_list.setEmptyView(tvEmptyList);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_STATE_DATE, fragmentDate);
    }

    // getting date value on orientation change if set
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(SAVE_STATE_DATE) != null) {
                fragmentDate = savedInstanceState.getString(SAVE_STATE_DATE);
            }
        }
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
    }

    //saving the date that this fragment belongs to
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ScoresTable.buildScoreWithDate(),
                Constants.SCORE_PROJECTION, null, new String[]{fragmentDate}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        if (cursor == null || cursor.getCount() == 0) {
            if (!Utility.isNetworkAvailable(getContext()))
                tvEmptyList.setText(R.string.no_matches_found_no_internet);
            else {
                switch (Utility.getScoreStatus(getContext())) {
                    case MyFetchService.SCORE_STATUS_OK:
                        tvEmptyList.setText(R.string.no_matches_found_status_ok);
                        update_scores();
                        break;
                    case MyFetchService.SCORE_STATUS_SERVER_DOWN:
                        tvEmptyList.setText(R.string.no_matches_found_status_server_down);
                        break;
                    case MyFetchService.SCORE_STATUS_SERVER_INVALID:
                        tvEmptyList.setText(R.string.no_matches_found_status_server_invalid);
                        break;
                    case MyFetchService.SCORE_STATUS_UNKNOWN:
                        tvEmptyList.setText(R.string.no_matches_found_status_server_unknown);
                        break;
                }
            }
        } else {
            tvEmptyList.setText("");
            mAdapter.swapCursor(cursor);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private void update_scores() {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }
}
