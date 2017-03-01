package com.pbasolutions.android.fragment;


import android.content.ContentResolver;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.pbasolutions.android.adapter.AttendanceSearchLineRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceSearchItem;
import com.pbasolutions.android.model.ModelConst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pbadell on 11/26/15.
 */
public class AttendanceSearchFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "AttendanceSearchFragment";

    ContentResolver cr;


    PBSAttendanceController attendanceCont;
    TextView deployDateFromView;
    TextView deployDateToView;
    protected Spinner shiftSpinner;
    protected ArrayAdapter shiftAdapter;

    protected Spinner projLocationSpinner;
    private ArrayAdapter projLocNameAdapter;

    RecyclerView recyclerView;
    AttendanceSearchLineRVA linesAdapter;
    Button searchButton;
    int screenHeight = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    private ObservableArrayList<MAttendanceSearchItem> attendances;

    public AttendanceSearchFragment() {
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
        View rootView = inflater.inflate(R.layout.attendance_search, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.attendancesrch_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenHeight = (screenHeight/3) * 2;

        setUI(rootView);
        setUIListener();

        attendances = new ObservableArrayList<MAttendanceSearchItem>();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        projLocNameAdapter = PandoraHelper.addListToSpinner(getActivity(), projLocationSpinner, getProjLocList());

        refreshAttendances();
        if (projLocNameAdapter.getCount() > 0)
        {
            PandoraMain pandoraMain = PandoraMain.instance;
            String projLocID = pandoraMain.getGlobalVariable().getC_projectlocation_id();

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

        refreshAttendanceLines();
    }

    void refreshAttendanceLines() {
        linesAdapter = new AttendanceSearchLineRVA(getActivity(), attendances);
        recyclerView.setAdapter(linesAdapter);

        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        if (recyclerView.getMeasuredHeight() > screenHeight)
            params.height = screenHeight;
        else params.height = recyclerView.getMeasuredHeight();
        recyclerView.setLayoutParams(params);
    }

    void setUI(View rootView) {
        shiftSpinner = (Spinner) rootView.findViewById(R.id.attsrchShiftSpinner);

        projLocationSpinner = (Spinner) rootView.findViewById(R.id.attsrchProjLocation);
        projLocationSpinner.setEnabled(false);

        deployDateFromView = (TextView) rootView.findViewById(R.id.attsrchDeployDateFrom);
        deployDateToView = (TextView) rootView.findViewById(R.id.attsrchDeployDateTo);

        Date date= new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        deployDateFromView.setText(sdf.format(date));
        deployDateToView.setText(sdf.format(date));

        searchButton = (Button) rootView.findViewById(R.id.atsrchSearch);
    }
    void setUIListener() {
        projLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshAttendances();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        deployDateFromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraHelper.promptDatePicker(deployDateFromView, getActivity());
            }
        });
        deployDateToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraHelper.promptDatePicker(deployDateToView, getActivity());
            }
        });

        TextWatcher tw = new TextWatcher() {
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
        };
        deployDateFromView.addTextChangedListener(tw);
        deployDateToView.addTextChangedListener(tw);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchAttendance();
            }
        });
    }

    public List<SpinnerPair> getProjLocList() {
        Bundle input = new Bundle();
        Bundle result = attendanceCont
                .triggerEvent(PBSAttendanceController.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result
                .getParcelableArrayList(PBSAttendanceController.ARG_PROJECTLOCATIONS);
    }

    public List<SpinnerPair> getPrefShiftList() {
        PandoraContext pc = ((PandoraMain)getActivity()).getGlobalVariable();

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
    /**
     * Request Attendance will make call to Server API for processing the requisition/purchase request.
     */
    protected void onSearchAttendance() {
        MAttendance attendance = new MAttendance();

        SpinnerPair spinnerPair = (SpinnerPair) shiftSpinner.getSelectedItem();
        if (spinnerPair.getKey() == null) {
            PandoraHelper.showWarningMessage((PandoraMain) getActivity(), getString(
                    R.string.no_shift_error, getString(R.string.proj_shift)));
            return;
        }

        String deployDateFrom = deployDateFromView.getText().toString();
        String deployDateTo = deployDateToView.getText().toString();
        if (deployDateFrom == null || deployDateFrom.length() == 0 || deployDateTo == null || deployDateTo.length() == 0) {
            PandoraHelper.showWarningMessage(getActivity(), "Please select deploy date.");
            return;
        }

        try {
            if (sdf.parse(deployDateFrom).after(sdf.parse(deployDateTo))) {
                PandoraHelper.showWarningMessage(getActivity(), "Invalid deploy date range.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PandoraContext pc = ((PandoraMain)getActivity()).getGlobalVariable();
        SpinnerPair projlocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        String projLocId = projlocPair.getKey();
        attendance.setC_ProjectLocation_ID(Integer.parseInt(projLocId));

        attendance.setDeploymentDateFrom(deployDateFrom);
        attendance.setDeploymentDateTo(deployDateTo);

        String shiftId = ModelConst.mapIDtoColumn(ModelConst.HR_SHIFT_TABLE, ModelConst.HR_SHIFT_ID_COL, spinnerPair.getKey(), ModelConst.HR_SHIFT_UUID_COL, cr);
        attendance.setHR_Shift_ID(Integer.parseInt(shiftId));


        Bundle input = new Bundle();
        input.putSerializable(PBSAttendanceController.ARG_SEARCH_ATTENDANCE_REQUEST, attendance);
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
                Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.SEARCH_ATTENDANCE_EVENT, params[0], new Bundle(), null);
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);

                searchButton.setBackgroundColor(getResources().getColor(R.color.colorButtons));

                if (PandoraConstant.RESULT.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    attendances = (ObservableArrayList<MAttendanceSearchItem>)result.get(PBSAttendanceController.ARG_ATTENDANCESEARCHRES);
                    refreshAttendanceLines();
                } else {
                    PandoraHelper.showErrorMessage(getActivity(), result.getString(PandoraConstant.ERROR));
                }

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
}
