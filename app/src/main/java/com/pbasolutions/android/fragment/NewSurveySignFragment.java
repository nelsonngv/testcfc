package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pbadell on 10/5/15.
 */
public class NewSurveySignFragment extends Fragment {
    /**
     * Class tag name.
     */
    PBSCheckInController checkInController;
    PBSAttendanceController attendanceCont;
    PBSSurveyController surveyCont;
    ContentResolver cr;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    protected MSurvey survey;

    /**
     * Constructor method.
     */
    public NewSurveySignFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        surveyCont = new PBSSurveyController(getActivity());
        checkInController = new PBSCheckInController(getActivity());
        attendanceCont = new PBSAttendanceController(getActivity());
        cr = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey_sign, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        PandoraMain.instance.getSupportActionBar().hide();
        PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setUI(rootView);
        setUIListener();

        return rootView;
    }

    void setUI(View rootView) {
        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mClearButton = (Button) rootView.findViewById(R.id.clear_button);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
    }

    void setUIListener() {
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                mClearButton.setEnabled(true);
                mSaveButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
                mClearButton.setEnabled(false);
                mSaveButton.setEnabled(false);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle resultBundle = checkInController.triggerEvent(PBSCheckInController.GET_LOCATION, null, new Bundle(), null);
                String.valueOf(resultBundle.getDouble("DEVICE_LAT"));
                String.valueOf(resultBundle.getDouble("DEVICE_LONG"));

                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
//                if (addJpgSignatureToGallery(signatureBitmap)) {
//                    Toast.makeText(MainActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
//                }
//                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
//                    Toast.makeText(MainActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PandoraMain.instance.getSupportActionBar().show();
        PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected void startSurvey() {
//        SpinnerPair spinnerPair = (SpinnerPair) templateSpinner.getSelectedItem();
//        if (spinnerPair.getKey() == null) {
//            PandoraHelper.showWarningMessage(getActivity(), getString(
//                    R.string.no_list_error, getString(R.string.label_template)));
//            return;
//        }
//
//        Date date = new Date();
//        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        dt.setTimeZone(TimeZone.getDefault());
//        String now = dt.format(date);
//        PBSSurveyController.name = etName.getText().toString();
//        PBSSurveyController.templateUUID = spinnerPair.getKey();
//        PBSSurveyController.dateStart = now;
//
//        ((PandoraMain) getActivity()).
//                displayView(PandoraMain.FRAGMENT_NEW_SURVEY, false);
    }
}
