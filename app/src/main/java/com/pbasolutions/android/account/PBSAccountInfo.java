package com.pbasolutions.android.account;

/**
 * Created by pbadell on 6/25/15.
 * This is a constant class which holds account information of Pandora App.
 */
public class PBSAccountInfo {

    /**
     * Account type id
     * Required by the Android authenticator to differentiate available accounts in Account Manager.
     */
    public static final String ACCOUNT_TYPE = "com.pbasolutions.android";
    /**
     * Content provider authority.
     */
    public static final String ACCOUNT_AUTHORITY = "com.pbasolutions.android.provider";
    /**
     * Auth token type full access.
     */
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    /**
     * Auth token type  sync.
     */
    public static final String AUTHTOKEN_TYPE_SYNC = "Sync";

}
