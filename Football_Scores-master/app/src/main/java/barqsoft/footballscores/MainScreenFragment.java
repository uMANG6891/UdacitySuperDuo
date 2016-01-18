package barqsoft.footballscores;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.service.MyFetchService;

/**
 * A placeholder fragment containing a simple emptyView.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public scoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentDate = new String[1];
    private int last_selected_item = -1;

    TextView tvEmptyList;

    public MainScreenFragment() {
    }

    private void update_scores() {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(String date) {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new scoresAdapter(getActivity(), null, 0);
        score_list.setAdapter(mAdapter);

        tvEmptyList = (TextView) rootView.findViewById(R.id.empty_text_view);
        score_list.setEmptyView(tvEmptyList);

        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentDate, null);
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
}
