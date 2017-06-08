package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
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
    private TextView tvName, tvCheckInOut;

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
        tvCheckInOut = (TextView) rootView.findViewById(R.id.tvCheckInOut);

        tvName.setText(PBSAttendanceController.empName);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.get(ATrackCheckInOutFragment.ARG_ATTENDANCETYPE) == null) {
            String inOutType = bundle.get(ATrackCheckInOutFragment.ARG_ATTENDANCETYPE).toString();
            if (inOutType.equals(PBSAttendanceController.ATTENDANCE_TRACKING_TYPE_IN))
                tvCheckInOut.setText(getString(R.string.checked_in_at, PBSAttendanceController.projectLocationName));
            else tvCheckInOut.setText(getString(R.string.checked_out_at, PBSAttendanceController.projectLocationName));
        }
        redirect();

        return rootView;
    }

    protected void redirect() {
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (PBSAttendanceController.isKioskMode)
                    ((PandoraMain) getActivity()).displayView(PandoraMain.FRAGMENT_ATTENDANCE_TRACKING_INOUT, false);

            }
        }.start();
    }
}
