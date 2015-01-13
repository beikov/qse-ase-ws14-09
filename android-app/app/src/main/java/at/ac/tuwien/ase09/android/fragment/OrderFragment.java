package at.ac.tuwien.ase09.android.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.OrderActionAdapter;
import at.ac.tuwien.ase09.android.adapater.ValuePaperItemLayoutPopulator;
import at.ac.tuwien.ase09.android.service.RestResultReceiver;
import at.ac.tuwien.ase09.android.service.RestService;
import at.ac.tuwien.ase09.android.singleton.PortfolioContext;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, TextWatcher, View.OnClickListener, RestResultReceiver.Receiver {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_VALUE_PAPER = "VALUE_PAPER";

    private ValuePaperDto valuePaper;

    private OnFragmentInteractionListener mListener;

    private OrderAction selectedOrderAction;
    private Calendar validFrom = Calendar.getInstance();
    private Calendar validTo = Calendar.getInstance();
    // points either to validFrom or validTo - used to set values in listener
    private Calendar target;

    /* UI Elements */
    private Button timePickerTarget;
    private Button datePickerTarget;
    private TextView estimatedOrderCostTextView;
    private EditText volumeEditText;
    private RadioGroup orderTypeRadioGroup;
    private CheckBox validToCheckbox;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param valuePaper Parameter 1.
     * @return A new instance of fragment OrderFragment.
     */
    public static OrderFragment newInstance(ValuePaperDto valuePaper) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VALUE_PAPER, valuePaper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            valuePaper = (ValuePaperDto) getArguments().getSerializable(ARG_VALUE_PAPER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View orderFragmentView = inflater.inflate(R.layout.order_fragment, container, false);

        Button validityFromTimeButton = (Button) orderFragmentView.findViewById(R.id.validityFromTimeButton);
        validityFromTimeButton.setText(DateFormat.getTimeFormat(getActivity()).format(validFrom.getTime()));
        validityFromTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((Button) v, validFrom);
            }
        });

        Button validityFromDateButton = (Button) orderFragmentView.findViewById(R.id.validityFromDateButton);
        validityFromDateButton.setText(DateFormat.getDateFormat(getActivity()).format(validFrom.getTime()));
        validityFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v, validFrom);
            }
        });

        Button validityToTimeButton = (Button) orderFragmentView.findViewById(R.id.validityToTimeButton);
        validityToTimeButton.setText(DateFormat.getTimeFormat(getActivity()).format(validTo.getTime()));
        validityToTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((Button) v, validTo);
            }
        });

        Button validityToDateButton = (Button) orderFragmentView.findViewById(R.id.validityToDateButton);
        validityToDateButton.setText(DateFormat.getDateFormat(getActivity()).format(validTo.getTime()));
        validityToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v, validTo);
            }
        });

        estimatedOrderCostTextView = (TextView) orderFragmentView.findViewById(R.id.estimatedOrderCost);

        volumeEditText = (EditText) orderFragmentView.findViewById(R.id.orderVolumeInput);
        volumeEditText.addTextChangedListener(this);

        afterTextChanged(volumeEditText.getEditableText());

        final View validToArea = orderFragmentView.findViewById(R.id.validToArea);
        validToArea.setVisibility(View.GONE);

        validToCheckbox = (CheckBox) orderFragmentView.findViewById(R.id.validToCheckbox);
        TextView validFromTextView = (TextView) orderFragmentView.findViewById(R.id.validFromTextView);
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

        orderTypeRadioGroup = (RadioGroup) orderFragmentView.findViewById(R.id.orderTypeRadioGroup);

        // set up order type spinner
        Spinner orderActionSpinner = (Spinner) orderFragmentView.findViewById(R.id.orderActionSpinner);
        // Create an ArrayAdapter using a default spinner layout
        final OrderActionAdapter orderActionAdapter = new OrderActionAdapter(getActivity(), android.R.layout.simple_spinner_item);
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

        Button confirmOrderButton = (Button) orderFragmentView.findViewById(R.id.confirmOrderButton);
        confirmOrderButton.setOnClickListener(this);

        View valuePaperItemView = orderFragmentView.findViewById(R.id.valuePaperItemView);
        ValuePaperItemLayoutPopulator.populateValuePaperItemView(valuePaperItemView, valuePaper);
        return orderFragmentView;
    }

    public void showTimePickerDialog(Button button, Calendar targetCal) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this, 0);
        timePickerTarget = button;
        target = targetCal;
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(Button button, Calendar targetCal) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 0);
        datePickerTarget = button;
        target = targetCal;
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        target.set(Calendar.HOUR_OF_DAY, hourOfDay);
        target.set(Calendar.MINUTE, minute);
        timePickerTarget.setText(DateFormat.getTimeFormat(getActivity()).format(target.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        target.set(Calendar.YEAR, year);
        target.set(Calendar.MONTH, monthOfYear);
        target.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        datePickerTarget.setText(DateFormat.getDateFormat(getActivity()).format(target.getTime()));
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (valuePaper.getLastPrice() != null && s.length() > 0) {
            int volume = Integer.valueOf(s.toString());
            BigDecimal estimatedCosts = valuePaper.getLastPrice().multiply(new BigDecimal(volume));
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(0);
            estimatedOrderCostTextView.setText(df.format(estimatedCosts) + " " + valuePaper.getCurrency().getCurrencyCode());
        }
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
//                progressBar.setVisibility(View.VISIBLE);
                break;
            case RestService.STATUS_FINISHED:
                //TODO: redirect to previous fragment or to portfolio view order tab
//                progressBar.setVisibility(View.GONE);
                break;
            case RestService.STATUS_ERROR:
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), resultData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        RestResultReceiver receiver = new RestResultReceiver(new Handler());
        receiver.setReceiver(this);
        Intent restQueryIntent = new Intent(Intent.ACTION_SYNC, null, getActivity(), RestService.class);

        Calendar validFrom = Calendar.getInstance();

        OrderType orderType;
        switch(orderTypeRadioGroup.getCheckedRadioButtonId()){
            case R.id.radioButtonLimitOrder:    orderType = OrderType.LIMIT;
                                                break;
            case R.id.radioButtonMarketOrder:   orderType = OrderType.MARKET;
                                                break;
            default: throw new IllegalStateException("Unknown radio button id [" + orderTypeRadioGroup.getCheckedRadioButtonId() + "]");
        }
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_TYPE_ARG, orderType);
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_ACTION_ARG, selectedOrderAction);
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALID_FROM_ARG, validFrom);
        if(validToCheckbox.isChecked()) {
            restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALID_TO_ARG, validTo);
        }
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_PORTFOLIO_ARG, PortfolioContext.getPortfolio().getId());
        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VALUE_PAPER_ARG, valuePaper.getId());
        // TODO: set stopLimit and limit for LimitOrders

        restQueryIntent.putExtra(RestService.COMMAND_CREATE_ORDER_VOLUME_ARG, Integer.valueOf(volumeEditText.getText().toString()));

        restQueryIntent.putExtra("receiver", receiver);
        restQueryIntent.putExtra("command", RestService.COMMAND_CREATE_ORDER);
                getActivity().startService(restQueryIntent);
        // create order and call REST api
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((TimePickerDialog.OnTimeSetListener) getTargetFragment()).onTimeSet(view, hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ((DatePickerDialog.OnDateSetListener) getTargetFragment()).onDateSet(view, year, monthOfYear, dayOfMonth);
        }
    }

}
