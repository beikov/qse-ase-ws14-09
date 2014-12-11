package at.ac.tuwien.ase09.portfolioapp.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

import android.widget.TextView;


import at.ac.tuwien.ase09.portfolioapp.fragment.PortfolioViewFragment.MyOrderViewHolder;
import at.ac.tuwien.ase09.portfolioapp.R;
import at.ac.tuwien.ase09.portfolioapp.model.Order;

public class OrderAdapter extends BaseAdapter implements OnClickListener {
    private static final String debugTag = "OrderAdapter";
    //private MainActivity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;
    public OrderAdapter (LayoutInflater l, ArrayList<Order> data)
    {
        //this.activity = a;
        this.layoutInflater = l;
        this.orders = data;
    }
    @Override
    public int getCount() {
        return this.orders.size();
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
        MyOrderViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate (R.layout.order_row, parent, false);
            holder = new MyOrderViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.order_name);
            holder.type = (TextView) convertView.findViewById(R.id.order_type);
            holder.status = (TextView) convertView.findViewById(R.id.order_status);
            holder.value = (TextView) convertView.findViewById(R.id.order_value);
            holder.count = (TextView) convertView.findViewById(R.id.order_count);
            convertView.setTag(holder);
        }
        else {
            holder = (MyOrderViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(this);
        Order order = orders.get(pos);

        holder.name.setText(order.getName());


        return convertView;
    }
    @Override
    public void onClick(View v) {

    }
}