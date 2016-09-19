package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.graphics.AvoidXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.json.PBSProjTaskJSON;
import com.pbasolutions.android.json.PBSProjTasksJSON;
import com.pbasolutions.android.model.MEmployee;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by pbadell on 11/11/15.
 */
public class ProjectTask implements Callable<Bundle> {

    private static final String TAG = "ProjectTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;
    public ProjectTask(Bundle input, Bundle result, ContentResolver cr) {
        this.input = input;
        this.output = result;
        this.cr = cr;
        event = input.getString(PBSIController.ARG_TASK_EVENT);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event){
            case PBSTaskController.GET_PROJTASKS_EVENT: {
                return getProjectTasks();
            }
            case PBSTaskController.GET_PROJTASK_EVENT: {
                return getProjectTask();
            }
            case PBSTaskController.SYNC_PROJTASKS_EVENT: {
                return syncProjectTasks();
            }
            case PBSTaskController.COMPLETE_PROJTASK_EVENT: {
                return completeProjectTask();
            }
            case PBSTaskController.CREATE_TASK_EVENT: {
                return createTask();
            }
            case PBSTaskController.GET_PROJECTLOCATIONS_EVENT:{
                return getProjectLocations();
            }
            case PBSTaskController.GET_USERS_EVENT:{
                return getUsers();
            }
            default:return null;
        }
    }

    private Bundle getUsers() {
        String cbpartner = ModelConst.C_BPARTNER_VIEW + ".";

        String projLocationUUID = input.getString(PBSTaskController.ARG_PROJLOC_UUID);
        String[] selectionArg = {projLocationUUID};
        String[] projection = {cbpartner + MEmployee.C_BPARTNER_UUID_COL,
                cbpartner + ModelConst.NAME_COL};

        String wherePhase = String.format("%s=? ",
                ModelConst.HR_PROJECTASSIGNMENT_TABLE + "." + ModelConst.C_PROJECTLOCATION_UUID_COL
        );
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TABLE),
                projection, wherePhase, selectionArg, "LOWER(" + cbpartner + ModelConst.NAME_COL + ") ASC");
        ObservableArrayList<SpinnerPair> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (MEmployee.C_BPARTNER_UUID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
                        Log.i(TAG, "getUsers: "+cursor.getString(x));
                        String getid = ModelConst.mapUUIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                                ModelConst.C_BPARTNER_UUID_COL, cursor.getString(x), ModelConst.C_BPARTNER_ID_COL, cr);
                        pair.setKey(getid);
                        Log.i(TAG, "getUsers: "+getid);
                    } else if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    }
                }
                employeeList.add(pair);
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSTaskController.ARG_USERS, employeeList);
        return output;
    }

    private SpinnerPair getUser(Cursor cursor) {
        SpinnerPair pair = new SpinnerPair();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (ModelConst.AD_USER_ID_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setKey(rowValue);
            } else if (ModelConst.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                String name = PandoraHelper.parseEscapedChar(rowValue);
                if (name != null) {
                    pair.setValue(name);
                } else {
                    pair.setValue(rowValue);
                }

            }
        }
        return pair;
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

    private Bundle createTask() {
        MProjectTask task = (MProjectTask)input.getSerializable(PBSTaskController.ARG_PROJTASK);
        ContentValues cv = getContentValuesFromTask(task);
//        boolean result = ModelConst.getURIResult(uri);
//
//        if (result) {
//            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
//            output.putString(PandoraConstant.RESULT, "Successfuly insert new task.");
//        } else {
//            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
//            output.putString(PandoraConstant.ERROR, "Failed to create task");
//        }
//        return output;
//
//        MProjectTask task = (MProjectTask)input.getSerializable(PBSTaskController.ARG_PROJTASK);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String result = serverAPI.createProjectTask(task,
                input.getString(PBSServerConst.PARAM_URL));
        if (result != null && !result.isEmpty()) {
            JsonParser p = new JsonParser();
            JsonObject jsonObj = p.parse(result).getAsJsonObject(); // get project task id and update local
            String projTaskId = jsonObj.get(MProjectTask.C_PROJECTTASK_ID_COL).getAsString();
            String success = jsonObj.get(PBSServerConst.SUCCESS).getAsString();
            if (PBSServerConst.TRUE.equalsIgnoreCase(success)){
                cv.put(MProjectTask.C_PROJECTTASK_ID_COL, projTaskId);
                Uri uri = cr.insert(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE), cv);
                output.putBoolean(PandoraConstant.RESULT, true);
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfuly insert new task.");
                return output;
            }

        }

        output.putBoolean(PandoraConstant.RESULT, false);
        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
        output.putString(PandoraConstant.ERROR, "Failed to create task");

        return output;
    }

    private Bundle completeProjectTask() {
        String projLocId = input.getString(PBSTaskController.ARG_PROJLOC_ID);
        String taskUUID = input.getString(PBSTaskController.ARG_TASK_UUID);
        String taskID = input.getString(PBSTaskController.ARG_TASK_ID);
        String comments = input.getString(PBSTaskController.ARG_COMMENTS);
        String assignedTo = input.getString(PBSTaskController.ARG_AD_USER_ID);

        PBSProjTaskJSON projTask = new PBSProjTaskJSON();
        projTask.setC_ProjectLocation_ID(projLocId);
        projTask.setC_ProjectTask_ID(taskID);
        projTask.setComments(comments);
        projTask.setAssignedTo(assignedTo);

        String pic1 = CameraUtil
                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_1));
            projTask.setAttachment_Pic1(pic1);

        String pic2 = CameraUtil
                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_2));
            projTask.setAttachment_Pic2(pic2);

        String pic3 = CameraUtil
                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_3));
            projTask.setAttachment_Pic3(pic3);

        String pic4 = CameraUtil
                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_4));
            projTask.setAttachment_Pic4(pic4);

        String pic5 = CameraUtil
                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_5));
            projTask.setAttachment_Pic5(pic5);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        PBSProjTasksJSON projTasks = serverAPI.completeProjTask(projTask, input.getString(PBSServerConst.PARAM_URL));

        if (PBSServerConst.TRUE.equalsIgnoreCase(projTasks.getSuccess())){

            String[] selectionArgs = {taskUUID};

            boolean result = ModelConst.deleteTableRow(cr, MProjectTask.TABLENAME, MProjectTask.C_PROJECTTASK_UUID_COL, selectionArgs);

            if (!result) {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to update Project Tasks.");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully update Project Task");
            }
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to update Project Tasks.");
            return output;
        }
        return output;
    }

    private Bundle getProjectTask(){
        String selection = ModelConst.C_PROJECTTASK_TABLE + ModelConst._UUID + "=?";
        String[] selectionArgs = {input.getString(PBSTaskController.ARG_C_PROJECTTASK_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE),
                projection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            output.putSerializable(PBSTaskController.ARG_PROJTASK,
                    populateProjectTask(cursor));
        }
        cursor.close();
        return output;
    }

    ContentValues getContentValuesFromTask(MProjectTask projTask) {
        ContentValues cv = new ContentValues();

        cv.put(MProjectTask.C_PROJECTTASK_UUID_COL, projTask.get_UUID());
//        cv.put(MProjectTask.C_PROJECTTASK_ID_COL, projTask.get_ID());
        cv.put(MProjectTask.CREATED_COL, projTask.getCreated());
        cv.put(MProjectTask.CREATEDBY_COL, projTask.getCreatedBy());

        cv.put(MProjectTask.C_PROJECTLOCATION_UUID_COL, projTask.getProjLocUUID());
        cv.put(MProjectTask.ASSIGNEDTO_COL, projTask.getAssignedTo());
        cv.put(MProjectTask.PRIORITY_COL, projTask.getPriority());
        cv.put(MProjectTask.NAME_COL, projTask.getName());
        cv.put(MProjectTask.DESCRIPTION_COL, projTask.getDescription());
        cv.put(MProjectTask.ISDONE_COL, projTask.isDone());

        cv.put(MProjectTask.COMMENTS_COL,projTask.getComments());

        return cv;
    }

    private Bundle syncProjectTasks() {
        String projLocId = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL, input.getString(PBSBroadcastController.ARG_PROJLOC_UUID),
                ModelConst.C_PROJECT_LOCATION_TABLE + ModelConst._ID, cr);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        PBSProjTasksJSON projTasks = serverAPI.getProjTasks
                (projLocId, input.getString(PBSServerConst.PARAM_URL));

        if (projTasks != null) {
            if (projTasks.getSuccess().equalsIgnoreCase(PBSServerConst.FALSE)){
                output.putString(PandoraConstant.ERROR, "Fail to sync Project Tasks.");
                return output;
            }

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();

            for (PBSProjTaskJSON projTask : projTasks.getProjTasks()){
                ContentValues cv = new ContentValues();


                cv.put(MProjectTask.C_PROJECTTASK_ID_COL, projTask.getC_ProjectTask_ID());
                cv.put(MProjectTask.CREATED_COL, projTask.getCreated());
                String createdBy = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                        ModelConst.AD_USER_UUID_COL,
                        projTask.getCreatedBy(), ModelConst.AD_USER_ID_COL, cr);
                cv.put(MProjectTask.CREATEDBY_COL, createdBy);

                cv.put(MProjectTask.C_PROJECTLOCATION_UUID_COL, input.getString(PBSBroadcastController.ARG_PROJLOC_UUID));
                cv.put(MProjectTask.ASSIGNEDTO_COL, projTask.getAssignedTo());
                cv.put(MProjectTask.PRIORITY_COL, projTask.getSeqNo());
                cv.put(MProjectTask.NAME_COL, projTask.getName());
                cv.put(MProjectTask.DESCRIPTION_COL, projTask.getDescription());
                cv.put(MProjectTask.ASSIGNEDTO_COL, projTask.getAssignedTo());
                String isDone = (projTask.getIsDone()) ? "Y":"N";
                cv.put(MProjectTask.ISDONE_COL, isDone);
                String selection = MProjectTask.C_PROJECTTASK_ID_COL;
                String[] arg = {cv.getAsString(selection)};
                String tableName = ModelConst.C_PROJECTTASK_TABLE;
                if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                    cv.put(MProjectTask.C_PROJECTTASK_UUID_COL, UUID.randomUUID().toString());
                    ops.add(ContentProviderOperation
                            .newInsert(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                            .withValues(cv)
                            .build());
                } else {
                    selection = selection + "=?";
                    ops.add(ContentProviderOperation
                            .newUpdate(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                            .withValues(cv)
                            .withSelection(selection, arg)
                            .build());
                }
            }
            try {
                ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
                for(ContentProviderResult result : results) {
                    boolean resultFlag = false;
                    if (result.uri != null) {
                        resultFlag = ModelConst.getURIResult(result.uri);
                    } else {
                        if (result.count != null && result.count !=0) {
                            resultFlag = true;
                        }
                    }
                    if (!resultFlag) {
                        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                        output.putString(PandoraConstant.ERROR, "Fail to sync Project Tasks.");
                        return output;
                    }
                }
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully synced Project Task");
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            } catch (OperationApplicationException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            output.putString(PandoraConstant.ERROR, "Fail to sync Project Tasks.");
        }
        return output;
    }

    String projection[] = {
            MProjectTask.C_PROJECTTASK_UUID_COL,
            MProjectTask.C_PROJECTTASK_ID_COL,
            MProjectTask.C_PROJECTLOCATION_UUID_COL,
            MProjectTask.NAME_COL,
            MProjectTask.ISDONE_COL,
            MProjectTask.ASSIGNEDTO_COL,
            MProjectTask.PRIORITY_COL,
            MProjectTask.DESCRIPTION_COL,
            MProjectTask.COMMENTS_COL,
            MProjectTask.CREATED_COL,
            MProjectTask.CREATEDBY_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_1_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_2_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_3_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_4_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_5_COL
    };

    private Bundle getProjectTasks() {
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE),
                projection, null, null, MProjectTask.PRIORITY_COL + " ASC");
        ObservableArrayList<MProjectTask> projectTaskList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                projectTaskList.add(populateProjectTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSTaskController.ARG_TASK_LIST, projectTaskList);
        return output;
    }

    private MProjectTask populateProjectTask(Cursor cursor) {
        MProjectTask projTask = new MProjectTask();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue =  cursor.getString(x);
            if (MProjectTask.C_PROJECTTASK_UUID_COL.equalsIgnoreCase(columnName)){
                projTask.set_UUID(rowValue);
            } else if (MProjectTask.C_PROJECTTASK_ID_COL.equalsIgnoreCase(columnName)) {
                if (rowValue != null)
                    projTask.set_ID(Integer.parseInt(rowValue));
            }
            else if (MProjectTask.C_PROJECTLOCATION_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                //map to project location name.
                if (rowValue != null){
                    if (!rowValue.isEmpty()) {
                        //String project location name.
                        String projLocName;
                        String projLocProjection[] = {MProjectTask.NAME_COL};
                        String projLocSelArgs[] = {rowValue};
                        Cursor projLocCursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE), projLocProjection, ModelConst.C_PROJECTLOCATION_UUID_COL + "=?", projLocSelArgs, null);
                        if (projLocCursor != null && projLocCursor.getCount() != 0) {
                            projLocCursor.moveToFirst();
                            for (int y = 0; y < projLocCursor.getColumnNames().length; y++) {
                                if(MProjectTask.NAME_COL.equalsIgnoreCase(projLocCursor.getColumnName(y)))
                                    projTask.setProjLocName(projLocCursor.getString(y));
                            }
                        }

                        projLocCursor.close();
                    }
                }
                projTask.setProjLocUUID(rowValue);
            } else if (MProjectTask.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setName(rowValue);
            } else if (MProjectTask.ISDONE_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setIsDone(rowValue);
                if ("N".equalsIgnoreCase(rowValue)) {
                    projTask.setStatus("Incomplete");
                } else {
                    projTask.setStatus("Completed");
                }
            } else if (MProjectTask.PRIORITY_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setPriority(cursor.getInt(x));
            } else if (MProjectTask.ASSIGNEDTO_COL
                    .equalsIgnoreCase(columnName)) {
                if (rowValue == null) rowValue = "0";
                projTask.setAssignedTo(Integer.parseInt(rowValue));
                String assignedToName = ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE, ModelConst.NAME_COL,
                        rowValue, ModelConst.C_BPARTNER_ID_COL, cr);
                projTask.setAssignedToName(assignedToName);
            } else if (MProjectTask.DESCRIPTION_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setDescription(rowValue);
            } else if (MProjectTask.COMMENTS_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setComments(rowValue);
            } else if (MProjectTask.CREATED_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setCreated(rowValue);
            } else if (MProjectTask.CREATEDBY_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setCreatedBy(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_1_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_1(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_2_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_2(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_3_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_3(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_4_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_4(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_5_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_5(rowValue);
            }
        }
        return projTask;
    }
}
