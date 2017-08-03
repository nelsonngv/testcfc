package com.pbasolutions.android;

import android.net.Uri;

import com.pbasolutions.android.model.ModelConst;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by pbadell on 6/25/15.
 */
public class PBSServerConst {

    public static final String RESULT = "RESULT";
    public static final String AUTH_JSP = "Auth.jsp?";

    public static final String PATH = "/wstore/";

    /**
     * Sync JSP Wrapper used.
     */
    public static final String SYNC_JSP = "Sync.jsp?";
    /**
     * Action parameter
     */
    public static final String ACTION = "action";
    /**
     * Login parameter
     */
    public static final String LOGIN = "Login";
    /**
     * Logout parameter
     */
    public static final String LOGOUT = "Logout";
    /**
     * Auth token parameter
     */
    public static final String AUTH_TOKEN = "token";
    /**
     * Authentication Token Action
     */
    public static final String TOKEN = "AuthToken";
    /**
     * And parameter
     */
    public static final char AND = '&';
    /**
     * Login name parameter
     */
    public static final String USER_NAME = "username";
    /**
     * Password parameter
     **/
    public static final String PASSWORD = "password";
    /**
     * Android device serial number or identifier.
     **/
    public static final String SERIAL = "serial";
    /**
     * Server version.
     */
    public static final String VERSION = "Version";
    /**
     * Server version.
     */
    public static final String PARAM_URL = "URL";
    /**
     * Action update
     */
    public static final String ACTION_UPDATE = "Update";

    /**
     * Action update
     */
    public static final String SYNC = "Sync";

    /**
     * Action update
     */
    public static final String ACTION_GET_UNSYNC_COUNT= "GetUnsyncCount";
    /**
     *
     */
    public static final String SET_ROLE_ORG = "SetRoleOrg";
    /**
     *
     */
    public static final String ROLE = "role";
    /**
     *
     */
    public static final String ORG = "org";
    /**
     *
     */
    public static final String CLIENT = "client";
    /**
     * UT8 String.
     */
    public static final String UTF_8 = "UTF-8";
    public static final String CFC_JSP = "CFC.jsp?";
    public static final String GET_NOTES = "getNotes";
    public static final String USERID = "userID";
    public static final String PROJLOCID = "projLocID";
    public static final String PROJTASKID = "projTaskID";
    public static final String GET_PROJTASKS = "getProjTasks";
    public static final String COMPLETE_PROJTASK = "completeProjTask";
    public static final String COMMENTS = "comments";
    public static final String CREATE_REQUISITION = "createRequisition";
    public static final String PRODID = "prodID";
    public static final String QTY = "qty";
    public static final String CREATED = "created";
    public static final String DATEREQUIRED = "dateRequired";
    public static final String AD_USER_ID_COL = "AD_User_ID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String SUCCESS = "Success";
    public static final String ASI = "asi";
    public static final String GET_REQUISITIONS = "getRequisitions";
    public static final String TRUE = "True" ;
    public static final String FALSE = "False";
    public static final String GET_MOVEMENTS = "getMovements";
    public static final String GET_MSTORAGE = "getMStorage";
    public static final String CREATE_MOVEMENT = "createMovement";
    public static final String CREATE_PROJECTTASK = "createProjTask";
    public static final String COMPLETE_MOVEMENT = "completeMovement";
    public static final String GET_DEPLOYMENTS = "getDeployments";
    public static final String UPDATE_ATTENDANCE = "updateAttendance";
    public static final String SEARCH_ATTENDANCE = "getAttendance";
    public static final String ATTACH_TO_PROJTASK = "attachToProjTask";


    public static CookieStore cookieStore;
    /**
     * Instantiate cookie instance.
     */
    public static void instantiateCookie(){
        cookieStore = new BasicCookieStore();
        CookieHandler.setDefault(new CookieManager());
    }

}