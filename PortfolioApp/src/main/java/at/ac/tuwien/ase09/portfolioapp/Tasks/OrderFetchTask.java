package at.ac.tuwien.ase09.portfolioapp.Tasks;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import at.ac.tuwien.ase09.portfolioapp.PortfolioView;
import at.ac.tuwien.ase09.portfolioapp.PortfolioViewFragment;
import at.ac.tuwien.ase09.portfolioapp.R;
import at.ac.tuwien.ase09.portfolioapp.models.Order;

public class OrderFetchTask extends AsyncTask<String, Integer, String>
{
    private ProgressDialog progDialog;
    private Context context;
    private PortfolioView activity;
    private PortfolioViewFragment fragment;


    public OrderFetchTask(PortfolioViewFragment fragment) {
        super();
       // this.activity = activity;
        this.fragment = fragment;
        this.context = this.activity.getApplicationContext();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progDialog = ProgressDialog.show(this.activity, "Search", this.context.getResources().getString(R.string.loading) , true, false);
    }
    @Override
    protected String doInBackground(String... params) {
        try {

            String result = OrderFetchHelper.downloadFromServer(params);
            return result;
        } catch (Exception e) {
            return new String();
        }
    }
    @Override
    protected void onPostExecute(String result)
    {
        ArrayList<Order> orders = new ArrayList<Order>();
        //progDialog.dismiss();

        /* TODO, manage result properly */
        if (result.length() == 0) {

            return;
        }

        this.fragment.setOrders(orders);
    }
}