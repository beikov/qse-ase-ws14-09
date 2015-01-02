package at.ac.tuwien.ase09.android.activity;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import java.io.IOException;
import java.util.Properties;

import at.ac.tuwien.ase09.android.fragment.NavigationDrawerFragment;
import at.ac.tuwien.ase09.android.fragment.PortfolioContextFragment;
import at.ac.tuwien.ase09.android.fragment.PortfolioViewFragment;
import at.ac.tuwien.ase09.android.service.RestQueryResultReceiver;
import at.ac.tuwien.ase09.android.service.RestQueryService;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.android.singleton.WebserviceFactory;
import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, PortfolioContextFragment.PortfolioContextChangeListener {
    private static final String LOG_TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        AssetManager assetMgr = getAssets();
        Properties settings = new Properties();
        try {
            settings.load(assetMgr.open("settings.properties"));
            WebserviceFactory.configure(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch(position) {
            case 0:
                // open portfolio view
                FragmentManager fragmentManager = getFragmentManager();
                PortfolioViewFragment fragment = new PortfolioViewFragment();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case 1:
                // TODO: instantiate value paper search fragment
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.menu_portfolio_view);
                break;
            case 2:
                mTitle = getString(R.string.menu_valuepaper_search);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_portfolio_context) {
            getFragmentManager().beginTransaction().replace(R.id.container, new PortfolioContextFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPortfolioContextChange(PortfolioDto portfolio) {
        PortfolioContext.setPortfolio(portfolio);
        getFragmentManager().beginTransaction().replace(R.id.container, new PortfolioViewFragment()).commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
