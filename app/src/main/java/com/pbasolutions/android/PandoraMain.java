package com.pbasolutions.android;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.fragment.ApplicantDetailsFragment;
import com.pbasolutions.android.fragment.ApplicantFragment;
import com.pbasolutions.android.fragment.AssetFragment;
import com.pbasolutions.android.fragment.AssetListFragment;
import com.pbasolutions.android.fragment.AssetMovementDetails;
import com.pbasolutions.android.fragment.AssetNewMovement;
import com.pbasolutions.android.fragment.BroadcastDetailsFragment;
import com.pbasolutions.android.fragment.BroadcastFragment;
import com.pbasolutions.android.fragment.CheckPointFragment;
import com.pbasolutions.android.fragment.CheckInDetailsFragment;
import com.pbasolutions.android.fragment.CheckInFragment;
import com.pbasolutions.android.fragment.DeploymentFragment;
import com.pbasolutions.android.fragment.EmployeeDetailsFragment;
import com.pbasolutions.android.fragment.EmployeeFragment;
import com.pbasolutions.android.fragment.FragmentDrawer;
import com.pbasolutions.android.controller.PBSServerController;
import com.pbasolutions.android.fragment.LoginFragment;
import com.pbasolutions.android.fragment.MovementLineDetails;
import com.pbasolutions.android.fragment.MovementListFragment;
import com.pbasolutions.android.fragment.NewApplicantFragment;
import com.pbasolutions.android.fragment.NewCheckInFragment;
import com.pbasolutions.android.fragment.AccountFragment;
import com.pbasolutions.android.fragment.NewMovementLineFragment;
import com.pbasolutions.android.fragment.NewRequisitionFragment;
import com.pbasolutions.android.fragment.NewRequisitionLineFragment;
import com.pbasolutions.android.fragment.ProjTaskDetailsFragment;
import com.pbasolutions.android.fragment.ProjTaskFragment;
import com.pbasolutions.android.fragment.RecruitFragment;
import com.pbasolutions.android.fragment.RequisitionDetailFragment;
import com.pbasolutions.android.fragment.RequisitionFragment;
import com.pbasolutions.android.fragment.RequisitionLineDetailsFragment;
import com.pbasolutions.android.json.PBSResultJSON;
import com.pbasolutions.android.utils.AlbumStorageDirFactory;
import com.pbasolutions.android.utils.BaseAlbumDirFactory;
import com.pbasolutions.android.utils.CameraUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by pbadell on 6/29/15.
 */
