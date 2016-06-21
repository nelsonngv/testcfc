package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MEmployee;
import com.pbasolutions.android.model.MShift;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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

    String atlProjection[] = {
            MAttendanceLine.M_ATTENDANCELINE_UUID_COL,
            MAttendanceLine.C_BPARTNER_UUID_COL,
            MAttendanceLine.ISABSENT_COL,
            MAttendanceLine.HR_LEAVETYPE_ID_COL,
            MAttendanceLine.COMMENT_COL,

            MAttendanceLine.ISNOSHOW_COL,
            MAttendanceLine.CHECKIN_COL,
            MAttendanceLine.CHECKOUT_COL,
    };

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
                return getAttendanceLines();
            }
            case PBSAttendanceController.GET_SHIFTS_EVENT: {
                return getHRShift();
            }
            case PBSAttendanceController.GET_EMPLOYEES_EVENT: {
                return getEmployees();
            }
            case PBSAttendanceController.GET_LEAVETYPES_EVENT: {
                return getLeaveTypes();
            }
            case PBSAttendanceController.SAVE_ATTENDANCELINE_EVENT: {
                return saveAttendanceLine();
            }
            case PBSAttendanceController.REMOVE_ATTDLINES_EVENT: {
                return removeLines();
            }
            case PBSAttendanceController.CREATE_ATTENDANCE_EVENT: {
                return createAttendance();
            }
            case PBSAttendanceController.GET_PROJECTLOCATIONS_EVENT:{
                return getProjectLocations();
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

    private Bundle getAttendanceLines() {
        ObservableArrayList<MAttendanceLine> atlList = new ObservableArrayList();
        //populate the attendanceLine.
        String[] selectionArg = {input.getString(PBSAttendanceController.ARG_PROJECTLOCATION_ID)};
        Cursor prlCursor = cr.query(ModelConst.uriCustomBuilder(MAttendanceLine.TABLENAME),
                atlProjection, ModelConst.C_PROJECTLOCATION_ID_COL + "=?", selectionArg, null);
        if (prlCursor != null && prlCursor.getCount() > 0) {

            prlCursor.moveToFirst();

            do {
                atlList.add(populateAttendanceLine(prlCursor));
            } while (prlCursor.moveToNext());

            prlCursor.close();
        }

        output.putSerializable(PBSAttendanceController.ARG_ATTENDANCES, atlList);

        return output;
    }

    private MAttendanceLine populateAttendanceLine(Cursor cursor) {
        MAttendanceLine prl = new MAttendanceLine();

        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);

            if (MAttendanceLine.M_ATTENDANCELINE_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.set_UUID(rowValue);
            } else if (MAttendanceLine.C_BPARTNER_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                String partnerId = ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                        ModelConst.C_BPARTNER_ID_COL, rowValue,
                        ModelConst.C_BPARTNER_UUID_COL, cr);

                String partnerName = ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                        ModelConst.NAME_COL, rowValue,
                        ModelConst.C_BPARTNER_UUID_COL, cr);

                prl.setC_BPartner_ID(partnerId);
                prl.setC_BPartner_Name(partnerName);
            } else if (MAttendanceLine.ISABSENT_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setIsAbsent(rowValue);
            } else if (MAttendanceLine.HR_LEAVETYPE_ID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setHR_LeaveType_ID(rowValue);

                String leaveTypeName = ModelConst.mapIDtoColumn(ModelConst.HR_LEAVETYPE_TABLE,
                        ModelConst.NAME_COL, rowValue,
                        ModelConst.HR_LEAVETYPE_ID_COL, cr);
                prl.setHR_LeaveType_Name(leaveTypeName);
            } else if (MAttendanceLine.COMMENT_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setComments(rowValue);
            } else if (MAttendanceLine.ISNOSHOW_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setIsNoShow(rowValue);
            } else if (MAttendanceLine.CHECKIN_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setCheckInDate(rowValue);
            } else if (MAttendanceLine.CHECKOUT_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setCheckOutDate(rowValue);
            }
        }
        return prl;
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
        String[] projection = {MAttendanceLine.C_BPARTNER_UUID_COL, ModelConst.NAME_COL, ModelConst.IDNUMBER_COL, ModelConst.PHONE_COL, MEmployee.JOB_TITLE_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_BPARTNER_VIEW),
                projection, null, null, null);
        ObservableArrayList<SpinnerPair> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (MAttendanceLine.C_BPARTNER_UUID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
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
        String[] projection = {ModelConst.HR_SHIFT_UUID_COL, ModelConst.NAME_COL};
        String projLocationUUID = ModelConst.getProjLocationUUID(input.getString(PBSAttendanceController.ARG_PROJECTLOCATION_ID), cr);
        String[] selectionArg = {projLocationUUID};
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
                    } else if (ModelConst.HR_SHIFT_UUID_COL
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

    private Bundle getLeaveTypes() {
        String[] projection = {ModelConst.HR_LEAVETYPE_ID_COL, ModelConst.NAME_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.HR_LEAVETYPE_TABLE),
                projection, null, null, null);
        ObservableArrayList<SpinnerPair> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.HR_LEAVETYPE_ID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
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
        output.putSerializable(PBSAttendanceController.LEAVETYPE_LIST, employeeList);
        return output;
    }

    private Bundle saveAttendanceLine() {
        ContentValues cv = input.getParcelable(PBSController.ARG_CONTENTVALUES);
        return ModelConst.insertData(cv, cr, MAttendanceLine.TABLENAME, output);
    }

    private Bundle removeLines() {
        ObservableArrayList<MAttendanceLine> prlList = (ObservableArrayList)
                input.getSerializable(PBSAttendanceController.ARG_ATTENDANCELINE_LIST);

        ArrayList<String> uuidList = new ArrayList();
        for (MAttendanceLine prlLine : prlList) {
            if (prlLine.isSelected()) {
                uuidList.add(prlLine.get_UUID());
            }
        }
        String selection = MAttendanceLine.M_ATTENDANCELINE_UUID_COL + "=?";
        String uuidArray[] = uuidList.toArray(new String[uuidList.size()]);
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();
        for (int x=0; x<uuidList.size(); x++) {
            String[] selectionArgs = {uuidArray[x]} ;
            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MAttendanceLine.TABLENAME))
                    .withSelection(selection, selectionArgs)
                    .build());
        }

        try {
            ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
            for (ContentProviderResult result : results) {
                boolean resultFlag = false;
                if (result.uri != null) {
                    resultFlag = ModelConst.getURIResult(result.uri);
                } else {
                    if (result.count != null && result.count != 0) {
                        resultFlag = true;
                    }
                }

                if (!resultFlag) {
                    output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                    output.putString(PandoraConstant.ERROR, "Fail to delete selected note(s).");
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully synced notes");
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    private Bundle createAttendance() {
        PBSIServerAPI serverAPI = new PBSServerAPI();

        MAttendance pr = (MAttendance) input.getSerializable(PBSAttendanceController.ARG_ATTENDANCE_REQUEST);
        pr.setDeploymentDate(PandoraHelper.parseToDisplaySDate(pr.getDeploymentDate(), "yyyy-MM-dd hh:mm", null));
        MAttendance resultAtt = serverAPI.createAttendance
                (
                        pr,
                        input.getString(PBSServerConst.PARAM_URL)
                );

        String selection = MAttendance.M_ATTENDANCE_UUID_COL + "=?";
        String selectionArgs[] = {pr.getM_Attendance_UUID()};

        Bundle dbout = new Bundle();

        if (PandoraConstant.TRUE.equalsIgnoreCase(resultAtt.getSuccess())) {
            //update the data document no and id
            ContentValues cv = new ContentValues();
            cv.put(MAttendance.M_ATTENDANCE_UUID_COL, resultAtt.getM_Attendance_UUID());

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();


            ops.add(ContentProviderOperation
                    .newUpdate(ModelConst.uriCustomBuilder(MAttendance.TABLENAME))
                    .withValues(cv)
                    .withSelection(selection, selectionArgs)
                    .build());

            //update lines
            ContentValues rlcv = new ContentValues();
            MAttendanceLine lines[] = resultAtt.getLines();
            MAttendanceLine originalLines[] = pr.getLines();

            for (int i = 0; i < lines.length; i++) {
                selection = MAttendanceLine.M_ATTENDANCELINE_UUID_COL + "=?";
                String lineSelectionArgs[] = {originalLines[i].get_UUID()};
                rlcv.put(MAttendanceLine.M_ATTENDANCELINE_UUID_COL, lines[i].get_UUID());
                ops.add(ContentProviderOperation
                        .newUpdate(ModelConst.uriCustomBuilder(MAttendanceLine.TABLENAME))
                        .withValues(rlcv)
                        .withSelection(selection, lineSelectionArgs)
                        .build());
            }
            dbout = PandoraHelper.providerApplyBatch(dbout, cr, ops, "create attendance");
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to request attendance");

            //delete the data
            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();

            //delete line tables first. due to dependency
            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MAttendanceLine.TABLENAME))
                    .build());

            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MAttendance.TABLENAME))
                    .build());

            dbout = PandoraHelper.providerApplyBatch(dbout, cr, ops, "delete attendance.");
        }
        return output;
    }

    private Bundle getProjectLocations() {
        String projection[] = {ModelConst.C_PROJECTLOCATION_ID_COL,
                ModelConst.NAME_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE),
                projection, null,
                null, null);

        //get the projectLocations list.
        ArrayList<SpinnerPair> projectLocations = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                projectLocations.add(getProjectLocation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putParcelableArrayList(PBSTaskController.ARG_PROJECTLOCATIONS, projectLocations);
        return output;
    }

    private SpinnerPair getProjectLocation(Cursor cursor) {
        SpinnerPair pair = new SpinnerPair();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (ModelConst.C_PROJECTLOCATION_ID_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setKey(rowValue);
            } else if (ModelConst.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setValue(rowValue);
            }
        }
        return pair;
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
