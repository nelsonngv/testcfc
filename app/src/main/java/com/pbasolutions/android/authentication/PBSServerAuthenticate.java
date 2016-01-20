package com.pbasolutions.android.authentication;

import android.util.Log;

import com.pbasolutions.android.PBSServer;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.json.PBSLoginJSON;

import com.pbasolutions.android.json.PBSResultJSON;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by pbadell on 6/25/15.
 */
public class PBSServerAuthenticate extends PBSServer implements PBSIServerAuthenticate {

    private static final String TAG = "PBSServerAuthenticate";

    @Override
    public PBSLoginJSON userSignIn(final String username, final String pass, final String serial, final String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.AUTH_JSP);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s&%s=%s&%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.LOGIN, PBSServerConst.UTF_8),
                    PBSServerConst.USER_NAME, URLEncoder.encode(username, PBSServerConst.UTF_8),
                    PBSServerConst.PASSWORD,  URLEncoder.encode(pass, PBSServerConst.UTF_8),
                    PBSServerConst.SERIAL, URLEncoder.encode(serial, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        url += query;
        //String url = serverURL + "/wstore/Auth.jsp?action=Login&username=" + username + "&password=" + password + "&serial=" + serial;
        return (PBSLoginJSON) callServer(url, PBSLoginJSON.class.getName());
    }

    @Override
    public boolean authenticateServerURI(final String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.AUTH_JSP);
        String query = null;
        try {
            //action
            query = String.format("%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.VERSION, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        url += query;
        //String url = serverURL + "/wstore/Auth.jsp?action=Version;
        PBSResultJSON resultJSON = null;
        resultJSON = (PBSResultJSON)callServer(url,PBSResultJSON.class.getName());
        if (resultJSON !=null){
            if (resultJSON.getVersion()!=null) {
                return true;
            } else {
                //invalid server request will return false.
                return false;
            }
        } else {
            //invalid server address will return false.
            return false;
        }
    }

    @Override
    public boolean userLogOut(final String username, final String authToken, final String serverURL) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.AUTH_JSP);
        //  "/wstore/Auth.jsp?action=Logout&loginName=" + username + "&authToken=" + authToken;
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s&%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.LOGOUT, "UTF-8"),
                    PBSServerConst.USER_NAME, URLEncoder.encode(username, PBSServerConst.UTF_8), PBSServerConst.AUTH_TOKEN,  URLEncoder.encode(authToken, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        url += query;
        //String url = serverURL + "/wstore/Auth.jsp?action=Logout&username=" + username + "&token=" + token + "&serial=" + serial;
        PBSResultJSON resultJSON;
        resultJSON = (PBSResultJSON)callServer(url,PBSResultJSON.class.getName());
        boolean result = false;
        if (resultJSON != null){
            if (resultJSON.getSuccess().equals(PBSResultJSON.FALSE_TEXT)) {
                return result;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean authenticateTokenServer(final String username, final String authToken, final String serverURL, final String deviceID) {
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.AUTH_JSP);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s&%s=%s&%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.LOGIN, PBSServerConst.UTF_8),
                    PBSServerConst.USER_NAME, URLEncoder.encode(username, PBSServerConst.UTF_8), PBSServerConst.AUTH_TOKEN,  URLEncoder.encode(authToken,PBSServerConst.UTF_8),
                    PBSServerConst.SERIAL, URLEncoder.encode(deviceID, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        url += query;
        //String url = serverURL + "/wstore/Auth.jsp?action=Login&username=" + username + "&token=" + token + "&serial=" + serial;
        PBSResultJSON resultJSON;
        boolean result;
        resultJSON = (PBSResultJSON)callServer(url, PBSResultJSON.class.getName());
        if (resultJSON != null) {
            if (resultJSON.getSuccess().equals(PBSResultJSON.FALSE_TEXT)) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public boolean submitRole(final String roleID, final String orgID, final String clientID, final String serverURL) {
        //  http://server:port/API/Auth.jsp?action=SetRoleOrg&role=1000001&org=1000000
        String url = getURL(serverURL, PBSServerConst.PATH, PBSServerConst.AUTH_JSP);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s&%s=%s&%s=%s", PBSServerConst.ACTION, URLEncoder.encode(PBSServerConst.SET_ROLE_ORG, "UTF-8"),
                    PBSServerConst.ROLE, URLEncoder.encode(roleID, PBSServerConst.UTF_8), PBSServerConst.ORG,  URLEncoder.encode(orgID, PBSServerConst.UTF_8),
                    PBSServerConst.CLIENT, URLEncoder.encode(clientID, PBSServerConst.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        url += query;
        //String url = serverURL + "/wstore/Auth.jsp?action=Login&username=" + username + "&token=" + token + "&serial=" + serial;
        PBSResultJSON resultJSON;
        boolean result;
        resultJSON = (PBSResultJSON)callServer(url, PBSResultJSON.class.getName());
        if (resultJSON != null){
            if (resultJSON.getSuccess().equals(PBSResultJSON.FALSE_TEXT)) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }

        return result;
    }
}
