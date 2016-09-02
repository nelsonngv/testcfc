package com.pbasolutions.android.fragment;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AttendanceLineRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSDeployController;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.ModelConst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by pbadell on 11/26/15.
 */
public class NewAttendanceFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewAttendanceFragment";

    private static final int ADD_ATTENDANCE_LINE = 500;
    private static final int REMOVE_ATTENDANCE_LINE = 501;

    private String _UUID;

    MAttendance attendance;
    ContentResolver cr;


    PBSAttendanceController attendanceCont;
    TextView deployDateView;
    protected Spinner shiftSpinner;
    protected ArrayAdapter shiftAdapter;

    protected Spinner projLocationSpinner;
    private ArrayAdapter projLocNameAdapter;

    RecyclerView recyclerView;
    AttendanceLineRVA linesAdapter;
    Button requestButton;

    private ObservableArrayList<MAttendanceLine> attendanceLines;

    public NewAttendanceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        attendanceCont = new PBSAttendanceController(getActivity());

        cr = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.attendance_new, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.attendance_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUI(rootView);
        setUIListener();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        projLocNameAdapter = PandoraHelper.addListToSpinner(getActivity(), projLocationSpinner, getProjLocList());

        refreshAttendances();
//        PandoraHelper.addRecyclerViewListener(recyclerView, deployList, getActivity(),
//                new RequisitionDetailFragment(), getString(R.string.title_deployment_details));
        if (projLocNameAdapter.getCount() > 0)
        {
            PandoraMain pandoraMain = PandoraMain.instance;
            String projLocID = pandoraMain.globalVariable.getC_projectlocation_id();

            for (int i = 0; i < projLocNameAdapter.getCount(); i++)
            {
                SpinnerPair pair = (SpinnerPair) projLocNameAdapter.getItem(i);
                if (projLocID.equalsIgnoreCase(pair.getKey()))
                {
                    projLocationSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    void refreshAttendances() {
        List<SpinnerPair> prefShiftList = getPrefShiftList();
        if(prefShiftList.size() == 0) {
            SpinnerPair pair = new SpinnerPair();
            pair.setKey(null);
            pair.setValue(getString(R.string.no_shift_spinner));
            prefShiftList = new ArrayList<>();
            prefShiftList.add(pair);
        }
        shiftAdapter = PandoraHelper.addListToSpinner(getActivity(), shiftSpinner,
                prefShiftList);
        attendanceLines = getAttendances();
        linesAdapter = new AttendanceLineRVA(getActivity(), attendanceLines);
        recyclerView.setAdapter(linesAdapter);
    }

    void setUI(View rootView) {
        shiftSpinner = (Spinner) rootView.findViewById(R.id.attShiftSpinner);

        projLocationSpinner = (Spinner) rootView.findViewById(R.id.attProjLocation);
        projLocationSpinner.setEnabled(false);

        deployDateView = (TextView) rootView.findViewById(R.id.attDeployDate);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        deployDateView.setText(sdf.format(date));

        requestButton = (Button) rootView.findViewById(R.id.atRequest);
    }
    void setUIListener() {
        projLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshAttendances();
                Date date;
                attendanceLines = getAttendances();
                PBSAttendanceController attendCont = new PBSAttendanceController(getActivity());
                if(PBSAttendanceController.deployDate != null && !PBSAttendanceController.deployDate.equals("")) {
                    String deployDate = PBSAttendanceController.deployDate;
                    date = PandoraHelper.stringToDate("dd-MM-yyyy", deployDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    deployDateView.setText(sdf.format(date));
                }

                refreshAttendances();
                SpinnerPair selectedPair = (SpinnerPair) shiftSpinner.getSelectedItem();
                if(shiftAdapter.getCount() > 0 && PBSAttendanceController.shiftUUID != null) {
                    if (!PBSAttendanceController.shiftUUID.equalsIgnoreCase(selectedPair.getKey())) {
                        for (int j = 0; j < shiftAdapter.getCount(); j++) {
                            SpinnerPair pair2 = (SpinnerPair) shiftAdapter.getItem(j);
                            if (PBSAttendanceController.shiftUUID.equalsIgnoreCase(pair2.getKey())) {
                                shiftSpinner.setSelection(j);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPair newPair = (SpinnerPair) shiftAdapter.getItem(position);
                shiftChanged(newPair.getKey());

                if(PBSAttendanceController.shiftUUID == null) {
                    attendanceLines = linesAdapter.getLines();
                    for (int i = 0; i < attendanceLines.size(); i++)
                        attendanceLines.get(i).setIsSelected(true);

                    if (attendanceLines.size() > 0)
                        removeLine();
                }

                if(shiftAdapter.getCount() > 0 && PBSAttendanceController.shiftUUID != null) {
                    if (!PBSAttendanceController.shiftUUID.equalsIgnoreCase(newPair.getKey())) {
                        for (int j = 0; j < shiftAdapter.getCount(); j++) {
                            SpinnerPair pair2 = (SpinnerPair) shiftAdapter.getItem(j);
                            if (PBSAttendanceController.shiftUUID.equalsIgnoreCase(pair2.getKey())) {
                                attendanceLines = linesAdapter.getLines();
                                for (int i = 0; i < attendanceLines.size(); i++)
                                    attendanceLines.get(i).setIsSelected(true);

                                if (attendanceLines.size() > 0)
                                    removeLine();
                                break;
                            }
                        }
                    }
                }
                PBSAttendanceController.shiftUUID = newPair.getKey();
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
            String deployDate = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(PBSAttendanceController.deployDate == null || PBSAttendanceController.deployDate.equals(""))
                    deployDate = deployDateView.getText().toString();
                else deployDate = PBSAttendanceController.deployDate;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateChanged(s.toString());
                if(!deployDate.equalsIgnoreCase(s.toString())) {
                    PBSAttendanceController.deployDate = s.toString();
                    attendanceLines = linesAdapter.getLines();
                    for (int i = 0; i < attendanceLines.size(); i++)
                        attendanceLines.get(i).setIsSelected(true);

                    if (attendanceLines.size() > 0)
                        removeLine();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveAttendance();
                requestAttendance();
            }
        });
    }

    private ObservableArrayList<MAttendanceLine> getAttendances() {
        Bundle input = new Bundle();
        PandoraMain pandoraMain = PandoraMain.instance;

        SpinnerPair projlocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        if (projlocPair == null) { // when no selected
            return new ObservableArrayList<MAttendanceLine>();
        }

        String projLocId = projlocPair.getKey();

        PandoraContext pc = pandoraMain.globalVariable;

        input.putString(PBSDeployController.ARG_PROJECTLOCATION_ID, projLocId);
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());

        input.putString(PBSDeployController.ARG_DEPLOYMENTDATE, deployDateView.getText().toString());

        Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.GET_ATTENDANCES_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MAttendanceLine>)result.get(PBSAttendanceController.ARG_ATTENDANCES);
    }

    public List<SpinnerPair> getProjLocList() {
        Bundle input = new Bundle();
        Bundle result = attendanceCont
                .triggerEvent(PBSAttendanceController.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result
                .getParcelableArrayList(PBSAttendanceController.ARG_PROJECTLOCATIONS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ADD_ATTENDANCE_LINE, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);

        add = menu.add(0, REMOVE_ATTENDANCE_LINE, 1, getString(R.string.text_icon_remove));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.minus_white);
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
                return addLine();
            }
            case REMOVE_ATTENDANCE_LINE: {
                removeLine();
                return  true;
            }
            default:return false;
        }
    }

    boolean addLine() {
        String deployDate = deployDateView.getText().toString();
        if (deployDate == null || deployDate.length() == 0)
        {
            PandoraHelper.showMessage(getContext(), "Please select Deployment Date.");
            return false;
        }

        SpinnerPair spinnerPair = (SpinnerPair) shiftSpinner.getSelectedItem();
        if (spinnerPair.getKey() == null)
        {
            PandoraHelper.showMessage(getContext(), getString(
                    R.string.no_shift_error, getString(R.string.proj_shift)));
            return false;
        }

        PBSAttendanceController.deployDate = deployDate;
        SpinnerPair projlocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        String projLocId = projlocPair.getKey();
        PBSAttendanceController.projectLocationId = projLocId;

        PBSAttendanceController.shiftUUID = spinnerPair.getKey();

        ((PandoraMain) getActivity()).
                displayView(PandoraMain.FRAGMENT_CREATE_ATTENDANCELINE, false);
        return  true;
    }

    public List<SpinnerPair> getPrefShiftList() {
        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;

        SpinnerPair projlocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        if (projlocPair == null) { // when no selected
            return new ArrayList<SpinnerPair>();
        }

        String projLocId = projlocPair.getKey();

        Bundle input = new Bundle();
        input.putString(PBSAttendanceController.ARG_PROJECTLOCATION_ID, projLocId);
        Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.GET_SHIFTS_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.SHIFT_LIST);
    }

    private void removeLine() {
        new AsyncTask<Void, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle input = new Bundle();
                input.putSerializable(PBSAttendanceController.ARG_ATTENDANCELINE_LIST, linesAdapter.getLines());
                Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.REMOVE_ATTDLINES_EVENT, input, new Bundle(), null);
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(NewAttendanceFragment.this).attach(NewAttendanceFragment.this).commit();
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute();
    }

    /**
     * Request Attendance will make call to Server API for processing the requisition/purchase request.
     */
    protected void requestAttendance() {
//        if (!get_UUID().isEmpty() && attendance == null){
//            populatePR();
//        }
//
        attendance = new MAttendance();

        if (attendance == null) {
            PandoraHelper.showWarningMessage((PandoraMain) getActivity(), getString(
                    R.string.no_line_error, getString(R.string.request)));
            return;
        }

        SpinnerPair spinnerPair = (SpinnerPair) shiftSpinner.getSelectedItem();
        if (spinnerPair.getKey() == null) {
            PandoraHelper.showWarningMessage((PandoraMain) getActivity(), getString(
                    R.string.no_shift_error, getString(R.string.proj_shift)));
            return;
        }

        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;
        SpinnerPair projlocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        String projLocId = projlocPair.getKey();
        attendance.setC_ProjectLocation_ID(Integer.parseInt(projLocId));

        attendance.setDeploymentDate(deployDateView.getText().toString());

        String shiftId = ModelConst.mapIDtoColumn(ModelConst.HR_SHIFT_TABLE, ModelConst.HR_SHIFT_ID_COL, spinnerPair.getKey(), ModelConst.HR_SHIFT_UUID_COL, cr);
        attendance.setHR_Shift_ID(Integer.parseInt(shiftId));

        ObservableArrayList<MAttendanceLine> lines = linesAdapter.getLines();
        if (lines == null || lines.size() == 0) {
            PandoraHelper.showWarningMessage((PandoraMain) getActivity(), getString(
                    R.string.no_line_error, getString(R.string.request)));
            return;
        }

        for (MAttendanceLine line: lines ) {
            line.prepareForGson();
        }

        attendance.setLines(lines.toArray(new MAttendanceLine[lines.size()]));

        requestButton.setBackgroundColor(getResources().getColor(R.color.colorButtonDisable));

        Bundle input = new Bundle();
        input.putSerializable(PBSAttendanceController.ARG_ATTENDANCE_REQUEST, attendance);
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());

        new AsyncTask<Bundle, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.CREATE_ATTENDANCE_EVENT, params[0], new Bundle(), null);
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);

                requestButton.setBackgroundColor(getResources().getColor(R.color.colorButtons));

                if (PandoraConstant.RESULT.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    PandoraHelper.showMessage(getActivity(), result.getString(PandoraConstant.RESULT));
                    refreshAttendances();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentManager.popBackStack();
//                    fragmentManager.popBackStack();
//                    fragmentTransaction.commit();
                } else {
                    PandoraHelper.showErrorMessage(getActivity(), result.getString(PandoraConstant.ERROR));
                }

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);

    }

    protected void saveAttendance() {
        Bundle input = new Bundle();
        ContentValues cv= new ContentValues();
        if (get_UUID() == null || get_UUID().isEmpty())
            set_UUID(UUID.randomUUID().toString());
        cv.put(MAttendance.M_ATTENDANCE_UUID_COL, get_UUID());
        cv.put(MAttendance.DEPLOYMENT_DATE_COL, deployDateView.getText().toString());
        PandoraContext cont = PandoraMain.instance.globalVariable;

        cv.put(ModelConst.C_PROJECTLOCATION_UUID_COL, cont.getC_projectlocation_uuid());

        SpinnerPair spinnerPair = (SpinnerPair) shiftSpinner.getSelectedItem();
        String shiftId = ModelConst.mapIDtoColumn(ModelConst.HR_LEAVETYPE_TABLE, ModelConst.HR_LEAVETYPE_ID_COL, spinnerPair.getKey(), ModelConst.HR_LEAVETYPE_UUID_COL, cr);
        cv.put(ModelConst.HR_SHIFT_UUID_COL, spinnerPair.getValue());

        input.putParcelable(PBSAttendanceController.ARG_CONTENTVALUES, cv);

        new AsyncTask<Bundle, Void, Void>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Void doInBackground(Bundle... params) {
                attendanceCont.triggerEvent(PBSAttendanceController.INSERT_ATTENDANCE_REQ_EVENT, params[0], new Bundle(), null);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    protected void shiftChanged(String shiftUUID) {
        Log.d("ttt", "Shift Changed:" + shiftUUID);
    }

    protected void dateChanged(String deployDate) {
        Log.d("ttt", "Date Changed:" + deployDate);
    }

    public String get_UUID() {
        return _UUID;
    }

    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }
}
