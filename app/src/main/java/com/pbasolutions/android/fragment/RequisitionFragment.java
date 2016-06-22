package com.pbasolutions.android.fragment;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.RequisitionRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MPurchaseRequest;

/**
 * Created by pbadell on 10/12/15.
 */
public class RequisitionFragment extends Fragment{
    /**
     * Class tag name.
     */
    private static final String TAG = "RequisitionFragment";
    private static final int ADD_REQUISITION_ID = 1;

    PBSRequisitionController requisCont;

    private Spinner statusSpinner;
    private ArrayAdapter statusAdapter;
    private SpinnerOnItemSelected statusItem;

    protected String requisitionDetailTitle;


    Context context;

    private ObservableArrayList<MPurchaseRequest> requisitionList;

    public RequisitionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requisCont = new PBSRequisitionController(getActivity());
        context =  getActivity();
        statusItem = new SpinnerOnItemSelected(statusSpinner, new SpinnerPair());

        requisitionDetailTitle = getString(R.string.title_requisitiondetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.requisition_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.requisition_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setIsApprovedSpinner(rootView);
        setOnItemSelectedListener();

        new AsyncTask<Object, Void, Void>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Void doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];

                if (PBSServerConst.cookieStore != null) {
                    syncRequsitions();
                }

                requisitionList = getRequisitionList();

                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);

                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(inflater, recyclerView);

        return rootView;
    }

    private void setIsApprovedSpinner(View rootView) {
      statusSpinner = (Spinner) rootView.findViewById(R.id.filterByStatus);
      statusAdapter = PandoraHelper
              .addListToSpinner(getActivity(),
                      statusSpinner, PandoraHelper.getStatusList());
    }

    private void setOnItemSelectedListener() {
        statusItem.setFragment(this);
        statusItem.setFragmentManager(getFragmentManager());
       statusSpinner.setOnItemSelectedListener(statusItem);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ADD_REQUISITION_ID, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_REQUISITION_ID: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_CREATE_REQUISITION, false);
                return  true;
            }
            default:return false;
        }
    }

    public void syncRequsitions(){
        PandoraContext globalVar = ((PandoraMain)context).globalVariable;
        Bundle input = new Bundle();
        input.putString(requisCont.ARG_USER_ID, globalVar.getAd_user_id());
        input.putString(requisCont.ARG_PROJECT_LOCATION_ID, globalVar.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
        Bundle result = requisCont.triggerEvent(requisCont.SYNC_REQUISITIONS_EVENT, input, new Bundle(), null);
    }

    public ObservableArrayList<MPurchaseRequest> getRequisitionList() {
        Bundle input = new Bundle();
        if (statusItem != null) {
              String orderBy = statusItem.getPair() == null ? null : statusItem.getPair().getValue();
              input.putString(requisCont.ARG_ORDERBY, orderBy);
        }

        Bundle result = requisCont.triggerEvent(requisCont.GET_REQUISITIONS_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MPurchaseRequest>)result.get(requisCont.ARG_REQUISITION_LIST);
    }
}

