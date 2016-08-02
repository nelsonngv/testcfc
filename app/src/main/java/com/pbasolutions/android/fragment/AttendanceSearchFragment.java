package com.pbasolutions.android.fragment;


import android.content.ContentResolver;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AttendanceLineRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.model.MAttendanceLine;

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
    TextView deployDateView;
    protected Spinner shiftSpinner;
    protected ArrayAdapter shiftAdapter;

    protected Spinner projLocationSpinner;
    private ArrayAdapter projLocNameAdapter;

    RecyclerView recyclerView;
    AttendanceLineRVA linesAdapter;
    Button requestButton;

    private ObservableArrayList<MAttendanceLine> attendanceLines;

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

        setUI(rootView);
        setUIListener();

        attendanceLines = new ObservableArrayList<MAttendanceLine>();
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
        linesAdapter = new AttendanceLineRVA(getActivity(), attendanceLines);
        recyclerView.setAdapter(linesAdapter);
    }

    void setUI(View rootView) {
        shiftSpinner = (Spinner) rootView.findViewById(R.id.attsrchShiftSpinner);

        projLocationSpinner = (Spinner) rootView.findViewById(R.id.attsrchProjLocation);
        projLocationSpinner.setEnabled(false);

        deployDateView = (TextView) rootView.findViewById(R.id.attsrchDeployDate);

        Date date= new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        deployDateView.setText(sdf.format(date));

        requestButton = (Button) rootView.findViewById(R.id.atsrchSearch);
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

        requestButton.setOnClickListener(new View.OnClickListener() {
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
    /**
     * Request Attendance will make call to Server API for processing the requisition/purchase request.
     */
    protected void onSearchAttendance() {
    }

    protected void shiftChanged(String shiftUUID) {
        Log.d("ttt", "Shift Changed:" + shiftUUID);
    }

    protected void dateChanged(String deployDate) {
        Log.d("ttt", "Date Changed:" + deployDate);
    }
}
