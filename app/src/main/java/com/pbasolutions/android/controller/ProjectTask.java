package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.fragment.ProjTaskDetailsFragment;
import com.pbasolutions.android.json.PBSProjTaskJSON;
import com.pbasolutions.android.json.PBSProjTasksJSON;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by pbadell on 11/11/15.
 */
public class ProjectTask implements Callable<Bundle>, ITask {

    private static final String TAG = "ProjectTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;
    private Context ctx;

    public ProjectTask(ContentResolver cr, Context ctx) {
        this.cr = cr;
        this.ctx = ctx;
    }

    @Override
    public Bundle getInput() {
        return input;
    }

    @Override
    public void setInput(Bundle input) {
        this.input = input;
        event = input.getString("ARG_TASK_EVENT");
    }

    @Override
    public Bundle getOutput() {
        return output;
    }

    @Override
    public void setOutput(Bundle output) {
        this.output = output;
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
            case PBSTaskController.CREATE_TASK_EVENT: {
                return createTask();
            }
            case PBSTaskController.UPDATE_TASK_EVENT: {
                return updateTask();
            }
            case PBSTaskController.COMPLETE_PROJTASK_EVENT: {
                return completeProjectTask();
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
        String aduser = ModelConst.AD_USER_TABLE + ".";

//        String projLocationUUID = input.getString(PBSTaskController.ARG_PROJLOC_UUID);
        String adUserID = input.getString(PBSTaskController.ARG_AD_USER_ID);
        String adClientID = input.getString(PBSTaskController.ARG_AD_CLIENT_ID);
        String adClientUUID = ModelConst.mapUUIDtoColumn(ModelConst.AD_CLIENT_TABLE, ModelConst.AD_CLIENT_ID_COL,
                adClientID, ModelConst.AD_CLIENT_UUID_COL, cr);

        String selection = aduser + ModelConst.AD_USER_ID_COL + "!= ? AND " +
                aduser + ModelConst.AD_CLIENT_UUID_COL + "= ? AND " +
                "EXISTS (SELECT C_BPartner_UUID FROM C_BPartner WHERE C_BPartner.C_BPartner_ID = AD_User.C_BPartner_UUID AND C_BPartner.IsEmployee = ?)";
        String[] selectionArg = {adUserID, adClientUUID, "Y"};
        String[] projection = {aduser + ModelConst.AD_USER_ID_COL,
                aduser + ModelConst.NAME_COL};

        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.AD_USER_TABLE),
                projection, selection, selectionArg, "LOWER(" + aduser + ModelConst.NAME_COL + ") ASC");
        ObservableArrayList<SpinnerPair> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.AD_USER_ID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
//                        Log.i(TAG, "getUsers: "+cursor.getString(x));
//                        String getid = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE,
//                                ModelConst.AD_USER_UUID_COL, cursor.getString(x), ModelConst.AD_USER_ID_COL, cr);
                        pair.setKey(cursor.getString(x));
//                        Log.i(TAG, "getUsers: "+getid);
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
        String ad_user_uuid = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_ID_COL,
                ((PandoraMain)ctx).getGlobalVariable().getAd_user_id(), ModelConst.AD_USER_UUID_COL, cr);
        String projection[] = {ModelConst.C_PROJECTLOCATION_ID_COL,
                ModelConst.NAME_COL};
        String selection = "hr_cluster_uuid='null' or hr_cluster_uuid in (select hr_cluster_uuid from hr_clustermanagement where isactive=? and ad_user_uuid=? group by hr_cluster_uuid)";
        String selectionArgs [] = {"Y", ad_user_uuid};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE),
                projection, selection,
                selectionArgs, "LOWER(" + ModelConst.NAME_COL + ") ASC");

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
        MProjectTask task = (MProjectTask) input.getSerializable(PBSTaskController.ARG_PROJTASK);
        ContentValues cv = getContentValuesFromTask(task);
        if (task.getATTACHMENT_BEFORETASKPICTURE_1() != null && !task.getATTACHMENT_BEFORETASKPICTURE_1().isEmpty()) {
            String pic1 = CameraUtil
                    .imageToBase64(task.getATTACHMENT_BEFORETASKPICTURE_1());
            task.setATTACHMENT_BEFORETASKPICTURE_1(pic1);
        }
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
            String success = jsonObj.get(PBSServerConst.SUCCESS).getAsString();
            if (PBSServerConst.TRUE.equalsIgnoreCase(success)){
                String projTaskId = jsonObj.get(MProjectTask.C_PROJECTTASK_ID_COL).getAsString();
                cv.put(MProjectTask.C_PROJECTTASK_ID_COL, projTaskId);
                Uri uri = cr.insert(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE), cv);
                output.putBoolean(PandoraConstant.RESULT, true);
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully inserted new task.");
                return output;
            }

        }

        output.putBoolean(PandoraConstant.RESULT, false);
        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
        output.putString(PandoraConstant.ERROR, "Failed to create task");

        return output;
    }

    private Bundle updateTask() {
        MProjectTask task = (MProjectTask) input.getSerializable(PBSTaskController.ARG_PROJTASK);
        ContentValues cv = getContentValuesFromTask(task);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String result = serverAPI.updateProjectTask(task, input.getString(PBSServerConst.PARAM_URL));
        if (result != null && !result.isEmpty()) {
            JsonParser p = new JsonParser();
            JsonObject jsonObj = p.parse(result).getAsJsonObject(); // get project task id and update local
            String success = jsonObj.get(PBSServerConst.SUCCESS).getAsString();
            if (PBSServerConst.TRUE.equalsIgnoreCase(success)){
                String[] arg = {String.valueOf(task.get_ID())};
                int rowUpdated = cr.update(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE), cv, MProjectTask.C_PROJECTTASK_ID_COL, arg);
                if (rowUpdated > 0) {
                    output.putBoolean(PandoraConstant.RESULT, true);
                    output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                    output.putString(PandoraConstant.RESULT, "Successfully updated new task.");
                    return output;
                }
            }

        }

        output.putBoolean(PandoraConstant.RESULT, false);
        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
        output.putString(PandoraConstant.ERROR, "Failed to update task");

        return output;
    }

    private Bundle completeProjectTask() {
        String projLocId = input.getString(PBSTaskController.ARG_PROJLOC_ID);
        String taskUUID = input.getString(PBSTaskController.ARG_TASK_UUID);
        final String taskID = input.getString(PBSTaskController.ARG_TASK_ID);
        String comments = input.getString(PBSTaskController.ARG_COMMENTS);
        String assignedTo = input.getString(PBSTaskController.ARG_AD_USER_ID);
        String dueDate = input.getString(PBSTaskController.ARG_DUEDATE);
        String latitude = input.getString(MSurvey.LATITUDE_COL);
        String longitude = input.getString(MSurvey.LONGITUDE_COL);
        String signature = input.getString(MSurvey.ATTACHMENT_SIGNATURE_COL);

        if (signature != null && !signature.isEmpty())
            signature = CameraUtil.imageToBase64(signature);

        PBSProjTaskJSON projTask = new PBSProjTaskJSON();
        projTask.setC_ProjectLocation_ID(projLocId);
        projTask.setC_ProjectTask_ID(taskID);
        projTask.setComments(comments);
        projTask.setAssignedTo(assignedTo);
        projTask.setDueDate(dueDate);
        projTask.setLatitude(latitude);
        projTask.setLongitude(longitude);
        projTask.setAttachment_Signature(signature);

//        String pic1 = CameraUtil
//                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_1));
//            projTask.setAttachment_Pic1(pic1);
//
//        String pic2 = CameraUtil
//                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_2));
//            projTask.setAttachment_Pic2(pic2);
//
//        String pic3 = CameraUtil
//                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_3));
//            projTask.setAttachment_Pic3(pic3);
//
//        String pic4 = CameraUtil
//                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_4));
//            projTask.setAttachment_Pic4(pic4);
//
//        String pic5 = CameraUtil
//                .imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC_5));
//            projTask.setAttachment_Pic5(pic5);

        final PBSIServerAPI serverAPI = new PBSServerAPI();
        PBSProjTasksJSON projTasks = serverAPI.completeProjTask(projTask, input.getString(PBSServerConst.PARAM_URL));

        if (PBSServerConst.TRUE.equalsIgnoreCase(projTasks.getSuccess())) {
            if (input.getString(PBSTaskController.ARG_TASKPIC_1) != null) {
                for (int i = 1; i <= 25; i += 5) {
                    final int finalI = i;
                    if (input.getString(PBSTaskController.ARG_TASKPIC + i) == null)
                        break;

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            JsonObject object = new JsonObject();
                            object.addProperty(MProjectTask.C_PROJECTTASK_ID_COL, taskID);
                            for (int j = 0; j < 5; j++) {
                                if (input.getString(PBSTaskController.ARG_TASKPIC + (finalI + j)) == null)
                                    break;
                                object.addProperty(MProjectTask.ATTACHMENT_PIC + (finalI + j), CameraUtil.imageToBase64(input.getString(PBSTaskController.ARG_TASKPIC + (finalI + j))));
                            }
                            PBSIServerAPI serverAPI = new PBSServerAPI();
                            serverAPI.attachToProjTask(object, input.getString(PBSServerConst.PARAM_URL));
                            return null;
                        }
                    }.execute();
                }
            }

            String[] selectionArgs = {taskUUID};
