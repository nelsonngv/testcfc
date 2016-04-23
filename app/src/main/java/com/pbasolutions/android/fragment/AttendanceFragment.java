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
import com.pbasolutions.android.adapter.AttendanceRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MPurchaseRequest;

/**
 * Created by tinker on 4/19/16.
 */
public class AttendanceFragment  extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "AttendanceFragment";
    private static final int ADD_ATTENDANCE_ID = 1;

    PBSAttendanceController attendanceCont;

    private Spinner statusSpinner;
    private ArrayAdapter statusAdapter;
    private SpinnerOnItemSelected statusItem;


    Context context;

    private ObservableArrayList<MPurchaseRequest> attendanceList;

    public AttendanceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        attendanceCont = new PBSAttendanceController(getActivity());
        context =  getActivity();
        statusItem = new SpinnerOnItemSelected(
                statusSpinner, new SpinnerPair());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.attendance_rv);
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
                    syncAttendances();
                }

                attendanceList = getAttendanceList();

                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);

                AttendanceRVA viewAdapter = new AttendanceRVA(getActivity(),attendanceList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, attendanceList, getActivity(),
                        new AttendanceDetailFragment(), getString(R.string.title_applicantdetails));

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
        add = menu.add(0, ADD_ATTENDANCE_ID, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_ATTENDANCE_ID: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_CREATE_ATTENDANCE, false);
                return  true;
            }
            default:return false;
        }
    }

    public void syncAttendances(){
        PandoraContext globalVar = ((PandoraMain)context).globalVariable;
        Bundle input = new Bundle();
        input.putString(attendanceCont.ARG_USER_ID, globalVar.getAd_user_id());
        input.putString(attendanceCont.ARG_PROJECT_LOCATION_ID, globalVar.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
        Bundle result = attendanceCont.triggerEvent(attendanceCont.SYNC_ATTENDANCES_EVENT, input, new Bundle(), null);
    }

    public ObservableArrayList<MPurchaseRequest> getAttendanceList() {
        Bundle input = new Bundle();
        if (statusItem != null) {
            String orderBy = statusItem.getPair() == null ? null : statusItem.getPair().getValue();
            input.putString(attendanceCont.ARG_ORDERBY, orderBy);
        }

        Bundle result = attendanceCont.triggerEvent(attendanceCont.GET_ATTENDANCES_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MPurchaseRequest>)result.get(attendanceCont.ARG_ATTENDANCE_LIST);
    }
}

