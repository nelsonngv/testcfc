package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.utils.CameraUtil;

/**
 * Created by pbadell on 10/5/15.
 */
public class NewApplicantSignFragment extends AbstractApplicantFragment {
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
    private Bundle input;

    /**
     * Constructor method.
     */
    public NewApplicantSignFragment() {
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
        ((PandoraMain)getActivity()).getSupportActionBar().hide();
        ((PandoraMain)getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setUI(rootView);
        setUIListener();

        input = getArguments();

        return rootView;
    }

    protected void setUI(View rootView) {
        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mClearButton = (Button) rootView.findViewById(R.id.clear_button);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        TextView mDesc = (TextView) rootView.findViewById(R.id.signature_pad_description);
        mDesc.setText(getString(R.string.applicant_agreement));
    }

    protected void setUIListener() {
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
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                String signImgPath = CameraUtil.storeSignature(signatureBitmap);
                ContentValues cv = input.getParcelable(PBSRecruitController.APPLICANT_VALUES);
                cv.put(MSurvey.ATTACHMENT_SIGNATURE_COL, signImgPath);
                input.putParcelable(PBSRecruitController.APPLICANT_VALUES, cv);
                submitApplicant(input);
            }
        });
    }

    @Override
    protected void applyUpdate() {

    }

    @Override
    protected void setValues() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((PandoraMain)getActivity()).getSupportActionBar().show();
        ((PandoraMain)getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected void submitApplicant(Bundle input) {
        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain) getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                return recCont.triggerEvent(recCont.INSERT_APPLICANT_EVENT, params[0], new Bundle(), null);
            }

            @Override
            protected void onPostExecute(Bundle output) {
                super.onPostExecute(output);
                ((PandoraMain) getActivity()).dismissProgressDialog();
                if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    PandoraHelper.showMessage(getActivity(), output.getString(output.getString(PandoraConstant.TITLE)));
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        }.execute(input);
    }
}
