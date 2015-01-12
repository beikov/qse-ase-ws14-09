package at.ac.tuwien.ase09.android.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.adapater.ValuePaperItemLayoutPopulator;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, TextWatcher {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_VALUE_PAPER = "VALUE_PAPER";

    private ValuePaperDto valuePaper;

    private OnFragmentInteractionListener mListener;

    private Button timePickerTarget;
    private Button datePickerTarget;
    private TextView estimatedOrderCostTextView;

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

        View.OnClickListener onTimePickerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((Button) v);
            }
        };

        View.OnClickListener onDatePickerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v);
            }
        };

        Calendar currentCal = Calendar.getInstance();
        Button validityFromTimeButton = (Button) orderFragmentView.findViewById(R.id.validityFromTimeButton);
        validityFromTimeButton.setText(DateFormat.getTimeFormat(getActivity()).format(currentCal.getTime()));
        validityFromTimeButton.setOnClickListener(onTimePickerClickListener);

        Button validityFromDateButton = (Button) orderFragmentView.findViewById(R.id.validityFromDateButton);
        validityFromDateButton.setText(DateFormat.getDateFormat(getActivity()).format(currentCal.getTime()));
        validityFromDateButton.setOnClickListener(onDatePickerClickListener);

        Button validityToTimeButton = (Button) orderFragmentView.findViewById(R.id.validityToTimeButton);
        validityToTimeButton.setText(DateFormat.getTimeFormat(getActivity()).format(currentCal.getTime()));
        validityToTimeButton.setOnClickListener(onTimePickerClickListener);

        Button validityToDateButton = (Button) orderFragmentView.findViewById(R.id.validityToDateButton);
        validityToDateButton.setText(DateFormat.getDateFormat(getActivity()).format(currentCal.getTime()));
        validityToDateButton.setOnClickListener(onDatePickerClickListener);

        estimatedOrderCostTextView = (TextView) orderFragmentView.findViewById(R.id.estimatedOrderCost);

        EditText volumeInput = (EditText) orderFragmentView.findViewById(R.id.orderVolumeInput);
        volumeInput.addTextChangedListener(this);

        afterTextChanged(volumeInput.getEditableText());

        final View validToArea = orderFragmentView.findViewById(R.id.validToArea);
        validToArea.setVisibility(View.GONE);

        CheckBox validToCheckbox = (CheckBox) orderFragmentView.findViewById(R.id.validToCheckbox);
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

        View valuePaperItemView = orderFragmentView.findViewById(R.id.valuePaperItemView);
        ValuePaperItemLayoutPopulator.populateValuePaperItemView(valuePaperItemView, valuePaper);
        return orderFragmentView;
    }

    public void showTimePickerDialog(Button button) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this, 0);
        timePickerTarget = button;
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(Button button) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 0);
        datePickerTarget = button;
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        timePickerTarget.setText(DateFormat.getTimeFormat(getActivity()).format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        datePickerTarget.setText(DateFormat.getDateFormat(getActivity()).format(calendar.getTime()));
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
