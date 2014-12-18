package at.ac.tuwien.ase09.android.adapater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.OrderDto;

public class OrderListAdapter extends BaseAdapter implements OnClickListener {
    private static final String debugTag = "OrderAdapter";
    //private MainActivity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<OrderDto> orders;
    public OrderListAdapter(LayoutInflater l, ArrayList<OrderDto> data)
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
            convertView = layoutInflater.inflate (R.layout.portfolioview_order_row, parent, false);
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
        OrderDto order = orders.get(pos);

        holder.name.setText(order.getName());


        return convertView;
    }
    @Override
    public void onClick(View v) {

    }

    private static class MyOrderViewHolder {
        public TextView name,type,status,value,count;
    }
}