package com.pbasolutions.android.api;

import com.google.gson.JsonObject;
import com.pbasolutions.android.json.PBSJson;
import com.pbasolutions.android.json.PBSNotesJSON;
import com.pbasolutions.android.json.PBSProjTasksJSON;


import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.pbasolutions.android.model.MStorage;

import org.json.JSONObject;

/**
 * Created by pbadell on 10/30/15.
 */
public interface PBSIServerAPI {
    PBSNotesJSON getNoteByUser(String username, String projLocID, String serverURL);
    PBSProjTasksJSON getProjTasks(String projLocID, String userID, String serverURL);
    PBSProjTasksJSON completeProjTask(PBSJson json, String serverURL);

    MPurchaseRequest createRequisition(PBSJson json, String serverURL);
    MMovement createMovement(PBSJson json, String serverURL);
    String getRequisitions(PBSJson json, String serverURL);
    String getMStorage(JsonObject json, String serverURL);
    MAttendance createAttendance(PBSJson json, String serverURL);

    String getMovements(JsonObject json, String serverURL);
    String completeMovement(JsonObject json, String serverURL);

    String getDeployments(JsonObject object, String serverURL);

    String searchAttendances(JsonObject object, String serverURL);

    String createProjectTask(PBSJson json, String serverURL);
    String attachToProjTask(JsonObject json, String serverURL);
}
