package at.ac.tuwien.ase09.android.adapater;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * Created by Moritz on 12.01.2015.
 */
public class LayoutPopulator {

    public static void populateValuePaperItemView(View view, ValuePaperDto valuePaper){
        TextView nameTextView = (TextView) view.findViewById(R.id.vpName);
        TextView codeTextView = (TextView) view.findViewById(R.id.vpCode);
        TextView relativePriceChangeTextView = (TextView) view.findViewById(R.id.vpRelativePriceChange);
        TextView absolutePriceChangeTextView = (TextView) view.findViewById(R.id.vpAbsolutePriceChange);
        TextView lastPriceTextView = (TextView) view.findViewById(R.id.vpLastPrice);

        nameTextView.setText(valuePaper.getName());
        codeTextView.setText(valuePaper.getCode());
        BigDecimal lastPrice = valuePaper.getLastPrice();
        BigDecimal previousDayPrice = valuePaper.getPreviousDayPrice();
        if(lastPrice != null) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(0);
            lastPriceTextView.setText(df.format(valuePaper.getLastPrice()) + " " + valuePaper.getCurrency().getCurrencyCode());

            if(previousDayPrice != null) {
                df.setNegativePrefix("-");
                df.setPositivePrefix("+");

                BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
                BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);

                relativePriceChangeTextView.setText(df.format(relativePriceChange.floatValue() * 100) + " %");

                df.setCurrency(valuePaper.getCurrency());
                absolutePriceChangeTextView.setText(df.format(absolutePriceChange) + " " + valuePaper.getCurrency().getCurrencyCode());

                setColorBySignum(absolutePriceChangeTextView, absolutePriceChange.signum());
                setColorBySignum(relativePriceChangeTextView, absolutePriceChange.signum());
            }else{
                relativePriceChangeTextView.setText("");
                absolutePriceChangeTextView.setText("");
            }
        }else {
            lastPriceTextView.setText("");
            relativePriceChangeTextView.setText("");
            absolutePriceChangeTextView.setText("");
        }
    }

    public static void populatePortfolioValuePaperItemView(View view, PortfolioValuePaperDto portfolioValuePaper){
        TextView nameTextView = (TextView) view.findViewById(R.id.vpName);
        TextView volumeTextView = (TextView) view.findViewById(R.id.vpVolume);
        TextView lastPriceTextView = (TextView) view.findViewById(R.id.vpLastPrice);
        TextView relativePriceChangeTextView = (TextView) view.findViewById(R.id.vpRelativePriceChange);
        TextView buyPriceTextView = (TextView) view.findViewById(R.id.vpBuyPrice);
        TextView relativeBuyPriceChangeTextView = (TextView) view.findViewById(R.id.vpRelativeBuyPriceChange);

        nameTextView.setText(portfolioValuePaper.getValuePaperDto().getName());
        volumeTextView.setText("x " + portfolioValuePaper.getVolume());

        BigDecimal lastPrice = portfolioValuePaper.getValuePaperDto().getLastPrice();
        BigDecimal previousDayPrice = portfolioValuePaper.getValuePaperDto().getPreviousDayPrice();
        BigDecimal buyPrice = portfolioValuePaper.getBuyPrice();

        Currency currency = portfolioValuePaper.getValuePaperDto().getCurrency();

        DecimalFormat moneyDf = new DecimalFormat();
        moneyDf.setMaximumFractionDigits(2);
        moneyDf.setMinimumFractionDigits(0);
        moneyDf.setCurrency(currency);

        DecimalFormat percentDf = new DecimalFormat();
        percentDf.setMaximumFractionDigits(2);
        percentDf.setMinimumFractionDigits(0);
        percentDf.setNegativePrefix("-");
        percentDf.setPositivePrefix("+");

        buyPriceTextView.setText(moneyDf.format(portfolioValuePaper.getBuyPrice().floatValue()) + " " + getCurrencySymbol(currency));

        if(lastPrice != null) {
            if(previousDayPrice != null) {
                BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
                BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);
                relativePriceChangeTextView.setText(percentDf.format(relativePriceChange.floatValue() * 100) + " %");
                setColorBySignum(relativeBuyPriceChangeTextView, absolutePriceChange.signum());
            }else{
                relativePriceChangeTextView.setText("");
            }

            lastPriceTextView.setText(moneyDf.format(portfolioValuePaper.getValuePaperDto().getLastPrice()) + " " + getCurrencySymbol(currency));

            BigDecimal relativeBuyPriceChange = lastPrice.divide(buyPrice, RoundingMode.HALF_DOWN).subtract(new BigDecimal(1));
            relativeBuyPriceChangeTextView.setText(percentDf.format(relativeBuyPriceChange.floatValue() * 100) + " %");
            setColorBySignum(relativeBuyPriceChangeTextView, relativeBuyPriceChange.signum());
        }else{
            relativePriceChangeTextView.setText("");
            lastPriceTextView.setText("");
            relativeBuyPriceChangeTextView.setText("");
        }
    }

    private static void setColorBySignum(TextView textView, int signum){
        int textViewColor;
        switch (signum) {
            case -1:
                textViewColor = Color.RED;
                break;
            case 1:
                textViewColor = Color.rgb(34, 177, 76);
                break;
            default:
                textViewColor = Color.BLACK;
        }
        textView.setTextColor(textViewColor);
    }

    private static String getCurrencySymbol(Currency currency){
        return (currency.getCurrencyCode() == null) ? "%" : currency.getCurrencyCode();
    }
}
