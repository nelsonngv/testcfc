package com.pbasolutions.android.syncAdapter;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.pbasolutions.android.PBSServer;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.json.PBSJson;
import com.pbasolutions.android.json.PBSResultJSON;
import com.pbasolutions.android.json.PBSSyncJSON;
import com.pbasolutions.android.json.PBSTableJSON;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by pbadell on 6/30/15.
 */
public class PBSServerAccessor extends PBSServer implements PBSIServerAccessor {
    /**
     * Update local table to server.
     *
     * @return Result of updating table to server.
     */
    @Override
    public Bundle updateTables(PBSTableJSON jsonUpdate, String username, String authToken, String serverURL) throws Exception {

        Gson gson = new Gson();

        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(jsonUpdate);

        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.SYNC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.ACTION_UPDATE, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += query;

        PBSResultJSON resultJSON = null;
        Bundle returnBundle  = new Bundle();
        String responseString =  postServer(url,json);
        resultJSON = new Gson().fromJson(responseString,  PBSResultJSON.class);

        if (resultJSON.getSuccess() != null) {
            if (resultJSON.getSuccess().equals("TRUE")) {
                returnBundle.putBoolean("RESULT", true);
                returnBundle.putString("ID", resultJSON.getID());
            } else if (resultJSON.getInvalidSession() != null) {
                returnBundle.putBoolean("RESULT", false);
                returnBundle.putString(PandoraConstant.ERROR, "Invalid Session, Please re-login to run sync.");
            } else {
                returnBundle.putBoolean("RESULT", false);
                returnBundle.putString(PandoraConstant.ERROR, "Error in Updating tables");
            }
        } else {
            returnBundle.putBoolean("RESULT", false);
            returnBundle.putString(PandoraConstant.ERROR, "Error in Updating tables");
        }
        return returnBundle;
    }

    @Override
    public PBSSyncJSON syncTables(final String username, final String authToken, final String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.SYNC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.SYNC, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += query;
        //   "/wstore/Sync.jsp?action=Sync
        return (PBSSyncJSON) callServer(url, PBSSyncJSON.class.getName());
    }

    @Override
    public PBSJson getUnsyncCount(final String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.SYNC_JSP);
        String query = null;
        try {
            query = String.format("%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.ACTION_GET_UNSYNC_COUNT, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += query;
        return callServer(url, PBSJson.class.getName());
    }
}
