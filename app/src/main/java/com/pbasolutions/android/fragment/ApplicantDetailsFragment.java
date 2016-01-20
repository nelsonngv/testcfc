package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.model.MApplicant;
import com.pbasolutions.android.utils.CameraUtil;

/**
 * Created by pbadell on 10/27/15.
 */
public class ApplicantDetailsFragment extends AbstractApplicantFragment {

    /**
     * Class tag name.
     */
    private static final String TAG = "ApplicantDetailsFrag";


    private MApplicant applicant;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.applicant_details, container, false);
        try {
            setUI(rootView);
            setValues();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        //disabled the option menu on details view.
        setHasOptionsMenu(false);
        return rootView;
    }

    /**
     * This method can be use to allow details to be updated again.
     */
    @Override
    protected void applyUpdate() {

    }

    /**
     *
     */
    protected void setValues() {
        applicant = getApplicant();
        status.setText(applicant.getStatus());
        name.setText(applicant.getName());
        idno.setText(applicant.getIdNumber());
        phone.setText(applicant.getPhone());

        genderSpinner.setSelection(((SpinAdapter) genderAdapter)
                .getPosition(applicant.getSex()));
        maritalStatSpinner.setSelection(((SpinAdapter) maritalStatusAdapter)
                .getPosition(applicant.getMaritalStat()));
        setupJobSpinner.setSelection(((SpinAdapter) setupJobAdapter)
                .getPosition(applicant.getHR_SETUP_JOB_UUID()));
        nationalitySpinner.setSelection(((SpinAdapter) nationalityAdapter)
                .getPosition(applicant.getHR_NATIONALITY_UUID()));
        shiftSpinner.setSelection(((SpinAdapter) shiftAdapter)
                .getPosition(applicant.getHR_PROJLOCATION_SHIFT_UUID()));
        idTypeSpinner.setSelection(((SpinAdapter) idtypeAdapter)
                .getPosition(applicant.getIdType()));

        age.setText(applicant.getAge());
        date.setText(applicant.getApplDate());
        yearsOfExp.setText(applicant.getNumOfExp());
        expSalary.setText(applicant.getExpSal());
        qualHigh.setText(applicant.getQualHighest());
        qualOther.setText(applicant.getQualOther());

        CameraUtil.loadPicture(applicant.getProfPic(), profileImage);
        CameraUtil.loadPicture(applicant.getCertPic1(), cert1Pic);
        CameraUtil.loadPicture(applicant.getCertPic2(), cert2Pic);
        CameraUtil.loadPicture(applicant.getCertPic3(), cert3Pic);
        CameraUtil.loadPicture(applicant.getCertPic4(), cert4Pic);
        CameraUtil.loadPicture(applicant.getCertPic5(), cert5Pic);
        CameraUtil.loadPicture(applicant.getCertPic6(), cert6Pic);
        CameraUtil.loadPicture(applicant.getCertPic7(), cert7Pic);
        CameraUtil.loadPicture(applicant.getCertPic8(), cert8Pic);
        CameraUtil.loadPicture(applicant.getCertPic9(), cert9Pic);
        CameraUtil.loadPicture(applicant.getCertPic10(), cert10Pic);

        genderSpinner.setEnabled(false);
        shiftSpinner.setEnabled(false);
        nationalitySpinner.setEnabled(false);

        maritalStatSpinner.setEnabled(false);
        setupJobSpinner.setEnabled(false);
        idTypeSpinner.setEnabled(false);

        name.setEnabled(false);
        idno.setEnabled(false);
        phone.setEnabled(false);

        age.setEnabled(false);
        date.setEnabled(false);
        yearsOfExp.setEnabled(false);
        expSalary.setEnabled(false);
        qualHigh.setEnabled(false);
        qualOther.setEnabled(false);
        status.setEnabled(false);
    }

    private MApplicant getApplicant(){
        Bundle input = new Bundle();
        input.putString(recCont.ARG_JOBAPP_UUID, _UUID);
        input.putSerializable(recCont.APPLICANT_LIST, modelList);
        Bundle result = recCont.triggerEvent(recCont.GET_APPLICANT_EVENT, input, new Bundle(), null);
        return (MApplicant)result.getSerializable(recCont.ARG_APPLICANT);
    }

}
