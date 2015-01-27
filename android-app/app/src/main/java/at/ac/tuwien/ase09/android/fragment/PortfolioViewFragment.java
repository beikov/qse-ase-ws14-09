package at.ac.tuwien.ase09.android.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.LayoutPopulator;
import at.ac.tuwien.ase09.android.adapater.PortfolioValuePaperArrayAdapter;
import at.ac.tuwien.ase09.android.listener.ValuePaperSelectionListener;
import at.ac.tuwien.ase09.android.service.RestResultReceiver;
import at.ac.tuwien.ase09.android.service.RestService;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.android.singleton.UserContext;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;

/**
 * Created by Moritz on 11.12.2014.
 */
public class PortfolioViewFragment extends Fragment implements RestResultReceiver.Receiver, AbsListView.OnItemClickListener {
    private static final String LOG_TAG = "PortfolioViewFragment";

    public static final String ARG_PORTFOLIO = "at.ac.tuwien.ase09.android.fragment.PortfolioViewFragment.PORTFOLIO";

    private RestResultReceiver receiver;

    private ArrayAdapter<PortfolioValuePaperDto> valuePaperListAdapter;
    private ProgressBar progressBar;
    private ProgressBar portfolioValuePapersProgressBar;
    private TextView portfolioNameTextView;
    private AbsListView valuePaperListView;
    private TextView costValueTextView;
    private TextView currentValueTextView;
    private TextView relativeCurrentValueChangeTextView;
    private TextView remainingCapitalTextView;

    private ValuePaperSelectionListener valuePaperSelectionListener;

    public static PortfolioViewFragment createInstance(PortfolioDto portfolio){
        PortfolioViewFragment portfolioViewFragment = new PortfolioViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PORTFOLIO, portfolio);
        portfolioViewFragment.setArguments(args);
        return portfolioViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolioview_fragment, container, false);
        progressBar = (ProgressBar) view.findViewById(android.R.id.progress);

        portfolioNameTextView = (TextView) view.findViewById(R.id.portfolio_name);
        costValueTextView = (TextView) view.findViewById(R.id.costValueTextView);
        currentValueTextView = (TextView) view.findViewById(R.id.currentValueTextView);
        relativeCurrentValueChangeTextView = (TextView) view.findViewById(R.id.relativeCurrentValueChangeTextView);
        remainingCapitalTextView = (TextView) view.findViewById(R.id.remainingCapitalTextView);

        FrameLayout valuePaperListContainer = (FrameLayout) view.findViewById(R.id.valuePaperListContainer);
        View portfolioValuePapersView = getActivity().getLayoutInflater().inflate(R.layout.valuepapers_layout, valuePaperListContainer);
        portfolioValuePapersProgressBar = (ProgressBar) portfolioValuePapersView.findViewById(android.R.id.progress);
        valuePaperListView = (AbsListView) portfolioValuePapersView.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) valuePaperListView).setAdapter(valuePaperListAdapter);
        valuePaperListView.setOnItemClickListener(this);

        resetViewContent();

        receiver = new RestResultReceiver(new Handler());
        receiver.setReceiver(this);

        PortfolioDto portfolio;
        if(savedInstanceState != null && (portfolio = (PortfolioDto) savedInstanceState.getSerializable(ARG_PORTFOLIO)) != null){
            showPortfolio(portfolio);
            loadValuePapers(portfolio);
        } else {
            final Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestService.class);
            intent.putExtra("receiver", receiver);
            intent.putExtra("command", RestService.COMMAND_INITIAL_PORTFOLIO);
            getActivity().startService(intent);
        }

        return view;
    }

    private void showPortfolio(PortfolioDto portfolio){
        portfolioNameTextView.setText(portfolio.getName());
        Currency currency = portfolio.getCurrency();
        DecimalFormat moneyFormat = LayoutPopulator.getMoneyFormat(currency);
        costValueTextView.setText(moneyFormat.format(portfolio.getCostValue()) + " " + currency.getCurrencyCode());
        currentValueTextView.setText(moneyFormat.format(portfolio.getCurrentValue()) + " " + currency.getCurrencyCode());
        BigDecimal relativeCurrentValueChange;
        if(portfolio.getCurrentValue().compareTo(BigDecimal.ZERO) == 0) {
            relativeCurrentValueChange = BigDecimal.ZERO;
        }else{
            relativeCurrentValueChange = portfolio.getCurrentValue().divide(portfolio.getCostValue(), RoundingMode.HALF_DOWN).subtract(new BigDecimal(1));
        }

        relativeCurrentValueChangeTextView.setText(LayoutPopulator.getPercentFormat().format(relativeCurrentValueChange.floatValue() * 100) + " %");
        LayoutPopulator.setColorBySignum(relativeCurrentValueChangeTextView, relativeCurrentValueChange.signum());
        remainingCapitalTextView.setText(moneyFormat.format(portfolio.getCurrentCapital()) + " " + currency.getCurrencyCode());
    }

    private void showPortfolioValuePapers(List<PortfolioValuePaperDto> valuePapers){
        valuePaperListAdapter = new PortfolioValuePaperArrayAdapter(getActivity(), valuePapers, getActivity());
        valuePaperListView.setAdapter(valuePaperListAdapter);
    }

    private void resetViewContent(){
        portfolioNameTextView.setText("");
        costValueTextView.setText("");
        currentValueTextView.setText("");
        relativeCurrentValueChangeTextView.setText("");
        remainingCapitalTextView.setText("");
        valuePaperListView.setAdapter(null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            valuePaperSelectionListener = (ValuePaperSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (valuePaperSelectionListener != null) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            valuePaperSelectionListener.onValuePaperSelected(valuePaperListAdapter.getItem(position).getValuePaperDto());
        }
    }

    private void loadValuePapers(PortfolioDto portfolio){
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestService.class);
        intent.putExtra("receiver", receiver);
        intent.putExtra("command", RestService.COMMAND_PORTFOLIO_VALUE_PAPERS);
        intent.putExtra(RestService.COMMAND_PORTFOLIO_VALUE_PAPERS_ID_ARG, portfolio.getId());
        getActivity().startService(intent);
    }

    public void onInitialPortfolioResult(int resultCode, Bundle resultData){
        switch (resultCode) {
            case RestService.STATUS_RUNNING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RestService.STATUS_FINISHED:
                PortfolioDto portfolio = PortfolioContext.getPortfolio();
                showPortfolio(portfolio);
                loadValuePapers(portfolio);

                progressBar.setVisibility(View.GONE);
                break;
            case RestService.STATUS_ERROR:
                resetViewContent();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void onPortfolioValuePapersResult(int resultCode, Bundle resultData){
        switch (resultCode) {
            case RestService.STATUS_RUNNING:
                portfolioValuePapersProgressBar.setVisibility(View.VISIBLE);
                break;
            case RestService.STATUS_FINISHED:
                showPortfolioValuePapers((List<PortfolioValuePaperDto>) resultData.getSerializable("results"));
                portfolioValuePapersProgressBar.setVisibility(View.GONE);
                break;
            case RestService.STATUS_ERROR:
                portfolioValuePapersProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        final int command = resultData.getInt("command");
        switch(command){
            case RestService.COMMAND_INITIAL_PORTFOLIO:
                onInitialPortfolioResult(resultCode, resultData);
                break;
            case RestService.COMMAND_PORTFOLIO_VALUE_PAPERS:
                onPortfolioValuePapersResult(resultCode, resultData);
                break;
        }
    }

}
