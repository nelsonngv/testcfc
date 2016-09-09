package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;

import java.util.List;

/**
 * Created by pbadell on 11/20/15.
 */
public abstract  class AbstractRequisitionLineFragment extends PBSDetailsFragment {
    /**
     *
     */
    protected PBSRequisitionController reqCont;
    protected PandoraMain context;
    protected PandoraContext appContext;

    protected Spinner prodNameSpinner;
    protected SpinnerOnItemSelected prodNameItem;
    protected TextView requiredDate;

    protected ArrayAdapter prodNameAdapter;
    EditText qtyRequested;
    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_SAVE_PRLINE = "EVENT_SAVE_PRLINE";
    protected Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqCont = new PBSRequisitionController(getActivity());
        context = (PandoraMain)getActivity();
        appContext = context.globalVariable;
    }

    protected void setUI(View rootView) {
        Activity act = getActivity();
        prodNameSpinner = (Spinner) rootView.findViewById(R.id.prlProdNameSpinner);
        prodNameAdapter = PandoraHelper.addListToSpinner(act, prodNameSpinner, getProdList());
        requiredDate = (TextView)rootView.findViewById(R.id.prlDateRequired);
        saveButton = (Button) rootView.findViewById(R.id.savePRLine);
        qtyRequested = (EditText) rootView.findViewById(R.id.prlQty);
    }

    public List<SpinnerPair> getProdList() {
        Bundle input = new Bundle();
        //TODO: change to selection colum.
        input.putString(reqCont.ARG_PROD_COL_SELECTION, "Name");
        Bundle result = reqCont.triggerEvent(reqCont.GET_PRODUCT_LIST_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(reqCont.ARG_PRODUCT_LIST);
    }

    protected void setUIListener() {
        setOnItemSelectedListener();
        setOnClickListener(requiredDate, EVENT_DATE);
        setOnClickListener(saveButton, EVENT_SAVE_PRLINE);
    }

    private void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptFutureDatePicker((TextView) object, getActivity());
                        break;
                    }
                    case EVENT_SAVE_PRLINE: {
                        savePRLine();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }

    public void savePRLine() {};

    private void setOnItemSelectedListener() {
        prodNameItem = new SpinnerOnItemSelected(prodNameSpinner, new SpinnerPair());
        prodNameSpinner.setOnItemSelectedListener(prodNameItem);
    }

}
