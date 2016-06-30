package com.pbasolutions.android.controller;

import android.accounts.AccountManager;
import android.content.Context;

/**
 * Created by pbadell on 6/26/15.
 */
public class PBSAuthenticatorController extends PBSController {
    /**
     * Class name tag.
     */
    private static final String TAG = "PBSAuthenticatorCon";
    /**
     * Static strings.
     */
    public static final String AUTHENTICATE_SERVER = "AUTHENTICATE_SERVER";
    public static final String AUTHENTICATE_TOKEN = "AUTHENTICATE_TOKEN";
    public static final String AUTHENTICATE_TOKEN_SERVER = "AUTHENTICATE_TOKEN_SERVER";
    public static final String SUBMIT_LOGIN = "SUBMIT_LOGIN";
    public static final String GET_USER_ID = "GET_USER_ID";
    public static final String LOG_OUT = "LOG_OUT";
    public static final String CLEAR_AUTH_TOKEN = "CLEAR_AUTH_TOKEN";
    public static final String USER_NAME_ARG= "USER_NAME";
    public static final String USER_PASS_ARG= "USER_PASS";
    public static final String USER_ACC_ARG= "USER_ACC";
    public static final String AUTH_TOKEN_ARG = "AUTH_TOKEN";
    public static final String SERVER_URL_ARG = "SERVER_URL";
    public static final String SUCCESSFULLY_LOGGED_OUT_TXT  = "Successfully logged out";
    public static final String PBS_LOGIN_JSON = "PBSLoginJSON";
    public static final String SERIAL_ARG = "SERIAL_ARG";
    public static final String ROLE_SUBMIT_EVENT = "ROLE_SUBMIT_EVENT";
    public static final String ROLE_ARG = "ROLE_ARG";
    public static final String ORG_ARG = "ORG_ARG";
    public static final String CLIENT_ARG = "CLIENT_ARG";
    public static final String PROJLOC_ARG = "PROJLOC_ARG";
    public static final String PROJLOC_ID_ARG = "PROJLOC_ID_ARG";
    public static final String PROJLOC_UUID_ARG = "PROJLOC_UUID_ARG";
    public static final String PROJLOCS_ARG = "PROJLOCS_ARG";
    public static final String GET_USER_ACCOUNT_EVENT = "GET_USER_ACCOUNT_EVENT";
    public static final String GET_ACCOUNT_DATA_EVENT = "GET_ACCOUNT_DATA_EVENT";
    public static final String SET_ACCOUNT_DATA_EVENT = "SET_ACCOUNT_DATA_EVENT";
    public static final String SET_ACCOUNT_USERDATA_EVENT = "SET_ACCOUNT_USERDATA_EVENT";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    public static final String ROLES_ARG = "ROLES_ARG";
    public static final String ROLE_INDEX_ARG = "ROLE_INDEX_ARG";
    public static final String ORG_INDEX_ARG = "ORG_INDEX_ARG";
    public static final String CLIENT_INDEX_ARG = "CLIENT_INDEX_ARG";
    public static final String PROJLOC_INDEX_ARG = "PROJLOC_INDEX_ARG";
    public static final String ARRAY_ARG = "ARRAY_ARG";
    public static final String AD_USER_ARG = "AD_USER_ARG";
    public static final String PROJLOC_NAME_ARG = "PROJLOC_NAME_ARG";
    public static final String ARG_PROGRESS_BAR = "ARG_PROGRESS_BAR";

    public static final String INITIALSYNC_ARG = "INITIALSYNC_ARG";

    /**
     * Account Manager.
     */
    private AccountManager accountManager;
    /**
     * Context.
     */
    private Context context;

    /**
     * Constructor.
     * @param ctx
     */
    public PBSAuthenticatorController(Context ctx) {
        super(ctx);
        cr = getContentResolver();
        accountManager = AccountManager.get(ctx);
        task = new AuthenticatorTask(cr, accountManager, ctx);
    }
}
