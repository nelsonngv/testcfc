package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.utils.CameraUtil;

import java.util.ArrayList;

public class NewAuditSign2Fragment extends Fragment {
    /**
     * Class tag name.
     */
    PBSCheckInController checkInController;
    PBSAttendanceController attendanceCont;
    PBSSurveyController auditCont;
    ContentResolver cr;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private ArrayList<MSurvey> answerList;
    private PBSTaskController taskCont;
    private Bundle input;

    /**
     * Constructor method.
     */
    public NewAuditSign2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        taskCont = new PBSTaskController(getActivity());
        auditCont = new PBSSurveyController(getActivity());
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

    void setUI(View rootView) {
        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mClearButton = (Button) rootView.findViewById(R.id.clear_button);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        TextView mDesc = (TextView) rootView.findViewById(R.id.signature_pad_description);
        mDesc.setText(getString(R.string.survey_agreement2));
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
                completeAudit();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((PandoraMain)getActivity()).getSupportActionBar().show();
        ((PandoraMain)getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected void completeAudit() {
        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        String signImgPath = CameraUtil.storeSignature(signatureBitmap);

        ContentValues auditCV = input.getParcelable(PBSSurveyController.SURVEY_VALUES);
        auditCV.put(MProjectTask.ATTACHMENT_STAFFSIGNATURE_COL, signImgPath);
        input.putParcelable(PBSSurveyController.SURVEY_VALUES, auditCV);

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain) getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                return auditCont.triggerEvent(auditCont.INSERT_SURVEY_EVENT, params[0], new Bundle(), null);
            }

            @Override
            protected void onPostExecute(Bundle output) {
                super.onPostExecute(output);
                ((PandoraMain) getActivity()).dismissProgressDialog();
                if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ((PandoraMain)getActivity()).getSupportActionBar().show();
                    ((PandoraMain)getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

//                    Fragment auditFrag = new AuditFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.container_body, auditFrag);
////                        fragmentTransaction.addToBackStack(surveyFrag.getClass().getName());
//                    fragmentTransaction.commit();
//                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.title_audit));

                    Fragment fragment = new NewAuditSummaryFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragment.setArguments(input);
                    fragment.setRetainInstance(true);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    fragmentTransaction.commit();
                } else {
                    PandoraHelper.showMessage(getActivity(), output.getString(output.getString(PandoraConstant.TITLE)));
                }
            }
        }.execute(input);
    }
}
