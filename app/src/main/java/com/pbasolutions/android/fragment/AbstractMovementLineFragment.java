package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.databinding.AssetMovementlineDetailsBinding;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MMovementLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbadell on 12/4/15.
 */
public abstract class AbstractMovementLineFragment extends PBSDetailsFragment{

    private static final String EVENT_SAVE_LINE = "EVENT_SAVE_LINE";
    protected PBSAssetController assetCont;
    protected PandoraMain context;
    protected PandoraContext appContext;

    protected Spinner prodName;
    protected SpinnerOnItemSelected prodNameItem;
    protected ArrayAdapter prodNameAdapter;

    protected Spinner prodValue;
    protected SpinnerOnItemSelected prodValueItem;
    protected ArrayAdapter prodValueAdapter;

    protected TextView qtyOnHand;

    protected Spinner asi;
    protected SpinnerOnItemSelected asiItem;
    protected ArrayAdapter asiAdapter;

    protected TextView uom;
    protected EditText movementQty;
    protected TextView textViewMovementQty;

    protected View rootView ;


    private static final int SAVE_LINE_ID = 1;
    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private AssetMovementlineDetailsBinding binding;
    protected MMovementLine line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetCont = new PBSAssetController(getActivity());
        context = (PandoraMain)getActivity();
        appContext = context.getGlobalVariable();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.asset_new_movementline,
                container, false);
        setUI(rootView);
        setUIListener();

        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle result = new Bundle();
                line = getMovementLine();
                result.putParcelableArrayList("prodList0", (ArrayList<SpinnerPair>) getProdList(true));
                result.putParcelableArrayList("prodList1", (ArrayList<SpinnerPair>)getProdList(false));
                result.putParcelableArrayList("asi", (ArrayList<SpinnerPair>)getAsi());
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                Activity act = getActivity();
                List<SpinnerPair> prodList0 = result.getParcelableArrayList("prodList0");
                prodNameAdapter = PandoraHelper.addListToSpinner(act, prodName, prodList0);

                List<SpinnerPair> prodList1 = result.getParcelableArrayList("prodList1");
                prodValueAdapter = PandoraHelper.addListToSpinner(act, prodValue, prodList1);

                List<SpinnerPair> asiList = result.getParcelableArrayList("asi");
                asiAdapter = PandoraHelper.addListToSpinner(act, asi, asiList);

                setOnItemSelectedListener();
                setValues();
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute();


        return rootView;
    }

    public void setUI(View view) {
        prodName = (Spinner) view.findViewById(R.id.prodName);
        prodValue = (Spinner) view.findViewById(R.id.prodValue);
        asi = (Spinner) view.findViewById(R.id.asi);
        uom = (TextView) view.findViewById(R.id.uom);
        movementQty = (EditText) view.findViewById(R.id.movementQty);
        qtyOnHand = (TextView) view.findViewById(R.id.qtyOnHand);
        textViewMovementQty = (TextView) view.findViewById(R.id.textViewMovementQty);
        PandoraHelper.setAsterisk(textViewMovementQty);
    }

    protected abstract List<SpinnerPair> getAsi();

    protected abstract List<SpinnerPair> getProdList(boolean isName);

    public void setUIListener() {
        setOnItemSelectedListener();
    }

    public void setValues() {};
    public void save() {};
    protected abstract MMovementLine getMovementLine();

    private void setOnItemSelectedListener() {
        prodNameItem = new SpinnerOnItemSelected(prodName, new SpinnerPair());
        FragmentActivity act = getActivity();
        prodNameItem.setFragmentActivity(act);
        prodNameItem.setFragment(this);
        prodName.setOnItemSelectedListener(prodNameItem);
        prodValueItem = new SpinnerOnItemSelected(prodValue, new SpinnerPair());
        prodValueItem.setFragmentActivity(act);
        prodValueItem.setFragment(this);
        prodValue.setOnItemSelectedListener(prodValueItem);
        asiItem = new SpinnerOnItemSelected(asi, new SpinnerPair());
        asiItem.setFragmentActivity(act);
        asiItem.setFragment(this);
        asi.setOnItemSelectedListener(asiItem);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, SAVE_LINE_ID, 0, getString(R.string.text_icon_save));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SAVE_LINE_ID: {
                save();
                return  true;
            }
            default:return false;
        }
    }

}
