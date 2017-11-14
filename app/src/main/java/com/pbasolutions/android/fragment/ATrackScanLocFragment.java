package com.pbasolutions.android.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;

import java.util.List;

/**
 * Created by pbadell on 10/5/15.
 */
public class ATrackScanLocFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackScanLocFragment.class.getSimpleName();
    private Intent nfcIntent = null;
    private NfcAdapter mNfcAdapter;
    private PBSCheckInController checkInController;
    private PBSAttendanceController attendanceCont;
    private PandoraMain context;
    private ProgressBar progressBar;
    private TextView tvDesc;
//    private Button btnScanCodeRear, btnScanCodeFront;

    /**
     * Constructor method.
     */
    public ATrackScanLocFragment() {
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
        context = (PandoraMain) getActivity();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        checkInController = new PBSCheckInController(context);
        attendanceCont = new PBSAttendanceController(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.atrack_scanloc, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        tvDesc = (TextView) rootView.findViewById(R.id.tvDesc);
//        btnScanCodeRear = (Button) rootView.findViewById(R.id.btnScanCodeRear);
//        btnScanCodeFront = (Button) rootView.findViewById(R.id.btnScanCodeFront);

//        startCheckInOut("BB:C6:2A:60");
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
                        startCheckInOut(nfcTagID);
                        setNfcIntent(null);
                    } catch (Exception e) {
                        Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                    }
                }
            }
        }

//        final IntentIntegrator integrator = new IntentIntegrator(getActivity());
//        integrator.setPrompt(getString(R.string.scan_qr_barcode_desc));
//        integrator.setOrientationLocked(false);
//        btnScanCodeRear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
//                integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
//            }
//        });
//        btnScanCodeFront.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
//            }
//        });

        return rootView;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if(result != null && getActivity() != null) {
//            if(result.getContents() == null) {
//                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    public void startCheckInOut(String nfcTagID) {
        // verify tag by searching project location table
        Bundle input = new Bundle();
        input.putString(PBSAttendanceController.ARG_NFCTAG, nfcTagID);
        Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.GET_PROJECTLOCATION_BY_TAG_EVENT,
                input, new Bundle(), null);
        List<String> projLocations = result.getStringArrayList(PBSAttendanceController.ARG_PROJECTLOCATIONS);
        if (projLocations.size() > 0) {
            PBSAttendanceController.projectLocationId = projLocations.get(0);
            PBSAttendanceController.projectLocationName = projLocations.get(1);
            PBSAttendanceController.isKioskMode = projLocations.get(2).equals("Y");
            PBSAttendanceController.isPhoto = projLocations.get(3).equals("Y");
//            PBSAttendanceController.isKioskMode = false;
//            PBSAttendanceController.isPhoto = true;
                    ((PandoraMain) getActivity()).displayView(PandoraMain.FRAGMENT_ATTENDANCE_TRACKING_EMPID, false);
        }
        else PandoraHelper.showWarningMessage(getActivity(), getString(R.string.invalid_tag));
    }
}
