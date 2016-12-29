package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.model.MApplicant;
import com.pbasolutions.android.model.ModelConst;

import java.util.UUID;

/**
 * Created by pbadell on 10/6/15.
 */
public class NewApplicantFragment extends AbstractApplicantFragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewApplicantFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.applicant_new, container, false);
        setUI(rootView);
        setUIListener();
        return rootView;
    }

    /**
     * Override the parent class, to ru
     */
    @Override
    protected void applyUpdate() {
        boolean isValid = true;

        if(name.getText().toString().equals(""))
            isValid = false;
        if(date.getText().toString().equals(""))
            isValid = false;
        if((genderSpinner.getSelectedItem()) == null || ((SpinnerPair)genderSpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if((shiftSpinner.getSelectedItem()) == null || ((SpinnerPair)shiftSpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if((setupJobSpinner.getSelectedItem()) == null || ((SpinnerPair)setupJobSpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if((nationalitySpinner.getSelectedItem()) == null || ((SpinnerPair)nationalitySpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if((maritalStatSpinner.getSelectedItem()) == null || ((SpinnerPair)maritalStatSpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if((idTypeSpinner.getSelectedItem()) == null || ((SpinnerPair)idTypeSpinner.getSelectedItem()).getValue().equals(""))
            isValid = false;
        if(age.getText().toString().equals(""))
            isValid = false;
        else {
            String idNoRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,}$";
            if (!idno.getText().toString().matches(idNoRegex)) {
                Toast.makeText(getActivity(), "Please make sure ID Number consists of at least 1 alphabet and 1 number. No special character allowed.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(expSalary.getText().toString().equals(""))
            isValid = false;
        if(idno.getText().toString().equals(""))
            isValid = false;
        else {
            String idNoRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,}$";
            if (!idno.getText().toString().matches(idNoRegex)) {
                Toast.makeText(getActivity(), "Please make sure ID Number consists of at least 1 alphabet and 1 number. No special character allowed.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(phone.getText().toString().equals(""))
            isValid = false;
        if(yearsOfExp.getText().toString().equals(""))
            isValid = false;
//        if(profileImage == null)
//            isValid = false;
//        if(qualHigh.getText().toString().equals(""))
//            isValid = false;
//        if(qualOther.getText().toString().equals(""))
//            isValid = false;
//        if(cert1Pic == null)
//            isValid = false;
//        if(cert2Pic == null)
//            isValid = false;
//        if(cert3Pic == null)
//            isValid = false;
//        if(cert4Pic == null)
//            isValid = false;
//        if(cert5Pic == null)
//            isValid = false;
//        if(cert6Pic == null)
//            isValid = false;
//        if(cert7Pic == null)
//            isValid = false;
//        if(cert8Pic == null)
//            isValid = false;
//        if(cert9Pic == null)
//            isValid = false;
//        if(cert10Pic == null)
//            isValid = false;
//        if(status.getText().toString().equals(""))
//            isValid = false;

        if(!isValid) {
            Toast.makeText(getActivity(), "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //insertion values.
            ContentValues cv = new ContentValues();
            String ad_org_uuid = appContext.getAd_org_uuid();
            //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
            // if it is .. we have to search the uuid in database
            if (ad_org_uuid.isEmpty()) {
                ad_org_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE,
                        ModelConst.AD_ORG_UUID_COL, context.globalVariable.getAd_org_id(),
                        ModelConst.AD_ORG_TABLE + ModelConst._ID, getActivity().getContentResolver());
                //set the uuid.
                context.globalVariable.setAd_org_uuid(ad_org_uuid);
            }
            cv.put(ModelConst.AD_ORG_UUID_COL, ad_org_uuid);

            String ad_client_uuid = appContext.getAd_client_uuid();
            //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
            // if it is .. we have to search the uuid in database
            if (ad_client_uuid.isEmpty()) {
                ad_client_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_CLIENT_TABLE,
                        ModelConst.AD_CLIENT_UUID_COL, context.globalVariable.getAd_client_id(),
                        ModelConst.AD_CLIENT_TABLE + ModelConst._ID, getActivity().getContentResolver());
                //set the uuid.
                context.globalVariable.setAd_client_uuid(ad_client_uuid);
            }
            cv.put(ModelConst.AD_CLIENT_UUID_COL, ad_client_uuid);

            String ad_user_uuid = appContext.getAd_user_uuid();
            //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
            // if it is .. we have to search the uuid in database
            if (ad_user_uuid.isEmpty()) {
                ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                        ModelConst.AD_USER_UUID_COL, context.globalVariable.getAd_user_id(),
                        ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
                //set the uuid.
                context.globalVariable.setAd_user_uuid(ad_user_uuid);
            }

            //Validate new application ensure that all foreign key are and non null columns
            boolean isError = false;
            if (name.getText() == null || name.getText().toString().isEmpty()) {
                isError = true;
            } else if (setupJobItem.getPair().getKey() == null ||
                    setupJobItem.getPair().getKey().isEmpty()) {
                isError = true;
            } else if (nationalityItem.getPair().getKey() == null ||
                    nationalityItem.getPair().getKey().isEmpty()) {
                isError = true;
            } else if (shiftItem.getPair().getKey() == null ||
                    shiftItem.getPair().getKey().isEmpty()) {
                isError = true;
            }

            if (isError) {
                PandoraHelper.showWarningMessage((PandoraMain) getActivity(), "Please ensure " +
                        "that Name/Job/Nationality/Shift is not empty");
                return;
            }

            cv.put(ModelConst.CREATEDBY_COL, ad_user_uuid);
            cv.put(ModelConst.UPDATEDBY_COL, ad_user_uuid);

            cv.put(MApplicant.HR_JOBAPPLICATION_UUID_COL, UUID.randomUUID().toString());

            SpinnerPair pair = setupJobItem.getPair();
            cv.put(MApplicant.HR_SETUP_JOB_UUID_COL, pair.getKey());

            pair = nationalityItem.getPair();
            cv.put(MApplicant.HR_NATIONALITY_UUID_COL, pair.getKey());

            pair = shiftItem.getPair();
            cv.put(MApplicant.HR_PROJLOCATION_SHIFT_UUID_COL, pair.getKey());

            pair = genderItem.getPair();
            cv.put(MApplicant.SEX_COL, pair.getKey());

            pair = maritalStatItem.getPair();
            cv.put(MApplicant.MARITALSTATUS_COL, pair.getKey());

            pair = idTypeItem.getPair();
            cv.put(MApplicant.IDTYPE_COL, pair.getKey());

            cv.put(MApplicant.YEARSOFEXPERIENCED_COL, yearsOfExp.getText().toString());
            cv.put(MApplicant.EXPECTEDSALARY_COL, expSalary.getText().toString());
            cv.put(MApplicant.NAME_COL, name.getText().toString().trim());
            cv.put(MApplicant.IDNUMBER_COL, idno.getText().toString());

            cv.put(MApplicant.PHONE_COL, phone.getText().toString());
            cv.put(MApplicant.AGE_COL, age.getText().toString());
            cv.put(MApplicant.QUALIFICATION_HIGHEST_COL, qualHigh.getText().toString());
            cv.put(MApplicant.QUALIFICATION_OTHER_COL, qualOther.getText().toString());

            cv.put(MApplicant.ATTACHMENT_APPLICANTPICTURE_COL, (String) profileImage.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_1, (String) cert1Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_2, (String) cert2Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_3, (String) cert3Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_4, (String) cert4Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_5, (String) cert5Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_6, (String) cert6Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_7, (String) cert7Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_8, (String) cert8Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_9, (String) cert9Pic.getTag());
            cv.put(MApplicant.ATTACHMENT_CERTPICTURE_10, (String) cert10Pic.getTag());
            cv.put(ModelConst.INTERVIEWER_NOTES_COL, interviewerNotes.getText().toString());
            cv.put(ModelConst.IS_SYNCED_COL, PandoraConstant.NO);
            cv.put(ModelConst.IS_UPDATED_COL, PandoraConstant.NO);

            Bundle input = new Bundle();
            input.putParcelable(recCont.APPLICANT_VALUES, cv);

            new AsyncTask<Bundle, Void, Bundle>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    ((PandoraMain) getActivity()).showProgressDialog("Loading...");
                }

                @Override
                protected Bundle doInBackground(Bundle... params) {
                    Bundle output = recCont.triggerEvent(recCont.INSERT_APPLICANT_EVENT, params[0], new Bundle(), null);
                    return output;
                }

                @Override
                protected void onPostExecute(Bundle output) {
                    super.onPostExecute(output);
                    if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentManager.popBackStack();
                        fragmentManager.popBackStack();
                        Fragment frag = new RecruitFragment();
                        frag.setRetainInstance(true);
                        ((RecruitFragment) frag).setIsAddApplicant(true);
                        fragmentTransaction.replace(R.id.container_body, frag);
                        fragmentTransaction.addToBackStack(frag.getClass().getName());
                        fragmentTransaction.commit();
                    } else {
                        PandoraHelper.showMessage(context, output.getString(output.getString(PandoraConstant.TITLE)));
                    }
                    ((PandoraMain) getActivity()).dismissProgressDialog();
                }
            }.execute(input);
        }
    }

    @Override
    protected void setValues() {

    }
}
