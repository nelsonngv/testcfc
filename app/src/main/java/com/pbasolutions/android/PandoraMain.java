package com.pbasolutions.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

//import com.google.firebase.iid.FirebaseInstanceId;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.fragment.ATrackDoneFragment;
import com.pbasolutions.android.fragment.ATrackScanEmpIDFragment;
import com.pbasolutions.android.fragment.ATrackScanLocFragment;
import com.pbasolutions.android.fragment.ApplicantDetailsFragment;
import com.pbasolutions.android.fragment.ApplicantFragment;
import com.pbasolutions.android.fragment.AssetFragment;
import com.pbasolutions.android.fragment.AssetListFragment;
import com.pbasolutions.android.fragment.AssetMovementDetails;
import com.pbasolutions.android.fragment.AssetNewMovement;
import com.pbasolutions.android.fragment.AttendanceDetailFragment;
import com.pbasolutions.android.fragment.AttendanceFragment;
import com.pbasolutions.android.fragment.AuditFragment;
import com.pbasolutions.android.fragment.BroadcastDetailsFragment;
import com.pbasolutions.android.fragment.BroadcastFragment;
import com.pbasolutions.android.fragment.CheckPointFragment;
import com.pbasolutions.android.fragment.CheckInDetailsFragment;
import com.pbasolutions.android.fragment.CheckInFragment;
import com.pbasolutions.android.fragment.EmployeeDetailsFragment;
import com.pbasolutions.android.fragment.EmployeeFragment;
import com.pbasolutions.android.fragment.FragmentDrawer;
import com.pbasolutions.android.controller.PBSServerController;
import com.pbasolutions.android.fragment.MovementLineDetails;
import com.pbasolutions.android.fragment.MovementListFragment;
import com.pbasolutions.android.fragment.NewApplicantFragment;
import com.pbasolutions.android.fragment.NewAttendanceFragment;
import com.pbasolutions.android.fragment.NewAttendanceLineFragment;
import com.pbasolutions.android.fragment.NewAuditPagerFragment;
import com.pbasolutions.android.fragment.NewAuditStartFragment;
import com.pbasolutions.android.fragment.NewCheckInFragment;
import com.pbasolutions.android.fragment.AccountFragment;
import com.pbasolutions.android.fragment.NewMovementLineFragment;
import com.pbasolutions.android.fragment.NewRequisitionFragment;
import com.pbasolutions.android.fragment.NewRequisitionLineFragment;
import com.pbasolutions.android.fragment.NewSurveyPagerFragment;
import com.pbasolutions.android.fragment.NewSurveyStartFragment;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.ProjTaskDetailsFragment;
import com.pbasolutions.android.fragment.ProjTaskFragment;
import com.pbasolutions.android.fragment.RecruitFragment;
import com.pbasolutions.android.fragment.RequisitionDetailFragment;
import com.pbasolutions.android.fragment.RequisitionFragment;
import com.pbasolutions.android.fragment.RequisitionLineDetailsFragment;
import com.pbasolutions.android.fragment.SurveyFragment;
import com.pbasolutions.android.json.PBSLoginJSON;
import com.pbasolutions.android.json.PBSResultJSON;
import com.pbasolutions.android.utils.AlbumStorageDirFactory;
import com.pbasolutions.android.utils.BaseAlbumDirFactory;
import com.pbasolutions.android.utils.CameraUtil;

import com.pbasolutions.android.listener.PBABackKeyListener;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;

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
    PBSTaskController taskCont;
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
    public AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /**
     *  Store current action codes
     */

    public int currentActionCode;

    /**
     *  Progress Indicator
     */
    private ProgressDialog progressDialog;
    private int             progressShowCount = 0;

    /**
     * Fragment id.
     */
    public static final int FRAGMENT_DEFAULT = -1;
    public static final int FRAGMENT_ATTENDANCE = 0;
    public static final int FRAGMENT_ATTENDANCE_TRACKING = 1;
    public static final int FRAGMENT_RECRUIT = 2;
