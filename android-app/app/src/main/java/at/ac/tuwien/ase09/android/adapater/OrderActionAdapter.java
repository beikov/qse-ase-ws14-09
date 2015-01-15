package at.ac.tuwien.ase09.android.adapater;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;

/**
 * Created by Moritz on 02.01.2015.
 */
public class OrderActionAdapter extends ArrayAdapter<OrderAction> {

    private final Context context;
    private final int layoutId;
    private int dropDownLayoutId;

    public OrderActionAdapter(Context context, int resource) {
        super(context, resource, OrderAction.values());
        this.context = context;
        this.layoutId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, layoutId);
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        this.dropDownLayoutId = resource;
    }

    private View getView(int position, View convertView, ViewGroup parent, int layoutId){
        TextView textView;
        if (convertView == null) {
            textView = (TextView) ((Activity) context).getLayoutInflater().inflate(layoutId, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        switch(OrderAction.values()[position]){
            // STOCK
            case BUY: textView.setText(context.getString(R.string.buy));
                break;
            case SELL:
                textView.setText(context.getString(R.string.sell));
                break;
        }

        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, dropDownLayoutId);
    }
}