//            boolean result = ModelConst.deleteTableRow(cr, MProjectTask.TABLENAME, MProjectTask.C_PROJECTTASK_UUID_COL, selectionArgs);
            ContentValues cv = new ContentValues();
            cv.put(MProjectTask.COMMENTS_COL, comments);
            cv.put(MProjectTask.ISDONE_COL, "Y");
            cv.put(MSurvey.LATITUDE_COL, latitude);
            cv.put(MSurvey.LONGITUDE_COL, longitude);
            for (int i = 1; i <= 25; i++) {
                if (input.getString(PBSTaskController.ARG_TASKPIC + i) == null)
                    break;
                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_COL + i, input.getString(PBSTaskController.ARG_TASKPIC + i));
            }
//                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_1_COL, input.getString(PBSTaskController.ARG_TASKPIC_1));
//                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_2_COL, input.getString(PBSTaskController.ARG_TASKPIC_2));
//                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_3_COL, input.getString(PBSTaskController.ARG_TASKPIC_3));
//                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_4_COL, input.getString(PBSTaskController.ARG_TASKPIC_4));
//                cv.put(MProjectTask.ATTACHMENT_TASKPICTURE_5_COL, input.getString(PBSTaskController.ARG_TASKPIC_5));
            boolean result = ModelConst.updateTableRow(cr, MProjectTask.TABLENAME, cv, MProjectTask.C_PROJECTTASK_UUID_COL, selectionArgs);

            if (result) {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully update Project Task");
                return output;
            }
        }
        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
        output.putString(PandoraConstant.ERROR, "Fail to update Project Tasks.");
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

        if (projTask.get_UUID() != null && !projTask.get_UUID().isEmpty())
            cv.put(MProjectTask.C_PROJECTTASK_UUID_COL, projTask.get_UUID());

        if (projTask.getCreated() != null && !projTask.getCreated().isEmpty())
            cv.put(MProjectTask.CREATED_COL, projTask.getCreated());

        if (projTask.getCreatedBy() != null && !projTask.getCreatedBy().isEmpty())
            cv.put(MProjectTask.CREATEDBY_COL, projTask.getCreatedBy());

        if (projTask.getProjLocUUID() != null && !projTask.getProjLocUUID().isEmpty())
            cv.put(MProjectTask.C_PROJECTLOCATION_UUID_COL, projTask.getProjLocUUID());

        if (projTask.getAssignedTo() != 0)
            cv.put(MProjectTask.ASSIGNEDTO_COL, projTask.getAssignedTo());

        if (projTask.getSecAssignedTo() != 0)
            cv.put(MProjectTask.SECASSIGNEDTO_COL, projTask.getSecAssignedTo());

        if (projTask.getPriority() != 0)
            cv.put(MProjectTask.PRIORITY_COL, projTask.getPriority());

        if (projTask.getName() != null && !projTask.getName().isEmpty())
            cv.put(MProjectTask.NAME_COL, projTask.getName());

        if (projTask.getDescription() != null && !projTask.getDescription().isEmpty())
            cv.put(MProjectTask.DESCRIPTION_COL, projTask.getDescription());

        if (projTask.getEquipment() != null && !projTask.getEquipment().isEmpty())
            cv.put(MProjectTask.EQUIPMENT_COL, projTask.getEquipment());

        if (projTask.getContact() != null && !projTask.getContact().isEmpty())
            cv.put(MProjectTask.CONTACT_COL, projTask.getContact());

        if (projTask.getContactNo() != null && !projTask.getContactNo().isEmpty())
            cv.put(MProjectTask.CONTACTNO_COL, projTask.getContactNo());

        if (projTask.isDone() != null && !projTask.isDone().isEmpty())
            cv.put(MProjectTask.ISDONE_COL, projTask.isDone());

        if (projTask.getComments() != null && !projTask.getComments().isEmpty())
            cv.put(MProjectTask.COMMENTS_COL, projTask.getComments());

        if (projTask.getDateAssigned() != null && !projTask.getDateAssigned().isEmpty())
            cv.put(MProjectTask.DATEASSIGNED_COL, projTask.getDateAssigned());

        if (projTask.getDueDate() != null && !projTask.getDueDate().isEmpty())
            cv.put(MProjectTask.DUEDATE_COL, projTask.getDueDate());

        if (projTask.getATTACHMENT_BEFORETASKPICTURE_1() != null && !projTask.getATTACHMENT_BEFORETASKPICTURE_1().isEmpty())
            cv.put(MProjectTask.ATTACHMENT_BEFORETASKPICTURE_1_COL, projTask.getATTACHMENT_BEFORETASKPICTURE_1());

        return cv;
    }

    private Bundle syncProjectTasks() {
        String projLocId = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL, input.getString(PBSBroadcastController.ARG_PROJLOC_UUID),
                ModelConst.C_PROJECT_LOCATION_TABLE + ModelConst._ID, cr);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        PBSProjTasksJSON projTasks = serverAPI.getProjTasks
                (projLocId, input.getString(PBSTaskController.ARG_AD_USER_ID), input.getString(PBSServerConst.PARAM_URL));

        if (projTasks != null) {
            if (projTasks.getSuccess() != null && projTasks.getSuccess().equalsIgnoreCase(PBSServerConst.FALSE)){
                output.putString(PandoraConstant.ERROR, "Fail to sync Project Tasks.");
                return output;
            }

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ArrayList<ContentProviderOperation> ops2 = new ArrayList<>();
            ContentValues cv2 = new ContentValues();

//            ops.add(ContentProviderOperation
//                    .newDelete(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
//                    .build());
//            try {
//                cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
//                ops.clear();
//            }
//            catch (Exception e) {
//                Log.e(TAG, e.getMessage());
//            }

            boolean isShowNotification = input.getBoolean("isShowNotification", false);
            String projTaskIDs = "";
            if (projTasks.getProjTasks().length > 0) {
                for (PBSProjTaskJSON projTask : projTasks.getProjTasks()) {
                    ContentValues cv = new ContentValues();

                    projTaskIDs += projTask.getC_ProjectTask_ID() + ", ";
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
                    String isDone = (projTask.getIsDone()) ? "Y" : "N";
                    cv.put(MProjectTask.ISDONE_COL, isDone);
                    if (projTask.getDateAssigned() != null && !projTask.getDateAssigned().isEmpty())
                        cv.put(MProjectTask.DATEASSIGNED_COL, projTask.getDateAssigned());
                    cv.put(MProjectTask.DUEDATE_COL, projTask.getDueDate());
                    cv.put(MProjectTask.COMMENTS_COL, projTask.getComments());
                    String selection = MProjectTask.C_PROJECTTASK_ID_COL;
                    String[] arg = {cv.getAsString(selection)};
                    String tableName = ModelConst.C_PROJECTTASK_TABLE;
                    if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                        String uuid = UUID.randomUUID().toString();
                        cv.put(MProjectTask.C_PROJECTTASK_UUID_COL, uuid);
                        ops.add(ContentProviderOperation
                                .newInsert(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                                .withValues(cv)
                                .build());

                        if (!projTask.getIsDone() && isShowNotification)
                            PandoraHelper.sendNotification(ctx, uuid, PandoraContext.getContext().getResources().getString(R.string.notification_new_proj_task), ProjTaskDetailsFragment.class.getSimpleName());
                    } else {
                        selection = selection + "=?";
                        ops.add(ContentProviderOperation
                                .newUpdate(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                                .withValues(cv)
                                .withSelection(selection, arg)
                                .build());

                        if (!projTask.getIsDone() && isShowNotification) {
                            String uuid = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECTTASK_TABLE, MProjectTask.C_PROJECTTASK_ID_COL,
                                    projTask.getC_ProjectTask_ID(), MProjectTask.C_PROJECTTASK_UUID_COL, cr);
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(sdf.parse(projTask.getDueDate()));
                                cal.add(Calendar.DATE, 1);
                                Date dueDate = cal.getTime();
                                cal.add(Calendar.HOUR, -2);
                                Date expiringDueDate = cal.getTime();
                                cv2.clear();
                                ops2.clear();
                                if (new Date().after(dueDate)) {
                                    String isExpiredNotified = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECTTASK_TABLE,
                                            MProjectTask.C_PROJECTTASK_ID_COL, projTask.getC_ProjectTask_ID(),
                                            MProjectTask.ISEXPIREDNOTIFIED_COL, cr);
                                    if (isExpiredNotified.equalsIgnoreCase("N")) {
                                        PandoraHelper.sendNotification(ctx, uuid, ctx.getResources().getString(R.string.notification_proj_task_expired), ProjTaskDetailsFragment.class.getSimpleName());
                                        cv2.put(MProjectTask.ISEXPIREDNOTIFIED_COL, "Y");
                                        ops2.add(ContentProviderOperation
                                                .newUpdate(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                                                .withValues(cv2)
                                                .build());
                                        try {
                                            cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops2);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (OperationApplicationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (new Date().after(expiringDueDate)) {
                                    String isExpiringNotified = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECTTASK_TABLE,
                                            MProjectTask.C_PROJECTTASK_ID_COL, projTask.getC_ProjectTask_ID(),
                                            MProjectTask.ISEXPIRINGNOTIFIED_COL, cr);
                                    if (isExpiringNotified.equalsIgnoreCase("N")) {
                                        PandoraHelper.sendNotification(ctx, uuid, ctx.getResources().getString(R.string.notification_proj_task_expiring), ProjTaskDetailsFragment.class.getSimpleName());
                                        cv2.put(MProjectTask.ISEXPIRINGNOTIFIED_COL, "Y");
                                        ops2.add(ContentProviderOperation
                                                .newUpdate(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE))
                                                .withValues(cv2)
                                                .build());
                                        try {
                                            cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops2);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (OperationApplicationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                try {
                    ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
                    String selection = MProjectTask.ISDONE_COL + "=? AND " + MProjectTask.C_PROJECTTASK_ID_COL + " NOT IN (" + projTaskIDs.substring(0, projTaskIDs.length() - 2) + ")";
                    String[] selectionArgs = {"Y"};
                    ModelConst.deleteTableRow(cr, MProjectTask.TABLENAME, selection, selectionArgs);
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
                            output.putString(PandoraConstant.ERROR, "Fail to sync Project Tasks.");
                            return output;
                        }
                    }
                    output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                    output.putString(PandoraConstant.RESULT, "Successfully synced Project Task");
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully synced Project Task");
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
            MProjectTask.SECASSIGNEDTO_COL,
            MProjectTask.PRIORITY_COL,
            MProjectTask.DESCRIPTION_COL,
            MProjectTask.EQUIPMENT_COL,
            MProjectTask.CONTACT_COL,
            MProjectTask.CONTACTNO_COL,
            MProjectTask.COMMENTS_COL,
            MProjectTask.CREATED_COL,
            MProjectTask.CREATEDBY_COL,
            MProjectTask.ATTACHMENT_BEFORETASKPICTURE_1_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_1_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_2_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_3_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_4_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_5_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_6_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_7_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_8_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_9_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_10_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_11_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_12_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_13_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_14_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_15_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_16_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_17_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_18_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_19_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_20_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_21_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_22_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_23_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_24_COL,
            MProjectTask.ATTACHMENT_TASKPICTURE_25_COL,
            MProjectTask.DATEASSIGNED_COL,
            MProjectTask.DUEDATE_COL
    };

    private Bundle getProjectTasks() {
        String selection = ModelConst.C_PROJECTLOCATION_UUID_COL + "=? OR ASSIGNEDTO=?";
        String[] selectionArgs = {input.getString(PBSTaskController.ARG_PROJLOC_UUID), input.getString(PBSTaskController.ARG_AD_USER_ID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECTTASK_TABLE),
                projection, selection, selectionArgs, MProjectTask.ISDONE_COL + " ASC, " + MProjectTask.PRIORITY_COL + " ASC");
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
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
                        String projLocName = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL,
                                rowValue, MProjectTask.NAME_COL, cr);
                        if (!projLocName.equals("null"))
                            projTask.setProjLocName(projLocName);
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
                String assignedToName = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.NAME_COL,
                        rowValue, ModelConst.AD_USER_ID_COL, cr);
                projTask.setAssignedToName(assignedToName);
            } else if (MProjectTask.SECASSIGNEDTO_COL
                    .equalsIgnoreCase(columnName)) {
                if (rowValue == null) rowValue = "0";
                projTask.setSecAssignedTo(Integer.parseInt(rowValue));
                String assignedToName = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.NAME_COL,
                        rowValue, ModelConst.AD_USER_ID_COL, cr);
                projTask.setSecAssignedToName(assignedToName);
            } else if (MProjectTask.DESCRIPTION_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setDescription(rowValue);
            } else if (MProjectTask.EQUIPMENT_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setEquipment(rowValue);
            } else if (MProjectTask.CONTACT_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setContact(rowValue);
            } else if (MProjectTask.CONTACTNO_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setContactNo(rowValue);
            } else if (MProjectTask.COMMENTS_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setComments(rowValue);
            } else if (MProjectTask.CREATED_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setCreated(rowValue);
            } else if (MProjectTask.CREATEDBY_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setCreatedBy(rowValue);
            } else if (MProjectTask.ATTACHMENT_BEFORETASKPICTURE_1_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_BEFORETASKPICTURE_1(rowValue);
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
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_6_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_6(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_7_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_7(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_8_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_8(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_9_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_9(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_10_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_10(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_11_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_11(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_12_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_12(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_13_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_13(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_14_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_14(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_15_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_15(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_16_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_16(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_17_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_17(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_18_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_18(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_19_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_19(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_20_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_20(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_21_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_21(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_22_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_22(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_23_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_23(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_24_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_24(rowValue);
            } else if (MProjectTask.ATTACHMENT_TASKPICTURE_25_COL
                    .equalsIgnoreCase(columnName)) {
                projTask.setATTACHMENT_TASKPICTURE_25(rowValue);
            } else if (MProjectTask.DUEDATE_COL
                    .equalsIgnoreCase(columnName)) {
                if (rowValue != null) {
                    try {
                        Date date = df.parse(rowValue);
                        projTask.setDueDate(sdf.format(date));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else
                    projTask.setDueDate(rowValue);
            } else if (MProjectTask.DATEASSIGNED_COL
                    .equalsIgnoreCase(columnName)) {
                if (rowValue != null) {
                    try {
                        Date date = df.parse(rowValue);
                        projTask.setDateAssigned(sdf.format(date));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else
                    projTask.setDateAssigned(rowValue);
            }
        }
        return projTask;
    }
}
