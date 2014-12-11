package at.ac.tuwien.ase09.portfolioapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;

import at.ac.tuwien.ase09.portfolioapp.fragment.PortfolioViewFragment;
import at.ac.tuwien.ase09.portfolioapp.singleton.WebserviceFactory;


public class PortfolioViewActivity extends Activity implements TabListener {

    private List<Fragment> fragList = new ArrayList<Fragment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tab = bar.newTab();
        tab.setText("Performance");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("Einzeltitel");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("Order");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("Transaktionen");
        tab.setTabListener(this);
        bar.addTab(tab);

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Fragment f = null;
        PortfolioViewFragment tf = null;

        if (fragList.size() > tab.getPosition())
            fragList.get(tab.getPosition());

        if (f == null) {
            tf = new PortfolioViewFragment();
            Bundle data = new Bundle();
            data.putInt("idx",  tab.getPosition());
            tf.setArguments(data);
            fragList.add(tf);
        }
        else
            tf = (PortfolioViewFragment) f;

        ft.replace(android.R.id.content, tf);

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (fragList.size() > tab.getPosition()) {
            ft.remove(fragList.get(tab.getPosition()));
        }

    }

}
