package at.ac.tuwien.ase09.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.android.singleton.WebserviceFactory;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.rest.OrderResource;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.ValuePaperResource;
import at.ac.tuwien.ase09.rest.model.OrderDto;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * Created by Moritz on 12.12.2014.
 */
public class RestService extends IntentService {
    private static final String LOG_TAG = "RestQueryService";

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 3;

    public static final int COMMAND_PORTFOLIOS = 0;

    public static final int COMMAND_INITIAL_PORTFOLIO = 1;

    public static final int COMMAND_SEARCH_VALUE_PAPERS = 2;
    public static final String COMMAND_SEARCH_VALUE_PAPERS_ARG_FILTER = "FILTER";
    public static final String COMMAND_SEARCH_VALUE_PAPERS_ARG_TYPE = "VALUE_PAPER_TYPE";

    public static final int COMMAND_CREATE_ORDER = 3;
    public static final String COMMAND_CREATE_ORDER_TYPE_ARG = "ORDER_TYPE";
    public static final String COMMAND_CREATE_ORDER_ACTION_ARG = "ORDER_ACTION";
    public static final String COMMAND_CREATE_ORDER_VALID_FROM_ARG = "VALID_FROM";
    public static final String COMMAND_CREATE_ORDER_VALID_TO_ARG = "VALID_TO";
    public static final String COMMAND_CREATE_ORDER_PORTFOLIO_ARG = "PORTFOLIO";
    public static final String COMMAND_CREATE_ORDER_VALUE_PAPER_ARG = "VALUE_PAPER";
    public static final String COMMAND_CREATE_ORDER_VOLUME_ARG = "VOLUME";
    public static final String COMMAND_CREATE_ORDER_STOP_LIMIT_ARG = "STOP_LIMIT";
    public static final String COMMAND_CREATE_ORDER_LIMIT_ARG = "LIMIT";

    public static final int COMMAND_PORTFOLIO_VALUE_PAPERS = 4;
    public static final String COMMAND_PORTFOLIO_VALUE_PAPERS_ID_ARG = "PORTFOLIO_ID";

    public RestService() {
        super(LOG_TAG);
    }

    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        final int command = intent.getIntExtra("command", -1);
        Bundle b = new Bundle();
        b.putInt("command", command);

        receiver.send(STATUS_RUNNING, b);