//    public static final int FRAGMENT_DEPLOY =1;
    public static final int FRAGMENT_ASSET = 3;
    public static final int FRAGMENT_REQUISITION = 4;
    public static final int FRAGMENT_TASK = 5;
    public static final int FRAGMENT_SURVEY = 6;
    public static final int FRAGMENT_AUDIT = 7;
    public static final int FRAGMENT_CHECKPOINTS = 8;
    public static final int FRAGMENT_CHECKPOINT_SEQ = 9;
    public static final int FRAGMENT_BROADCAST = 10;
    public static final int SETTING_MENU = 11;
    public static final int FRAGMENT_CHECKPOINTS_DETAILS = 50;
    public static final int FRAGMENT_NEW_CHECK_IN = 51;
    public static final int FRAGMENT_ACCOUNT = 80;
    public static final int FRAGMENT_ADD_APPLICANT = 21;
    public static final int FRAGMENT_CREATE_REQUISITION = 22;
    public static final int FRAGMENT_CREATE_REQUISITIONLINE = 23;
    public static final int FRAGMENT_CREATE_MOVEMENT = 24;
    public static final int FRAGMENT_CREATE_MOVEMENTLINE = 25;
    public static final int FRAGMENT_CREATE_ATTENDANCE = 26;
    public static final int FRAGMENT_CREATE_ATTENDANCELINE = 27;
    public static final int FRAGMENT_START_SURVEY = 28;
    public static final int FRAGMENT_NEW_SURVEY = 29;
    public static final int FRAGMENT_START_AUDIT = 30;
    public static final int FRAGMENT_NEW_AUDIT = 31;
    public static final int FRAGMENT_ATTENDANCE_TRACKING_EMPID = 32;
    public static final int FRAGMENT_ATTENDANCE_TRACKING_DONE = 33;

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

    /**
     * Handler.
     */
    private Handler checkLoginHandler = new Handler();
    private Handler checkProjTaskHandler = new Handler();
    private Runnable checkLoginRunnable, checkProjTaskRunnable;
    private int checkLoginDelay = 600000; //10 mins
    private int checkProjTaskDelay = 1800000; //30 mins

    // to check sync result
    public static final String GOTO_RECRUIT = "GotoRecruit";

    //authorized menu
    public String[] menuList = null;

    //menu list constant
    public static final String[] MENU_LIST = {"Attendance", "Attendance Tracking", "Recruit", "Asset", "Requisition", "Task",
            "Survey", "Audit", "Check In", "Check Point", "Broadcast", "Setting"};

    //welcome dialog indicator
    private boolean isWelcomeDisplayed = false;

    //dialog builder
    android.support.v7.app.AlertDialog dialog = null;

    private ContentObserver mObserver;

    //receive broadcast message from push notification
