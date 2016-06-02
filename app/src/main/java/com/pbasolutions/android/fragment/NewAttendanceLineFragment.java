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

import com.pbasolutions.android.PBSServerConst;
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
    Switch  switchNoShow;
    Spinner leavetypeSpinner;
    TextView textCheckinDate;
    TextView textCheckoutDate;
    TextView textComment;

    Button saveButton;

    View rootView;

    PBSAttendanceController attendCont;

    private ArrayAdapter employAdapter;
    private ArrayAdapter leaveTypeAdapter;

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
        switchNoShow = (Switch)rootView.findViewById(R.id.atNoShowSwitch);
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
        switchNoShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        leaveTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), leavetypeSpinner, getLeaveTypeList());
        refreshUIState();
    }

    void refreshUIState() {
        View leavetypeRow = rootView.findViewById(R.id.atLeavetypeRow);
        View checkinRow = rootView.findViewById(R.id.atCheckinRow);
        View checkoutRow = rootView.findViewById(R.id.atCheckoutRow);
        View noShowSwitchRow = rootView.findViewById(R.id.atNoShowSwitchRow);

        if (switchAbsent.isChecked()) {
            noShowSwitchRow.setVisibility(View.VISIBLE);
            if (switchNoShow.isChecked()) {
                leavetypeRow.setVisibility(View.GONE);
            } else {
                leavetypeRow.setVisibility(View.VISIBLE);
                leaveTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), leavetypeSpinner, getLeaveTypeList());
            }
            checkinRow.setVisibility(View.GONE);
            checkoutRow.setVisibility(View.GONE);
        } else {
            noShowSwitchRow.setVisibility(View.GONE);
            leavetypeRow.setVisibility(View.GONE);
            checkinRow.setVisibility(View.VISIBLE);
            checkoutRow.setVisibility(View.VISIBLE);
        }
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
        boolean isNoShow = switchNoShow.isChecked();
        if (isAbsent) {
            if (!isNoShow)
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

        tempATLine.setC_BPartner_ID(emplSpinner.getKey());
        tempATLine.setIsAbsent(switchAbsent.isChecked() ? "Y" : "N");
        tempATLine.setIsNoShow(switchNoShow.isChecked() ? "Y" : "N");
        if (isAbsent) {
            if (!isNoShow) {
                tempATLine.setHR_LeaveType_ID(leaveSpinner.getKey());
                tempATLine.setHR_LeaveType_Name(leaveSpinner.getValue());
            }
        } else {
            tempATLine.setCheckInDate(textCheckinDate.getText().toString());
            tempATLine.setCheckOutDate(textCheckoutDate.getText().toString());
        }
        tempATLine.setComments(textComment.getText().toString());

        //insertion values.
        ContentValues cv = new ContentValues();

        cv.put(MAttendanceLine.M_ATTENDANCELINE_UUID_COL, UUID.randomUUID().toString());
        cv.put(MAttendanceLine.C_BPARTNER_UUID_COL, emplSpinner.getKey());
        cv.put(MAttendanceLine.ISABSENT_COL, switchAbsent.isChecked() ? "Y" : "N");
        cv.put(MAttendanceLine.ISNOSHOW_COL, switchNoShow.isChecked() ? "Y" : "N");
        if (isAbsent) {
            if (isNoShow)
                cv.put(MAttendanceLine.HR_LEAVETYPE_ID_COL, 0);
            else
                cv.put(MAttendanceLine.HR_LEAVETYPE_ID_COL, Integer.parseInt(leaveSpinner.getKey()));

            cv.put(MAttendanceLine.CHECKIN_COL, "");
            cv.put(MAttendanceLine.CHECKOUT_COL, "");
        } else {
            cv.put(MAttendanceLine.HR_LEAVETYPE_ID_COL, 0);

            cv.put(MAttendanceLine.CHECKIN_COL, textCheckinDate.getText().toString());
            cv.put(MAttendanceLine.CHECKOUT_COL, textCheckoutDate.getText().toString());
        }
        cv.put(MAttendanceLine.COMMENT_COL, textComment.getText().toString());


        PandoraContext appContext = PandoraMain.instance.globalVariable;

        Bundle input = new Bundle();
        input.putParcelable(PBSAttendanceController.ARG_CONTENTVALUES, cv);
        input.putString(PBSAttendanceController.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
        Bundle output = attendCont.triggerEvent(PBSAttendanceController.SAVE_ATTENDANCELINE_EVENT, input, new Bundle(), null);

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
        Bundle input = new Bundle();
        Bundle result = attendCont.triggerEvent(PBSAttendanceController.GET_EMPLOYEES_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.EMPLOYEE_LIST);
    }

    public List<SpinnerPair> getLeaveTypeList() {
        Bundle input = new Bundle();
        Bundle result = attendCont.triggerEvent(PBSAttendanceController.GET_LEAVETYPES_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.LEAVETYPE_LIST);
    }



    public String getAtUUID() {
        return atUUID;
    }

    public void setAtUUID(String atUUID) {
        this.atUUID = atUUID;
    }
}
