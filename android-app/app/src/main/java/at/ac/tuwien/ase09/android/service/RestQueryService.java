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
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;

/**
 * Created by Moritz on 12.12.2014.
 */
public class RestQueryService extends IntentService {
    private static final String LOG_TAG = "RestQueryService";

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 3;

    public static final String COMMAND_PORTFOLIOS = "portfolios";

    public RestQueryService() {
        super(LOG_TAG);
    }

    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if(command.equals(COMMAND_PORTFOLIOS)) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                // get some data or something
                b.putSerializable("results", getPortfolios());
                receiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
                Log.e(LOG_TAG, "Could not load portfolio context", e);
            }
        }
    }

    private PortfolioDto getPortfolioContext(){
        if(PortfolioContext.getPortfolio() == null){
            Log.i(LOG_TAG, "Query default portfolio context");
            PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
            List<PortfolioDto> portfolios = portfolioResource.getPortfolios(7730l); //TODO: use user id of logged in user
            if(!portfolios.isEmpty()){
                PortfolioContext.setPortfolio(portfolios.get(0));
            }
        }
        return PortfolioContext.getPortfolio();
    }

    private ArrayList<PortfolioDto> getPortfolios(){
        PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
        return new ArrayList<PortfolioDto>(portfolioResource.getPortfolios(7730l)); //TODO: use user id of logged in user
    }
}
