package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MEmployee;
import com.pbasolutions.android.model.MShift;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by pbadell on 12/15/15.
 */
public class AttendanceTask implements Callable<Bundle> {

    private static final String TAG = "AttendanceTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;
    private boolean isSelected = false;

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event) {
            case PBSAttendanceController.GET_ATTENDANCES_EVENT: {
                return getDeploys();
            }
            case PBSAttendanceController.GET_SHIFTS_EVENT: {
                return getHRShift();
            }
            case PBSAttendanceController.GET_EMPLOYEES_EVENT: {
                return getEmployees();
            }

            default:
                return null;
        }
    }

    private Bundle getDeploys() {
        int project_location_id =  input.getInt(PBSAttendanceController.ARG_PROJECTLOCATION_ID);
        String deploymentDate = input.getString(PBSAttendanceController.ARG_DEPLOYMENTDATE);

        JsonObject object = new JsonObject();
        object.addProperty(ModelConst.C_PROJECTLOCATION_ID_COL, project_location_id);
        object.addProperty(MDeploy.DEPLOYMENT_DATE_COL, deploymentDate);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String json = serverAPI.getDeployments(
                object, input.getString(PBSServerConst.PARAM_URL)
        );

        Pair pair = PandoraHelper.parseJsonWithArraytoPair(json, "Success", "Deployments", MDeploy[].class.getName());
        MDeploy deploy[] = (MDeploy[])pair.second;

        //convert from array to list
        ObservableArrayList<MDeploy> list = new ObservableArrayList();
        for (int x=0; x<deploy.length; x++) {
            deploy[x].setEmployeesName(getEmployeesName(deploy[x].getC_BPartner_IDs()));
            deploy[x].setProjectLocationName(getProjectLocationName(
                    deploy[x].getC_ProjectLocation_ID()));

            //get shift model.
            MShift shift = new MShift(deploy[x].getHR_Shift_ID());
            shift = shift.getShift(cr, false);

            deploy[x].setHRShiftName(shift.getName());
            deploy[x].setHRShiftTimeFrom(shift.getTimeFrom().toString());
            deploy[x].setHRShiftTimeTo(shift.getTimeTo().toString());

            list.add(deploy[x]);
        }
        output.putSerializable(PBSAttendanceController.ARG_ATTENDANCES,  list);
        return output;
    }

    private String getHRShiftName(int hr_shift_id) {
        return ModelConst.mapIDtoColumn(ModelConst.HR_SHIFT_TABLE,
                ModelConst.NAME_COL, String.valueOf(hr_shift_id),
                ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private String getProjectLocationName(int c_projectLocation_id) {
        return ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.NAME_COL, String.valueOf(c_projectLocation_id),
                ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private String getEmployeesName(int C_BPartner_IDs[]){
        StringBuffer names = new StringBuffer();
        for (int x=0; x<C_BPartner_IDs.length; x++){
            String name =ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                    ModelConst.NAME_COL, String.valueOf(C_BPartner_IDs[x]),ModelConst.C_BPARTNER_ID_COL, cr);
            if (x != C_BPartner_IDs.length-1){
                names.append(name);
                names.append(',');
            } else {
                names.append(name);
            }

        }
        return names.toString();
    }

    private Bundle getEmployees() {
        String[] projection = {MEmployee.C_BPARTNER_UUID_COL, ModelConst.NAME_COL, ModelConst.IDNUMBER_COL, ModelConst.PHONE_COL, MEmployee.JOB_TITLE_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_BPARTNER_VIEW),
                projection, null, null, null);
        ObservableArrayList<SpinnerPair> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (MEmployee.C_BPARTNER_UUID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    } else if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    }
                }
                employeeList.add(pair);
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSAttendanceController.EMPLOYEE_LIST, employeeList);
        return output;
    }

    private Bundle getHRShift(){
        String[] projection = {ModelConst.HR_PROJLOCATION_SHIFT_UUID_COL, ModelConst.NAME_COL};
        String[] selectionArg = {input.getString(PBSAttendanceController.ARG_PROJECTLOCATION_UUID)};
        //grab the shift names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.JOBAPP_SHIFTS_VIEW),
                projection, ModelConst.C_PROJECTLOCATION_UUID_COL + "=?", selectionArg, null);

        ArrayList<SpinnerPair> shiftList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (ModelConst.HR_PROJLOCATION_SHIFT_UUID_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                shiftList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putParcelableArrayList(PBSRecruitController.SHIFT_LIST, shiftList);
        return output;
    }


    public AttendanceTask(ContentResolver cr) {
        this.cr = cr;
    }


    public Bundle getInput() {
        return input;
    }

    public void setInput(Bundle input) {
        this.input = input;
        event = input.getString(PBSIController.ARG_TASK_EVENT);
    }

    public Bundle getOutput() {
        return output;
    }

    public void setOutput(Bundle output) {
        this.output = output;
    }
}
