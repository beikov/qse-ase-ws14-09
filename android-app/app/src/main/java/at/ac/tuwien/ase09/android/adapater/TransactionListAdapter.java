package at.ac.tuwien.ase09.android.adapater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.TransactionDto;

public class TransactionListAdapter extends BaseAdapter implements OnClickListener {

    private LayoutInflater layoutInflater;
    private ArrayList<TransactionDto> transactions;
    public TransactionListAdapter(LayoutInflater l, ArrayList<TransactionDto> data)
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
            convertView = layoutInflater.inflate (R.layout.portfolioview_transaction_row, parent, false);
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
        TransactionDto transaction = transactions.get(pos);

        holder.date.setText(transaction.getDate());


        return convertView;
    }
    @Override
    public void onClick(View v) {

    }

    private static class MyTransactionViewHolder {
        public TextView date,desc,type,value;
    }
}