package at.ac.tuwien.ase09.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.android.singleton.WebserviceFactory;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.ValuePaperResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * Created by Moritz on 12.12.2014.
 */
public class RestQueryService extends IntentService {
    private static final String LOG_TAG = "RestQueryService";

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 3;

    public static final String COMMAND_PORTFOLIOS = "PORTFOLIOS";
    public static final String COMMAND_INITIAL_PORTFOLIO = "INITIAL_PORTFOLIOS";
    public static final String COMMAND_SEARCH_VALUE_PAPERS = "SEARCH_VALUE_PAPERS";

    public static final String COMMAND_SEARCH_VALUE_PAPERS_ARG_FILTER = "FILTER";
    public static final String COMMAND_SEARCH_VALUE_PAPERS_ARG_TYPE = "VALUE_PAPER_TYPE";

    public RestQueryService() {
        super(LOG_TAG);
    }

    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        final String command = intent.getStringExtra("command");
        Bundle b = new Bundle();

        try {
            if (command.equals(COMMAND_PORTFOLIOS)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                b.putSerializable("results", getPortfolios());
                receiver.send(STATUS_FINISHED, b);
            } else if (command.equals(COMMAND_INITIAL_PORTFOLIO)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                b.putSerializable("results", getInitialPortfolioContext());
                receiver.send(STATUS_FINISHED, b);
            } else if (command.equals(COMMAND_SEARCH_VALUE_PAPERS)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                b.putSerializable("results", searchValuePapers(intent.getStringExtra(COMMAND_SEARCH_VALUE_PAPERS_ARG_FILTER), (ValuePaperType) intent.getSerializableExtra(COMMAND_SEARCH_VALUE_PAPERS_ARG_TYPE)));
                receiver.send(STATUS_FINISHED, b);
            }
        } catch (Exception e) {
            b.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, b);
            Log.e(LOG_TAG, "Command failed: " + command, e);
        }
    }

    private PortfolioDto getInitialPortfolioContext() {
        Log.i(LOG_TAG, "Query default portfolio context");
        PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
        List<PortfolioDto> portfolios = portfolioResource.getPortfolios(7730l); //TODO: use user id of logged in user
        if (!portfolios.isEmpty()) {
            PortfolioContext.setPortfolio(portfolios.get(0));
        }
        return PortfolioContext.getPortfolio();
    }

    private ArrayList<PortfolioDto> getPortfolios() {
        Log.i(LOG_TAG, "Query portfolios");
        PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
        return new ArrayList<PortfolioDto>(portfolioResource.getPortfolios(7730l)); //TODO: use user id of logged in user
    }

    private ArrayList<ValuePaperDto> searchValuePapers(String filter, ValuePaperType valuePaperType) {
        Log.i(LOG_TAG, "Query value papers by filter");
        ValuePaperResource valuePaperResource = WebserviceFactory.getInstance().getValuePaperResource();
        return new ArrayList<ValuePaperDto>(valuePaperResource.getValuePapers(filter, valuePaperType));
    }
}
