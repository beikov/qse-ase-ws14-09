package at.ac.tuwien.ase09.portfolioapp.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

import android.widget.TextView;


import at.ac.tuwien.ase09.portfolioapp.PortfolioViewFragment.MyTransactionViewHolder;
import at.ac.tuwien.ase09.portfolioapp.R;
import at.ac.tuwien.ase09.portfolioapp.model.*;

public class TransactionAdapter extends BaseAdapter implements OnClickListener {

    private LayoutInflater layoutInflater;
    private ArrayList<Transaction> transactions;
    public TransactionAdapter (LayoutInflater l, ArrayList<Transaction> data)
    {

        this.layoutInflater = l;
        this.transactions = data;
    }
    @Override
    public int getCount() {
        return this.transactions.size();
    }
    @Override
    public boolean areAllItemsEnabled ()
    {
        return true;
    }
    @Override
    public Object getItem(int arg0) {
        return null;
    }
    @Override
    public long getItemId(int pos) {
        return pos;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        MyTransactionViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate (R.layout.transaction_row, parent, false);
            holder = new MyTransactionViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.transaction_date);
            holder.desc = (TextView) convertView.findViewById(R.id.transaction_desc);
            holder.type = (TextView) convertView.findViewById(R.id.transaction_type);
            holder.value = (TextView) convertView.findViewById(R.id.transaction_value);

            convertView.setTag(holder);
        }
        else {
            holder = (MyTransactionViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(this);
        Transaction transaction = transactions.get(pos);

        holder.date.setText(transaction.getDate());


        return convertView;
    }
    @Override
    public void onClick(View v) {

    }
}