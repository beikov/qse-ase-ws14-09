package at.ac.tuwien.ase09.android.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.LayoutPopulator;
import at.ac.tuwien.ase09.android.adapater.OrderActionAdapter;
import at.ac.tuwien.ase09.android.service.RestResultReceiver;
import at.ac.tuwien.ase09.android.service.RestService;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;
import at.ac.tuwien.ase09.validator.OrderValidator;

public class OrderActivity extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, TextWatcher, View.OnClickListener, RestResultReceiver.Receiver {
    // the activity parameters
    public static final String ARG_VALUE_PAPER = "VALUE_PAPER";
    public static final String RESULT_MESSAGE = "at.ac.tuwien.ase09.android.activity.OrderActivity.RESULT_MESSAGE";

    private ValuePaperDto valuePaper;

    private OrderAction selectedOrderAction;
    private Calendar validFrom = Calendar.getInstance();
    private Calendar validTo = Calendar.getInstance();
    // points either to validFrom or validTo - used to set values in listener
    private Calendar target;

    private RestResultReceiver receiver;

    /* UI Elements */
    private Button timePickerTarget;
    private Button datePickerTarget;
    private TextView estimatedOrderCostTextView;
    private EditText volumeEditText;
    private RadioGroup orderTypeRadioGroup;
    private CheckBox validToCheckbox;
    private ProgressBar progressBar;
    private TextView limitTextView;
    private EditText limitEditText;
    private TextView stopLimitTextView;
    private EditText stopLimitEditText;
    private TextView orderFeeTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        valuePaper =  (ValuePaperDto) getIntent().getSerializableExtra(ARG_VALUE_PAPER);
        initializeView();
    }

    public void initializeView() {
        // Inflate the layout for this fragment
        setContentView(R.layout.order_activity);

        receiver = new RestResultReceiver(new Handler());
        receiver.setReceiver(this);

        Button validityFromTimeButton = (Button) this.findViewById(R.id.validityFromTimeButton);
        validityFromTimeButton.setText(DateFormat.getTimeFormat(this).format(validFrom.getTime()));
        validityFromTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((Button) v, validFrom);
            }
        });

        Button validityFromDateButton = (Button) this.findViewById(R.id.validityFromDateButton);
        validityFromDateButton.setText(DateFormat.getDateFormat(this).format(validFrom.getTime()));
        validityFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v, validFrom);
            }
        });

        Button validityToTimeButton = (Button) this.findViewById(R.id.validityToTimeButton);
        validityToTimeButton.setText(DateFormat.getTimeFormat(this).format(validTo.getTime()));
        validityToTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((Button) v, validTo);
            }
        });

        Button validityToDateButton = (Button) this.findViewById(R.id.validityToDateButton);
        validityToDateButton.setText(DateFormat.getDateFormat(this).format(validTo.getTime()));
        validityToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v, validTo);
            }
        });

        estimatedOrderCostTextView = (TextView) this.findViewById(R.id.estimatedOrderCost);
        orderFeeTextView = (TextView) findViewById(R.id.orderFee);

        volumeEditText = (EditText) this.findViewById(R.id.orderVolumeInput);
        volumeEditText.addTextChangedListener(this);

        afterTextChanged(volumeEditText.getEditableText());

        final View validToArea = this.findViewById(R.id.validToArea);
        validToArea.setVisibility(View.GONE);

        validToCheckbox = (CheckBox) this.findViewById(R.id.validToCheckbox);
        TextView validFromTextView = (TextView) this.findViewById(R.id.validFromTextView);
        validToCheckbox.setTextColor(validFromTextView.getTextColors().getDefaultColor());
        validToCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    validToArea.setVisibility(View.VISIBLE);
                }else{
                    validToArea.setVisibility(View.GONE);
                }
            }
        });

        orderTypeRadioGroup = (RadioGroup) this.findViewById(R.id.orderTypeRadioGroup);
        orderTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                OrderType checkedOrderType = getOrderTypeForRadioButtonId(checkedId);
                switch (checkedOrderType) {
                    case LIMIT:     setLimitOrderUIElementsVisibility(View.VISIBLE);
                                    break;
                    case MARKET:    setLimitOrderUIElementsVisibility(View.GONE);
                                    break;
                }
            }
        });

        // set up order type spinner
        Spinner orderActionSpinner = (Spinner) this.findViewById(R.id.orderActionSpinner);
        // Create an ArrayAdapter using a default spinner layout
        final OrderActionAdapter orderActionAdapter = new OrderActionAdapter(this, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        orderActionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        orderActionSpinner.setAdapter(orderActionAdapter);
        orderActionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrderAction = orderActionAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        limitTextView = (TextView) findViewById(R.id.limitTextView);
        limitEditText = (EditText) findViewById(R.id.limitInput);
        stopLimitTextView = (TextView) findViewById(R.id.stopLimitTextView);
        stopLimitEditText = (EditText) findViewById(R.id.stopLimitInput);

        setLimitOrderUIElementsVisibility(View.GONE);
        limitEditText.setText("");
        stopLimitEditText.setText("");

        Button confirmOrderButton = (Button) this.findViewById(R.id.confirmOrderButton);
        confirmOrderButton.setOnClickListener(this);

        progressBar = (ProgressBar) this.findViewById(android.R.id.progress);

        View valuePaperItemView = this.findViewById(R.id.valuePaperItemView);
        LayoutPopulator.populateValuePaperItemView(valuePaperItemView, valuePaper);
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.setReceiver(null);
    }

    private void setLimitOrderUIElementsVisibility(int visibility){
        limitTextView.setVisibility(visibility);
        limitEditText.setVisibility(visibility);
        stopLimitTextView.setVisibility(visibility);
        stopLimitEditText.setVisibility(visibility);
    }

    public void showTimePickerDialog(Button button, Calendar targetCal) {
        DialogFragment newFragment = new TimePickerFragment();
        timePickerTarget = button;
        target = targetCal;
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(Button button, Calendar targetCal) {
        DialogFragment newFragment = new DatePickerFragment();
        datePickerTarget = button;
        target = targetCal;
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        target.set(Calendar.HOUR_OF_DAY, hourOfDay);
        target.set(Calendar.MINUTE, minute);
        timePickerTarget.setText(DateFormat.getTimeFormat(this).format(target.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        target.set(Calendar.YEAR, year);
        target.set(Calendar.MONTH, monthOfYear);
        target.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        datePickerTarget.setText(DateFormat.getDateFormat(this).format(target.getTime()));
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (valuePaper.getLastPrice() != null && valuePaper.getCurrency() != null && s.length() > 0) {
            int volume = Integer.valueOf(s.toString());
            BigDecimal estimatedCosts = valuePaper.getLastPrice().multiply(new BigDecimal(volume));
            estimatedOrderCostTextView.setText(LayoutPopulator.formatMoney(estimatedCosts, valuePaper.getCurrency()));
        }else{
            estimatedOrderCostTextView.setText("-");
        }
        PortfolioDto portfolio = PortfolioContext.getPortfolio();
        orderFeeTextView.setText(LayoutPopulator.formatMoney(portfolio.getOrderFee(), portfolio.getCurrency()));
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RestService.STATUS_RUNNING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RestService.STATUS_FINISHED:
                Intent result = new Intent();
                String orderActionString;
                switch(selectedOrderAction){
                    case BUY:   orderActionString = getString(R.string.buy);
                                break;
                    case SELL:  orderActionString = getString(R.string.sell);
                                break;
                    default:    throw new IllegalStateException("Unknown order action [" + selectedOrderAction + "]");
                }
                result.putExtra(RESULT_MESSAGE, getString(R.string.create_order_success_message, orderActionString, valuePaper.getName()));
                setResult(RESULT_OK, result);
                progressBar.setVisibility(View.GONE);
                finish();
                break;
            case RestService.STATUS_ERROR:
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
                break;
        }
    }

    private OrderType getOrderTypeForRadioButtonId(int radioButtonId){
        switch(orderTypeRadioGroup.getCheckedRadioButtonId()){
            case R.id.radioButtonLimitOrder:    return OrderType.LIMIT;
            case R.id.radioButtonMarketOrder:   return OrderType.MARKET;
            default: throw new IllegalStateException("Unknown radio button id [" + orderTypeRadioGroup.getCheckedRadioButtonId() + "]");
        }
    }

    private boolean validateInput(OrderType orderType){
        BigDecimal limit = null;
        if(limitEditText.getText().length() > 0){
            limit = new BigDecimal(limitEditText.getText().toString());
        }

        BigDecimal stopLimit = null;
        if(stopLimitEditText.getText().length() > 0){
            stopLimit = new BigDecimal(stopLimitEditText.getText().toString());
        }

        Calendar actualValidTo = validTo;
        if(!validToCheckbox.isChecked()) {
            actualValidTo = null;
        }

        int result = OrderValidator.validateOrder(orderType, selectedOrderAction, valuePaper.getLastPrice(), limit, stopLimit, validFrom, actualValidTo);

        if(result != OrderValidator.OK) {
            String message;
            switch (result) {
                case OrderValidator.LIMIT_REQUIRED: message = getString(R.string.required_message, "Limit"); break;
                case OrderValidator.LIMIT_EQUAL_OR_HIGHER_THAN_PRICE: message = getString(R.string.limit_equal_or_higher_than_price_message); break;
                case OrderValidator.LIMIT_EQUAL_OR_LOWER_THAN_PRICE: message = getString(R.string.limit_equal_or_lower_than_price_message); break;
                case OrderValidator.LIMIT_HIGHER_THAN_STOP_LIMIT: message = getString(R.string.limit_higher_than_stop_limit_message); break;
                case OrderValidator.LIMIT_LOWER_THAN_STOP_LIMIT: message = getString(R.string.limit_lower_than_stop_limit_message); break;
                case OrderValidator.STOP_LIMIT_EQUAL_OR_HIGHER_THAN_PRICE: message = getString(R.string.stop_limit_equal_or_higher_than_price_message); break;
                case OrderValidator.STOP_LIMIT_EQUAL_OR_LOWER_THAN_PRICE: message = getString(R.string.stop_limit_equal_or_lower_than_price_message); break;
                case OrderValidator.VALID_FROM_REQUIRED: message = getString(R.string.required_message, "Valid from"); break;
                case OrderValidator.VALID_TO_BEFORE_VALID_FROM: message = getString(R.string.valid_to_before_now_message, getString(R.string.valid_from), getString(R.string.valid_to)); break;
                case OrderValidator.VALID_TO_BEFORE_NOW: message = getString(R.string.valid_to_before_now, getString(R.string.valid_to)); break;
                default: throw new IllegalArgumentException("Unknown validation code [" + result + "]");
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        Intent restQueryIntent = new Intent(Intent.ACTION_SYNC, null, this, RestService.class);

        Calendar validFrom = Calendar.getInstance();

        OrderType orderType = getOrderTypeForRadioButtonId(orderTypeRadioGroup.getCheckedRadioButtonId());

        // validate
        if(!validateInput(orderType)){
            return;
        }

        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_TYPE_ARG, orderType);
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_ACTION_ARG, selectedOrderAction);
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALID_FROM_ARG, validFrom);
        if(validToCheckbox.isChecked()) {
            restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALID_TO_ARG, validTo);
        }
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_PORTFOLIO_ARG, PortfolioContext.getPortfolio().getId());
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALUE_PAPER_ARG, valuePaper.getId());
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VOLUME_ARG, Integer.valueOf(volumeEditText.getText().toString()));

        if(orderType == OrderType.LIMIT) {
            restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_LIMIT_ARG, new BigDecimal(limitEditText.getText().toString()));
            BigDecimal stopLimit = null;
            if(stopLimitEditText.getText().toString().length() <= 0){
                stopLimit = new BigDecimal(stopLimitEditText.getText().toString());
            }
            restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_STOP_LIMIT_ARG, stopLimit);
        }

        restQueryIntent.putExtra("receiver", receiver);
        restQueryIntent.putExtra("command", RestService.COMMAND_CREATE_ORDER);
                this.startService(restQueryIntent);
        // create order and call REST api
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private TimePickerDialog.OnTimeSetListener listener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            listener = (TimePickerDialog.OnTimeSetListener) activity;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            listener.onTimeSet(view, hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private DatePickerDialog.OnDateSetListener listener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            listener = (DatePickerDialog.OnDateSetListener) activity;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            listener.onDateSet(view, year, monthOfYear, dayOfMonth);
        }
    }

}
