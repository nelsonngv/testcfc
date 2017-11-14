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
import com.pbasolutions.android.listener.PBABackKeyListener;

/**
 * Created by pbadell on 10/5/15.
 */
public class ATrackDoneFragment extends Fragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = ATrackDoneFragment.class.getSimpleName();
    private TextView tvText;

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
        tvText = (TextView) rootView.findViewById(R.id.tvText);
        tvText.setText(getString(R.string.attendance_done1, PBSAttendanceController.empName, PBSAttendanceController.projectLocationName));
        redirect();

        return rootView;
    }

    @Override
    public boolean onBackKeyPressed() {
        return false;
    }

    protected void redirect() {
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                ((PandoraMain)getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
//                fragmentManager.popBackStack();
                //go back 1st attendance tracking loc tag page else emp tag page
                if (!PBSAttendanceController.isKioskMode) {
                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_attendance_tracking));
                    fragmentManager.popBackStack();
//                    fragmentManager.popBackStack();
                }
            }
        }.start();
    }
}
