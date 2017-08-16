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
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by pbadell on 10/5/15.
 */
public class ProjTaskSignFragment extends Fragment {
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
    private PBSTaskController taskCont;
    private Bundle input;

    /**
     * Constructor method.
     */
    public ProjTaskSignFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        taskCont = new PBSTaskController(getActivity());
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

        input = getArguments();

        return rootView;
    }

    void setUI(View rootView) {
        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mClearButton = (Button) rootView.findViewById(R.id.clear_button);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        TextView mDesc = (TextView) rootView.findViewById(R.id.signature_pad_description);
        mDesc.setText(getString(R.string.proj_task_agreement));
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
                completeProj();
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

    protected void completeProj() {
        Bundle resultBundle = checkInController.triggerEvent(PBSCheckInController.GET_LOCATION, null, new Bundle(), null);
        String latitude = String.valueOf(resultBundle.getDouble("DEVICE_LAT"));
        String longitude = String.valueOf(resultBundle.getDouble("DEVICE_LONG"));

        if (!latitude.equals("0.0") && !longitude.equals("0.0")) {
            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
            String signImgPath = CameraUtil.storeSignature(signatureBitmap);

            input.putString(MSurvey.LATITUDE_COL, latitude);
            input.putString(MSurvey.LONGITUDE_COL, longitude);
            input.putString(MSurvey.ATTACHMENT_SIGNATURE_COL, signImgPath);

            new AsyncTask<Bundle, Void, Bundle>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    ((PandoraMain)getActivity()).showProgressDialog("Loading...");
                }

                @Override
                protected Bundle doInBackground(Bundle... params) {
                    Bundle output = new Bundle();
                    output = taskCont.triggerEvent(taskCont.COMPLETE_PROJTASK_EVENT, params[0], output, null);
                    return output;
                }

                @Override
                protected void onPostExecute(Bundle result) {
                    super.onPostExecute(result);
                    if (PandoraConstant.RESULT.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                        PandoraMain.instance.getSupportFragmentManager().popBackStack();
                        PandoraMain.instance.getSupportFragmentManager().popBackStack();
                    } else {
                        PandoraHelper.showMessage((PandoraMain)getActivity(),
                                result.getString(result.getString(PandoraConstant.TITLE)));
                        PandoraMain.instance.getSupportFragmentManager().popBackStack();
                    }
                    ((PandoraMain)getActivity()).dismissProgressDialog();
                }
            }.execute(input);
        }
    }
}
