package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.utils.CameraUtil;

import java.util.List;

/**
 * Created by pbadell on 10/27/15.
 */
public abstract class AbstractApplicantFragment extends PBSDetailsFragment {
    /**
     *
     */
    protected PBSRecruitController recCont;
    /**
     *
     */
    protected Spinner genderSpinner;
    /**
     *
     */
    protected Spinner shiftSpinner;
    /**
     *
     */
    protected Spinner setupJobSpinner;
    /**
     *
     */
    protected Spinner nationalitySpinner;
    /**
     *
     */
    protected Spinner maritalStatSpinner;
    /**
     *
     */
    protected Spinner idTypeSpinner;
    /**
     *
     */
    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_PROFPIC = "EVENT_PROFPIC";
    protected static final String EVENT_CERTPIC_1 = "EVENT_CERTPIC_1";
    protected static final String EVENT_CERTPIC_2 = "EVENT_CERTPIC_2";
    protected static final String EVENT_CERTPIC_3 = "EVENT_CERTPIC_3";
    protected static final String EVENT_CERTPIC_4 = "EVENT_CERTPIC_4";
    protected static final String EVENT_CERTPIC_5 = "EVENT_CERTPIC_5";
    protected static final String EVENT_CERTPIC_6 = "EVENT_CERTPIC_6";
    protected static final String EVENT_CERTPIC_7 = "EVENT_CERTPIC_7";
    protected static final String EVENT_CERTPIC_8 = "EVENT_CERTPIC_8";
    protected static final String EVENT_CERTPIC_9 = "EVENT_CERTPIC_9";
    protected static final String EVENT_CERTPIC_10 = "EVENT_CERTPIC_10";

    protected EditText date;
    protected EditText expSalary;
    protected ImageView profileImage;
    protected EditText name;
    protected EditText idno;
    protected EditText phone;
    protected EditText age;
    protected EditText yearsOfExp;
    protected EditText qualHigh;
    protected EditText qualOther;
    protected ImageView cert1Pic;
    protected ImageView cert2Pic;
    protected ImageView cert3Pic;
    protected ImageView cert4Pic;
    protected ImageView cert5Pic;
    protected ImageView cert6Pic;
    protected ImageView cert7Pic;
    protected ImageView cert8Pic;
    protected ImageView cert9Pic;
    protected ImageView cert10Pic;
    protected EditText status;

    protected PandoraMain context;
    protected PandoraContext appContext;

    protected SpinnerOnItemSelected genderItem;
    protected SpinnerOnItemSelected shiftItem;
    protected SpinnerOnItemSelected setupJobItem;
    protected SpinnerOnItemSelected nationalityItem;
    protected SpinnerOnItemSelected maritalStatItem;
    protected SpinnerOnItemSelected idTypeItem;

    protected ArrayAdapter genderAdapter;
    protected ArrayAdapter shiftAdapter;
    protected ArrayAdapter setupJobAdapter;
    protected ArrayAdapter nationalityAdapter;
    protected ArrayAdapter maritalStatusAdapter;
    protected ArrayAdapter idtypeAdapter;

