package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSAttendanceController;

/**
 * Created by pbadell on 10/5/15.
 */
public class ATrackDoneFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackDoneFragment.class.getSimpleName();
    private TextView tvName, tvLocation;

    /**
     * Constructor method.
     */
    public ATrackDoneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.atrack_done, container, false);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);

        tvName.setText(PBSAttendanceController.empName);
        tvLocation.setText(PBSAttendanceController.projectLocationName);
        redirect();

        return rootView;
    }

    protected void redirect() {
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                PandoraMain.instance.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();
                //go back 1st attendance tracking loc tag page else emp tag page
                if (!PBSAttendanceController.isKioskMode) {
                    fragmentManager.popBackStack();
                    fragmentManager.popBackStack();
                }
            }
        }.start();
    }
}
