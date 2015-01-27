package at.ac.tuwien.ase09.android.listener;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 *
 * Created by Moritz on 17.01.2015.
 */
public interface ValuePaperSelectionListener {
    public void onValuePaperSelected(ValuePaperDto valuePaper);
}