    private static final int SAVE_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recCont = new PBSRecruitController(getActivity());
        context = (PandoraMain)getActivity();
        appContext = context.globalVariable;
        setHasOptionsMenu(true);
    }

    /**
     *
     * @param rootView
     */
    protected void setUI(View rootView) {
        Activity act = getActivity();

        date = (EditText)rootView.findViewById(R.id.editTextApplDate);
        genderSpinner = (Spinner) rootView.findViewById(R.id.genderSpinner);
        genderAdapter = PandoraHelper.addListToSpinner(act, genderSpinner,
                PandoraHelper.getGenderList());

        shiftSpinner = (Spinner) rootView.findViewById(R.id.prefShiftSpinner);
        shiftAdapter = PandoraHelper.addListToSpinner(act, shiftSpinner,
                getPrefShiftList());

        setupJobSpinner = (Spinner) rootView.findViewById(R.id.setupJobSpinner);
        setupJobAdapter = PandoraHelper.addListToSpinner(act, setupJobSpinner,
                getSetupJobList());

        nationalitySpinner = (Spinner) rootView.findViewById(R.id.nationalitySpinner);
        nationalityAdapter = PandoraHelper.addListToSpinner(act, nationalitySpinner,
                getNationalityList());

        maritalStatSpinner = (Spinner) rootView.findViewById(R.id.maritalStatSpinner);
        maritalStatusAdapter = PandoraHelper.addListToSpinner(act, maritalStatSpinner,
                PandoraHelper.getMaritalStatList());

        idTypeSpinner = (Spinner) rootView.findViewById(R.id.idTypeSpinner);
        idtypeAdapter = PandoraHelper.addListToSpinner(act, idTypeSpinner,
                getIdTypeList());

        age  = (EditText) rootView.findViewById(R.id.editTextApplAge);

        expSalary = (EditText) rootView.findViewById(R.id.editTextExpSal);

        profileImage = (ImageView) rootView.findViewById(R.id.imageViewProfPic);

        name = (EditText) rootView.findViewById(R.id.editTextEmpName);

        idno = (EditText) rootView.findViewById(R.id.editTextEmpIDNumber);

        phone = (EditText) rootView.findViewById(R.id.editTextEmpPhone);

        qualHigh = (EditText) rootView.findViewById(R.id.editTextQualHi);

        qualOther = (EditText) rootView.findViewById(R.id.editTextQualOther);

        cert1Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic1);

        cert2Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic2);

        cert3Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic3);

        cert4Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic4);

        cert5Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic5);

        cert6Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic6);

        cert7Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic7);

        cert8Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic8);

        cert9Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic9);

        cert10Pic = (ImageView) rootView.findViewById(R.id.imageViewcertPic10);

        yearsOfExp = (EditText) rootView.findViewById(R.id.editTextNumOfExp);

        status = (EditText) rootView.findViewById(R.id.editTextStatus);
    }

    /**
     *
     * @return
     */
    private List<SpinnerPair> getIdTypeList() {
        Bundle result = recCont.triggerEvent(recCont.GET_IDTYPE_EVENT, new Bundle(),
                new Bundle(),null);
        return result.getParcelableArrayList(recCont.IDTYPE_LIST);
    }

    /**
     *
     */
    protected void setUIListener(){
        setOnItemSelectedListener();
        setOnClickListener(date, EVENT_DATE);
        setOnClickListener(profileImage, EVENT_PROFPIC);

        setOnClickListener(cert1Pic, EVENT_CERTPIC_1);
        setOnClickListener(cert2Pic, EVENT_CERTPIC_2);
        setOnClickListener(cert3Pic, EVENT_CERTPIC_3);
        setOnClickListener(cert4Pic, EVENT_CERTPIC_4);
        setOnClickListener(cert5Pic, EVENT_CERTPIC_5);
        setOnClickListener(cert6Pic, EVENT_CERTPIC_6);
        setOnClickListener(cert7Pic, EVENT_CERTPIC_7);
        setOnClickListener(cert8Pic, EVENT_CERTPIC_8);
        setOnClickListener(cert9Pic, EVENT_CERTPIC_9);
        setOnClickListener(cert10Pic, EVENT_CERTPIC_10);
    }

    /**
     *
     * @param object
     * @param event
     */
    protected void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptDatePicker((EditText) object, getActivity());
                        break;
                    }
                    case EVENT_PROFPIC: {
                        takePicture(CameraUtil.CAPTURE_PROF_PIC);
                        break;
                    }
                    case EVENT_CERTPIC_1: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_1);
                        break;
                    }
                    case EVENT_CERTPIC_2: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_2);
                        break;
                    }
                    case EVENT_CERTPIC_3: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_3);
                        break;
                    }
                    case EVENT_CERTPIC_4: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_4);
                        break;
                    }
                    case EVENT_CERTPIC_5: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_5);
                        break;
                    }
                    case EVENT_CERTPIC_6: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_6);
                        break;
                    }
                    case EVENT_CERTPIC_7: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_7);
                        break;
                    }
                    case EVENT_CERTPIC_8: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_8);
                        break;
                    }
                    case EVENT_CERTPIC_9: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_9);
                        break;
                    }
                    case EVENT_CERTPIC_10: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_10);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    /**
     *
     *
     */
    protected void setOnItemSelectedListener() {
        genderItem = new SpinnerOnItemSelected(genderSpinner, new SpinnerPair());
        genderSpinner.setOnItemSelectedListener(genderItem);

        shiftItem = new SpinnerOnItemSelected(shiftSpinner, new SpinnerPair());
        shiftSpinner.setOnItemSelectedListener(shiftItem);

        setupJobItem = new SpinnerOnItemSelected(setupJobSpinner, new SpinnerPair());
        setupJobSpinner.setOnItemSelectedListener(setupJobItem);

        nationalityItem = new SpinnerOnItemSelected(nationalitySpinner, new SpinnerPair());
        nationalitySpinner.setOnItemSelectedListener(nationalityItem);

        maritalStatItem = new SpinnerOnItemSelected(maritalStatSpinner, new SpinnerPair());
        maritalStatSpinner.setOnItemSelectedListener(maritalStatItem);

        idTypeItem = new SpinnerOnItemSelected(idTypeSpinner, new SpinnerPair());
        idTypeSpinner.setOnItemSelectedListener(idTypeItem);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == context.RESULT_OK) {
            switch (requestCode) {
                case CameraUtil.CAPTURE_PROF_PIC : {
                    CameraUtil.handleBigCameraPhoto(profileImage,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_1: {
                    CameraUtil.handleBigCameraPhoto(cert1Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_2: {
                    CameraUtil.handleBigCameraPhoto(cert2Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_3: {
                    CameraUtil.handleBigCameraPhoto(cert3Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_4: {
                    CameraUtil.handleBigCameraPhoto(cert4Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_5: {
                    CameraUtil.handleBigCameraPhoto(cert5Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_6: {
                    CameraUtil.handleBigCameraPhoto(cert6Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_7: {
                    CameraUtil.handleBigCameraPhoto(cert7Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_8: {
                    CameraUtil.handleBigCameraPhoto(cert8Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_9: {
                    CameraUtil.handleBigCameraPhoto(cert9Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_10: {
                    CameraUtil.handleBigCameraPhoto(cert10Pic,
                            context.getmCurrentPhotoPath(), context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     *
     * @return
     */
    public List<SpinnerPair> getPrefShiftList() {
        Bundle input = new Bundle();
        input.putString(recCont.ARG_PROJECT_LOCATION_UUID, appContext.getC_projectlocation_uuid());
        Bundle result = recCont.triggerEvent(recCont.GET_SHIFTS_EVENT, input, new Bundle(),null);
        return result.getParcelableArrayList(recCont.SHIFT_LIST);
    }

    /**
     *
     * @return
     */
    private List<SpinnerPair> getSetupJobList() {
        Bundle result = recCont.triggerEvent(recCont.GET_SETUPJOB_EVENT, new Bundle(),
                new Bundle(),null);
        return result.getParcelableArrayList(recCont.SETUPJOB_LIST);
    }

    /**
     *
     * @return
     */
    public List<SpinnerPair> getNationalityList() {
        Bundle result = recCont.triggerEvent(recCont.GET_NATIONALITY_EVENT, new Bundle(),
                new Bundle(),null);
        return result.getParcelableArrayList(recCont.NATIONALITY_LIST);
    }

    /**
     *
     */
    protected abstract void applyUpdate();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, SAVE_ID, 1, getString(R.string.text_icon_save));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SAVE_ID: {
                applyUpdate();
                return  true;
            }
            default:return false;
        }
    }
}
