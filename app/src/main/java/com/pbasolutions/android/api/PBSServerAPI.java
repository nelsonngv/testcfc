package com.pbasolutions.android.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pbasolutions.android.PBSServer;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.json.PBSJson;
import com.pbasolutions.android.json.PBSNotesJSON;
import com.pbasolutions.android.json.PBSProjTasksJSON;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.pbasolutions.android.model.MStorage;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by pbadell on 10/30/15.
 */
//TODO: refactor code to be less.
public class PBSServerAPI extends PBSServer implements PBSIServerAPI {
    private static final String TAG = "PBSServerAPI";
    @Override
    public PBSNotesJSON getNoteByUser(String userID, String projLocID, String serverURL) {
        //
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s&%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_NOTES, PBSServerConst.UTF_8),
                    PBSServerConst.USERID, URLEncoder.encode(userID, PBSServerConst.UTF_8),
                    PBSServerConst.PROJLOCID, URLEncoder.encode(projLocID, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        return (PBSNotesJSON) callServer(url, PBSNotesJSON.class.getName());
    }

    @Override
    public PBSProjTasksJSON getProjTasks(String projLocID, String serverURL) {
        //
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_PROJTASKS, PBSServerConst.UTF_8),
                    PBSServerConst.PROJLOCID, URLEncoder.encode(projLocID, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        return (PBSProjTasksJSON) callServer(url, PBSProjTasksJSON.class.getName());
    }

    @Override
    public PBSProjTasksJSON completeProjTask(PBSJson json, String serverURL) {

        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.COMPLETE_PROJTASK, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        String res = postServer(url, strJson);
        return new Gson().fromJson(res,PBSProjTasksJSON.class);
    }

    @Override
    public MPurchaseRequest createRequisition(PBSJson json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format(
                    "%s=%s",
                    PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.CREATE_REQUISITION, PBSServerConst.UTF_8)
            );
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        String res = postServer(url, strJson);
        return new Gson().fromJson(res,MPurchaseRequest.class);
    }

    @Override
    public MAttendance createAttendance(PBSJson json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format(
                    "%s=%s",
                    PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.UPDATE_ATTENDANCE, PBSServerConst.UTF_8)
            );
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        String res = postServer(url, strJson);
        return new Gson().fromJson(res,MAttendance.class);
    }

    @Override
    public MMovement createMovement(PBSJson json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format(
                    "%s=%s",
                    PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.CREATE_MOVEMENT, PBSServerConst.UTF_8)
            );
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        String res = postServer(url, strJson);
        return new Gson().fromJson(res, MMovement.class);
    }


    @Override
    public String createProjectTask(PBSJson json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format(
                    "%s=%s",
                    PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.CREATE_PROJECTTASK, PBSServerConst.UTF_8)
            );
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        return  postServer(url, strJson);
    }

    @Override
    public String getRequisitions(PBSJson json, String serverURL) {
        //
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_REQUISITIONS, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        return postServer(url, strJson);
    }

    @Override
    public String getMStorage(JsonObject json, String serverURL) {
        //
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_MSTORAGE, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        return postServer(url,strJson);
    }

    @Override
    public String getMovements(JsonObject json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_MOVEMENTS, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        return postServer(url,strJson);
    }

    @Override
    public String completeMovement(JsonObject json, String serverURL) {
        //
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.COMPLETE_MOVEMENT, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;

        String strJson = new Gson().toJson(json);
        return postServer(url,strJson);
    }

    @Override
    public String getDeployments(JsonObject json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.GET_DEPLOYMENTS, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;
        String strJson = new Gson().toJson(json);
        return postServer(url,strJson);
    }

    @Override
    public String searchAttendances(JsonObject json, String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.CFC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION,
                    URLEncoder.encode(PBSServerConst.SEARCH_ATTENDANCE, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        url += query;
        String strJson = new Gson().toJson(json);
        return postServer(url,strJson);
    }
}
