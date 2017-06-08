package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSRecruitController;
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
public class ATrackScanEmpIDFragment extends PBSDetailsFragment {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackScanEmpIDFragment.class.getSimpleName();
    private Intent nfcIntent = null;
    private NfcAdapter mNfcAdapter;
    private PBSCheckInController checkInController;
    private PBSRecruitController recruitCont;
    private PBSAttendanceController attendanceCont;
    private PandoraMain context;
    private ProgressBar progressBar;
    private TextView tvDesc;
    private String picturePath;
    private ContentValues cv;
    private Bundle bundle;

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
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        checkInController = new PBSCheckInController(getActivity());
        recruitCont = new PBSRecruitController(getActivity());
        attendanceCont = new PBSAttendanceController(getActivity());
        context = (PandoraMain) getActivity();
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
                        String tagID = resultBundle2.getString(PBSCheckInController.NFC_TAG_ID);
                        checkInOut(tagID);
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

    protected void checkInOut(String tagID) {
        bundle = getArguments();
        if (bundle == null || bundle.get(ATrackCheckInOutFragment.ARG_ATTENDANCETYPE) == null)
            return;

        String inOutType = bundle.get(ATrackCheckInOutFragment.ARG_ATTENDANCETYPE).toString();
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
            input.putString(PBSAttendanceController.ARG_TAGID, tagID);
            Bundle result = recruitCont.triggerEvent(PBSRecruitController.GET_EMPLOYEEBYTAG_EVENT,
                    input, new Bundle(), null);
            List<SpinnerPair> employees = result.getParcelableArrayList(PBSRecruitController.ARG_EMPLOYEE);
            if (employees.size() > 0) {
                String empUUID = employees.get(0).getKey();
                PBSAttendanceController.empName = employees.get(0).getValue();
                String projLocUUID = ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL, PBSAttendanceController.projectLocationId,
                        ModelConst.C_PROJECTLOCATION_ID_COL, getActivity().getContentResolver());

                // check if check in/out action is duplicate, example check out before check in
                input = new Bundle();
                input.putString(PBSRecruitController.ARG_EMP_UUID, empUUID);
                input.putString(PBSAttendanceController.ARG_PROJECTLOCATION_UUID, projLocUUID);
                input.putString(PBSAttendanceController.ARG_ATTENDANCE_TRACKING_TYPE, inOutType);
                result = attendanceCont.triggerEvent(PBSAttendanceController.IS_ATTANDENCE_ACTION_DUPLICATE_EVENT,
                        input, new Bundle(), null);

                if (!result.getBoolean(PBSAttendanceController.ARG_IS_DUPLICATE)) {
                    cv = new ContentValues();
                    cv.put(MAttendanceLog.C_PROJECTLOCATION_UUID_COL, projLocUUID);
                    cv.put(MAttendanceLog.C_BPARTNER_UUID_COL, empUUID);
                    cv.put(MAttendanceLog.CHECKINOUTDATE_COL, time);
                    cv.put(MAttendanceLog.CHECKINOUTTYPE_COL, inOutType);
                    cv.put(MAttendanceLog.LONGITUDE_COL, longitude);
                    cv.put(MAttendanceLog.LATITUDE_COL, latitude);

                    if (PBSAttendanceController.isPhoto)
                        takePhoto();
                    else insertAttendance();
                } else {
                    if (inOutType.equals(PBSAttendanceController.ATTENDANCE_TRACKING_TYPE_IN))
                        PandoraHelper.showWarningMessage(getActivity(), getString(R.string.pls_checkout_before_checkin));
                    else PandoraHelper.showWarningMessage(getActivity(), getString(R.string.pls_checkin_before_checkout));

                    Fragment fragment = new ATrackCheckInOutFragment();
                    ((PandoraMain) getActivity()).updateFragment(fragment, getString(R.string.title_survey), false);
                }
            }
            else PandoraHelper.showWarningMessage(getActivity(), getString(R.string.invalid_tag));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == context.RESULT_OK) {
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
        fragment.setArguments(bundle);
        ((PandoraMain) getActivity()).updateFragment(fragment, getString(R.string.title_survey), false);
    }
}