        try {
            switch (command){
                case COMMAND_PORTFOLIOS:
                    b.putSerializable("results", getPortfolios());
                    receiver.send(STATUS_FINISHED, b);
                    break;
                case COMMAND_INITIAL_PORTFOLIO:
                    b.putSerializable("results", getInitialPortfolioContext());
                    receiver.send(STATUS_FINISHED, b);
                    break;
                case COMMAND_SEARCH_VALUE_PAPERS:
                    b.putSerializable("results", searchValuePapers(intent));
                    receiver.send(STATUS_FINISHED, b);
                    break;
                case COMMAND_CREATE_ORDER:
                    int responseStatus = createOrder(intent);
                    if(responseStatus != Response.Status.CREATED.getStatusCode()){
                        b.putString(Intent.EXTRA_TEXT, "Order creation failed - status " + responseStatus);
                        receiver.send(STATUS_ERROR, b);
                    }else {
                        receiver.send(STATUS_FINISHED, null);
                    }
                    break;
                case COMMAND_PORTFOLIO_VALUE_PAPERS:
                    b.putSerializable("results", getValuePapersForPortfolio(intent));
                    receiver.send(STATUS_FINISHED, b);
                    break;
                default:
                    b.putString(Intent.EXTRA_TEXT, "Unknown command code [" + command + "]");
                    receiver.send(STATUS_ERROR, b);
                    Log.e(LOG_TAG, "Command unknown: " + command);
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
        List<PortfolioDto> portfolios = portfolioResource.getPortfolios();
        if (!portfolios.isEmpty()) {
            PortfolioContext.setPortfolio(portfolios.get(0));
        }
        return PortfolioContext.getPortfolio();
    }

    private ArrayList<PortfolioDto> getPortfolios() {
        Log.i(LOG_TAG, "Query portfolios");
        PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
        return new ArrayList<PortfolioDto>(portfolioResource.getPortfolios());
    }

    private ArrayList<ValuePaperDto> searchValuePapers(Intent intent) {
        Log.i(LOG_TAG, "Query value papers by filter");
        String filter = intent.getStringExtra(COMMAND_SEARCH_VALUE_PAPERS_ARG_FILTER);
        ValuePaperType valuePaperType = (ValuePaperType) intent.getSerializableExtra(COMMAND_SEARCH_VALUE_PAPERS_ARG_TYPE);
        ValuePaperResource valuePaperResource = WebserviceFactory.getInstance().getValuePaperResource();
        return new ArrayList<ValuePaperDto>(valuePaperResource.getValuePapers(PortfolioContext.getPortfolio().getId(), filter, valuePaperType));
    }

    private ArrayList<PortfolioValuePaperDto> getValuePapersForPortfolio(Intent intent){
        if(!intent.hasExtra(COMMAND_PORTFOLIO_VALUE_PAPERS_ID_ARG)){
            throw new IllegalArgumentException(COMMAND_PORTFOLIO_VALUE_PAPERS_ID_ARG + " missing");
        }
        long portfolioId = intent.getLongExtra(COMMAND_PORTFOLIO_VALUE_PAPERS_ID_ARG, -1);
        Log.i(LOG_TAG, "Query value papers for portfolio id " + portfolioId);
        PortfolioResource portfolioResource = WebserviceFactory.getInstance().getPortfolioResource();
        return new ArrayList<PortfolioValuePaperDto>(portfolioResource.getValuePapers(portfolioId));
    }

    private int createOrder(Intent intent){
        if(!intent.hasExtra(COMMAND_CREATE_ORDER_PORTFOLIO_ARG)){
            throw new IllegalArgumentException(COMMAND_CREATE_ORDER_PORTFOLIO_ARG + " missing");
        }
        if(!intent.hasExtra(COMMAND_CREATE_ORDER_VALUE_PAPER_ARG)){
            throw new IllegalArgumentException(COMMAND_CREATE_ORDER_VALUE_PAPER_ARG + " missing");
        }
        if(!intent.hasExtra(COMMAND_CREATE_ORDER_VOLUME_ARG)){
            throw new IllegalArgumentException(COMMAND_CREATE_ORDER_VOLUME_ARG + " missing");
        }

        OrderType orderType = (OrderType) intent.getSerializableExtra(COMMAND_CREATE_ORDER_TYPE_ARG);
        OrderAction orderAction = (OrderAction) intent.getSerializableExtra(COMMAND_CREATE_ORDER_ACTION_ARG);
        Calendar validFrom = (Calendar) intent.getSerializableExtra(COMMAND_CREATE_ORDER_VALID_FROM_ARG);
        Calendar validTo = (Calendar) intent.getSerializableExtra(COMMAND_CREATE_ORDER_VALID_TO_ARG);
        long portfolioId = intent.getLongExtra(COMMAND_CREATE_ORDER_PORTFOLIO_ARG, -1);
        long valuePaperId = intent.getLongExtra(COMMAND_CREATE_ORDER_VALUE_PAPER_ARG, -1);
        int volume = intent.getIntExtra(COMMAND_CREATE_ORDER_VOLUME_ARG, -1);
        BigDecimal stopLimit = (BigDecimal) intent.getSerializableExtra(COMMAND_CREATE_ORDER_STOP_LIMIT_ARG);
        BigDecimal limit = (BigDecimal) intent.getSerializableExtra(COMMAND_CREATE_ORDER_LIMIT_ARG);

        Log.i(LOG_TAG, "Create order");

        OrderDto order = new OrderDto(orderType, orderAction, validFrom, validTo, portfolioId, valuePaperId, volume, stopLimit, limit);
        OrderResource orderResource = WebserviceFactory.getInstance().getOrderResource();
        Response response = orderResource.createOrder(order);
        return response.getStatus();
    }
}
