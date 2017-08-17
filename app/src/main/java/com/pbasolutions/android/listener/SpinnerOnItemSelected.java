package com.pbasolutions.android.listener;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.fragment.NewMovementLineFragment;

/**
 * Created by pbadell on 10/20/15.
 */
public class SpinnerOnItemSelected implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    SpinnerPair pair;
    Fragment fragment;
    FragmentActivity fragmentActivity;
    private FragmentManager fragmentManager;

    public SpinnerOnItemSelected(Spinner spinner, SpinnerPair pair) {
        this.spinner = spinner;
        this.pair = pair;
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pair = (SpinnerPair)parent.getItemAtPosition(position);
        if (fragment instanceof NewMovementLineFragment) {
            ((NewMovementLineFragment)fragment).updateValues(pair, parent.getId());
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public SpinnerPair getPair() {
        return pair;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentActivity;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
