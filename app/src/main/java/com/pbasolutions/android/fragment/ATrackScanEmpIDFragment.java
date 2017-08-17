package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.json.PBSLoginJSON;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.model.MAttendanceLog;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pbadell on 10/5/15.
 */
public class ATrackScanEmpIDFragment extends PBSDetailsFragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackScanEmpIDFragment.class.getSimpleName();
    private static final int CANCEL_ID = 1;
    private Intent nfcIntent = null;
    private NfcAdapter mNfcAdapter;
    private PBSCheckInController checkInController;
    private PBSRecruitController recruitCont;
    private PBSAttendanceController attendanceCont;
    private PBSAuthenticatorController authController;
    private PandoraMain context;
    private ProgressBar progressBar;
    private TextView tvDesc;
    private String picturePath;
    private ContentValues cv;

    /**
     * Constructor method.
     */
    public ATrackScanEmpIDFragment() {
    }

    public Intent getNfcIntent() {
        return nfcIntent;
    }

    public void setNfcIntent(Intent intent) {
        this.nfcIntent = intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PBSAttendanceController.isKioskMode) {
            setHasOptionsMenu(true);
            PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        context = (PandoraMain) getActivity();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        checkInController = new PBSCheckInController(context);
        recruitCont = new PBSRecruitController(context);
        attendanceCont = new PBSAttendanceController(context);
        authController = new PBSAuthenticatorController(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.atrack_scanemp, container, false);
        setUI(rootView);

        if (mNfcAdapter == null) {
            tvDesc.setText(getString(R.string.nfc_notsupport));
            tvDesc.setTextColor(Color.RED);
            progressBar.setVisibility(View.GONE);
        }
        if  (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                tvDesc.setText(getString(R.string.nfc_disabled));
                tvDesc.setTextColor(Color.RED);
                progressBar.setVisibility(View.GONE);
            }
        }
        if (getNfcIntent() != null) {
            if (getNfcIntent().getAction() != null) {
                if (getNfcIntent().getAction().equals("android.nfc.action.TAG_DISCOVERED")) {
                    Bundle inputBundle = new Bundle();
                    Bundle resultBundle2 = new Bundle();
                    try {
                        resultBundle2 = checkInController.triggerEvent(PBSCheckInController.PROCESS_NFC, inputBundle, resultBundle2, getNfcIntent());
                        String nfcTagID = resultBundle2.getString(PBSCheckInController.NFC_TAG_ID);
                        checkInOut(nfcTagID);
                        setNfcIntent(null);
                    } catch (Exception e) {
                        Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                    }
                }
            }
        }
        return rootView;
    }

    @Override
    protected void setUI(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);
    }

    @Override
    protected void setUIListener() {

    }

    @Override
    protected void setValues() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem exit;
        exit = menu.add(0, CANCEL_ID, 0, getString(R.string.label_cancel));
        exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        exit.setIcon(R.drawable.x);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case CANCEL_ID: {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.label_password));

                    LinearLayout ll = new LinearLayout(getActivity());
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(50, 0, 50, 0);
                    input.setLayoutParams(layoutParams);
                    ll.addView(input);
                    builder.setView(ll);

                    builder.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Bundle bundle = new Bundle();
                            bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_NAME, context.getGlobalVariable().getAd_user_name());
                            bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
                            bundle.putString(PBSAuthenticatorController.USER_PASS_ARG, input.getText().toString());
                            bundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE, PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                            bundle.putString(PBSAuthenticatorController.SERIAL_ARG, context.getGlobalVariable().getSerial());
                            bundle.putString(PBSAuthenticatorController.SERVER_URL_ARG, context.getGlobalVariable().getServer_url());

                            new LoginAsyncTask().execute(bundle);
                        }
                    });

                    builder.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } catch (Exception e) {
                    Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                }
                return  true;
            }
            default: return false;
        }
    }

    @Override
    public boolean onBackKeyPressed() {
        if (PBSAttendanceController.isKioskMode)
            return false;
        else {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            return true;
        }
    }

    private class LoginAsyncTask extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected void onPreExecute() {
            context.showProgressDialog("Loading...");
        }

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle resultBundle = new Bundle();
            resultBundle = authController.triggerEvent(PBSAuthenticatorController.SUBMIT_LOGIN,
                    params[0], resultBundle, null);
            return resultBundle;
        }

        protected void onPostExecute(Bundle resultBundle) {
            context.dismissProgressDialog();
            if (resultBundle.getString(PandoraConstant.ERROR) != null) {
                PandoraHelper.showMessage(PandoraMain.instance, resultBundle.getString(resultBundle.getString(PandoraConstant.TITLE)));
                return;
            }

            final PBSLoginJSON loginJSON = (PBSLoginJSON) resultBundle.getSerializable(PBSAuthenticatorController.PBS_LOGIN_JSON);
            if (loginJSON != null) {
                if (loginJSON.getSuccess().equals("TRUE")) {
                    PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_attendance_tracking));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    fragmentManager.popBackStack();
                } else {
                    PandoraHelper.showAlertMessage((PandoraMain) getActivity(),
                            "Invalid username or password", PandoraConstant.ERROR, "Retry", "Ok");
                }
            }
        }
    }

    protected void checkInOut(String nfcTagID) {
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String time = dt.format(date);

        Bundle resultBundle = checkInController.triggerEvent(PBSCheckInController.GET_LOCATION, null, new Bundle(), null);
        String latitude = String.valueOf(resultBundle.getDouble("DEVICE_LAT"));
        String longitude = String.valueOf(resultBundle.getDouble("DEVICE_LONG"));

        if (!latitude.equals("0.0") && !longitude.equals("0.0")) {
            // get employee info by tag
            Bundle input = new Bundle();
            input.putString(PBSAttendanceController.ARG_NFCTAG, nfcTagID);
            Bundle result = recruitCont.triggerEvent(PBSRecruitController.GET_EMPLOYEEBYTAG_EVENT,
                    input, new Bundle(), null);
            List<SpinnerPair> employees = result.getParcelableArrayList(PBSRecruitController.ARG_EMPLOYEE);
            if (employees.size() > 0) {
                String empUUID = employees.get(0).getKey();
                PBSAttendanceController.empName = employees.get(0).getValue();
                String projLocUUID = ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL, PBSAttendanceController.projectLocationId,
                        ModelConst.C_PROJECTLOCATION_ID_COL, getActivity().getContentResolver());

                cv = new ContentValues();
                cv.put(MAttendanceLog.C_PROJECTLOCATION_UUID_COL, projLocUUID);
                cv.put(MAttendanceLog.C_BPARTNER_UUID_COL, empUUID);
                cv.put(MAttendanceLog.DATETRX_COL, time);
                cv.put(MAttendanceLog.LONGITUDE_COL, longitude);
                cv.put(MAttendanceLog.LATITUDE_COL, latitude);

                if (PBSAttendanceController.isPhoto)
                    takePhoto();
                else insertAttendance();
            }
            else PandoraHelper.showWarningMessage(getActivity(), getString(R.string.invalid_tag));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            picturePath = CameraUtil.getPicPath(context, data);

            if (picturePath != null) {
                context.mCurrentPhotoPath = null;
                cv.put(MAttendanceLog.ATTACHMENT_IMAGE_COL, picturePath);
                insertAttendance();
            }
        }
    }

    protected void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
            PandoraHelper.showErrorMessage(getActivity(), "Unexpected error occurred");
            return;
        }

        try {
            File f = CameraUtil.setUpPhotoFile(context.mAlbumStorageDirFactory);
            context.mCurrentPhotoPath = f.getAbsolutePath();
            CameraUtil.galleryAddPic(context.mCurrentPhotoPath, getActivity());
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            context.mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, 0);
        PandoraHelper.showMessage(getActivity(), "Please show your face");
    }

    protected void insertAttendance() {
        Bundle input = new Bundle();
        input.putParcelable(PBSAttendanceController.ARG_CONTENTVALUES, cv);
        Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.CREATE_ATTENDANCETRACKING_EVENT,
                input, new Bundle(), null);

        Fragment fragment = new ATrackDoneFragment();
        ((PandoraMain) getActivity()).updateFragment(fragment, getString(R.string.title_attendance_tracking), false);
    }
}
