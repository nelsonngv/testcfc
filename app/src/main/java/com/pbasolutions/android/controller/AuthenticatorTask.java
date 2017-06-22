package com.pbasolutions.android.controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.authentication.PBSIServerAuthenticate;
import com.pbasolutions.android.authentication.PBSServerAuthenticate;
import com.pbasolutions.android.database.PBSDBHelper;
import com.pbasolutions.android.json.PBSLoginJSON;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.json.PBSRoleJSON;
import com.pbasolutions.android.json.PBSTableJSON;
import com.pbasolutions.android.model.ModelConst;

import java.io.IOException;


/**
 * Created by pbadell on 1/5/16.
 */
public class AuthenticatorTask extends Task {

    private static final String TAG = "AuthenticatorTask";

    private ContentResolver cr;

    private AccountManager am;

    private Context ctx;

    public final static int SINGLE_ACCOUNT = 0;
    public AuthenticatorTask(ContentResolver cr, AccountManager am, Context ctx) {
        this.cr = cr;
        this.am = am;
        this.ctx = ctx;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event) {
            case PBSAuthenticatorController.SUBMIT_LOGIN: {
                return submitLogin();
            }
            case PBSAuthenticatorController.AUTHENTICATE_SERVER: {
                return authenticateServer();
            }
            case PBSAuthenticatorController.AUTHENTICATE_TOKEN: {
                return authenticateToken();
            }
            case PBSAuthenticatorController.AUTHENTICATE_TOKEN_SERVER: {
                return authTokenServer();
            }
            case PBSAuthenticatorController.GET_USER_ID: {
                return getUserID();
            }
            case PBSAuthenticatorController.LOG_OUT: {
                return logOut();
            }
            case PBSAuthenticatorController.CLEAR_AUTH_TOKEN: {
                clearAuthToken();
                return null;
            }
            case PBSAuthenticatorController.ROLE_SUBMIT_EVENT: {
                return submitRole();
            }
            case PBSAuthenticatorController.GET_USER_ACCOUNT_EVENT: {
                return getUserAccount();
            }
            case PBSAuthenticatorController.GET_ACCOUNT_DATA_EVENT: {
                return getAccountData();
            }
            case PBSAuthenticatorController.SET_ACCOUNT_DATA_EVENT: {
                setAccountData();
                return null;
            }
            case PBSAuthenticatorController.SET_ACCOUNT_USERDATA_EVENT: {
                return setAccountUserData();
            }
            default:
                return null;
        }
    }

    private Bundle setAccountUserData() {
        Account account = getAccount(input.getString(PBSAuthenticatorController.USER_NAME_ARG),
                input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE));
        String args[] = input.getStringArray(PBSAuthenticatorController.ARRAY_ARG);
        try {
            for (String arg : args) {
                am.setUserData(account, arg, input.getString(arg));
            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage() + PandoraConstant.SPACE
                    + Log.getStackTraceString(e));
        }
        return null;
    }

    public Account getAccount(String userName, String accountType) {
        for (Account account : getAccounts(accountType)) {
            if (account.name.equals(userName)) {
                return account;
            }
        }
        return null;
    }

    public Account[] getAccounts(String accountType) {
        return am.getAccountsByType(accountType);
    }

    private void setAccountData() {
        try {
            Account account = getAccount(input.getString(PBSAuthenticatorController.USER_NAME_ARG),
                    input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE));
            am.setUserData(account, PBSAuthenticatorController.SERVER_URL_ARG,
                    input.getString(PBSAuthenticatorController.SERVER_URL_ARG));
            am.setUserData(account, PBSAuthenticatorController.ROLE_ARG,
                    input.getString(PBSAuthenticatorController.ROLE_ARG));
            am.setUserData(account, PBSAuthenticatorController.ORG_ARG,
                    input.getString(PBSAuthenticatorController.ORG_ARG));
            am.setUserData(account, PBSAuthenticatorController.CLIENT_ARG,
                    input.getString(PBSAuthenticatorController.CLIENT_ARG));
            am.setUserData(account, PBSAuthenticatorController.PROJLOC_ARG,
                    input.getString(PBSAuthenticatorController.PROJLOC_ARG));
            am.setUserData(account, PBSAuthenticatorController.PROJLOC_ID_ARG,
                    input.getString(PBSAuthenticatorController.PROJLOC_ID_ARG));
            am.setUserData(account, PBSAuthenticatorController.PROJLOC_NAME_ARG,
                    input.getString(PBSAuthenticatorController.PROJLOC_NAME_ARG));
            am.setUserData(account, PBSAuthenticatorController.ROLES_ARG,
                    input.getString(PBSAuthenticatorController.ROLES_ARG));
            am.setUserData(account, PBSAuthenticatorController.ROLE_INDEX_ARG,
                    input.getString(PBSAuthenticatorController.ROLE_INDEX_ARG));
            am.setUserData(account, PBSAuthenticatorController.ORG_INDEX_ARG,
                    input.getString(PBSAuthenticatorController.ROLE_INDEX_ARG));
            am.setUserData(account, PBSAuthenticatorController.CLIENT_INDEX_ARG,
                    input.getString(PBSAuthenticatorController.CLIENT_INDEX_ARG));
            am.setUserData(account, PBSAuthenticatorController.PROJLOC_INDEX_ARG,
                    input.getString(PBSAuthenticatorController.PROJLOC_INDEX_ARG));
            am.setUserData(account, PBSAuthenticatorController.PROJLOCS_ARG,
                    input.getString(PBSAuthenticatorController.PROJLOCS_ARG));
            am.setUserData(account, PBSAuthenticatorController.AD_USER_ARG,
                    input.getString(PBSAuthenticatorController.AD_USER_ARG));

            am.setUserData(account, PBSAuthenticatorController.INITIALSYNC_ARG,
                    input.getString(PBSAuthenticatorController.INITIALSYNC_ARG));
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage()
                    + PandoraConstant.SPACE
                    + Log.getStackTraceString(e));
        }
    }

    /**
     *
     * @return
     */
    private Bundle getAccountData() {
        final String accountType = input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE);
        final Account userAccount[] = getAccounts(accountType);
        if (userAccount.length > 1) {
            String names[] = new String[userAccount.length];
            //prompt user. which account they want to use?
            for (int x = 0; x < userAccount.length; x++) {
                names[x] = userAccount[x].name;
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Select Account")
                    .setItems(names, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            populateUserAccountData(userAccount[which]);
                            restoreGlobalVariables();
                        }
                    });

            ((PandoraMain)ctx).runOnUiThread(new Runnable() {
                public void run() {
                    builder.create().show();
                }
            });
        } else if (userAccount.length == 1) {
           populateUserAccountData(userAccount[SINGLE_ACCOUNT]);
        }
        return output;
    }

    private void populateUserAccountData(Account acc){
        output.putParcelable(PBSAuthenticatorController.USER_ACC_ARG,
                getAccount(acc.name,
                        input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE)));
        output.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG,
                getAuthToken(acc, input.getString(
                        PBSAuthenticatorController.ARG_AUTH_TYPE)));
        output.putString(PBSAuthenticatorController.USER_NAME_ARG,
                acc.name);
        output.putString(PBSAuthenticatorController.USER_PASS_ARG,
                am.getPassword(acc));
        output.putString(PBSAuthenticatorController.SERVER_URL_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.SERVER_URL_ARG));
        output.putString(PBSAuthenticatorController.SERIAL_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.SERIAL_ARG));
        output.putString(PBSAuthenticatorController.ROLE_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.ROLE_ARG));
        output.putString(PBSAuthenticatorController.ORG_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.ORG_ARG));
        output.putString(PBSAuthenticatorController.CLIENT_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.CLIENT_ARG));
        output.putString(PBSAuthenticatorController.PROJLOC_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.PROJLOC_ARG));
        output.putString(
                PBSAuthenticatorController.PROJLOC_ID_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.PROJLOC_ID_ARG));
        output.putString(
                PBSAuthenticatorController.PROJLOCS_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.PROJLOCS_ARG));
        output.putString(PBSAuthenticatorController.PROJLOC_NAME_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.PROJLOC_NAME_ARG));
        //Convert the role json to string.
        output.putString(
                PBSAuthenticatorController.ROLES_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.ROLES_ARG));
        output.putString(PBSAuthenticatorController.ROLE_INDEX_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.ROLE_INDEX_ARG));
        output.putString(
                PBSAuthenticatorController.ORG_INDEX_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.ORG_INDEX_ARG));
        output.putString(
                PBSAuthenticatorController.CLIENT_INDEX_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.CLIENT_INDEX_ARG));
        output.putString(
                PBSAuthenticatorController.PROJLOC_INDEX_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.PROJLOC_INDEX_ARG));
        output.putString(
                PBSAuthenticatorController.AD_USER_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.AD_USER_ARG));

        output.putString(
                PBSAuthenticatorController.INITIALSYNC_ARG,
                am.getUserData(acc,
                        PBSAuthenticatorController.INITIALSYNC_ARG));
    }

    private String authToken;
    /**
     *
     * @param account
     * @param authType
     * @return
     */
    public String getAuthToken(Account account, String authType) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        OnTokenAcquired acquired = new OnTokenAcquired();
        AccountManagerFuture<Bundle>
                future = am.getAuthToken(account, authType, null, (Activity) ctx, acquired, null);
        acquired.run(future);
        return authToken;
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Do something useful
            try {
                if (result.isDone()) {
                    Bundle output = result.getResult();
                    if (output != null) {
                        authToken = output.getString(AccountManager.KEY_AUTHTOKEN);
                    }
                }
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            }
        }
    }

    private Bundle getUserAccount() {
        Account userAccount = getAccount(input.getString(PBSAuthenticatorController.USER_NAME_ARG),
                input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE));
        if (userAccount != null) {
            output.putParcelable(PBSAuthenticatorController.USER_ACC_ARG, userAccount);
        }
        return output;
    }

    private Bundle submitRole() {
        PBSIServerAuthenticate sServerAuthenticate = new PBSServerAuthenticate();
        PBSLoginJSON status = sServerAuthenticate.submitRole(
                input.getString(PBSAuthenticatorController.ROLE_ARG),
                input.getString(PBSAuthenticatorController.ORG_ARG),
                input.getString(PBSAuthenticatorController.CLIENT_ARG),
                input.getString(PBSAuthenticatorController.SERVER_URL_ARG));
        if (status != null && status.getSuccess().equals("TRUE")) {
            PandoraHelper.populateMenuForms(status.getForms());
            output.putBoolean(PBSServerConst.RESULT, true);
        } else {
            output.putBoolean(PBSServerConst.RESULT, false);
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Unable to submit role selection to server.");
        }
        return output;
    }

    private void clearAuthToken() {
        Account account = getAccount(input.getString(PBSAuthenticatorController.USER_NAME_ARG),
                input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE));
        String authType = input.getString(PBSAuthenticatorController.ARG_AUTH_TYPE);
        String authToken = getAuthToken(account, authType);
        if (authToken != null) {
            authToken = null;
            am.setAuthToken(account, authType, authToken);
            am.invalidateAuthToken(PBSAccountInfo.ACCOUNT_TYPE, authToken);
        }
    }

    private Bundle logOut() {
        //192.168.4.30:8081/wstore/Auth.jsp
        PBSIServerAuthenticate sServerAuthenticate = new PBSServerAuthenticate();
        boolean logOutResult = sServerAuthenticate.userLogOut(input.getString(
                        PBSAuthenticatorController.USER_NAME_ARG),
                input.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG),
                input.getString(PBSAuthenticatorController.SERVER_URL_ARG));
        if (logOutResult == true) {
            // remove account
//            String userName = ((PandoraMain) ctx).globalVariable.getAd_user_name();
//            String accType = PBSAccountInfo.ACCOUNT_TYPE;
//            Account userAccount = getAccount(userName, accType);
//            am.removeAccount(userAccount, null, null);

//            ((PandoraMain) ctx).globalVariable = null;
            ((PandoraMain) ctx).globalVariable.setAd_user_name("");
            ((PandoraMain) ctx).globalVariable.setAd_user_password("");
            ((PandoraMain) ctx).globalVariable.setAuth_token("");
            PandoraHelper.populateMenuForms(null);
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully logged out");
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.FAIL);
            output.putString(PandoraConstant.FAIL, "Server unable to log out user");
        }
        return output;
    }

    private Bundle getUserID() {
        String[] name = {input.getString(ModelConst.NAME_COL)};

        try {
            Cursor queryResult = cr.query(ModelConst.uriCustomBuilder(ModelConst.AD_USER_TABLE),
                    null, ModelConst.NAME_COL + " = ?", name, null);
            if (queryResult != null) {
                queryResult.moveToFirst();
                output.putString(ModelConst.AD_USER_UUID_COL, queryResult.getString(
                        queryResult.getColumnIndex(ModelConst.AD_USER_UUID_COL)));
                queryResult.close();
            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        return output;
    }

    private Bundle authenticateToken() {
        String accountType = input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE);
        String userName = input.getString(PBSAuthenticatorController.USER_NAME_ARG);

        Account arrayAccounts[] = am.getAccountsByType(input.getString
                (PBSAuthenticatorController.ARG_ACCOUNT_TYPE));
        if (arrayAccounts.length > 0 &&
                input.getString(PBSAuthenticatorController.USER_NAME_ARG) != null) {
            String authToken = getAuthToken(getAccount(userName, accountType),
                    input.getString(PBSAuthenticatorController.ARG_AUTH_TYPE));
            if (authToken != null) {
                output.putBoolean(PBSAuthenticatorController.AUTH_TOKEN_ARG, true);
            } else {
                output.putBoolean(PBSAuthenticatorController.AUTH_TOKEN_ARG, false);
                output.putString(PandoraConstant.LOGGED_OUT,
                        "Please re-login, you mighted have logged out in previous session");
            }
        } else {
            output.putBoolean(PBSAuthenticatorController.AUTH_TOKEN_ARG, false);
            output.putString(PandoraConstant.LOGIN,
                    "Welcome to Pandora, please login to use this app");
        }
        return output;
    }

    private Bundle authenticateServer() {
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        String serverURL = input.getString(PBSServerConst.VERSION);
        //192.168.4.30:8081/wstore/Auth.jsp
        PBSIServerAuthenticate sServerAuthenticate = new PBSServerAuthenticate();
        boolean isAuthenticate = sServerAuthenticate.authenticateServerURI(serverURL);
        if (isAuthenticate) {
            output.putBoolean(PBSServerConst.RESULT, true);
        } else {
            output.putBoolean(PBSServerConst.RESULT, false);
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "The specified server is unavailable. Please try again later.");
        }
        return output;
    }

    private Bundle submitLogin() {
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        try {
            final String userName = input.getString(PBSAuthenticatorController.ARG_ACCOUNT_NAME);
            final String userPass = input.getString(PBSAuthenticatorController.USER_PASS_ARG);
            final String authType = input.getString(PBSAuthenticatorController.ARG_AUTH_TYPE);
            final String deviceID = input.getString(PBSAuthenticatorController.SERIAL_ARG);
            final String serverURL = input.getString(PBSAuthenticatorController.SERVER_URL_ARG);
            final String accType = input.getString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE);
            PBSIServerAuthenticate sServerAuthenticate = new PBSServerAuthenticate();
            PBSLoginJSON user = sServerAuthenticate.userSignIn(userName, userPass, deviceID, serverURL);
            if (user != null) {
                if (PBSServerConst.cookieStore == null) {
                    PBSServerConst.instantiateCookie();
                }
                if (user.getSuccess().equals("TRUE")) {
//                    PandoraHelper.populateMenuForms(user.getForms());
                    Account arrayAccounts[] = getAccounts(accType);

                    // clear db and account if connects to another server
                    if (!PandoraMain.instance.getGlobalVariable().getServer_url().equals("") && !serverURL.equalsIgnoreCase(PandoraMain.instance.getGlobalVariable().getServer_url())) {
                        PBSDBHelper.reCreateDatabase(ctx.getApplicationContext());
                        PandoraMain.instance.resetServerData(serverURL);
                        PandoraMain.instance.setGlobalVariable(null);
                        PandoraHelper.populateMenuForms(null);

                        // remove all accounts
                        for (int i = 0; i < arrayAccounts.length; i++) {
                            am.removeAccount(getAccount(arrayAccounts[i].name, accType), null, null);
                        }
                    }

                    //if account already created.
                    if (arrayAccounts.length > 0) {
                        Account userAccount = getAccount(userName, accType);
//                        Account userAccount2;
//                        // allow only 1 account to be stored on the phone
//                        for (int i = 0; i < arrayAccounts.length; i++) {
//                            if (!arrayAccounts[i].name.equals(userName)) {
//                                userAccount2 = getAccount(arrayAccounts[i].name, accType);
//                                am.removeAccount(userAccount2, null, null);
//                            }
//                        }
                        if (userAccount != null) {
                            //work around: delete account and recreate new.
                            //   accountManager.removeAccount(userAccount, null, null);
                            createNewAccount(userName, accType, deviceID, serverURL,
                                    userPass, authType, user.getToken());
                        } else {
                            createNewAccount(userName, accType, deviceID, serverURL,
                                    userPass, authType, user.getToken());
                        }
                    } else {
                        PandoraMain.instance.resetServerData(serverURL);
                        createNewAccount(userName, accType, deviceID, serverURL,
                                userPass, authType, user.getToken());
                    }
                    //insert data into master data tables. for first time, check the master
                    //table data is empty first.
                    for (PBSTableJSON table : user.getTables()) {
                        ContentValues cv = ModelConst.mapDataToContentValues(table, cr);
                        String selection = ModelConst.getTableColumnIdName(table.getTableName(),cv);
                        String[] arg = {cv.getAsString(selection)};
                        String tableName = table.getTableName();
                        if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                            ModelConst.insertTableRow(cr, tableName, cv, selection, arg);
                        } else {
                            ModelConst.updateTableRow(cr, tableName, cv, selection, arg);
                        }
                    }
                    Bundle extras = new Bundle();
                    Account userAccount = getAccount(userName, accType);
                    ContentResolver.setIsSyncable(userAccount, PBSAccountInfo.ACCOUNT_AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(userAccount, PBSAccountInfo.ACCOUNT_AUTHORITY, true);
                    ContentResolver.requestSync(userAccount, PBSAccountInfo.ACCOUNT_AUTHORITY, extras);
                    ContentResolver.addPeriodicSync(userAccount, PBSAccountInfo.ACCOUNT_AUTHORITY, extras, user.getSyncInterval());

                    output.putSerializable(PBSAuthenticatorController.PBS_LOGIN_JSON, user);
                    output.putBoolean(PBSServerConst.RESULT, true);
                } else {
                    output.putBoolean(PBSServerConst.RESULT, false);
                    output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                    output.putString(PandoraConstant.ERROR, "Invalid username or password." +
                            " Please try again.");
                }
            }

            return  output;
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE +e.getMessage());
            e.printStackTrace();
        }
        output.putBoolean(PBSServerConst.RESULT, false);
        return output;
    }

    private void createNewAccount(String userName, String accType, String deviceID,
                                  String serverURL, String userPass, String authType,
                                  String token) {
        Account newAccount = new Account(userName, accType);
        Bundle userData = new Bundle();
        userData.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);
        userData.putString(PBSAuthenticatorController.SERVER_URL_ARG, serverURL);
        am.addAccountExplicitly(newAccount, userPass, userData);
        am.setAuthToken(newAccount, authType, token);
    }

    /**
     * @return
     */
    private Bundle authTokenServer() {
        String userName = input.getString(PBSAuthenticatorController.USER_NAME_ARG);
        String serverURL = input.getString(PBSAuthenticatorController.SERVER_URL_ARG);
        String authToken = input.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG);
        String deviceID = input.getString(PBSAuthenticatorController.SERIAL_ARG);
        boolean isError = false;
        if (authToken != null) {
            PBSIServerAuthenticate sServerAuthenticate = new PBSServerAuthenticate();
            boolean isAuthenticateTokenServer = sServerAuthenticate.
                    authenticateTokenServer(userName, authToken, serverURL, deviceID);
            if (isAuthenticateTokenServer) {
                output.putBoolean(PBSServerConst.RESULT, true);
            } else {
                isError = true;
            }
        } else {
            isError = true;
        }
        if (isError) {
            output.putBoolean(PBSServerConst.RESULT, false);
            output.putString(PandoraConstant.TITLE, PandoraConstant.FAIL);
            output.putString(PandoraConstant.FAIL, "The token is " +
                    "invalid/expired. Please re-login.");
        }
        return output;
    }

    private void restoreGlobalVariables() {
        if(((PandoraMain)ctx).getGlobalVariable() == null)
            ((PandoraMain)ctx).setGlobalVariable((PandoraContext) ctx.getApplicationContext());
        PandoraContext var = ((PandoraMain)ctx).getGlobalVariable();
        if (output.getString(PBSAuthenticatorController.USER_NAME_ARG) != null){
            var.setAd_user_name(output.getString(PBSAuthenticatorController.USER_NAME_ARG));
            var.setAd_user_password(
                    output.getString(PBSAuthenticatorController.USER_PASS_ARG));
            var.setServer_url(
                    output.getString(PBSAuthenticatorController.SERVER_URL_ARG));

            String synced = output.getString(PBSAuthenticatorController.INITIALSYNC_ARG);
            if (synced != null)
                var.setIsInitialSynced(Integer.parseInt(synced) != 0);
        }

        if (output.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG) != null) {
            //   resultBundle.putString(AUTH_TOKEN_ARG, getAuthToken(userAccount[which], inputBundle.getString(ARG_AUTH_TYPE)));
            var.setAd_user_password(
                    output.getString(PBSAuthenticatorController.USER_PASS_ARG));
            var.setServer_url(
                    output.getString(PBSAuthenticatorController.SERVER_URL_ARG));
            var.setSerial(
                    output.getString(PBSAuthenticatorController.SERIAL_ARG));
            var.setAuth_token(
                    output.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG));
            var.setAd_role_id(
                    output.getString(PBSAuthenticatorController.ROLE_ARG));
            var.setAd_org_id(
                    output.getString(PBSAuthenticatorController.ORG_ARG));
            var.setAd_client_id(
                    output.getString(PBSAuthenticatorController.CLIENT_ARG));
            var.setAd_user_id(
                    output.getString(PBSAuthenticatorController.AD_USER_ARG));
            var.setC_projectlocation_uuid(
                    output.getString(PBSAuthenticatorController.PROJLOC_ARG));
            var.setC_projectlocation_id(
                    output.getString(PBSAuthenticatorController.PROJLOC_ID_ARG));
            var.setC_projectlocation_name(
                    output.getString(PBSAuthenticatorController.PROJLOC_NAME_ARG)
            );

            String projLoc = output.getString(PBSAuthenticatorController.PROJLOCS_ARG);
            if(projLoc != null && !projLoc.equalsIgnoreCase("null")) {
                PBSProjLocJSON projLocs[] = new Gson().fromJson(projLoc, PBSProjLocJSON [].class);
                var.setProjLocJSON(projLocs);
            }
            //Convert the role json to string.
            PBSRoleJSON role[] = (PBSRoleJSON[]) new Gson().fromJson(output.getString(PBSAuthenticatorController.ROLES_ARG), PBSRoleJSON[].class);
            var.setRoleJSON(role);
            if (output.getString(PBSAuthenticatorController.ROLE_INDEX_ARG) != null) {
                var.setAd_role_spinner_index(Integer.parseInt(output.getString(PBSAuthenticatorController.ROLE_INDEX_ARG)));
            }

            if (output.getString(PBSAuthenticatorController.ORG_INDEX_ARG) != null) {
                var.setAd_org_spinner_index(Integer.parseInt(output.getString(PBSAuthenticatorController.ORG_INDEX_ARG)));
            }

            if (output.getString(PBSAuthenticatorController.CLIENT_INDEX_ARG) != null) {
                var.setAd_client_spinner_index(Integer.parseInt(output.getString(PBSAuthenticatorController.CLIENT_INDEX_ARG)));
            }

            String projLocIndex = output.getString(PBSAuthenticatorController.PROJLOC_INDEX_ARG);
            if (projLocIndex != null)
                var.setC_ProjectLocation_Spinner_Index(Integer.parseInt(projLocIndex));

        }

        ((PandoraMain)ctx).runOnUiThread(new Runnable() {
            public void run() {
                if (((PandoraMain)ctx).getGlobalVariable() != null) {
                    if (((PandoraMain)ctx).getGlobalVariable().getAd_user_name() != null) {
                        ((TextView) ((PandoraMain) ctx).findViewById(R.id.accountName))
                                .setText(((PandoraMain) ctx).getGlobalVariable().getAd_user_name());
                    }
                    if (((PandoraMain)ctx).getGlobalVariable().getAd_user_password() != null) {
                        ((TextView) ((PandoraMain)ctx).findViewById(R.id.accountPassword))
                                .setText(((PandoraMain)ctx).getGlobalVariable().getAd_user_password());
                    }
                    if (((PandoraMain)ctx).getGlobalVariable().getServer_url() != null) {
                        ((TextView) ((PandoraMain)ctx).findViewById(R.id.serverURL))
                                .setText(((PandoraMain)ctx).getGlobalVariable().getServer_url());
                    }
                }
            }
        });
    }

}
