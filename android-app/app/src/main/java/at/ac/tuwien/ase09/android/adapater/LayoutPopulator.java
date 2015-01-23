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
            DecimalFormat percentDf = LayoutPopulator.getPercentFormat();

            lastPriceTextView.setText(formatMoney(valuePaper.getLastPrice(), valuePaper.getCurrency()));

            if(previousDayPrice != null) {
                BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
                BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);

                relativePriceChangeTextView.setText(percentDf.format(relativePriceChange.floatValue() * 100) + " %");

                absolutePriceChangeTextView.setText(formatMoneyChange(absolutePriceChange, valuePaper.getCurrency()));

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

        DecimalFormat percentDf = LayoutPopulator.getPercentFormat();

        buyPriceTextView.setText(formatMoney(portfolioValuePaper.getBuyPrice(), currency));

        if(lastPrice != null) {
            if(previousDayPrice != null) {
                BigDecimal absolutePriceChange = lastPrice.subtract(previousDayPrice);
                BigDecimal relativePriceChange = absolutePriceChange.divide(previousDayPrice, RoundingMode.HALF_DOWN);
                relativePriceChangeTextView.setText(percentDf.format(relativePriceChange.floatValue() * 100) + " %");
                setColorBySignum(relativeBuyPriceChangeTextView, absolutePriceChange.signum());
            }else{
                relativePriceChangeTextView.setText("");
            }

            lastPriceTextView.setText(formatMoney(portfolioValuePaper.getValuePaperDto().getLastPrice(), currency));

            BigDecimal relativeBuyPriceChange = lastPrice.divide(buyPrice, RoundingMode.HALF_DOWN).subtract(new BigDecimal(1));
            relativeBuyPriceChangeTextView.setText(percentDf.format(relativeBuyPriceChange.floatValue() * 100) + " %");
            setColorBySignum(relativeBuyPriceChangeTextView, relativeBuyPriceChange.signum());
        }else{
            relativePriceChangeTextView.setText("");
            lastPriceTextView.setText("");
            relativeBuyPriceChangeTextView.setText("");
        }
    }

    public static void setColorBySignum(TextView textView, int signum){
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

    public static String formatMoney(BigDecimal value, Currency currency){
        return formatMoney(value, currency, currency == null ? getMoneyFormat() : getMoneyFormat(currency));
    }

    public static String formatMoneyChange(BigDecimal value, Currency currency){
        return formatMoney(value, currency, currency == null ? getMoneyChangeFormat() : getMoneyChangeFormat(currency));
    }

    private static String formatMoney(BigDecimal value, Currency currency, DecimalFormat format){
        String result = format.format(value);
        if(currency != null){
            result += " " + currency.getCurrencyCode();
        }
        return result;
    }

    public static DecimalFormat getMoneyFormat(){
        DecimalFormat moneyDf = new DecimalFormat();
        moneyDf.setMaximumFractionDigits(2);
        moneyDf.setMinimumFractionDigits(0);
        return moneyDf;
    }

    public static DecimalFormat getMoneyFormat(Currency currency){
        DecimalFormat moneyDf = getMoneyFormat();
        moneyDf.setCurrency(currency);
        return moneyDf;
    }

    public static DecimalFormat getMoneyChangeFormat(Currency currency){
        DecimalFormat moneyDf = LayoutPopulator.getMoneyFormat(currency);
        moneyDf.setNegativePrefix("-");
        moneyDf.setPositivePrefix("+");
        return moneyDf;
    }

    public static DecimalFormat getMoneyChangeFormat(){
        DecimalFormat moneyDf = LayoutPopulator.getMoneyFormat();
        moneyDf.setNegativePrefix("-");
        moneyDf.setPositivePrefix("+");
        return moneyDf;
    }

    public static DecimalFormat getPercentFormat(){
        DecimalFormat percentDf = new DecimalFormat();
        percentDf.setMaximumFractionDigits(2);
        percentDf.setMinimumFractionDigits(0);
        percentDf.setNegativePrefix("-");
        percentDf.setPositivePrefix("+");
        return percentDf;
    }
}
