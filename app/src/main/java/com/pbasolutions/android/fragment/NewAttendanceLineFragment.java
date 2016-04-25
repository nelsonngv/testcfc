package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.MPurchaseRequestLine;

import java.util.List;
import java.util.UUID;

/**
 * Created by pbadell on 11/27/15.
 */
public class NewAttendanceLineFragment extends Fragment {

    /**
     * Class tag name.
     */
    private static final String TAG = "NewAttendanceLineFragment";

    MAttendanceLine tempATLine;
    String atUUID;
    Fragment attendanceFragment;

    Spinner employSpinner;
    Switch  switchAbsent;
    Switch  switchShow;
    Spinner leavetypeSpinner;
    TextView textCheckinDate;
    TextView textCheckoutDate;
    TextView textComment;

    Button saveButton;

    View rootView;

    PBSAttendanceController attendCont;

    private ArrayAdapter employAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendCont = new PBSAttendanceController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.attendanceline_new, container, false);
        try {
            setUI(rootView);
            setUIListener();
            setValues();
        } catch (Exception e) {
            Log.e("ttt", e.getMessage());
        }
        return rootView;
    }

    protected void setUI(View rootView) {
        employSpinner = (Spinner) rootView.findViewById(R.id.atEmplShiftSpinner);
        switchAbsent = (Switch) rootView.findViewById(R.id.atAbsentSwitch);
        switchShow = (Switch)rootView.findViewById(R.id.atShowSwitch);
        leavetypeSpinner = (Spinner)rootView.findViewById(R.id.attLeaveTypeSpinner);
        textCheckinDate = (TextView)rootView.findViewById(R.id.atCheckinDate);
        textCheckoutDate = (TextView)rootView.findViewById(R.id.atCheckoutDate);
        textComment = (TextView)rootView.findViewById(R.id.atComment);

        saveButton = (Button)rootView.findViewById(R.id.saveATLine);
    }

    protected void setUIListener() {
        textCheckinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraHelper.promptDatePicker(textCheckinDate, getActivity());
            }
        });

        textCheckoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraHelper.promptDatePicker(textCheckoutDate, getActivity());
            }
        });

        switchAbsent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshUIState();
            }
        });
        switchShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshUIState();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveATLine();
            }
        });
    }

    protected void setValues() {
        employAdapter = PandoraHelper.addListToSpinner(getActivity(), employSpinner, getEmployeeList());

        refreshUIState();
    }

    void refreshUIState() {
        View leavetypeRow = rootView.findViewById(R.id.atLeavetypeRow);
        View checkinRow = rootView.findViewById(R.id.atCheckinRow);
        View checkoutRow = rootView.findViewById(R.id.atCheckoutRow);

        leavetypeRow.setVisibility(switchAbsent.isChecked() ? View.VISIBLE : View.GONE);
        checkinRow.setVisibility(switchAbsent.isChecked() ? View.GONE : View.VISIBLE);
        checkoutRow.setVisibility(switchAbsent.isChecked() ? View.GONE : View.VISIBLE);
    }

    public Fragment getAttendanceFragment() {
        return attendanceFragment;
    }

    public void setAttendanceFragment(Fragment attendanceFragment) {
        this.attendanceFragment = attendanceFragment;
    }

    public void saveATLine(){
        //check all value is not null.
        SpinnerPair emplSpinner = (SpinnerPair) employSpinner.getSelectedItem();
        SpinnerPair leaveSpinner = (SpinnerPair) leavetypeSpinner.getSelectedItem();

        boolean isEmpty = false;
        if (emplSpinner == null
                || emplSpinner.getValue().isEmpty()) isEmpty = true;

        boolean isAbsent = switchAbsent.isChecked();
        if (isAbsent) {
            isEmpty = leaveSpinner == null || leaveSpinner.getValue().isEmpty();
        } else {
            isEmpty = textCheckoutDate.getText().toString().isEmpty()
                    || textCheckoutDate.getText().toString().isEmpty();
        }

        if (isEmpty)
        {
            PandoraHelper.showWarningMessage(getActivity(), "Please fill up all fields");
            return;
        }
        tempATLine = new MAttendanceLine();

        tempATLine.setEmployeeId(emplSpinner.getKey());
        tempATLine.setIsAbsent(switchAbsent.isChecked());
        tempATLine.setIsPresent(switchShow.isChecked());
        if (isAbsent) {
            tempATLine.setLeaveType(Integer.parseInt(leaveSpinner.getValue()));
        } else {
            tempATLine.setCheckInDate(textCheckinDate.getText().toString());
            tempATLine.setCheckOutDate(textCheckoutDate.getText().toString());
        }
        tempATLine.setComment(textComment.getText().toString());

        //insertion values.
        ContentValues cv = new ContentValues();

        cv.put(MAttendanceLine.C_BPARTNER_ID, emplSpinner.getValue());
        cv.put(MAttendanceLine.ISABSENT, switchAbsent.isChecked());
        cv.put(MAttendanceLine.ISNOSHOW, switchShow.isChecked());
        if (isAbsent) {
            cv.put(MAttendanceLine.HR_LEAVETYPE_ID, Integer.parseInt(leaveSpinner.getValue()));
        } else {
            cv.put(MAttendanceLine.CHECKIN, textCheckinDate.getText().toString());
            cv.put(MAttendanceLine.CHECKOUT, textCheckoutDate.getText().toString());
        }
        cv.put(MAttendanceLine.COMMENTS, textComment.getText().toString());

        Bundle input = new Bundle();
        input.putParcelable(PBSAttendanceController.ARG_CONTENTVALUES, cv);
        Bundle output = attendCont.triggerEvent(PBSAttendanceController.INSERT_REQLINE_EVENT, input, new Bundle(), null);
        if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {

            PandoraMain.instance.getSupportFragmentManager().popBackStack();
//            Fragment fragment = new NewRequisitionFragment();
//         //   if (get_UUID() != null){
//                ((NewRequisitionFragment) fragment).set_UUID(getPrUUID());
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragment.setRetainInstance(true);
//                    fragmentTransaction.replace(R.id.container_body, fragment);
//                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                    fragmentTransaction.commit();
//                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newrequisitionline));
//                }
//           // }

        } else {
            PandoraHelper.showMessage(getActivity(), output.getString(output.getString(PandoraConstant.TITLE)));
        }
    }

    public List<SpinnerPair> getEmployeeList() {
        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;

        Bundle input = new Bundle();
        Bundle result = attendCont.triggerEvent(PBSAttendanceController.GET_EMPLOYEES_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.EMPLOYEE_LIST);
    }



    public String getAtUUID() {
        return atUUID;
    }

    public void setAtUUID(String atUUID) {
        this.atUUID = atUUID;
    }
}
