package com.pbasolutions.android.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;

import com.pbasolutions.android.PandoraController;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSServerController;
import com.pbasolutions.android.json.PBSProjLocJSON;

import java.io.IOException;

/**
 * Class that handle syncing in accounts.
 * Created by pbadell on 6/30/15.
 */
public class PBSSyncAdapter extends AbstractThreadedSyncAdapter {

    /**
     * Class name tag.
     */
    private static final String TAG = "PBSServer";
    private final AccountManager accountManager;
    private Context context;

    /**
     *
     * @param context
     * @param autoInitialize
     */
    public PBSSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        accountManager = AccountManager.get(context);
        this.context = context;
    }

    /**
     * Perform a sync for this account. SyncAdapter-specific parameters may
     * be specified in extras, which is guaranteed to not be null. Invocations
     * of this method are guaranteed to be serialized.
     *
     * @param account    the account that should be synced
     * @param extras     SyncAdapter-specific parameters
     * @param authority  the authority of this sync request
     * @param provider   a ContentProviderClient that points to the ContentProvider for this
     *                   authority
     * @param syncResult SyncAdapter-specific parameters
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Context context;
        if (PandoraMain.instance != null)
            context = PandoraMain.instance.getApplicationContext();
        else context = getContext();

        //call the controller update sync here.
        PBSServerController serverController = new PBSServerController(context);
        PBSAuthenticatorController authController = new PBSAuthenticatorController(context);
        try {
            PandoraContext global =  ((PandoraContext)context);

            //work around to get latest authToken after authToken has been changed.
          //  String globalAuthToken = global.getAuth_token();
            String authToken = accountManager.blockingGetAuthToken(account,
                    PBSAccountInfo.AUTHTOKEN_TYPE_SYNC, true);
          //  if (!authToken.equalsIgnoreCase(globalAuthToken) && !globalAuthToken.isEmpty()){
            //    authToken = globalAuthToken;
           // }

            //work around to get latest serverURL after serverURL has been changed.
//            String globalServerURL = global.getServer_url();
            String serverURL = accountManager.getUserData(account,
                    PBSAuthenticatorController.SERVER_URL_ARG);
//            if (!serverURL.equalsIgnoreCase(globalServerURL) && !globalServerURL.isEmpty()) {
//                serverURL = globalServerURL;
//            }

            //work around to get latest deviceID after deviceID has been changed.
            String globalDeviceID = global.getSerial();
            String deviceID = accountManager.getUserData(account,
                    PBSAuthenticatorController.SERIAL_ARG);
            if (!deviceID.equalsIgnoreCase(globalDeviceID) && !globalDeviceID.isEmpty()) {
                deviceID = globalDeviceID;
            }
            String userName = account.name;

            Bundle inputAuth = new Bundle();
            inputAuth.putString(PBSAuthenticatorController.USER_NAME_ARG, userName);
            inputAuth.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG, authToken);
            inputAuth.putString(PBSAuthenticatorController.SERVER_URL_ARG, serverURL);
            inputAuth.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);

            Bundle updateResultBundle = new Bundle();
            Bundle deleteRetentionPeriod = new Bundle();
            Bundle syncResultBundle = new Bundle();
            Bundle authenticateResult = new Bundle();

            authenticateResult = authController.triggerEvent(
                    PBSAuthenticatorController.AUTHENTICATE_TOKEN_SERVER,
                    inputAuth, authenticateResult, null);

            boolean isAuthSuccess = authenticateResult.getBoolean(PandoraConstant.RESULT);

            if (!isAuthSuccess) {
                inputAuth = new Bundle();

                inputAuth.putString(PBSAuthenticatorController.ARG_ACCOUNT_NAME, global.getAd_user_name());
                inputAuth.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                        PBSAccountInfo.ACCOUNT_TYPE);
                inputAuth.putString(PBSAuthenticatorController.USER_PASS_ARG, global.getAd_user_password());
                inputAuth.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                        PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                inputAuth.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);
                inputAuth.putString(PBSAuthenticatorController.SERVER_URL_ARG, serverURL);

                authenticateResult = new Bundle();
                authenticateResult = authController.triggerEvent(
                        PBSAuthenticatorController.SUBMIT_LOGIN,
                        inputAuth, authenticateResult, null);

                isAuthSuccess = authenticateResult.getBoolean(PandoraConstant.RESULT);
            }
            if (isAuthSuccess) {
                deleteRetentionPeriod = serverController.
                        triggerEvent(PBSServerController.DELETE_RETENTION_RECORD,
                                inputAuth, deleteRetentionPeriod,null);
                updateResultBundle = serverController.
                        triggerEvent(PBSServerController.UPDATE_LOCAL_TABLES,
                                inputAuth, updateResultBundle, null);
                syncResultBundle  = serverController.
                        triggerEvent(PBSServerController.SYNC_LOCAL_TABLES,
                                inputAuth, syncResultBundle, null);

                if (!updateResultBundle.getBoolean(PandoraConstant.RESULT) &&
                        !syncResultBundle.getBoolean(PandoraConstant.RESULT)
                        && !authenticateResult.getBoolean(PandoraConstant.RESULT)){
                    syncResult.hasError();
                }

                boolean isSyncCompleted = syncResultBundle.getInt(PandoraConstant.SYNC_COUNT) == 0;
                if (PandoraMain.instance != null && PandoraMain.instance.getSupportActionBar().isShowing() == true) {
                    PBSProjLocJSON[] projLoc = null;
                    if (isSyncCompleted) {
                        PandoraController cont = new PandoraController(PandoraMain.instance);
                        Bundle input = new Bundle();
                        input.putString(PBSAssetController.ARG_AD_USER_ID, global.getAd_user_id());
                        Bundle result = cont.triggerEvent(cont.GET_PROJLOC_EVENT, input, new Bundle(), null);
                        projLoc = (PBSProjLocJSON[]) result.getSerializable(cont.ARG_PROJECT_LOCATION_JSON);
                        if (projLoc == null) {
                            PandoraMain.instance.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PandoraMain.instance.getBaseContext(), "Please login again. Connection disrupted.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    if(!global.isInitialSynced() || (global.isInitialSynced() && isSyncCompleted && projLoc != null))
                        PandoraMain.instance.updateInitialSyncState(isSyncCompleted && projLoc != null);
                    if (!isSyncCompleted) {
                        ContentResolver.requestSync(account, authority, extras);
                        PandoraMain.instance.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PandoraMain.instance.getBaseContext(), "Initial syncing...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else{
                syncResult.hasError();
            }
        } catch (OperationCanceledException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.hasError();
        } catch (IOException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.hasError();
            syncResult.stats.numIoExceptions++;
        } catch (AuthenticatorException e) {
            syncResult.hasError();
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.stats.numAuthExceptions++;
        }
    }
}
