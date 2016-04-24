package com.pbasolutions.android.fragment;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AttendanceRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSDeployController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MPurchaseRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by tinker on 4/19/16.
 */
public class AttendanceFragment  extends Fragment {
    /**

     * Class tag name.
     */
    private static final String TAG = "AttendanceFragment";

    private static final int ADD_ATTENDANCE_LINE = 500;

    PBSDeployController deployCont;
    TextView deployDateView;
    protected Spinner shiftSpinner;
    protected ArrayAdapter shiftAdapter;

    private ObservableArrayList<MDeploy> deployList;

    public AttendanceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deployCont = new PBSDeployController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.attendance_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUI(rootView);
        setUIListener();

//        deployList  = getDeploys();
//        DeployRVA viewAdapter = new DeployRVA(getActivity(), deployList, inflater);
//        recyclerView.setAdapter(viewAdapter);
//        PandoraHelper.addRecyclerViewListener(recyclerView, deployList, getActivity(),
//                new RequisitionDetailFragment(), getString(R.string.title_deployment_details));
        return rootView;
    }

    void setUI(View rootView) {
        shiftSpinner = (Spinner) rootView.findViewById(R.id.attShiftSpinner);
        shiftAdapter = PandoraHelper.addListToSpinner(getActivity(), shiftSpinner,
                getPrefShiftList());

        deployDateView = (TextView) rootView.findViewById(R.id.attDeployDate);

        Date date= new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        deployDateView.setText(sdf.format(date));
    }
    void setUIListener() {
        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPair pair = (SpinnerPair) shiftAdapter.getItem(position);
                shiftChanged(pair.getKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deployDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraHelper.promptDatePicker(deployDateView, getActivity());
            }
        });

        deployDateView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private ObservableArrayList<MDeploy> getDeploys() {
        Bundle input = new Bundle();
        PandoraMain pandoraMain = PandoraMain.instance;

        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;
        input.putInt(PBSDeployController.ARG_PROJECTLOCATION_ID, Integer.parseInt(pc.getC_projectlocation_id()));
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());

        input.putString(PBSDeployController.ARG_DEPLOYMENTDATE, deployDateView.getText().toString());

        Bundle result = deployCont.triggerEvent(deployCont.GET_DEPLOYS_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MDeploy>)result.get(deployCont.ARG_DEPLOYS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ADD_ATTENDANCE_LINE, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuItem sync;
//        sync = menu.add(0, PandoraMain.SYNC_DEPLOY_ID, 0, "Sync Deploy");
//        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        sync.setIcon(R.drawable.refresh);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_ATTENDANCE_LINE: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_CREATE_ATTENDANCELINE, false);
                return  true;
            }
            default:return false;
        }
    }


    public List<SpinnerPair> getPrefShiftList() {
        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;

        Bundle input = new Bundle();
        input.putString(PBSDeployController.ARG_PROJECTLOCATION_UUID, pc.getC_projectlocation_uuid());
        Bundle result = deployCont.triggerEvent(PBSDeployController.GET_SHIFTS_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSDeployController.SHIFT_LIST);
    }

    protected void shiftChanged(String shiftUUID) {
        Log.d("ttt", "Shift Changed:" + shiftUUID);
    }

    protected void dateChanged(String deployDate) {
        Log.d("ttt", "Date Changed:" + deployDate);
    }
}

