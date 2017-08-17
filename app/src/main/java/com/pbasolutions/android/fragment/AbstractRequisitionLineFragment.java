package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.control.SearchableSpinnerForRequisition;
import com.pbasolutions.android.control.SimpleAdapter;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MProduct;
import com.pbasolutions.android.model.MUOM;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected SearchableSpinnerForRequisition prodNameSpinner;
    protected SpinnerOnItemSelected prodNameItem;
    protected TextView requiredDate;
    protected TextView uom;
    protected CheckBox isEmergency;
    protected EditText purcReason;
    protected TextView textViewPurcReason;

    protected ArrayAdapter prodNameAdapter;
    EditText qtyRequested;
    protected static final String EVENT_DATE = "EVENT_DATE";
    private static final int SUBMIT = 500;
    protected String prodName;
    protected String prodUUID;
    MenuItem add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        reqCont = new PBSRequisitionController(getActivity());
        context = (PandoraMain)getActivity();
        appContext = context.getGlobalVariable();
    }

    protected void setUI(View rootView) {
        Activity act = getActivity();
        prodNameSpinner = (SearchableSpinnerForRequisition) rootView.findViewById(R.id.prlProdNameSpinner);
        prodNameAdapter = PandoraHelper.addTwoItemListToSpinner(act, prodNameSpinner, getProdList());
        requiredDate = (TextView)rootView.findViewById(R.id.prlDateRequired);
        qtyRequested = (EditText) rootView.findViewById(R.id.prlQty);
        uom = (TextView) rootView.findViewById(R.id.uom);
        isEmergency = (CheckBox) rootView.findViewById(R.id.is_emergency);
        purcReason = (EditText) rootView.findViewById(R.id.prlPurcReason);
        TextView textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        TextView textViewQty = (TextView) rootView.findViewById(R.id.textViewQty);
        TextView textViewDateRequired = (TextView) rootView.findViewById(R.id.textViewDateRequired);
        textViewPurcReason = (TextView) rootView.findViewById(R.id.textViewPurcReason);
        PandoraHelper.setAsterisk(textViewDateRequired);
        PandoraHelper.setAsterisk(textViewName);
        PandoraHelper.setAsterisk(textViewQty);
    }

    public List<SpinnerPair> getProdList() {
        Bundle input = new Bundle();
        input.putString(PBSRequisitionController.ARG_PROD_COL_SELECTION, MProduct.NAME_COL);
        Bundle result = reqCont.triggerEvent(PBSRequisitionController.GET_PRODUCT_LIST_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(PBSRequisitionController.ARG_PRODUCT_LIST);
    }

    protected void setUIListener() {
//        setOnItemSelectedListener();
        setOnClickListener(requiredDate, EVENT_DATE);

        prodNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle input = new Bundle();
                PBSAssetController assetCont = new PBSAssetController(getActivity());
                input.putString(PBSAssetController.ARG_M_PRODUCT_UUID, ((SpinnerPair) prodNameAdapter.getItem(position)).getKey());
                Bundle result = assetCont.triggerEvent(PBSAssetController.GET_UOM_EVENT, input, new Bundle(), null);
                MUOM mUOM = (MUOM)result.getSerializable(PBSAssetController.ARG_UOM);
                uom.setText(mUOM.getName());
                prodUUID = ((SpinnerPair) prodNameAdapter.getItem(position)).getKey();
                prodName = ((SpinnerPair) prodNameAdapter.getItem(position)).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        isEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    PandoraHelper.setAsterisk(textViewPurcReason);
                else textViewPurcReason.setText(getString(R.string.label_purchase_reason));
            }
        });
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
                    default:
                        break;
                }
            }
        });

    }

    public void savePRLine() {}

//    private void setOnItemSelectedListener() {
//        prodNameItem = new SpinnerOnItemSelected(prodNameSpinner, new SpinnerPair());
//        prodNameSpinner.setOnItemSelectedListener(prodNameItem);
//    }

    public static ArrayAdapter addListToSpinner(Activity activity, Spinner spinner, List<SpinnerPair> productList) {
        List<Map<String, String>> prodList = new ArrayList<>();
        HashMap temp;
        for (int i = 0; i < productList.size(); i++) {
            SpinnerPair sp = productList.get(i);
            temp = new HashMap<>(2);
            try {
                JSONArray jsonArray = new JSONArray(sp.getValue());
                temp.put(MProduct.NAME_COL, jsonArray.get(0).toString());
                temp.put(MProduct.VALUE_COL, jsonArray.get(1).toString());
                prodList.add(temp);
                productList.get(i).value = jsonArray.get(0).toString();
            } catch (Exception e) {
                Log.e("AbstractRequisitionLine", Log.getStackTraceString(e));
            }
        }

        ArrayAdapter adapter;
        if (productList == null) {
            adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter = new SpinAdapter(activity, android.R.layout.simple_spinner_dropdown_item, productList);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SimpleAdapter simpleAdapter = new SimpleAdapter(activity, prodList, android.R.layout.simple_spinner_item,
                new String[] {MProduct.NAME_COL, MProduct.VALUE_COL}, new int[] {android.R.id.text1, android.R.id.text2});
        simpleAdapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        spinner.setAdapter(simpleAdapter);
        return adapter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        add = menu.add(0, SUBMIT, 1, getString(R.string.label_button_save));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.ic_done);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SUBMIT: {
                savePRLine();
                return true;
            }
            default: return false;
        }
    }
}
