package at.ac.tuwien.ase09.android.adapater;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * Created by Moritz on 17.01.2015.
 */
public class ValuePaperArrayAdapter extends ArrayAdapter<ValuePaperDto> {

    private final Activity activity;

    public ValuePaperArrayAdapter(Context context, List<ValuePaperDto> objects, Activity activity) {
        super(context, 0, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView nameTextView;
        TextView codeTextView;
        TextView relativePriceChangeTextView;
        TextView absolutePriceChangeTextView;

        if (convertView == null) {
            view = activity.getLayoutInflater().inflate(R.layout.valuepaper_item, parent, false);
        } else {
            view = convertView;
        }

        LayoutPopulator.populateValuePaperItemView(view, getItem(position));
        return view;
    }
}
