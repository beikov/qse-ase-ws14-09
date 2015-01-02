package at.ac.tuwien.ase09.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ase09.android.R;

import at.ac.tuwien.ase09.android.service.RestQueryResultReceiver;
import at.ac.tuwien.ase09.android.service.RestQueryService;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link at.ac.tuwien.ase09.android.fragment.PortfolioContextFragment.PortfolioContextChangeListener}
 * interface.
 */
public class PortfolioContextFragment extends Fragment implements AbsListView.OnItemClickListener, RestQueryResultReceiver.Receiver {

    private PortfolioContextChangeListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private RestQueryResultReceiver receiver;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private PortfolioArrayAdapter mAdapter;

    private ProgressBar progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PortfolioContextFragment() {
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RestQueryService.STATUS_RUNNING:
                progress.setVisibility(View.VISIBLE);
                break;
            case RestQueryService.STATUS_FINISHED:
                List<PortfolioDto> portfolios = (List<PortfolioDto>) resultData.getSerializable("results");
                mAdapter = new PortfolioArrayAdapter(getActivity(),
                        R.layout.portfolio_item, R.id.portfolio_name, portfolios);
                ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

                // do something interesting
                progress.setVisibility(View.GONE);
                break;
            case RestQueryService.STATUS_ERROR:
                progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        receiver.setReceiver(null); // clear receiver so no leaks.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new RestQueryResultReceiver(new Handler());
        receiver.setReceiver(this);
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestQueryService.class);
        intent.putExtra("receiver", receiver);
        intent.putExtra("command", RestQueryService.COMMAND_PORTFOLIOS);
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio_context_fragment, container, false);

        progress = (ProgressBar) view.findViewById(android.R.id.progress);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PortfolioContextChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPortfolioContextChange(mAdapter.getItem(position));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface PortfolioContextChangeListener {
        public void onPortfolioContextChange(PortfolioDto portfolio);
    }

    private class PortfolioArrayAdapter extends ArrayAdapter<PortfolioDto>  {
        private static final String LOG_TAG = "PortfolioArrayAdapter";

        private int resource;
        private int fieldId = 0;

        public PortfolioArrayAdapter(Context context, int resource) {
            super(context, resource);
            this.resource = resource;
        }

        public PortfolioArrayAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.resource = resource;
            this.fieldId = textViewResourceId;
        }

        public PortfolioArrayAdapter(Context context, int resource, PortfolioDto[] objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        public PortfolioArrayAdapter(Context context, int resource, int textViewResourceId, PortfolioDto[] objects) {
            super(context, resource, textViewResourceId, objects);
            this.resource = resource;
            this.fieldId = textViewResourceId;
        }

        public PortfolioArrayAdapter(Context context, int resource, List<PortfolioDto> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        public PortfolioArrayAdapter(Context context, int resource, int textViewResourceId, List<PortfolioDto> objects) {
            super(context, resource, textViewResourceId, objects);
            this.resource = resource;
            this.fieldId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(resource, parent, false);
            } else {
                view = convertView;
            }

            try {
                if (fieldId == 0) {
                    //  If no custom field is assigned, assume the whole resource is a TextView
                    text = (TextView) view;
                } else {
                    //  Otherwise, find the TextView field within the layout
                    text = (TextView) view.findViewById(fieldId);
                }
            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }

            PortfolioDto portfolio = getItem(position);
            text.setText(portfolio.getName());

            if(PortfolioContext.getPortfolio() != null){
                if(portfolio.getId().equals(PortfolioContext.getPortfolio().getId())){
                    text.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            return view;
        }
    }
}
