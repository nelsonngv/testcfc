package com.pbasolutions.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pbasolutions.android.R;

import java.util.List;

/**
 * Created by pbadell on 10/19/15.
 */
public class SpinAdapter extends ArrayAdapter<SpinnerPair> {
    private List<SpinnerPair> sPairs;
    private Context context;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param pairs  The objects to represent in the ListView.
     */
    public SpinAdapter(Context context, int resource, List<SpinnerPair> pairs) {
        super(context, resource);
        this.context = context;
        this.sPairs = pairs;
    }

    public int getCount(){
        return sPairs.size();
    }

    public SpinnerPair getItem(int position) {
        return sPairs.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView lbl = (TextView) super.getView(position, convertView, parent);
        lbl.setText(sPairs.get(position).getValue());
        return lbl;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView lbl = (TextView) super.getView(position, convertView, parent);
        lbl.setText(sPairs.get(position).getValue());
        return lbl;
    }

    public int getPosition(String key) {
        SpinnerPair toFindPair = null;
        for (SpinnerPair pair : sPairs) {
            if(key.equalsIgnoreCase(pair.getKey())){
                toFindPair = pair;

            }
        }
        return sPairs.indexOf(toFindPair);
    }

}


