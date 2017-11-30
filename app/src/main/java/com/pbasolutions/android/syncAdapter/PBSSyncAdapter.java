package com.pbasolutions.android.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.pbasolutions.android.BuildConfig;
import com.pbasolutions.android.PBSServerConst;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that handle syncing in accounts.
 * Created by pbadell on 6/30/15.
 */
public class PBSSyncAdapter extends AbstractThreadedSyncAdapter {

    /**
     * Class name tag.
     */
    private static final String TAG = "PBSSyncAdapter";
    private static final String SYNCIDENTIFIER = "syncIdentifier";
    private final AccountManager accountManager;
    private Context context;
    private int syncIdentifier;
    private Handler handler;
    private SharedPreferences prefs;

    /**
     *
     * @param context
     * @param autoInitialize
     */
    public PBSSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        accountManager = AccountManager.get(context);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        syncIdentifier = prefs.getInt(SYNCIDENTIFIER, 0);
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
//        Context context;
//        if (PandoraMain.instance != null)
//            context = PandoraMain.instance.getApplicationContext();
//        else context = getContext();

        //call the controller update sync here.
        PBSServerController serverController = new PBSServerController(context);
        PBSAuthenticatorController authController = new PBSAuthenticatorController(context);
        try {
            PandoraContext global =  ((PandoraContext)context);

            if (PBSServerConst.cookieStore == null) {
                PBSServerConst.instantiateCookie();
            }

            //work around to get latest authToken after authToken has been changed.
          //  String globalAuthToken = global.getAuth_token();
            String authToken = accountManager.blockingGetAuthToken(account,
                    PBSAccountInfo.AUTHTOKEN_TYPE_SYNC, true);
          //  if (!authToken.equalsIgnoreCase(globalAuthToken) && !globalAuthToken.isEmpty()){
            //    authToken = globalAuthToken;
           // }

            //work around to get latest serverURL after serverURL has been changed.
//            String globalServerURL = global.getServer_url();
//            String serverURL = accountManager.getUserData(account,
//                    PBSAuthenticatorController.SERVER_URL_ARG);
            SharedPreferences prefs = context.getSharedPreferences(
                    BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
            String serverURL = prefs.getString("serverURL", "");
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

            if (syncIdentifier == 0) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("ddHHmmss");
                syncIdentifier = Integer.parseInt(sdf.format(date));
            }

            boolean isAuthSuccess = false;
            Bundle inputAuth = new Bundle();
            inputAuth.putInt(PBSServerConst.IDENTIFIER, syncIdentifier);
            inputAuth.putString(PBSAuthenticatorController.USER_NAME_ARG, userName);
            inputAuth.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG, authToken);
            inputAuth.putString(PBSAuthenticatorController.SERVER_URL_ARG, serverURL);
            inputAuth.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);

            Bundle getUnsyncCountBundle = new Bundle();
            Bundle updateResultBundle = new Bundle();
            Bundle deleteRetentionPeriod = new Bundle();
            Bundle syncResultBundle = new Bundle();
            Bundle authenticateResult = new Bundle();

//            if (isAuthSuccess)
            {
                getUnsyncCountBundle = serverController.
                        triggerEvent(PBSServerController.GET_UNSYNC_COUNT,
                                inputAuth, getUnsyncCountBundle, null);
                boolean success = getUnsyncCountBundle.getBoolean(PandoraConstant.RESULT, false);
                if (!success) {
                    authenticateResult = authController.triggerEvent(
                            PBSAuthenticatorController.AUTHENTICATE_TOKEN_SERVER,
                            inputAuth, authenticateResult, null);

                    isAuthSuccess = authenticateResult.getBoolean(PandoraConstant.RESULT);

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
                        getUnsyncCountBundle = serverController.
                                triggerEvent(PBSServerController.GET_UNSYNC_COUNT,
                                        inputAuth, getUnsyncCountBundle, null);
                        success = getUnsyncCountBundle.getBoolean(PandoraConstant.RESULT, false);
                    } else {
                        syncResult.hasError();
                    }
                }

                if (!success) return;
                else {
                    int count = getUnsyncCountBundle.getInt("count", 0);
                    int total = getUnsyncCountBundle.getInt("total", 0);

                    if (total <= 0) return;
                    prefs.edit().putInt("count", count).apply();
                    prefs.edit().putInt("total", total).apply();
                    getContext().getContentResolver().notifyChange(Uri.parse("http://" + BuildConfig.APPLICATION_ID + ".syncProgress"), null, false);
                }

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

                int syncCount = syncResultBundle.getInt(PandoraConstant.SYNC_COUNT);
                boolean isSyncCompleted = syncCount == 0;
