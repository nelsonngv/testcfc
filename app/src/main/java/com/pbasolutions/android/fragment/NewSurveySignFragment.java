package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

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
    private ArrayList<MSurvey> answerList;

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

        Bundle bundle = getArguments();
        if (bundle != null)
            answerList = bundle.getParcelableArrayList("answers");

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
                if (answerList == null || answerList.size() == 0) {
                    PandoraHelper.showWarningMessage(getActivity(), "No question is answered");
                    return;
                }
                saveSurvey();
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

    protected void saveSurvey() {
        Bundle resultBundle = checkInController.triggerEvent(PBSCheckInController.GET_LOCATION, null, new Bundle(), null);
        String latitude = String.valueOf(resultBundle.getDouble("DEVICE_LAT"));
        String longitude = String.valueOf(resultBundle.getDouble("DEVICE_LONG"));

        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        String signImgPath = CameraUtil.storeSignature(signatureBitmap);

        PandoraContext appContext = ((PandoraMain) getActivity()).getGlobalVariable();
        ContentValues surveyCV = new ContentValues();
        String ad_org_uuid = appContext.getAd_org_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_org_uuid.isEmpty()) {
            ad_org_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE,
                    ModelConst.AD_ORG_UUID_COL, appContext.getAd_org_id(),
                    ModelConst.AD_ORG_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            appContext.setAd_org_uuid(ad_org_uuid);
        }
        surveyCV.put(ModelConst.AD_ORG_UUID_COL, ad_org_uuid);

        String ad_client_uuid = appContext.getAd_client_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_client_uuid.isEmpty()) {
            ad_client_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_CLIENT_TABLE,
                    ModelConst.AD_CLIENT_UUID_COL, appContext.getAd_client_id(),
                    ModelConst.AD_CLIENT_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            appContext.setAd_client_uuid(ad_client_uuid);
        }
        surveyCV.put(ModelConst.AD_CLIENT_UUID_COL, ad_client_uuid);

        String ad_user_uuid = appContext.getAd_user_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_UUID_COL, appContext.getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            appContext.setAd_user_uuid(ad_user_uuid);
        }
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String end = dt.format(date);
        surveyCV.put(MSurvey.C_SURVEY_UUID_COL, UUID.randomUUID().toString());
        surveyCV.put(MSurvey.C_PROJECTLOCATION_UUID_COL, PBSSurveyController.projLocUUID);
        surveyCV.put(MSurvey.C_SURVEYTEMPLATE_UUID_COL, PBSSurveyController.templateUUID);
        surveyCV.put(MSurvey.ISACTIVE_COL, "Y");
        surveyCV.put(MSurvey.STATUS_COL, "C");
        surveyCV.put(MSurvey.VALUE_COL, "");
        surveyCV.put(MSurvey.EMAIL_COL, "");
        surveyCV.put(MSurvey.DATEDELIVERY_COL, PBSSurveyController.dateStart.substring(0, 10) + " 00:00:00");
        surveyCV.put(MSurvey.NAME_COL, PBSSurveyController.name);
        surveyCV.put(MSurvey.DATESTART_COL, PBSSurveyController.dateStart);
        surveyCV.put(MSurvey.DATEEND_COL, end);
        surveyCV.put(MSurvey.LATITUDE_COL, latitude);
        surveyCV.put(MSurvey.LONGITUDE_COL, longitude);
        surveyCV.put(MSurvey.ATTACHMENT_SIGNATURE_COL, signImgPath);
        surveyCV.put(ModelConst.CREATEDBY_COL, ad_user_uuid);
        surveyCV.put(ModelConst.UPDATEDBY_COL, ad_user_uuid);
        surveyCV.put(ModelConst.IS_SYNCED_COL, PandoraConstant.NO);
        surveyCV.put(ModelConst.IS_UPDATED_COL, PandoraConstant.NO);

        ContentValues[] answerCV = new ContentValues[answerList.size()];
        for (int i = 0; i < answerList.size(); i++) {
            MSurvey answer = answerList.get(i);
            answerCV[i] = new ContentValues();
            answerCV[i].put(MSurvey.C_SURVEYRESPONSE_UUID_COL, UUID.randomUUID().toString());
            answerCV[i].put(ModelConst.AD_ORG_UUID_COL, ad_org_uuid);
            answerCV[i].put(ModelConst.AD_CLIENT_UUID_COL, ad_client_uuid);
            answerCV[i].put(MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL, answer.getC_SurveyTemplateQuestion_UUID());
            answerCV[i].put(MSurvey.ISACTIVE_COL, "Y");
            answerCV[i].put(MSurvey.REMARKS_COL, answer.getRemarks());
            answerCV[i].put(MSurvey.AMT_COL, answer.getAmt());
            answerCV[i].put(ModelConst.CREATEDBY_COL, ad_user_uuid);
            answerCV[i].put(ModelConst.UPDATEDBY_COL, ad_user_uuid);
            answerCV[i].put(ModelConst.IS_SYNCED_COL, PandoraConstant.NO);
            answerCV[i].put(ModelConst.IS_UPDATED_COL, PandoraConstant.NO);
        }

        Bundle input = new Bundle();
        input.putParcelable(PBSSurveyController.SURVEY_VALUES, surveyCV);
        input.putParcelableArray(PBSSurveyController.SURVEY_ANSWERS, answerCV);

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain) getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle output = surveyCont.triggerEvent(surveyCont.INSERT_SURVEY_EVENT, params[0], new Bundle(), null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle output) {
                super.onPostExecute(output);
                ((PandoraMain) getActivity()).dismissProgressDialog();
                if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    PandoraMain.instance.getSupportActionBar().show();
                    PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    Fragment surveyFrag = new SurveyFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, surveyFrag);
                    fragmentTransaction.addToBackStack(surveyFrag.getClass().getName());
                    fragmentTransaction.commit();
                } else {
                    PandoraHelper.showMessage(getActivity(), output.getString(output.getString(PandoraConstant.TITLE)));
                }
            }
        }.execute(input);
    }
}
