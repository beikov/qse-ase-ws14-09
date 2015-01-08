package at.ac.tuwien.ase09.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.ValuePaperTypeAdapter;
import at.ac.tuwien.ase09.android.service.RestQueryResultReceiver;
import at.ac.tuwien.ase09.android.service.RestQueryService;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ValuePaperSelectionListener}
 * interface.
 */
public class ValuePaperSearchFragment extends Fragment implements AbsListView.OnItemClickListener, RestQueryResultReceiver.Receiver, TextWatcher {

    private ValuePaperSelectionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private RestQueryResultReceiver receiver;

    private Intent restQueryIntent;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private EditText filterEditText;

    private Spinner valuePaperTypeSpinner;

    private ValuePaperType selectedValuePaperType;

    private String searchFilter;

    private ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ValuePaperSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.valuepapersearch_fragment, container, false);

        FrameLayout searchResultPlaceholder = (FrameLayout) view.findViewById(R.id.searchResults);
        View searchResultView = inflater.inflate(R.layout.valuepapers_layout, searchResultPlaceholder);

        // Set the adapter
        mListView = (AbsListView) searchResultView.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        progressBar = (ProgressBar) searchResultView.findViewById(android.R.id.progress);

        filterEditText = (EditText) view.findViewById(R.id.filterEditText);
        filterEditText.addTextChangedListener(this);

        valuePaperTypeSpinner = (Spinner) view.findViewById(R.id.valuePaperTypeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ValuePaperTypeAdapter valuePaperTypeAdapter = new ValuePaperTypeAdapter(getActivity(), android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        valuePaperTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        valuePaperTypeSpinner.setAdapter(valuePaperTypeAdapter);
        valuePaperTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValuePaperType = valuePaperTypeAdapter.getItem(position);
                updateSearchResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        selectedValuePaperType = valuePaperTypeAdapter.getItem(0);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ValuePaperSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    private void updateSearchResults(){
        ((AdapterView<ListAdapter>) mListView).setAdapter(null);
        if(searchFilter != null && searchFilter.length() > 1) {
            final String effectiveFilter = "*" + searchFilter + "*";
            receiver = new RestQueryResultReceiver(new Handler());
            receiver.setReceiver(this);
            restQueryIntent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestQueryService.class);
            restQueryIntent.putExtra(RestQueryService.COMMAND_SEARCH_VALUE_PAPERS_ARG_TYPE, selectedValuePaperType);
            restQueryIntent.putExtra(RestQueryService.COMMAND_SEARCH_VALUE_PAPERS_ARG_FILTER, effectiveFilter);
            restQueryIntent.putExtra("receiver", receiver);
            restQueryIntent.putExtra("command", RestQueryService.COMMAND_SEARCH_VALUE_PAPERS);
            getActivity().startService(restQueryIntent);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
        searchFilter = s.toString();
        updateSearchResults();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RestQueryService.STATUS_RUNNING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RestQueryService.STATUS_FINISHED:
                List<ValuePaperDto> valuePapers = (List<ValuePaperDto>) resultData.getSerializable("results");
                mAdapter = new ValuePaperArrayAdapter(getActivity(), valuePapers);
                ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

                progressBar.setVisibility(View.GONE);
                break;
            case RestQueryService.STATUS_ERROR:
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG);
                break;
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
            mListener.onValuePaperSelected((ValuePaperDto) mAdapter.getItem(position));
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
    public interface ValuePaperSelectionListener {
        public void onValuePaperSelected(ValuePaperDto valuePaper);
    }

    private class ValuePaperArrayAdapter extends ArrayAdapter<ValuePaperDto>  {

        public ValuePaperArrayAdapter(Context context, List<ValuePaperDto> objects) {
            super(context, 0, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView nameTextView;
            TextView codeTextView;
            TextView relativePriceChangeTextView;
            TextView absolutePriceChangeTextView;

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.valuepaper_item, parent, false);
            } else {
                view = convertView;
            }

            nameTextView = (TextView) view.findViewById(R.id.vpName);
            codeTextView = (TextView) view.findViewById(R.id.vpCode);
            relativePriceChangeTextView = (TextView) view.findViewById(R.id.vpRelativePriceChange);
            absolutePriceChangeTextView = (TextView) view.findViewById(R.id.vpAbsolutePriceChange);

            ValuePaperDto valuePaper = getItem(position);
            nameTextView.setText(valuePaper.getName());
            codeTextView.setText(valuePaper.getCode());
            BigDecimal lastPrice = valuePaper.getLastPrice();
            BigDecimal previousDayPrice = valuePaper.getPreviousDayPrice();
            if(lastPrice != null && previousDayPrice != null) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                df.setMinimumFractionDigits(0);
                df.setNegativePrefix("-");
                df.setPositivePrefix("+");

                BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
                BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);

                relativePriceChangeTextView.setText(df.format(relativePriceChange.floatValue() * 100) + " %");

                df.setCurrency(valuePaper.getCurrency());
                absolutePriceChangeTextView.setText(df.format(absolutePriceChange) + " " + valuePaper.getCurrency().getCurrencyCode());

                int textViewColor;
                switch(absolutePriceChange.signum()){
                    case -1:    textViewColor = Color.RED;
                                break;
                    case  1:    textViewColor = Color.rgb(34, 177, 76);
                                break;
                    default:    textViewColor = Color.BLACK;
                }
                absolutePriceChangeTextView.setTextColor(textViewColor);
                relativePriceChangeTextView.setTextColor(textViewColor);
            }else{
                relativePriceChangeTextView.setText("");
                absolutePriceChangeTextView.setText("");
            }
            return view;
        }
    }

}
