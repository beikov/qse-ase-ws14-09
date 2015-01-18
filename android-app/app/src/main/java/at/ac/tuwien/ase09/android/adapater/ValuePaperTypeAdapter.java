package at.ac.tuwien.ase09.android.adapater;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.ValuePaperResource;

/**
 * Created by Moritz on 02.01.2015.
 */
public class ValuePaperTypeAdapter extends ArrayAdapter<ValuePaperType> {

    private final Context context;
    private final int layoutId;
    private int dropDownLayoutId;

    public ValuePaperTypeAdapter(Context context, int resource) {
        super(context, resource, ValuePaperType.values());
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

        switch(ValuePaperType.values()[position]){
            // STOCK
            case STOCK: textView.setText(context.getString(R.string.stock));
                break;
            case FUND: textView.setText(context.getString(R.string.fund));
                break;
            case BOND: textView.setText(context.getString(R.string.stockbond));
                break;
        }

        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, dropDownLayoutId);
    }
}