//                global.setIsFirstBatchSynced(true);
//                if (PandoraMain.instance != null && PandoraMain.instance.getSupportActionBar().isShowing()) {
                    PBSProjLocJSON[] projLoc;
                    if (isSyncCompleted) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                            Settings.Global.getInt(PandoraMain.instance.getContentResolver(), Settings.Global.AUTO_TIME, 1);
//                            Settings.Global.getInt(PandoraMain.instance.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 1);
//                        } else {
//                            Settings.System.getInt(PandoraMain.instance.getContentResolver(), Settings.System.AUTO_TIME, 1);
//                            Settings.System.getInt(PandoraMain.instance.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 1);
//                        }

                        PandoraController cont = new PandoraController(context);
                        Bundle input = new Bundle();
                        input.putString(PBSAssetController.ARG_AD_USER_ID, global.getAd_user_id());
                        Bundle result = cont.triggerEvent(PandoraController.GET_PROJLOC_EVENT, input, new Bundle(), null);
                        projLoc = (PBSProjLocJSON[]) result.getSerializable(PandoraController.ARG_PROJECT_LOCATION_JSON);
                        if (projLoc == null) {
                            input = new Bundle();
                            result = cont.triggerEvent(PandoraController.GET_PROJLOC_EVENT, input, new Bundle(), null);
                            projLoc = (PBSProjLocJSON[]) result.getSerializable(PandoraController.ARG_PROJECT_LOCATION_JSON);
                            if (projLoc == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Done syncing but incomplete of data detected. Please contact admin for assistance.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "No Project Location available for current user's team. Please contact admin for assistance.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            if (/*!global.isInitialSynced() ||*/ !global.isInitialSynced()) {
//                                PandoraMain.instance.updateInitialSyncState(isSyncCompleted && projLoc != null);
                                getContext().getContentResolver().notifyChange(Uri.parse("http://" + BuildConfig.APPLICATION_ID + ".syncNotify"), null, false);
                            }
                            syncIdentifier = 0;
                            global.setProjLocJSON(projLoc);
                        }
                    } else {
                        if (syncCount != -1) {
                            syncIdentifier = 0;
                            extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                            ContentResolver.requestSync(account, authority, extras);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String text;
                                    if (((PandoraContext)context).isInitialSynced())
                                        text = "Syncing...";
                                    else text = "Initial syncing...";
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                }
                            });
//                            PandoraMain.instance.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    String text;
//                                    if (PandoraMain.instance.getGlobalVariable().isInitialSynced())
//                                        text = "Syncing...";
//                                    else text = "Initial syncing...";
//                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }
//                        else {
//                            PandoraMain.instance.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(PandoraMain.instance.getBaseContext(), "Could not receive data when sync. Please contact admin for assistance", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
                    }
//                }
            }
//            else {
//                syncResult.hasError();
//            }
        } catch (OperationCanceledException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.hasError();
        } catch (IOException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.hasError();
            syncResult.stats.numIoExceptions++;
        } catch (Exception e) {
            syncResult.hasError();
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            syncResult.stats.numAuthExceptions++;
        } finally {
            prefs.edit().putInt(SYNCIDENTIFIER, syncIdentifier).apply();
        }
    }
}
