package barqsoft.footballscores.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ui.activity.AboutActivity;
import barqsoft.footballscores.ui.activity.MainActivity;
import barqsoft.footballscores.utility.Utility;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment {
    FragmentActivity parentActivity;
    public static final int NUM_PAGES = 5;

    public ViewPager mPagerHandler;
    private TabLayout tabLayout;
    private MyPageAdapter mPagerAdapter;

    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

        setHasOptionsMenu(true);
        parentActivity = getActivity();

        ((AppCompatActivity) parentActivity).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));

        parentActivity.setTitle(R.string.app_name);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new MyPageAdapter(getChildFragmentManager());
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(sdf.format(fragmentDate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.CURRENT_FRAGMENT);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPagerHandler);

        return rootView;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return Utility.getDayName(getActivity(), System.currentTimeMillis() + ((position - 2) * 86400000));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(parentActivity, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
