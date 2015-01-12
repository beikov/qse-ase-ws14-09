package at.ac.tuwien.ase09.android.adapater;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * Created by Moritz on 12.01.2015.
 */
public class ValuePaperItemLayoutPopulator {
    public static void populateValuePaperItemView(View view, ValuePaperDto valuePaper){
        TextView nameTextView;
        TextView codeTextView;
        TextView relativePriceChangeTextView;
        TextView absolutePriceChangeTextView;

        nameTextView = (TextView) view.findViewById(R.id.vpName);
        codeTextView = (TextView) view.findViewById(R.id.vpCode);
        relativePriceChangeTextView = (TextView) view.findViewById(R.id.vpRelativePriceChange);
        absolutePriceChangeTextView = (TextView) view.findViewById(R.id.vpAbsolutePriceChange);

        nameTextView.setText(valuePaper.getName());
        codeTextView.setText(valuePaper.getCode());
        BigDecimal lastPrice = valuePaper.getLastPrice();
        BigDecimal previousDayPrice = valuePaper.getPreviousDayPrice();
        if(lastPrice != null && previousDayPrice != null) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(0);
            df.setNegativePrefix("-");
            df.setPositivePrefix("+");

            BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
            BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);

            relativePriceChangeTextView.setText(df.format(relativePriceChange.floatValue() * 100) + " %");

            df.setCurrency(valuePaper.getCurrency());
            absolutePriceChangeTextView.setText(df.format(absolutePriceChange) + " " + valuePaper.getCurrency().getCurrencyCode());

            int textViewColor;
            switch(absolutePriceChange.signum()){
                case -1:    textViewColor = Color.RED;
                    break;
                case  1:    textViewColor = Color.rgb(34, 177, 76);
                    break;
                default:    textViewColor = Color.BLACK;
            }
            absolutePriceChangeTextView.setTextColor(textViewColor);
            relativePriceChangeTextView.setTextColor(textViewColor);
        }else{
            relativePriceChangeTextView.setText("");
            absolutePriceChangeTextView.setText("");
        }
    }
}