//    ReceiveMessages myReceiver = null;
//    Boolean myReceiverIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CookieHandler.setDefault(new CookieManager());
        init();

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                updateInitialSyncState();
            }
        };
        getContentResolver().registerContentObserver(Uri.parse("http://" + BuildConfig.APPLICATION_ID + ".syncNotify"), true, mObserver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getCurrentFragment();
        if (frag instanceof PBABackKeyListener) {
            if (((PBABackKeyListener)frag).onBackKeyPressed())
                super.onBackPressed();
        }
        else super.onBackPressed();
    }

    /**
     * Initial.
     */
    private void init() {
        authenticatorController = new PBSAuthenticatorController(this);
        taskCont = new PBSTaskController(this);
        setGlobalVariable((PandoraContext) getApplicationContext());
        if (getGlobalVariable() != null) {
            if (getGlobalVariable().getAd_user_name().isEmpty()) {
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
        displayView(FRAGMENT_ACCOUNT, true);
        checkLogin(false);
        ContentResolver.setMasterSyncAutomatically(true);
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

        SharedPreferences prefs = getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        String formString = prefs.getString("forms", "");
        menuList = new Gson().fromJson(formString, String[].class);
        updateDrawer(menuList);

        if (PBSServerConst.cookieStore == null) {
            PBSServerConst.instantiateCookie();
        }

//        Log.d(TAG, "Refreshed token: " + FirebaseInstanceId.getInstance().getToken());
//        //receive push notification broadcast
//        myReceiver = new ReceiveMessages();
//        registerReceiver(myReceiver, new IntentFilter("Received.PandoraCFC.Push.Notification"));

        //check login
        checkLoginRunnable = new Runnable(){
            public void run() {
                if(getSupportActionBar().isShowing()) {
                    checkLogin(true);
                }
                checkLoginHandler.postDelayed(this, checkLoginDelay);
            }
        };
        checkLoginHandler.postDelayed(checkLoginRunnable, checkLoginDelay);

        //check proj task
        checkProjTaskRunnable = new Runnable(){
            public void run() {
                if(getSupportActionBar().isShowing() && PBSServerConst.cookieStore != null && !getGlobalVariable().getC_projectlocation_uuid().isEmpty()) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Bundle input = new Bundle();
                            input.putString(PBSTaskController.ARG_PROJLOC_UUID, getGlobalVariable().getC_projectlocation_uuid());
                            input.putString(PBSTaskController.ARG_AD_USER_ID, getGlobalVariable().getAd_user_id());
                            input.putString(PBSServerConst.PARAM_URL, getGlobalVariable().getServer_url());
                            input.putBoolean("isShowNotification", true);
                            taskCont.triggerEvent(PBSTaskController.SYNC_PROJTASKS_EVENT, input, new Bundle(), null);
                        }
                    });
                }
                checkProjTaskHandler.postDelayed(this, checkProjTaskDelay);
            }
        };
        checkProjTaskHandler.postDelayed(checkProjTaskRunnable, checkProjTaskDelay);
    }

    /**
     * Set drawer fragment.
     */
    public void setDrawerFragment() {
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(
                R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.resetUsername(getGlobalVariable().getAd_user_name());
        drawerFragment.invalidateView();
    }

    /**
     * Update drawer fragment.
     */
    public void updateDrawer(String[] list) {
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(
                R.id.fragment_navigation_drawer);
        drawerFragment.updateDrawer(list);
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
        } else if (AttendanceFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_attentance;
        } else if (NewAttendanceFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_create_attendance;
        } else if (NewAttendanceLineFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_create_attendanceline;
        } else if (AttendanceDetailFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_attentance;
        } else if (SurveyFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_survey;
        } else if (NewSurveyStartFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_new_survey;
        } else if (AuditFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_audit;
        } else if (NewAuditStartFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_new_audit;
        } else if (ATrackScanLocFragment.class.getName().equalsIgnoreCase(fragClassName)){
            titleID = R.string.title_attendance_tracking;
        } else if (ATrackScanEmpIDFragment.class.getName().equalsIgnoreCase(fragClassName)
                || ATrackDoneFragment.class.getName().equalsIgnoreCase(fragClassName)){
            if (PBSAttendanceController.isKioskMode)
                titleID = R.string.title_attendance_tracking_kiosk;
            else titleID = R.string.title_attendance_tracking;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && titleID != R.string.title_default)
            actionBar.setTitle(titleID);
    }

    /**
     * Redirect the screen view depending on the authToken state.
     */
    public void directToFragment(boolean isShowDialog) {
        try {
            //check from account manager. no request send to server.
            Bundle inputBundle = new Bundle();
            if (getGlobalVariable() != null) {
                inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                        getGlobalVariable().getAd_user_name());
                inputBundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                        PBSAccountInfo.ACCOUNT_TYPE);
                inputBundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                        PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);

                boolean isAuthToken = (!getGlobalVariable().getAuth_token().isEmpty());

                defaultFragment = -1;
                if (!isAuthToken) {
                    defaultFragment = FRAGMENT_ACCOUNT;
//                    if (dialog != null && !dialog.isShowing()) {
                        if (!isWelcomeDisplayed) {
                            if (isShowDialog)
                                PandoraHelper.showAlertMessage(this, "Welcome to Pandora, " +
                                                "Please login to use this app.",
                                        PandoraConstant.LOGIN, "Ok", null);
                            isWelcomeDisplayed = true;
                        } else {
                            if (isShowDialog && dialog != null && !dialog.isShowing()) {
                                PandoraHelper.showAlertMessage(this, "Session timeout, " +
                                                "Please login again to use this app.",
                                        PandoraConstant.LOGIN, "Ok", null);
                            }
                        }
//                    }
                } else {
                    PandoraHelper.getProjLocAvailable(this, false);
//                    if (!getGlobalVariable().isInitialSynced())
//                        defaultFragment = FRAGMENT_ATTENDANCE;
//                    else
                        defaultFragment = FRAGMENT_ACCOUNT;
                }

                if (PBSServerConst.cookieStore == null) {
                    PBSServerConst.instantiateCookie();
                }

            } else {
                defaultFragment = FRAGMENT_ACCOUNT;
            }
            if (defaultFragment != FRAGMENT_DEFAULT)
                displayView(defaultFragment, true);
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
            PandoraHelper.showMessage(this, "Error: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
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
                    sync(true);
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

        if (!getGlobalVariable().isInitialSynced()) {
            PandoraHelper.showMessage(PandoraMain.this, "Please logout after complete initial sync.");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you wish to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearAllFragmentStack();
                        Bundle inputBundle = new Bundle();
                        if (PandoraHelper.isInternetOn(PandoraMain.this)) {
                            inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                                    getGlobalVariable().getAd_user_name());
                            inputBundle.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG,
                                    getGlobalVariable().getAuth_token());
                            inputBundle.putString(PBSAuthenticatorController.SERVER_URL_ARG,
                                    getGlobalVariable().getServer_url());
                            inputBundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                                    PBSAccountInfo.ACCOUNT_TYPE);
                            inputBundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                                    PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                        } else {
                            inputBundle.putString(PBSAuthenticatorController.USER_NAME_ARG,
                                    getGlobalVariable().getAd_user_name());
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

                                authenticatorController.triggerEvent(PBSAuthenticatorController.CLEAR_AUTH_TOKEN,
                                        inputBundle, null, this);

//                                globalVariable = null;
                                PBSServerConst.cookieStore = null;

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Bundle result) {
                                super.onPostExecute(result);

                                showLogOutDialog(PBSAuthenticatorController.SUCCESSFULLY_LOGGED_OUT_TXT);

                                dismissProgressDialog();
                                checkLoginHandler.removeCallbacks(checkLoginRunnable);
                                checkProjTaskHandler.removeCallbacks(checkProjTaskRunnable);

                                directToFragment(false);
                            }
                        }.execute(inputBundle);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    public static void resetServerData(String serverUrl) {
        // danny test code to reset sync data
        PBSServer pbsServer = new PBSServer();
        String resetUrl = serverUrl + "/wstore/Sync.jsp?action=Reset";
        PBSResultJSON resultJSON = (PBSResultJSON) pbsServer.callServer(resetUrl, PBSResultJSON.class.getName());

        boolean result;
        if (resultJSON != null){
            result = !resultJSON.getSuccess().equals(PBSResultJSON.FALSE_TEXT);
        } else {
            result = false;
        }

        Log.d("Sync Reset", " reset result : " + result);
    }

    /**
     * Perform sync.
     */
    public void sync(boolean gotoRecrut) {
        Bundle inputAuth = new Bundle();
        inputAuth.putString(PBSAuthenticatorController.USER_NAME_ARG,
                getGlobalVariable().getAd_user_name());
        inputAuth.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG,
                getGlobalVariable().getAuth_token());
        inputAuth.putString(PBSAuthenticatorController.SERVER_URL_ARG,
                getGlobalVariable().getServer_url());
        inputAuth.putString(PBSAuthenticatorController.SERIAL_ARG,
                getGlobalVariable().getSerial());
        inputAuth.putBoolean(GOTO_RECRUIT, gotoRecrut);

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
                result.putBoolean(GOTO_RECRUIT, inputAuth.getBoolean(GOTO_RECRUIT));

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
                        //set the auth token to null.
                        Bundle input = new Bundle();
                        input.putString(PBSAuthenticatorController.USER_NAME_ARG,
                                getGlobalVariable().getAd_user_name());
                        input.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                                PBSAccountInfo.ACCOUNT_TYPE);
                        input.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                                PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                        authenticatorController.triggerEvent(PBSAuthenticatorController.CLEAR_AUTH_TOKEN,
                                input, new Bundle(), null);
                        setGlobalVariable(null);
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
                            if (result.getBoolean(GOTO_RECRUIT))
                            {
                                getSupportFragmentManager().popBackStack();
                                Fragment fragment = new RecruitFragment();
                                updateFragment(fragment, getString(R.string.title_recruit),
                                        false);
                            }
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
        try {
            fm.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (NullPointerException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
    }

    /**
     * Log Out dialog. need to redirect upon "OK" click.
     * @param message
     */
    public void showLogOutDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(PandoraConstant.RESULT);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(PandoraConstant.OK_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkLogin(false);
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
        if (position != FRAGMENT_ACCOUNT)
        {
            if (!getGlobalVariable().isInitialSynced()) {
                Toast.makeText(this, "Please wait while initial syncing.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (position < MENU_LIST.length) {
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(
                    R.id.fragment_navigation_drawer);
            String[] titlesList = FragmentDrawer.titles;
            if (titlesList.length > 0)
                for (int i = 0; i < MENU_LIST.length; i++) {
                    if (titlesList[position].equalsIgnoreCase(MENU_LIST[i])) {
                        position = i;
                        break;
                    }
                }
            else position = FRAGMENT_ACCOUNT;
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
//            case FRAGMENT_DEPLOY: {
//                fragment = new DeploymentFragment();
//                title = getString(R.string.title_deploy);
//                break;
//            }
//
            case FRAGMENT_ASSET: {
                String projId = getGlobalVariable().getC_projectlocation_id();
                if (projId == null || projId.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Please select project location.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

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

            case FRAGMENT_ATTENDANCE: {
                String projUuid = getGlobalVariable().getC_projectlocation_uuid();
                if (projUuid == null || projUuid.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Please select project location.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                fragment = new AttendanceFragment();
                title = getString(R.string.title_attentance);
                break;
            }

            case FRAGMENT_CREATE_ATTENDANCE: {
                fragment = new NewAttendanceFragment();
                title = getString(R.string.title_create_attendance);
                break;
            }

            case FRAGMENT_CREATE_ATTENDANCELINE: {
                fragment = new NewAttendanceLineFragment();
                title = getString(R.string.title_create_attendanceline);
                break;
            }

            case FRAGMENT_SURVEY: {
                fragment = new SurveyFragment();
                title = getString(R.string.title_survey);
                break;
            }

            case FRAGMENT_START_SURVEY: {
                fragment = new NewSurveyStartFragment();
                title = getString(R.string.title_new_survey);
                break;
            }

            case FRAGMENT_NEW_SURVEY: {
                fragment = new NewSurveyPagerFragment();
                title = "";
//                title = getString(R.string.title_new_survey);
                break;
            }

            case FRAGMENT_AUDIT: {
                fragment = new AuditFragment();
                title = getString(R.string.title_audit);
                break;
            }

            case FRAGMENT_START_AUDIT: {
                fragment = new NewAuditStartFragment();
                title = getString(R.string.title_new_audit);
                break;
            }

            case FRAGMENT_NEW_AUDIT: {
                fragment = new NewAuditPagerFragment();
                title = "";
                break;
            }

            case FRAGMENT_ATTENDANCE_TRACKING: {
                fragment = new ATrackScanLocFragment();
                title = getString(R.string.title_attendance_tracking);
                break;
            }

            case FRAGMENT_ATTENDANCE_TRACKING_EMPID: {
                fragment = new ATrackScanEmpIDFragment();
                title = getString(R.string.title_attendance_tracking);
                break;
            }

            case FRAGMENT_ATTENDANCE_TRACKING_DONE: {
                fragment = new ATrackDoneFragment();
                title = getString(R.string.title_attendance_tracking);
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
        PandoraHelper.hideSoftKeyboard(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, fragment.getClass().getName());
        if (!isAddToStack) {
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

    /**
     * On click icon_profile show account fragment.
     *
     * @param view
     */
    public void onSync(View view) {
        Bundle extras = new Bundle();
        Bundle input = new Bundle();
        PBSAuthenticatorController authController = new PBSAuthenticatorController(this);
        input.putString(PBSAuthenticatorController.USER_NAME_ARG, getGlobalVariable().getAd_user_name());
        input.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
        Bundle accountBundle = authController.triggerEvent(PBSAuthenticatorController.GET_USER_ACCOUNT_EVENT,
                input, new Bundle(), null);
        if (accountBundle != null) {
            Account acc = accountBundle.getParcelable(PBSAuthenticatorController.USER_ACC_ARG);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(acc, PBSAccountInfo.ACCOUNT_AUTHORITY, extras);
            Toast.makeText(this, "Synced", Toast.LENGTH_SHORT).show();
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (intent.getExtras() != null && intent.getExtras().get("isNotification") != null && (boolean) intent.getExtras().get("isNotification")) {
                if (intent.getExtras().get("className") != null && intent.getExtras().get("className").equals(ProjTaskDetailsFragment.class.getSimpleName())) {
                    Fragment fragment = new ProjTaskDetailsFragment();
                    ((PBSDetailsFragment) fragment).set_UUID(intent.getExtras().getString("uuid"));
                    updateFragment(fragment, getString(R.string.title_projtaskdetails), false);
                }
            }
            else if (intent.getAction() != null) {
                if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                    if (getGlobalVariable().getAd_user_name() == null) {
                        checkLogin(false);
                    } else {
                        fragment = getSupportFragmentManager().findFragmentById(R.id.container_body);
                        if (fragment != null && fragment instanceof NewCheckInFragment) {
                            fragment = new NewCheckInFragment();
                            ((NewCheckInFragment) fragment).setNfcIntent(intent);
                            updateFragment(fragment, getString(R.string.title_newcheckin), false);
                        }
                        else if (fragment != null && fragment instanceof ATrackScanLocFragment) {
                            fragment = new ATrackScanLocFragment();
                            ((ATrackScanLocFragment) fragment).setNfcIntent(intent);
                            updateFragment(fragment, getString(R.string.title_attendance_tracking), false);
                        }
                        else if (fragment != null && fragment instanceof ATrackScanEmpIDFragment) {
                            fragment = new ATrackScanEmpIDFragment();
                            ((ATrackScanEmpIDFragment) fragment).setNfcIntent(intent);
                            if (PBSAttendanceController.isKioskMode)
                                updateFragment(fragment, getString(R.string.title_attendance_tracking_kiosk), false);
                            else updateFragment(fragment, getString(R.string.title_attendance_tracking), false);
                        }
                    }
                }
                else if (intent.getAction().equals(Intent.ACTION_VIEW) && intent.getDataString() != null && intent.getDataString().contains("www.pandorasuite.com/app/")) {
                    String[] intentArray = intent.getDataString().split("/");
                    if (intentArray[2].equalsIgnoreCase("attendance")) {
                        Bundle bundle = new Bundle();
                        if (intentArray[3].equalsIgnoreCase("location")) {
                            fragment = new ATrackScanLocFragment();
                            bundle.putString("location_id", intentArray[4]);
                            fragment.setArguments(bundle);
                            updateFragment(fragment, getString(R.string.title_attendance_tracking), false);
                        }
                        else if (intentArray[3].equalsIgnoreCase("bp")) {
                            fragment = new ATrackScanEmpIDFragment();
                            bundle.putString("bpartner_id", intentArray[4]);
                            fragment.setArguments(bundle);
                            if (PBSAttendanceController.isKioskMode)
                                updateFragment(fragment, getString(R.string.title_attendance_tracking_kiosk), false);
                            else updateFragment(fragment, getString(R.string.title_attendance_tracking), false);
                        }
                    }
                }
            } else {
                checkLogin(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        checkLogin(false);
//        if (!myReceiverIsRegistered) {
//            registerReceiver(myReceiver, new IntentFilter("Received.PandoraCFC.Push.Notification"));
//            myReceiverIsRegistered = true;
//        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (myReceiverIsRegistered) {
//            unregisterReceiver(myReceiver);
//            myReceiverIsRegistered = false;
//        }
//    }

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
                    CameraUtil.galleryAddPic(mCurrentPhotoPath, PandoraMain.this);
//                    if (getCurrentFragment() instanceof ATrackScanEmpIDFragment)
//                        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    mCurrentPhotoPath = null;
                }

                startActivityForResult(takePictureIntent, currentActionCode);
                return true;
            }
        });


//        MenuItem loadPictureItem  = popup.getMenu().add(R.string.title_gallery_button);
//
//        loadPictureItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                if (Build.VERSION.SDK_INT >= 14) {
//                    Log.e("-->", " >= 14");
//                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Log.e("ExternalStorage", "Scanned " + path + ":");
//                            Log.e("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
//                } else {
//                    Log.e("-->", " < 14");
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//                }
//
//                Intent loadPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                if (loadPictureIntent.resolveActivity(getPackageManager()) == null) return true;
//
//                startActivityForResult(loadPictureIntent, currentActionCode);
//                return true;
//            }
//        });

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

    public void updateInitialSyncState(/*boolean isCompleted*/) {
//        if (!isCompleted) return;

        boolean prevSyncState = false;
        if(getGlobalVariable() == null)
            setGlobalVariable((PandoraContext) getApplicationContext());
//        prevSyncState = getGlobalVariable().isInitialSynced();
        getGlobalVariable().setIsInitialSynced(true);
        PandoraHelper.setAccountData(this);
        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... prevSyncState) {
                PandoraHelper.getProjLocAvailable(PandoraMain.this, false);
                return new Boolean(prevSyncState[0]);
            }

            @Override
            protected void onPostExecute(Boolean bPrevState) {
                super.onPostExecute(bPrevState);
                if (!bPrevState.booleanValue()) {
                    Toast.makeText(PandoraMain.this, "Initial Sync completed",
                            Toast.LENGTH_SHORT).show();
                    Fragment frag = getCurrentFragment();
                    if (frag != null && frag instanceof AccountFragment) {
                        ((AccountFragment) frag).completedInitialSync();
                    }
                }
            }
        }.execute(Boolean.valueOf(prevSyncState));
    }

    Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragList = fragmentManager.getFragments();
        for (Fragment frag : fragList) {
            if (frag != null && frag.isVisible())
                return frag;
        }

        return null;
    }

    public void checkLogin(final boolean isLoop) {
        if(getGlobalVariable() != null) {
            new AsyncTask<Void, Void, Bundle>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
//                    if(!isLoop) {
//                        directToFragment();
//                        cancel(true);
//                    }
                }

                @Override
                protected Bundle doInBackground(Void... params) {
                    Bundle input = new Bundle();
                    Bundle result = new Bundle();

                    input.putString(PBSAuthenticatorController.USER_NAME_ARG,
                            getGlobalVariable().getAd_user_name());
                    input.putString(PBSAuthenticatorController.AUTH_TOKEN_ARG,
                            getGlobalVariable().getAuth_token());
                    input.putString(PBSAuthenticatorController.SERVER_URL_ARG,
                            getGlobalVariable().getServer_url());
                    input.putString(PBSAuthenticatorController.SERIAL_ARG,
                            getGlobalVariable().getSerial());
                    result = authenticatorController
                            .triggerEvent(PBSAuthenticatorController.AUTHENTICATE_TOKEN_SERVER,
                                    input, result, null);

                    return result;
                }

                @Override
                protected void onPostExecute(Bundle result) {
                    super.onPostExecute(result);
                    if (!result.getBoolean(PandoraConstant.RESULT)) {
                        getGlobalVariable().setAuth_token("");
                        new LoginAsyncTask().execute();
                    }
                }
            }.execute();
        }
//        else directToFragment(true);
    }

    private class LoginAsyncTask extends AsyncTask<Bundle, Void, Bundle> {

        @Override
        protected Bundle doInBackground(Bundle... params) {
            String deviceID = PandoraHelper.getDeviceID(PandoraMain.this);
            if (deviceID == null || deviceID.isEmpty()) {
                return new Bundle();
            }

            PBSAuthenticatorController authController = new PBSAuthenticatorController(PandoraMain.this);
            Bundle resultBundle = new Bundle();
            Bundle bundle = new Bundle();
            bundle.putString(PBSServerConst.VERSION, getGlobalVariable().getServer_url());
            resultBundle = authController.triggerEvent(PBSAuthenticatorController.AUTHENTICATE_SERVER, bundle, resultBundle, null);

            if (resultBundle.getBoolean(PBSServerConst.RESULT)) {
                resultBundle = new Bundle();
                bundle = new Bundle();
                bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
                bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_NAME, getGlobalVariable().getAd_user_name());
                bundle.putString(PBSAuthenticatorController.USER_PASS_ARG, getGlobalVariable().getAd_user_password());
                bundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE, PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                bundle.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);
                bundle.putString(PBSAuthenticatorController.SERVER_URL_ARG, getGlobalVariable().getServer_url());
                resultBundle = authController.triggerEvent(PBSAuthenticatorController.SUBMIT_LOGIN,
                        bundle, resultBundle, null);
            }
            return resultBundle;
        }

        protected void onPostExecute(Bundle resultBundle) {
            if (resultBundle.getString(PandoraConstant.ERROR) != null) {
                return;
            }

            final PBSLoginJSON loginJSON = (PBSLoginJSON)
                    resultBundle.getSerializable(PBSAuthenticatorController.PBS_LOGIN_JSON);
            if (loginJSON != null) {
                if (loginJSON.getSuccess().equals("TRUE")) {
                    getGlobalVariable().setRoleJSON(loginJSON.getRoles());
                    getGlobalVariable().setAuth_token(loginJSON.getToken());

                    boolean isAuthToken = (!getGlobalVariable().getAuth_token().isEmpty());
                    // request sync if is not finish syncing
                    if (isAuthToken && !getGlobalVariable().isInitialSynced()) {
                        Bundle extras = new Bundle();
                        AccountManager accountManager = AccountManager.get(getApplicationContext());
                        Account[] acc = accountManager.getAccountsByType(PBSAccountInfo.ACCOUNT_TYPE);
                        for (Account account : acc) {
                            if (account.name.equals(getGlobalVariable().getAd_user_name())) {
                                extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                                ContentResolver.requestSync(account, PBSAccountInfo.ACCOUNT_AUTHORITY, extras);
                                break;
                            }
                        }
                    }
//                    if (!isAuthToken)
//                        directToFragment(true);
                }
            }
        }
    }

    public PandoraContext getGlobalVariable() {
        if(globalVariable == null && PBSServerConst.cookieStore != null)
            globalVariable = (PandoraContext) getApplicationContext();
        return globalVariable;
    }

    public void setGlobalVariable(PandoraContext globalVariable) {
        this.globalVariable = globalVariable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PBSServerConst.cookieStore = null;
        checkLoginHandler.removeCallbacks(checkLoginRunnable);
        checkProjTaskHandler.removeCallbacks(checkProjTaskRunnable);
        getContentResolver().unregisterContentObserver(mObserver);
    }

//    private class ReceiveMessages extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            Log.d(TAG, "...................");
//            String action = intent.getAction();
//            Log.d(TAG, "...............action: " + action);
//            if(action.equalsIgnoreCase("Received.PandoraCFC.Push.Notification")){
//                displayView(FRAGMENT_ATTENDANCE, false);
//            }
//        }
//    }
}