public class PandoraMain extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    /**
     * Class name tag.
     */
    private static final String TAG = "PandoraMain";
    /**
     * Server controller.
     */
    PBSServerController serverController;
    /**
     * Authenticator controller.
     */
    PBSAuthenticatorController authenticatorController;
    /**
     * Content resolver.
     */
    ContentResolver contentResolver;
    /**
     * This page toolbar.
     */
    private Toolbar mToolbar;
    /**
     * Current fragment of this activity.
     */
    public Fragment fragment = null;
    /**
     * Global variable to be pass around.
     */
    public PandoraContext globalVariable;
    /**
     * Drawer fragment.
     */
    public FragmentDrawer drawerFragment;
    /**
     * Current photo taken path.
     */
    public String mCurrentPhotoPath;
    /**
     * Photo album directory.
     */
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /**
     *  Store current action codes
     */

    private int    currentActionCode;

    /**
     *  Progress Indicator
     */
    private ProgressDialog progressDialog;
    private int             progressShowCount = 0;

    public static PandoraMain instance;

    /**
     * Fragment id.
     */
    public static final int FRAGMENT_DEFAULT = -1;
    public static final int FRAGMENT_RECRUIT = 0;
    public static final int FRAGMENT_DEPLOY = -11;
    public static final int FRAGMENT_ASSET = 1;
    public static final int FRAGMENT_REQUISITION = 2;
    public static final int FRAGMENT_TASK = 3;
    public static final int FRAGMENT_CHECKPOINTS = 4;
    public static final int FRAGMENT_CHECKPOINT_SEQ = 5;
    public static final int FRAGMENT_BROADCAST = 6;
    public static final int SETTING_MENU = 7;
    public static final int FRAGMENT_CHECKPOINTS_DETAILS = 50;
    public static final int FRAGMENT_NEW_CHECK_IN = 51;
    public static final int FRAGMENT_ACCOUNT = 80;
    public static final int FRAGMENT_ADD_APPLICANT = 10;
    public static final int FRAGMENT_CREATE_REQUISITION = 11;
    public static final int FRAGMENT_CREATE_REQUISITIONLINE = 12;
    public static final int FRAGMENT_CREATE_MOVEMENT = 13;
    public static final int FRAGMENT_CREATE_MOVEMENTLINE = 14;

    /**
     * Toolbar menu id.
     */
    public static final int SYNC_ID = 100;
    public static final int LOGOUT_ID = 200;
    public static final int SYNC_DEPLOY_ID = 600;
    public static final int SAVE_DEPLOY_ID = 700;

    /**
     * Default fragment int value.
     */
    int defaultFragment;

    /**
     * Drawer layout.
     */
    public DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Initial.
     */
    private void init(){
        authenticatorController = new PBSAuthenticatorController(this);
        globalVariable = (PandoraContext) getApplicationContext();
        if (globalVariable != null) {
            if (globalVariable.getAd_user_name().isEmpty()) {
                PandoraHelper.restoreGlobalVariables(this);
            }
        }
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        contentResolver = getContentResolver();
        serverController = new PBSServerController(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        currentActionCode = 0;

        setSupportActionBar(mToolbar);
        setDrawerFragment();
        directToFragment();
        //on add or remove back stack always update the fragment titles.
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_body);
                if (f != null) {
                    updateFragmentTitle(f);
                }
            }
        });

        instance = this;
    }

    /**
     * Set drawer fragment.
     */
    private void setDrawerFragment() {
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(
                R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.resetUsername(globalVariable.getAd_user_name());
        drawerFragment.invalidateView();
    }

    /**
     * Method to update the title of fragment triggered.
     *
     * @param f fragment.
     */
    private void updateFragmentTitle(Fragment f) {
        String fragClassName = f.getClass().getName();
        int titleID = R.string.title_default;
        if (CheckInFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_guardtour_checkin;
        } else if (NewCheckInFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newcheckin;
        } else if (CheckInDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_details;
        } else if (AccountFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_account;
        } else if (CheckPointFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_guardtour_projectlocation;
        } else if (RecruitFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_recruit;
        } else if (EmployeeFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_employees;
        } else if (ApplicantFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_applicants;
        } else if (NewApplicantFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newapplicant;
        } else if (ApplicantDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_applicantdetails;
        } else if (AssetFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_assets;
        } else if (AssetListFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_assets;
        } else if (MovementListFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_movement;
        } else if (AssetMovementDetails.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_movement_details;
        } else if (AssetNewMovement.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newmovement;
        } else if (NewMovementLineFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newmovementline;
        } else if (MovementLineDetails.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_movementline_details;
        } else if (ProjTaskFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_task;
        } else if (ProjTaskDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_task_details;
        } else if (BroadcastFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_broadcast;
        } else if (BroadcastDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_broadcastdetails;
        } else if (RequisitionFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_requisition;
        } else if (RequisitionDetailFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_requisitiondetails;
        } else if (NewRequisitionFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newrequisition;
        } else if (NewRequisitionLineFragment.class.getName().equalsIgnoreCase(fragClassName)) {
            titleID = R.string.title_newrequisitionline;
        } else if (RequisitionLineDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_requisitionlinedetails;
        } else if (EmployeeDetailsFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_employee_details;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(titleID);
    }

    /**
     * Redirect the screen view depending on the authToken state.
     */
    public void directToFragment() {
        try {
            //check from account manager. no request send to server.
            Bundle inputBundle = new Bundle();
            if (globalVariable != null){
                inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                        globalVariable.getAd_user_name());
                inputBundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                        PBSAccountInfo.ACCOUNT_TYPE);
                inputBundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                        PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);

                boolean isAuthToken = (!globalVariable.getAuth_token().isEmpty());

                if (!isAuthToken) {
                    defaultFragment = FRAGMENT_ACCOUNT;
                    PandoraHelper.showAlertMessage(this, "Welcome to Pandora, " +
                                    "Please Login to use this app.",
                            PandoraConstant.LOGIN, "Ok", null);
                } else {
                    PandoraHelper.getProjLocAvailable(this, false);
                    defaultFragment = FRAGMENT_RECRUIT;
                }
            } else {
                defaultFragment = FRAGMENT_ACCOUNT;
            }
            displayView(defaultFragment, true);
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            PandoraHelper.showMessage(this, "Error: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: SHIFT sync and logout at respected fragment instead put in main!.
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Bundle pressedButton = new Bundle();
        int lockMode = mDrawerLayout.getDrawerLockMode(Gravity.LEFT);
        if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED) {
            switch (id) {
                case SYNC_ID: {
                    sync();
                    break;
                }
                case LOGOUT_ID: {
                    logout();
                    break;
                }
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Logout from the app.
     */
    private void logout() {
//        if (true) {
//            resetServerData();
////            updateInitialSyncState(false);
//            return;
//        }
        clearAllFragmentStack();
        Bundle inputBundle = new Bundle();
        if (PandoraHelper.isInternetOn(this)) {
            inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                    globalVariable.getAd_user_name());
            inputBundle.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG,
                    globalVariable.getAuth_token());
            inputBundle.putString(PBSAuthenticatorController.SERVER_URL_ARG,
                    globalVariable.getServer_url());
            inputBundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                    PBSAccountInfo.ACCOUNT_TYPE);
            inputBundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                    PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
        } else {
            inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                    globalVariable.getAd_user_name());
        }
        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle inputBundle = params[0];
                Bundle logOutResult = null;
                if (PandoraHelper.isInternetOn(PandoraMain.this))
                    logOutResult = authenticatorController.triggerEvent(
                        PBSAuthenticatorController.LOG_OUT, inputBundle, new Bundle(), this);

                globalVariable = null;
                PBSServerConst.cookieStore = null;
                authenticatorController.triggerEvent(PBSAuthenticatorController.CLEAR_AUTH_TOKEN,
                        inputBundle, null, this);

                return null;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);

                showLogOutDialog(PBSAuthenticatorController.SUCCESSFULLY_LOGGED_OUT_TXT);

                dismissProgressDialog();
            }
        }.execute(inputBundle);
    }

    private void resetServerData() {
        // danny test code to reset sync data
        (new Thread() {
            public void run() {
                PBSServer pbsServer = new PBSServer();
                PBSResultJSON resultJSON = (PBSResultJSON) pbsServer.callServer("http://103.250.56.171:8000/wstore/Sync.jsp?action=Reset", PBSResultJSON.class.getName());

                boolean result;
                if (resultJSON != null){
                    if (resultJSON.getSuccess().equals(PBSResultJSON.FALSE_TEXT)) {
                        result = false;
                    } else {
                        result = true;
                    }
                } else {
                    result = false;
                }

                Log.d("Sync Reset", " reset result : " + result);
            }
        }).start();
    }

    /**
     * Perform sync.
     */
    private void sync() {
        Bundle inputAuth = new Bundle();
        inputAuth.putString(authenticatorController.USER_NAME_ARG,
                globalVariable.getAd_user_name());
        inputAuth.putString(authenticatorController.AUTH_TOKEN_ARG,
                globalVariable.getAuth_token());
        inputAuth.putString(authenticatorController.SERVER_URL_ARG,
                globalVariable.getServer_url());
        inputAuth.putString(authenticatorController.SERIAL_ARG,
                globalVariable.getSerial());

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog("Syncing...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle inputAuth = params[0];
                Bundle authenticateResult = new Bundle();
                Bundle updateResult = new Bundle();
                Bundle syncResult = new Bundle();

                Bundle result = new Bundle();
                try {
                    authenticateResult = authenticatorController
                            .triggerEvent(PBSAuthenticatorController.AUTHENTICATE_TOKEN_SERVER,
                                    inputAuth, authenticateResult, null);

                    result.putBoolean(PandoraConstant.RESULT, authenticateResult.getBoolean(PandoraConstant.RESULT));
                    if (authenticateResult.getBoolean(PandoraConstant.RESULT)) {
                        updateResult = serverController.triggerEvent(PBSServerController.UPDATE_LOCAL_TABLES,
                                inputAuth,
                                updateResult,
                                contentResolver);

                        syncResult = serverController.triggerEvent(PBSServerController.SYNC_LOCAL_TABLES,
                                inputAuth,
                                syncResult,
                                contentResolver);

                        result.putBundle("updateResult", updateResult);
                        result.putBundle("syncResult", syncResult);

                    } else {
                        //TODO: test ya.
                        //set the auth token to null.
                        Bundle input = new Bundle();
                        input.putString(authenticatorController.USER_NAME_ARG,
                                globalVariable.getAd_user_name());
                        input.putString(authenticatorController.ARG_ACCOUNT_TYPE,
                                PBSAccountInfo.ACCOUNT_TYPE);
                        input.putString(authenticatorController.ARG_AUTH_TYPE,
                                PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                        authenticatorController.triggerEvent(PBSAuthenticatorController.CLEAR_AUTH_TOKEN,
                                input, new Bundle(), null);
                        globalVariable = null;
                        result.putString(PandoraConstant.FAIL, authenticateResult.getString(PandoraConstant.FAIL));
                    }
                } catch (Exception e) {
                    result.putBoolean(PandoraConstant.ERROR, true);
                    result.putString("ErrorMessage", e.getMessage());
                }

                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (result.getBoolean(PandoraConstant.ERROR)) {
                    String errorMsg = result.getString("ErrorMessage");
                    Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + errorMsg);
                    PandoraHelper.showErrorMessage(PandoraMain.this, errorMsg);
                } else {
                    if (result.getBoolean(PandoraConstant.RESULT)) {
                        Bundle updateResult = result.getBundle("updateResult");
                        Bundle syncResult = result.getBundle("syncResult");
                        if (updateResult.getBoolean(PandoraConstant.RESULT)
                                && syncResult.getBoolean(PandoraConstant.RESULT)) {
                            PandoraHelper.showMessage(PandoraMain.this, "Successfully sync");
                            getSupportFragmentManager().popBackStack();
                            Fragment fragment = new RecruitFragment();
                            updateFragment(fragment, getString(R.string.title_recruit),
                                    false);
                        } else if (updateResult.getString(PandoraConstant.ERROR) != null) {
                            PandoraHelper.showErrorMessage(PandoraMain.this,
                                    updateResult.getString(PandoraConstant.ERROR));
                        } else {
                            PandoraHelper.showErrorMessage(PandoraMain.this, "Failed to sync");
                        }

                    } else {
                        showLogOutDialog(result.getString(PandoraConstant.FAIL));
                    }
                }
                dismissProgressDialog();
            }
        }.execute(inputAuth);
    }

    /**
     * Clear all fragment stack *on logout.
     */
    private void clearAllFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Log Out dialog. need to redirect upon "OK" click.
     * @param message
     */
    public void showLogOutDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(PandoraConstant.RESULT);
        dialog.setMessage(message);
        dialog.setPositiveButton(PandoraConstant.OK_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                directToFragment();
            }
        });
        dialog.show();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position, false);
    }

    /**
     * Fragment display on trigger.
     * @param position
     * @param firstInstantiate
     */
    public void displayView(int position, boolean firstInstantiate) {
        if (position != FRAGMENT_ACCOUNT && !PandoraHelper.isInitialSyncCompleted)
        {
            Toast.makeText(this, "Please wait while initial syncing.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String title = getString(R.string.app_name);
        switch (position) {
            case FRAGMENT_CHECKPOINTS: {
                fragment = new CheckInFragment();
                title = getString(R.string.title_guardtour_checkin);
                break;
            }
            case FRAGMENT_CHECKPOINT_SEQ: {
                fragment = new CheckPointFragment();
                title = getString(R.string.title_guardtour_projectlocation);
                break;
            }
            case FRAGMENT_NEW_CHECK_IN: {
                fragment = new NewCheckInFragment();
                title = getString(R.string.title_newcheckin);
                break;
            }
            case SETTING_MENU: {
                fragment = new AccountFragment();
                title = getString(R.string.title_account);
                break;
            }
            case FRAGMENT_CHECKPOINTS_DETAILS: {
                fragment = new CheckInDetailsFragment();
                title = getString(R.string.title_details);
                break;
            }
            case FRAGMENT_ACCOUNT: {
                fragment = new AccountFragment();
                title = getString(R.string.title_account);
                break;
            }
            case FRAGMENT_RECRUIT: {
                fragment = new RecruitFragment();
                title = getString(R.string.title_recruit);
                break;
            }
            case FRAGMENT_ADD_APPLICANT: {
                fragment = new NewApplicantFragment();
                title = getString(R.string.title_newapplicant);
                break;
            }
            case FRAGMENT_DEPLOY: {
                fragment = new DeploymentFragment();
                title = getString(R.string.title_deploy);
                break;
            }

            case FRAGMENT_ASSET: {
                fragment = new AssetFragment();
                title = getString(R.string.title_assets);
                break;
            }

            case FRAGMENT_REQUISITION: {
                fragment = new RequisitionFragment();
                title = getString(R.string.title_requisition);
                break;
            }

            case FRAGMENT_BROADCAST: {
                fragment = new BroadcastFragment();
                title = getString(R.string.title_broadcast);
                break;
            }

            case FRAGMENT_TASK: {
                fragment = new ProjTaskFragment();
                title = getString(R.string.title_task);
                break;
            }

            case FRAGMENT_CREATE_REQUISITION: {
                fragment = new NewRequisitionFragment();
                title = getString(R.string.title_newrequisition);
                break;
            }

            case FRAGMENT_CREATE_REQUISITIONLINE: {
                fragment = new NewRequisitionLineFragment();
                title = getString(R.string.title_newrequisitionline);
                break;
            }

            case FRAGMENT_CREATE_MOVEMENT: {
                fragment = new AssetNewMovement();
                title = getString(R.string.title_newmovement);
                break;
            }

            case FRAGMENT_CREATE_MOVEMENTLINE: {
                fragment = new NewMovementLineFragment();
                title = getString(R.string.title_newmovementline);
                break;
            }

            default:
                break;
        }

        if (fragment != null) {
            updateFragment(fragment, title, firstInstantiate);
        }
    }

    /**
     * Update for new fragment selected.
     * @param fragment
     * @param title
     * @param isAddToStack
     */
    public void updateFragment(Fragment fragment, String title, boolean isAddToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        if (isAddToStack == false) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
    }

    /**
     * On click icon_profile show account fragment.
     *
     * @param view
     */
    public void onAccount(View view) {
        displayView(PandoraMain.FRAGMENT_ACCOUNT, false);
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null){
            if (intent.getAction() != null){
                if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                    if (globalVariable.getAd_user_name() == null) {
                        directToFragment();
                    } else {
                        if (fragment != null) {
                            fragment = new NewCheckInFragment();
                            ((NewCheckInFragment) fragment).setNfcIntent(intent);
                            updateFragment(fragment, "New Check In", false);
                        }
                    }
                }
            }else {
                directToFragment();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    /**
     * On picture taken.
     * @param actionCode
     */
    public void dispatchTakePictureIntent(int actionCode, View view) {
        currentActionCode = actionCode;

        PopupMenu popup = new PopupMenu(this, view);

        MenuItem cameraItem  = popup.getMenu().add(R.string.title_camera_button);
        cameraItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) == null) return true;

                File f;
                try {
                    f = CameraUtil.setUpPhotoFile(mAlbumStorageDirFactory);
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    mCurrentPhotoPath = null;
                }

                startActivityForResult(takePictureIntent, currentActionCode);
                return true;
            }
        });


        MenuItem loadPictureItem  = popup.getMenu().add(R.string.title_gallery_button);

        loadPictureItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent loadPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (loadPictureIntent.resolveActivity(getPackageManager()) == null) return true;

                startActivityForResult(loadPictureIntent, currentActionCode);
                return true;
            }
        });

        popup.show();
    }

    /**
     *
     * @return
     */
    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        progressDialog.setMessage(message);
        progressDialog.show();
        progressShowCount++;
    }

    public void dismissProgressDialog() {
        progressShowCount--;
        if (progressShowCount < 0)
            progressShowCount = 0;
        if (progressShowCount == 0)
            progressDialog.dismiss();
    }

    public void updateInitialSyncState(boolean isFirst) {
//        if (PandoraHelper.isInitialSyncCompleted)
//            dismissProgressDialog();
//        else
//            showProgressDialog("Initial Syncing");
        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... isFirst) {
                boolean prevSyncState = true;
                if (!isFirst[0].booleanValue())
                    prevSyncState = PandoraHelper.isInitialSyncCompleted;
                PandoraHelper.getProjLocAvailable(PandoraMain.this, false);
                return new Boolean(prevSyncState);
            }

            @Override
            protected void onPostExecute(Boolean bPrevState) {
                super.onPostExecute(bPrevState);
                if (!PandoraHelper.isInitialSyncCompleted)
                    Toast.makeText(PandoraMain.this, "Initial Syncing",
                            Toast.LENGTH_SHORT).show();
                else if (bPrevState.booleanValue() == false) {
                    Toast.makeText(PandoraMain.this, "Initial Sync completed",
                            Toast.LENGTH_SHORT).show();
                    if (fragment != null && fragment instanceof AccountFragment) {
                        ((AccountFragment) fragment).completedInitialSync();
                    }
                }
            }
        }.execute(Boolean.valueOf(isFirst));
    }
}
