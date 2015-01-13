package at.ac.tuwien.ase09.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.OrderListAdapter;
import at.ac.tuwien.ase09.android.adapater.TransactionListAdapter;
import at.ac.tuwien.ase09.android.service.RestResultReceiver;
import at.ac.tuwien.ase09.android.service.RestService;
import at.ac.tuwien.ase09.rest.model.OrderDto;
import at.ac.tuwien.ase09.rest.model.TransactionDto;

/**
 * Created by Moritz on 11.12.2014.
 */
public class PortfolioViewFragment extends SlidingTabsBasicFragment implements RestResultReceiver.Receiver {
    private static final String LOG_TAG = "PortfolioViewFragment";

    private RestResultReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        receiver = new RestResultReceiver(new Handler());
        receiver.setReceiver(this);
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestService.class);
        intent.putExtra("receiver", receiver);
        intent.putExtra("command", RestService.COMMAND_INITIAL_PORTFOLIO);
        getActivity().startService(intent);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        receiver.setReceiver(null); // clear receiver so no leaks.
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RestService.STATUS_RUNNING:
                //show progress
                break;
            case RestService.STATUS_FINISHED:
//                List results = resultData.getParcelableList("results");
                // do something interesting
                // hide progress
                break;
            case RestService.STATUS_ERROR:
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    protected PagerAdapter createPagerAdapter() {
        return new PortfolioViewPagerAdapter();
    }

    private class PortfolioViewPagerAdapter extends PagerAdapter {
        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the
         * same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link at.ac.tuwien.ase09.android.common.view.SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0: return getString(R.string.tab_performance);
                case 1: return getString(R.string.tab_transactions);
                case 2: return getString(R.string.tab_orders);
                case 3: return getString(R.string.tab_valuepapers);
            }
            return null;
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link android.view.View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view;
            if(position == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.portfolioview_performance_tab,
                        container, false);
            }else if(position >= 1 && position <= 2) {
                view = getActivity().getLayoutInflater().inflate(R.layout.portfolioview_listview_tab,
                        container, false);
                ListView list = (ListView) view.findViewById(R.id.portfolio_list);
                switch(position) {
                    case 1: ArrayList<TransactionDto> transactions = new ArrayList<TransactionDto>();
                            transactions.add(new TransactionDto());
                            transactions.add(new TransactionDto());
                            list.setAdapter(new TransactionListAdapter(getActivity().getLayoutInflater(), transactions));
                            break;
                    case 2: ArrayList<OrderDto> orders = new ArrayList<OrderDto>();
                            orders.add(new OrderDto());
                            orders.add(new OrderDto());
                            list.setAdapter(new OrderListAdapter(getActivity().getLayoutInflater(), orders));
                            break;
                    case 3: //TODO: value papers
                }
            }else{
                throw new IllegalStateException("Position [" + position + "] does not exist");
            }

            // Add the newly created View to the ViewPager
            container.addView(view);

            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
         * {@link android.view.View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }
    }
}
