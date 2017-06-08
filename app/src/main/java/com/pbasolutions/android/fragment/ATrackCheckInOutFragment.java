package com.pbasolutions.android.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.json.PBSLoginJSON;
import com.pbasolutions.android.listener.PBABackKeyListener;

import java.util.List;

/**
 * Created by pbadell on 10/5/15.
 */
public class ATrackCheckInOutFragment extends Fragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackCheckInOutFragment.class.getSimpleName().substring(0, ATrackCheckInOutFragment.class.getSimpleName().length() - 1);
    private static final int CANCEL_ID = 1;
    private PBSAuthenticatorController authController;
    private PandoraMain context;
    private TextView tvProjLoc;
    private Button btnCheckIn;
    private Button btnCheckOut;
    public static final String ARG_ATTENDANCETYPE = "attendanceType";

    /**
     * Constructor method.
     */
    public ATrackCheckInOutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        authController = new PBSAuthenticatorController(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PandoraMain.instance.getSupportActionBar().hide();
        PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        View rootView = inflater.inflate(R.layout.atrack_checkinout, container, false);
        tvProjLoc = (TextView) rootView.findViewById(R.id.tvProjLoc);
        btnCheckIn = (Button) rootView.findViewById(R.id.btnCheckIn);
        btnCheckOut = (Button) rootView.findViewById(R.id.btnCheckOut);

        tvProjLoc.setText(PBSAttendanceController.projectLocationName);
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCheckInOut(PBSAttendanceController.ATTENDANCE_TRACKING_TYPE_IN);
            }
        });
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCheckInOut(PBSAttendanceController.ATTENDANCE_TRACKING_TYPE_OUT);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem exit;
        exit = menu.add(0, CANCEL_ID, 0, "Cancel");
        exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        exit.setIcon(R.drawable.x);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case CANCEL_ID: {
                try {
                    final Bundle bundle = new Bundle();
                    bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_NAME, context.getGlobalVariable().getAd_user_name());
                    bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
                    bundle.putString(PBSAuthenticatorController.USER_PASS_ARG, context.getGlobalVariable().getAd_user_password());
                    bundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE, PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
                    bundle.putString(PBSAuthenticatorController.SERIAL_ARG, context.getGlobalVariable().getSerial());
                    bundle.putString(PBSAuthenticatorController.SERVER_URL_ARG, context.getGlobalVariable().getServer_url());

                    new LoginAsyncTask().execute(bundle);
                } catch (Exception e) {
                    Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                }
                return  true;
            }
            default:return false;
        }
    }

    protected void startCheckInOut(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ATTENDANCETYPE, type);
        Fragment fragment = new NewSurveySummaryFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
        PandoraMain.instance.getSupportActionBar().setTitle("");
    }

    private class LoginAsyncTask extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected void onPreExecute() {
            context.showProgressDialog("Loading...");
        }

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle resultBundle = new Bundle();
            resultBundle = authController.triggerEvent(PBSAuthenticatorController.SUBMIT_LOGIN,
                    params[0], resultBundle, null);
            return resultBundle;
        }

        protected void onPostExecute(Bundle resultBundle) {
            context.dismissProgressDialog();
            if (resultBundle.getString(PandoraConstant.ERROR) != null) {
                PandoraHelper.showMessage(PandoraMain.instance, resultBundle.getString(resultBundle.getString(PandoraConstant.TITLE)));
                return;
            }

            final PBSLoginJSON loginJSON = (PBSLoginJSON) resultBundle.getSerializable(authController.PBS_LOGIN_JSON);
            if (loginJSON != null) {
                if (loginJSON.getSuccess().equals("TRUE")) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                } else {
                    PandoraHelper.showAlertMessage((PandoraMain) getActivity(),
                            "Invalid username or password", PandoraConstant.ERROR, "Retry", "Ok");
                }
            }
        }
    }

    @Override
    public boolean onBackKeyPressed() {
        return false;
    }
}
