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
import android.widget.AdapterView;
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
import com.pbasolutions.android.model.ModelConst;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    Fragment newAttendanceFragment;

    Spinner employSpinner;
    Spinner daytypeSpinner;
    Spinner attTypeSpinner;
    Switch  switchWork;
    Spinner leavetypeSpinner;
    TextView textCheckinDate;
    TextView textCheckoutDate;
    TextView textComment;

    Button saveButton;

    View rootView;

    PBSAttendanceController attendCont;

    private ArrayAdapter employAdapter;
    private ArrayAdapter leaveTypeAdapter;
    private ArrayAdapter dayTypeAdapter;
    private ArrayAdapter attTypeAdapter;

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
            setValues();
            setUIListener();
            refreshUIState();
        } catch (Exception e) {
            Log.e("ttt", e.getMessage());
        }
        return rootView;
    }

    protected void setUI(View rootView) {
        employSpinner = (Spinner) rootView.findViewById(R.id.atEmplShiftSpinner);
        switchWork = (Switch) rootView.findViewById(R.id.atWorkSwitch);
        attTypeSpinner = (Spinner) rootView.findViewById(R.id.attAttTypeSpinner);
        leavetypeSpinner = (Spinner)rootView.findViewById(R.id.attLeaveTypeSpinner);
        daytypeSpinner = (Spinner)rootView.findViewById(R.id.attDayTypeSpinner);
        textCheckinDate = (TextView)rootView.findViewById(R.id.atCheckinDate);
        textCheckoutDate = (TextView)rootView.findViewById(R.id.atCheckoutDate);
        textComment = (TextView)rootView.findViewById(R.id.atComment);
        saveButton = (Button)rootView.findViewById(R.id.saveATLine);
        setDateTimeFromShift();
        setDateTimeToShift();
    }

    protected void setUIListener() {
        textCheckinDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deployDate = PBSAttendanceController.deployDate;
                Date dt =  PandoraHelper.stringToDate("dd-MM-yyyy", deployDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                PandoraHelper.promptDateTimePicker(textCheckinDate, cal, getActivity());
            }
        });

        textCheckoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deployDate = PBSAttendanceController.deployDate;
                Date dt =  PandoraHelper.stringToDate("dd-MM-yyyy", deployDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                PandoraHelper.promptDateTimePicker(textCheckoutDate, cal, getActivity());
            }
        });

        attTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPair newPair = (SpinnerPair) attTypeAdapter.getItem(position);
                PBSAttendanceController.attType = newPair.getKey();
                refreshUIState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        dayTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), daytypeSpinner, getDayTypeList());

        List<SpinnerPair> attTypeList = new ArrayList<>();
        String[] dayTypeKeys = {"", "A", "O", "R"};
        String[] dayTypeValues = {"Standard Work", "Absent", "Off Day/Work", "Rest Day/Work"};
        for(int i = 0; i < dayTypeKeys.length; i++) {
            SpinnerPair pair = new SpinnerPair();
            pair.setKey(dayTypeKeys[i]);
            pair.setValue(dayTypeValues[i]);
            attTypeList.add(pair);
        }
        attTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), attTypeSpinner, attTypeList);

    }

    void refreshUIState() {
        boolean isLeaveTypeHidden = leavetypeSpinner.getVisibility() != View.VISIBLE;
        boolean isDayTypeHidden = daytypeSpinner.getVisibility() != View.VISIBLE;
        View leavetypeRow = rootView.findViewById(R.id.atLeavetypeRow);
        View daytypeRow = rootView.findViewById(R.id.atDaytypeRow);
        View checkinRow = rootView.findViewById(R.id.atCheckinRow);
        View checkoutRow = rootView.findViewById(R.id.atCheckoutRow);
        View workSwitchRow = rootView.findViewById(R.id.atWorkSwitchRow);

        String attTypeKey = ((SpinnerPair) attTypeSpinner.getSelectedItem()).getKey();
        //absent
        if(attTypeKey.equalsIgnoreCase("A")) {
            PandoraHelper.setVisibleView(workSwitchRow, false);
            PandoraHelper.setVisibleView(leavetypeRow, true);
            PandoraHelper.setVisibleView(daytypeRow, true);
            PandoraHelper.setVisibleView(checkinRow, false);
            PandoraHelper.setVisibleView(checkoutRow, false);
        }
        //off day/work or rest day/work
        else if(attTypeKey.equalsIgnoreCase("O") || attTypeKey.equalsIgnoreCase("R")) {
            boolean isShow = false;
            if(switchWork.isChecked())
                isShow = true;

            PandoraHelper.setVisibleView(workSwitchRow, true);
            PandoraHelper.setVisibleView(leavetypeRow, false);
            PandoraHelper.setVisibleView(daytypeRow, false);
            PandoraHelper.setVisibleView(checkinRow, isShow);
            PandoraHelper.setVisibleView(checkoutRow, isShow);
        }
        //normal work
        else {
            PandoraHelper.setVisibleView(workSwitchRow, false);
            PandoraHelper.setVisibleView(leavetypeRow, false);
            PandoraHelper.setVisibleView(daytypeRow, false);
            PandoraHelper.setVisibleView(checkinRow, true);
            PandoraHelper.setVisibleView(checkoutRow, true);
        }

//        if (isLeaveTypeHidden && leavetypeSpinner.getVisibility() == View.VISIBLE)
//            leaveTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), leavetypeSpinner, getLeaveTypeList());
//        if (isDayTypeHidden && daytypeSpinner.getVisibility() == View.VISIBLE)
//            dayTypeAdapter = PandoraHelper.addListToSpinner(getActivity(), daytypeSpinner, getDayTypeList());
    }

    public Fragment getNewAttendanceFragment() {
        return newAttendanceFragment;
    }

    public void setNewAttendanceFragment(Fragment ewattendanceFragment) {
        this.newAttendanceFragment = newAttendanceFragment;
    }

    public void saveATLine(){
        //check all value is not null.
        SpinnerPair emplSpinner = (SpinnerPair) employSpinner.getSelectedItem();
        SpinnerPair leaveSpinner = (SpinnerPair) leavetypeSpinner.getSelectedItem();
        SpinnerPair daySpinner = (SpinnerPair) daytypeSpinner.getSelectedItem();
        String attTypeKey = ((SpinnerPair) attTypeSpinner.getSelectedItem()).getKey();

        boolean isEmpty = false;
        if (emplSpinner == null
                || emplSpinner.getValue().isEmpty()) isEmpty = true;

        boolean isAbsent = attTypeKey.equalsIgnoreCase("A");
        boolean isOff = attTypeKey.equalsIgnoreCase("O");
        boolean isRest = attTypeKey.equalsIgnoreCase("R");
        String checkinDate = textCheckinDate.getText().toString();
        String checkoutDate = textCheckoutDate.getText().toString();

        if (isAbsent) {
            isEmpty |= leaveSpinner == null || leaveSpinner.getValue().isEmpty();
            isEmpty |= daySpinner == null || daySpinner.getValue().isEmpty();
        } else {
            isEmpty |= checkinDate.isEmpty()
                    || checkoutDate.isEmpty();
        }


        if (isEmpty)
        {
            PandoraHelper.showWarningMessage(getActivity(), "Please fill up all fields");
            return;
        }

        Date checkin = null, checkout = null;
        SimpleDateFormat sdf = new SimpleDateFormat(PandoraHelper.SERVER_DATE_FORMAT4);
        if (!isAbsent && !((isOff || isRest) && !switchWork.isChecked())) {
            checkin = PandoraHelper.stringToDate(PandoraHelper.SERVER_DATE_FORMAT5, checkinDate);
            checkout = PandoraHelper.stringToDate(PandoraHelper.SERVER_DATE_FORMAT5, checkoutDate);

            if (!checkout.after(checkin)) {
                PandoraHelper.showWarningMessage(getActivity(), "Check Out Date should be later than Check In Date.");
                return;
            }
        }

        tempATLine = new MAttendanceLine();

        tempATLine.setC_BPartner_ID(emplSpinner.getKey());
        tempATLine.setIsAbsent(isAbsent ? "Y" : "N");
        tempATLine.setIsOff(isOff ? "Y" : "N");
        tempATLine.setIsRest(isRest ? "Y" : "N");
        if (isAbsent) {
                tempATLine.setHR_LeaveType_ID(leaveSpinner.getKey());
                tempATLine.setHR_LeaveType_Name(leaveSpinner.getValue());
                tempATLine.setHR_DaysType(daySpinner.getValue());
        } else {
            if (!((isOff || isRest) && !switchWork.isChecked())) {
                tempATLine.setCheckInDate(sdf.format(checkin));
                tempATLine.setCheckOutDate(sdf.format(checkout));
            }
        }
        tempATLine.setComments(textComment.getText().toString());

        //insertion values.
        ContentValues cv = new ContentValues();

        cv.put(MAttendanceLine.M_ATTENDANCELINE_UUID_COL, UUID.randomUUID().toString());
        cv.put(MAttendanceLine.C_BPARTNER_UUID_COL, emplSpinner.getKey());
        cv.put(MAttendanceLine.ISABSENT_COL, isAbsent ? "Y" : "N");
        cv.put(MAttendanceLine.ISOFF_COL, isOff ? "Y" : "N");
        cv.put(MAttendanceLine.ISREST_COL, isRest ? "Y" : "N");
        if (isAbsent) {
            cv.put(MAttendanceLine.HR_LEAVETYPE_ID_COL, Integer.parseInt(leaveSpinner.getKey()));
            cv.put(MAttendanceLine.HR_DAYS_COL, Double.parseDouble(daySpinner.getKey()));

            cv.put(MAttendanceLine.CHECKIN_COL, "");
            cv.put(MAttendanceLine.CHECKOUT_COL, "");
        } else {
            cv.put(MAttendanceLine.HR_LEAVETYPE_ID_COL, 0);
            cv.put(MAttendanceLine.HR_DAYS_COL, 0);

            if (!((isOff || isRest) && !switchWork.isChecked())) {
                cv.put(MAttendanceLine.CHECKIN_COL, sdf.format(checkin));
                cv.put(MAttendanceLine.CHECKOUT_COL, sdf.format(checkout));
            } else {
                cv.put(MAttendanceLine.CHECKIN_COL, "");
                cv.put(MAttendanceLine.CHECKOUT_COL, "");
            }
        }
        cv.put(ModelConst.C_PROJECTLOCATION_ID_COL, PBSAttendanceController.projectLocationId);
        cv.put(ModelConst.HR_SHIFT_UUID_COL, PBSAttendanceController.shiftUUID);
        cv.put(MAttendanceLine.COMMENT_COL, textComment.getText().toString());

        PandoraContext appContext = PandoraMain.instance.globalVariable;

        Bundle input = new Bundle();
        input.putParcelable(PBSAttendanceController.ARG_CONTENTVALUES, cv);
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
        input.putString(PBSAttendanceController.ARG_PROJECTLOCATION_ID, PBSAttendanceController.projectLocationId);

        Bundle result = attendCont.triggerEvent(PBSAttendanceController.GET_EMPLOYEES_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.EMPLOYEE_LIST);
    }

    public List<SpinnerPair> getLeaveTypeList() {
        Bundle input = new Bundle();
        Bundle result = attendCont.triggerEvent(PBSAttendanceController.GET_LEAVETYPES_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(PBSAttendanceController.LEAVETYPE_LIST);
    }

    public List<SpinnerPair> getDayTypeList(){
        List<SpinnerPair> daytype = new ArrayList<SpinnerPair>();
        daytype.add(new SpinnerPair("1", "Full Day "));
        daytype.add(new SpinnerPair("0.5", "Half Day "));
        return daytype;
    }

    public void setDateTimeFromShift(){

        String timefrom = ModelConst.mapUUIDtoColumn(ModelConst.HR_SHIFT_TABLE, ModelConst.HR_SHIFT_UUID_COL,
                PBSAttendanceController.shiftUUID , "timefrom" ,getActivity().getContentResolver());

        String getTimeFrom = "";

        //To split the String and concatenate all
        StringBuilder CharAddTimeFrom = new StringBuilder();
        char[] splitStringTimeFrom  = timefrom.toCharArray();
        for(int i=0;i<splitStringTimeFrom.length;i++){
            if(i>10 && i <16){
                CharAddTimeFrom.append(splitStringTimeFrom[i]);
            }
            getTimeFrom = CharAddTimeFrom.toString();
        }

        String timeFrom = PBSAttendanceController.deployDate + " " + getTimeFrom;
        textCheckinDate.setText(timeFrom);

    }

    public void setDateTimeToShift(){
        String timeto = ModelConst.mapUUIDtoColumn(ModelConst.HR_SHIFT_TABLE, ModelConst.HR_SHIFT_UUID_COL,
                PBSAttendanceController.shiftUUID , "timeto" ,getActivity().getContentResolver());
        String getTimeTo = "";

        //To split the String and concatenate all
        StringBuilder charAddTimeTo = new StringBuilder();
        char[] splitStringTimeTo = timeto.toCharArray();
        for(int i=0;i<splitStringTimeTo.length;i++){
            if(i>10 && i <16){
                charAddTimeTo.append(splitStringTimeTo[i]);
            }
            getTimeTo = charAddTimeTo.toString();
        }

        String timeTo = PBSAttendanceController.deployDate + " " + getTimeTo;
        textCheckoutDate.setText(timeTo);
    }


    public String getAtUUID() {
        return atUUID;
    }

    public void setAtUUID(String atUUID) {
        this.atUUID = atUUID;
    }
}
