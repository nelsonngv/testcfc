package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSRecruitController;
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
        PandoraHelper.hideSoftKeyboard(getActivity());
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(name.getText().toString().equals("")) {
            warnToFill(imm, name);
            return;
        }
        if((idTypeSpinner.getSelectedItem()) == null || ((SpinnerPair)idTypeSpinner.getSelectedItem()).getValue().equals("")) {
            warnToFill(imm, idTypeSpinner);
            return;
        }
        if(idno.getText().toString().equals("")) {
            warnToFill(imm, idno);
            return;
        }
        else {
            String idNoRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,}$";
            if (!idno.getText().toString().matches(idNoRegex)) {
                PandoraHelper.showMessage(getActivity(), "Please make sure ID Number consists of at least 1 alphabet and 1 number. No special character allowed.");
                idno.requestFocus();
                imm.showSoftInput(idno, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
        }
        if(phone.getText().toString().equals("")) {
            warnToFill(imm, phone);
            return;
        }
        if((genderSpinner.getSelectedItem()) == null || ((SpinnerPair)genderSpinner.getSelectedItem()).getValue().equals("")) {
            warnToFill(imm, genderSpinner);
            return;
        }
        if(age.getText().toString().equals("")) {
            warnToFill(imm, age);
            return;
        }
        if((maritalStatSpinner.getSelectedItem()) == null || ((SpinnerPair)maritalStatSpinner.getSelectedItem()).getValue().equals("")) {
            warnToFill(imm, maritalStatSpinner);
            return;
        }
        if(date.getText().toString().equals("")) {
            date.requestFocus();
            PandoraHelper.showMessage(getActivity(), "Please fill in all the required fields.");
            return;
        }
        if((setupJobSpinner.getSelectedItem()) == null || ((SpinnerPair)setupJobSpinner.getSelectedItem()).getValue().equals("")) {
            warnToFill(imm, setupJobSpinner);
            return;
        }
        if((nationalitySpinner.getSelectedItem()) == null || ((SpinnerPair)nationalitySpinner.getSelectedItem()).getValue().equals("")) {
            warnToFill(imm, nationalitySpinner);
            return;
        }
        if(yearsOfExp.getText().toString().equals("")) {
            warnToFill(imm, yearsOfExp);
            return;
        }
        if(expSalary.getText().toString().equals("")) {
            warnToFill(imm, expSalary);
            return;
        }
//        if((shiftSpinner.getSelectedItem()) == null || ((SpinnerPair)shiftSpinner.getSelectedItem()).getValue().equals("")) {
//            warnToFill(imm, shiftSpinner);
//            return;
//        }

        //insertion values.
        ContentValues cv = new ContentValues();
        String ad_org_uuid = appContext.getAd_org_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_org_uuid == null || ad_org_uuid.equalsIgnoreCase("null") || ad_org_uuid.isEmpty()) {
            ad_org_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE,
                    ModelConst.AD_ORG_UUID_COL, context.getGlobalVariable().getAd_org_id(),
                    ModelConst.AD_ORG_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            context.getGlobalVariable().setAd_org_uuid(ad_org_uuid);
        }
        cv.put(ModelConst.AD_ORG_UUID_COL, ad_org_uuid);

        String ad_client_uuid = appContext.getAd_client_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_client_uuid == null || ad_client_uuid.equalsIgnoreCase("null") || ad_client_uuid.isEmpty()) {
            ad_client_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_CLIENT_TABLE,
                    ModelConst.AD_CLIENT_UUID_COL, context.getGlobalVariable().getAd_client_id(),
                    ModelConst.AD_CLIENT_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            context.getGlobalVariable().setAd_client_uuid(ad_client_uuid);
        }
        cv.put(ModelConst.AD_CLIENT_UUID_COL, ad_client_uuid);

        String ad_user_uuid = appContext.getAd_user_uuid();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_user_uuid == null || ad_user_uuid.equalsIgnoreCase("null") || ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_UUID_COL, context.getGlobalVariable().getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            context.getGlobalVariable().setAd_user_uuid(ad_user_uuid);
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
            PandoraHelper.showWarningMessage(getActivity(), "Please ensure " +
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

        if (shiftItem != null && shiftItem.getPair() != null) {
            pair = shiftItem.getPair();
            cv.put(MApplicant.HR_PROJLOCATION_SHIFT_UUID_COL, pair.getKey());
        }
        else cv.put(MApplicant.HR_PROJLOCATION_SHIFT_UUID_COL, "");

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
        cv.put(MApplicant.APPLICATIONDATE_COL, date.getText().toString());

        cv.put(MApplicant.ATTACHMENT_APPLICANTPICTURE_COL, (String) profileImage.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_1, (String) cert1Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_2, (String) cert2Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_3, (String) cert3Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_4, (String) cert4Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_5, (String) cert5Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_6, (String) cert6Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_7, (String) cert7Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_8, (String) cert8Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_9, (String) cert9Pic.getTag(R.string.tag_imageview_path));
        cv.put(MApplicant.ATTACHMENT_CERTPICTURE_10, (String) cert10Pic.getTag(R.string.tag_imageview_path));
        cv.put(ModelConst.INTERVIEWER_NOTES_COL, interviewerNotes.getText().toString());
        cv.put(ModelConst.IS_SYNCED_COL, PandoraConstant.NO);
        cv.put(ModelConst.IS_UPDATED_COL, PandoraConstant.NO);

        Bundle input = new Bundle();
        input.putParcelable(PBSRecruitController.APPLICANT_VALUES, cv);

        PandoraHelper.hideSoftKeyboard(getActivity());
        Fragment fragment = new NewApplicantSignFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.setArguments(input);
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    private void warnToFill(InputMethodManager imm, Object obj) {
        if (obj instanceof EditText) {
            EditText editText = (EditText) obj;
            editText.requestFocusFromTouch();
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        else if (obj instanceof Spinner) {
            Spinner spinner = (Spinner) obj;
            spinner.requestFocusFromTouch();
        }
        PandoraHelper.showMessage(getActivity(), "Please fill in all the required fields.");
    }

    @Override
    protected void setValues() {

    }
}
