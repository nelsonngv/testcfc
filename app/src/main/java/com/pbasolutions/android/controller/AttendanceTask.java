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
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.MAttendanceLog;
import com.pbasolutions.android.model.MAttendanceSearchItem;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MShift;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.UUID;
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

    String atlProjection[] = {
            MAttendanceLine.M_ATTENDANCELINE_UUID_COL,
            MAttendanceLine.C_BPARTNER_UUID_COL,
            MAttendanceLine.ISABSENT_COL,
            MAttendanceLine.ISOFF_COL,
            MAttendanceLine.ISREST_COL,
            MAttendanceLine.HR_LEAVETYPE_ID_COL,
            MAttendanceLine.COMMENT_COL,

            MAttendanceLine.CHECKIN_COL,
            MAttendanceLine.CHECKOUT_COL,

            MAttendanceLine.HR_DAYS_COL,
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
            case PBSAttendanceController.SEARCH_ATTENDANCE_EVENT: {
                return searchAttendances();
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
            case PBSAttendanceController.GET_PROJECTLOCATIONS_EVENT: {
                return getProjectLocations();
            }
            case PBSAttendanceController.GET_PROJECTLOCATION_BY_TAG_EVENT: {
                return getProjectLocationByTag();
            }
            case PBSAttendanceController.CREATE_ATTENDANCETRACKING_EVENT: {
                return createAttendanceTracking();
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
        }
        if (prlCursor != null)
            prlCursor.close();

        output.putSerializable(PBSAttendanceController.ARG_ATTENDANCES, atlList);

        return output;
    }

    private Bundle searchAttendances() {
        MAttendance attendance = (MAttendance) input.get(PBSAttendanceController.ARG_SEARCH_ATTENDANCE_REQUEST);

        JsonObject object = new JsonObject();
        object.addProperty(MAttendance.C_PROJECTLOCATION_ID_COL, attendance.getC_ProjectLocation_ID());
        object.addProperty(MAttendance.DEPLOYMENT_DATE_FROM_COL, attendance.getDeploymentDateFrom());
        object.addProperty(MAttendance.DEPLOYMENT_DATE_TO_COL, attendance.getDeploymentDateTo());
        object.addProperty(MAttendance.HR_SHIFT_ID_COL, attendance.getHR_Shift_ID());

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String json = serverAPI.searchAttendances(
                object, input.getString(PBSServerConst.PARAM_URL)
        );

//        String json = "{\"ResourceAllocations\":[{\"Status\":\"Checked Out\",\"HR_LeaveType_ID\":0,\"CheckOut\":\"2016-07-10T20:00:00.000+0800\",\"C_BPartner_ID\":1003932,\"CheckIn\":\"2016-07-10T18:00:00.000+0800\",\"HR_ResourceAllocation_ID\":1006065},{\"Status\":\"Checked Out\",\"HR_LeaveType_ID\":0,\"CheckOut\":\"2016-07-10T20:00:00.000+0800\",\"C_BPartner_ID\":1003529,\"CheckIn\":\"2016-07-10T18:00:00.000+0800\",\"HR_ResourceAllocation_ID\":1006066}],\"Success\":\"TRUE\"}";

        if (json != null) {
            Pair pair = PandoraHelper.parseJsonWithArraytoPair(json, "Success", "ResourceAllocations", MAttendanceSearchItem[].class.getName());
            String success = (String) pair.first;
            if (success != null && success.equalsIgnoreCase(PandoraConstant.TRUE)) {
                MAttendanceSearchItem attendancesSearchRes[] = (MAttendanceSearchItem[]) pair.second;

                //convert from array to list
                ObservableArrayList<MAttendanceSearchItem> list = new ObservableArrayList<>();
                if (attendancesSearchRes != null) {
                    for (int x = 0; x < attendancesSearchRes.length; x++) {
                        MAttendanceSearchItem item = attendancesSearchRes[x];
                        item.setC_BPartner_Name(getEmployeeName(item.getC_BPartner_ID()));
                        item.setHR_LeaveType_Name(getHRLeaveTypeName(item.getHR_LeaveType_ID()));
                        item.setCheckIn(PandoraHelper.parseToDisplaySDate(item.getCheckIn(), "yyyy-MM-dd HH:mm", null));
                        item.setCheckOut(PandoraHelper.parseToDisplaySDate(item.getCheckOut(), "yyyy-MM-dd HH:mm", null));
                        item.setDeploymentDate(PandoraHelper.parseToDisplaySDate(item.getDeploymentDate(), "yyyy-MM-dd", null));
                        item.setHR_Shift_Name(getHRShiftName(item.getHR_Shift_ID()));
                        item.setHR_DaysType(getHRDaysName(item.getHR_DaysType()));
                        item.setComments(item.getComments());
                        list.add(item);
                    }
                }

                output.putSerializable(PBSAttendanceController.ARG_ATTENDANCESEARCHRES, list);
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to load attendances");
            }
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to load attendances");
        }
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
            } else if (MAttendanceLine.ISOFF_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setIsOff(rowValue);
            } else if (MAttendanceLine.ISREST_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setIsRest(rowValue);
            } else if (MAttendanceLine.HR_LEAVETYPE_ID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setHR_LeaveType_ID(rowValue);

                String leaveTypeName = ModelConst.mapIDtoColumn(ModelConst.HR_LEAVETYPE_TABLE,
                        ModelConst.NAME_COL, rowValue,
                        ModelConst.HR_LEAVETYPE_ID_COL, cr);
                prl.setHR_LeaveType_Name(leaveTypeName);
            } else if (MAttendanceLine.HR_DAYS_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setHR_DaysType(rowValue);
            } else if (MAttendanceLine.COMMENT_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setComments(rowValue);
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
                ModelConst.HR_SHIFT_ID_COL, cr);
    }

    private String getHRDaysName(String hr_days)
    {
        if(hr_days.equalsIgnoreCase("1"))
            return "Full Day";
        else
            return "Half Day";
    }

    private String getHRLeaveTypeName(int hr_leavetype_id) {
        return ModelConst.mapIDtoColumn(ModelConst.HR_LEAVETYPE_TABLE,
                ModelConst.NAME_COL, String.valueOf(hr_leavetype_id),
                ModelConst.HR_LEAVETYPE_ID_COL, cr);
    }

    private String getProjectLocationName(int c_projectLocation_id) {
        return ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.NAME_COL, String.valueOf(c_projectLocation_id),
                ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private String getEmployeesName(int C_BPartner_IDs[]){
        StringBuilder names = new StringBuilder();
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

    private String getEmployeeName(int C_BPartner_ID){
        return ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                ModelConst.NAME_COL, String.valueOf(C_BPartner_ID),ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private Bundle getEmployees() {
        String cbpartner = ModelConst.C_BPARTNER_VIEW + ".";

        String projLocationId = input.getString(PBSAttendanceController.ARG_PROJECTLOCATION_ID);
        String projLocationUUID = ModelConst.getProjLocationUUID(projLocationId, cr);

        String[] projection = {cbpartner + MAttendanceLine.C_BPARTNER_UUID_COL,
                cbpartner + ModelConst.NAME_COL};

        String[] selectionArg = { projLocationUUID, PBSAttendanceController.shiftUUID };

        String wherePhase = String.format("%s NOT IN (SELECT %s FROM %s) AND %s=? AND %s=?",
                cbpartner + MAttendanceLine.C_BPARTNER_UUID_COL,
                MAttendanceLine.C_BPARTNER_UUID_COL,
                ModelConst.M_ATTENDANCELINE_TABLE,
                ModelConst.HR_PROJECTASSIGNMENT_TABLE + "." + ModelConst.C_PROJECTLOCATION_UUID_COL,
                ModelConst.HR_PROJECTASSIGNMENT_TABLE + "." + ModelConst.HR_SHIFT_UUID_COL
                );
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TABLE),
                projection, wherePhase, selectionArg, "LOWER(" + cbpartner + ModelConst.NAME_COL + ") ASC");
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
        }
        if (cursor != null)
            cursor.close();
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
        if (cursor != null)
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
        } catch (RemoteException | OperationApplicationException e) {
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

        Bundle dbout = new Bundle();

        if (resultAtt != null && PandoraConstant.TRUE.equalsIgnoreCase(resultAtt.getSuccess())) {
            //update the data document no and id
            ContentValues cv = new ContentValues();
            cv.put(MAttendance.M_ATTENDANCE_UUID_COL, resultAtt.getM_Attendance_UUID());

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();
            //update lines
            ContentValues rlcv = new ContentValues();
            MAttendanceLine originalLines[] = pr.getLines();

            for (int i = 0; i < originalLines.length; i++) {
                String selection = MAttendanceLine.M_ATTENDANCELINE_UUID_COL + "=?";
                String lineSelectionArgs[] = {originalLines[i].get_UUID()};
                rlcv.put(MAttendanceLine.M_ATTENDANCELINE_UUID_COL, originalLines[i].get_UUID());
                ops.add(ContentProviderOperation
                        .newDelete(ModelConst.uriCustomBuilder(MAttendanceLine.TABLENAME))
                        .withSelection(selection, lineSelectionArgs)
                        .build());
            }
            dbout = PandoraHelper.providerApplyBatch(dbout, cr, ops, "create attendance");
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully sent request");

        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
//            output.putString(PandoraConstant.ERROR, "Fail to request attendance");
            //this is for displaying error msg received directly from api
            output.putString(PandoraConstant.ERROR, resultAtt.getErrorMessage());

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
                null, "LOWER(" + ModelConst.NAME_COL + ") ASC");

        //get the projectLocations list.
        ArrayList<SpinnerPair> projectLocations = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                projectLocations.add(getProjectLocation(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
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

    private Bundle getProjectLocationByTag() {
        String nfcTagID = (String) input.get(PBSAttendanceController.ARG_NFCTAG);

        String[] projection = {ModelConst.C_PROJECTLOCATION_ID_COL,
                ModelConst.NAME_COL, ModelConst.ISKIOSKMODE_COL, ModelConst.ISPHOTO_COL};
        String[] selectionArg = {nfcTagID};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE),
                projection, ModelConst.NFCTAG_COL + "=?", selectionArg, "LOWER(" + ModelConst.NAME_COL + ") ASC");

        ArrayList<String> projectLocation = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    projectLocation.add(cursor.getString(x));
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        output.putStringArrayList(PBSTaskController.ARG_PROJECTLOCATIONS, projectLocation);
        return output;
    }

    private Bundle createAttendanceTracking() {
        PandoraContext appContext = PandoraMain.instance.getGlobalVariable();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentValues cv = input.getParcelable(PBSAttendanceController.ARG_CONTENTVALUES);

        String ad_org_uuid = appContext.getAd_org_uuid();
        if (ad_org_uuid.isEmpty()) {
            ad_org_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE,
                    ModelConst.AD_ORG_UUID_COL, appContext.getAd_org_id(),
                    ModelConst.AD_ORG_TABLE + ModelConst._ID, appContext.getContentResolver());
            appContext.setAd_org_uuid(ad_org_uuid);
        }

        String ad_client_uuid = appContext.getAd_client_uuid();
        if (ad_client_uuid.isEmpty()) {
            ad_client_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_CLIENT_TABLE,
                    ModelConst.AD_CLIENT_UUID_COL, appContext.getAd_client_id(),
                    ModelConst.AD_CLIENT_TABLE + ModelConst._ID, appContext.getContentResolver());
            appContext.setAd_client_uuid(ad_client_uuid);
        }

        String ad_user_uuid = appContext.getAd_user_uuid();
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_UUID_COL, appContext.getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, appContext.getContentResolver());
            appContext.setAd_user_uuid(ad_user_uuid);
        }

        cv.put(ModelConst.AD_ORG_UUID_COL, ad_org_uuid);
        cv.put(ModelConst.AD_CLIENT_UUID_COL, ad_client_uuid);
        cv.put(MAttendanceLog.HR_ENTRYLOG_UUID_COL, UUID.randomUUID().toString());
        cv.put(ModelConst.CREATEDBY_COL, ad_user_uuid);
        cv.put(ModelConst.UPDATEDBY_COL, ad_user_uuid);

        ops.add(ContentProviderOperation
                .newInsert(ModelConst.uriCustomBuilder(MAttendanceLog.TABLENAME))
                .withValues(cv)
                .build());

        try {
            ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
            for(ContentProviderResult result : results) {
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
                    output.putString(PandoraConstant.ERROR, "Fail to create attendance.");
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully created attendance");
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
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
