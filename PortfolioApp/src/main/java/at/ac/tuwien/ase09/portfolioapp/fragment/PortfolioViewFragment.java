package at.ac.tuwien.ase09.portfolioapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import at.ac.tuwien.ase09.portfolioapp.R;
import at.ac.tuwien.ase09.portfolioapp.adapter.*;
import at.ac.tuwien.ase09.portfolioapp.model.*;

public class PortfolioViewFragment extends Fragment {

    private int index;
    private ArrayList<Order> orders;
    private ArrayList<Transaction> transactions;
    private ListView portfolio_list;
    private LayoutInflater layoutInflator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        index = data.getInt("idx");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.layoutInflator = inflater;
        View v = inflater.inflate(R.layout.portfolio_view_fragment, null);
        if(index==0) {
            v = inflater.inflate(R.layout.fragment_table, null);
        }else if(index==1) {
            this.portfolio_list = (ListView) v.findViewById(R.id.portfolio_list);



            ArrayList<Transaction> tempList = new ArrayList<Transaction>();
            tempList.add(new Transaction());
            tempList.add(new Transaction());
            setTransactions(tempList);

        }else if(index==3) {
            this.portfolio_list = (ListView) v.findViewById(R.id.portfolio_list);

            ArrayList<Transaction> tempList = new ArrayList<Transaction>();
            tempList.add(new Transaction());
            tempList.add(new Transaction());
            setTransactions(tempList);

        } else if(index==2){


            this.portfolio_list = (ListView) v.findViewById(R.id.portfolio_list);

            /* DISABLED
            OrderFetchTask ofTask = new OrderFetchTask(PortfolioViewFragment.this);
            try {
                String id="currentId";
                ofTask.execute(id);
            } catch (Exception e) {
                ofTask.cancel(true);

            }
            */
            ArrayList<Order> tempList = new ArrayList<Order>();
            tempList.add(new Order());
            tempList.add(new Order());
            setOrders(tempList);

        } else {
            v = inflater.inflate(R.layout.fragment_table, null);
        }
        return v;

    }
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        this.portfolio_list.setAdapter(new OrderAdapter(this.layoutInflator, this.orders));
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        this.portfolio_list.setAdapter(new TransactionAdapter(this.layoutInflator, this.transactions));
    }

    public static class MyOrderViewHolder {
        public TextView name,type,status,value,count;
    }

    public static class MyTransactionViewHolder {
        public TextView date,desc,type,value;
    }



}
