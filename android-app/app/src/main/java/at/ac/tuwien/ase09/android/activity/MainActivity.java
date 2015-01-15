package at.ac.tuwien.ase09.android.activity;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Properties;

import at.ac.tuwien.ase09.android.fragment.NavigationDrawerFragment;
import at.ac.tuwien.ase09.android.fragment.PortfolioContextFragment;
import at.ac.tuwien.ase09.android.fragment.PortfolioViewFragment;
import at.ac.tuwien.ase09.android.fragment.ValuePaperSearchFragment;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.android.singleton.WebserviceFactory;
import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, PortfolioContextFragment.PortfolioContextChangeListener, ValuePaperSearchFragment.ValuePaperSelectionListener {
    private static final String LOG_TAG = "MainActivity";

    private static final int REQUEST_CREATE_ORDER = 1;

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
        FragmentManager fragmentManager = getFragmentManager();
        switch(position) {
            case 0:
                // open portfolio view
                PortfolioViewFragment portfolioViewFragment = new PortfolioViewFragment();
                fragmentManager.beginTransaction().replace(R.id.container, portfolioViewFragment).commit();
                mTitle = getString(R.string.menu_portfolio_view);
                break;
            case 1:
                // open value paper search
                ValuePaperSearchFragment valuePaperSearchFragment = new ValuePaperSearchFragment();
                fragmentManager.beginTransaction().replace(R.id.container, valuePaperSearchFragment).commit();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CREATE_ORDER && resultCode == RESULT_OK){
            Toast.makeText(this, data.getStringExtra(OrderActivity.RESULT_MESSAGE), Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onValuePaperSelected(ValuePaperDto valuePaper) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(OrderActivity.ARG_VALUE_PAPER, valuePaper);
        startActivityForResult(intent, REQUEST_CREATE_ORDER);
    }

}
