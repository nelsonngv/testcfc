package com.pbasolutions.android.syncAdapter;

import android.os.Bundle;

import com.pbasolutions.android.json.PBSJson;
import com.pbasolutions.android.json.PBSSyncJSON;
import com.pbasolutions.android.json.PBSTableJSON;

/**
 * Created by pbadell on 6/30/15.
 */
public interface PBSIServerAccessor {
    /**
     * Authenticate user upon sign in process. Result will return
     * a User object with stored of username, password and authToken.
     * @return  PBSResultJSON result of updating tables to server.
     **/
    public Bundle updateTables(final PBSTableJSON updateJSON, final String username, final String authToken, final String serverURL, String updateIdentifier) throws Exception;

    public PBSSyncJSON syncTables(final String username, String authToken, final String serverURL, String syncIdentifier);

    public PBSJson getUnsyncCount(final String serverURL);
}
