package com.pbasolutions.android.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;

import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSServerController;

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
        //call the controller update sync here.
        PBSServerController serverController = new PBSServerController(getContext());
        PBSAuthenticatorController authController = new PBSAuthenticatorController(getContext());
        try {
            PandoraContext global =  ((PandoraContext)getContext());

            //work around to get latest authToken after authToken has been changed.
          //  String globalAuthToken = global.getAuth_token();
            String authToken = accountManager.blockingGetAuthToken(account,
                    PBSAccountInfo.AUTHTOKEN_TYPE_SYNC, true);
          //  if (!authToken.equalsIgnoreCase(globalAuthToken) && !globalAuthToken.isEmpty()){
            //    authToken = globalAuthToken;
           // }

            //work around to get latest serverURL after serverURL has been changed.
            String globalServerURL = global.getServer_url();
            String serverURL = accountManager.getUserData(account,
                    PBSAuthenticatorController.SERVER_URL_ARG);
            if (!serverURL.equalsIgnoreCase(globalServerURL) && !globalServerURL.isEmpty()) {
                serverURL = globalServerURL;
            }

            //work around to get latest deviceID after deviceID has been changed.
            String globalDeviceID = global.getSerial();
            String deviceID = accountManager.getUserData(account,
                    PBSAuthenticatorController.SERIAL_ARG);
            if (!deviceID.equalsIgnoreCase(globalDeviceID) && !globalDeviceID.isEmpty()) {
                serverURL = globalDeviceID;
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
            if (authenticateResult.getBoolean(PandoraConstant.RESULT)) {
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
