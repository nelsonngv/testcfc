package com.pbasolutions.android.fragment;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.AttendanceTA;
import com.pbasolutions.android.controller.PBSAttendanceController;

/**
 * Created by tinker on 4/19/16.
 */
public class AttendanceFragment  extends Fragment {
    /**

     * Class tag name.
     */
    private static final String TAG = "AttendanceFragment";

    public ViewPager viewPager;
    private PBSAttendanceController attendanceCont;
    private String userName;
    private boolean isAddAttendance = false;

    public AttendanceFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userName = ((PandoraMain)getActivity()).globalVariable.getAd_user_name();
        attendanceCont = new PBSAttendanceController(getActivity());
        PandoraHelper.setAutoSync(getActivity(), userName, PBSAccountInfo.ACCOUNT_TYPE);
        PandoraContext context = ((PandoraMain) getActivity()).globalVariable;
        //TODO: check why the projectlocation uuid is not saved.
        if (context.getC_projectlocation_uuid() == null
                || context.getC_projectlocation_uuid().isEmpty()) {
            PandoraHelper.getProjLocAvailable(getActivity(), true);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.attendance, container, false);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.attendance_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_attendances)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_attendancesearch)));

        viewPager = (ViewPager) rootView.findViewById(R.id.attendance_pager);
        final AttendanceTA adapter = new AttendanceTA(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(isAddAttendance){
            viewPager.setCurrentItem(1);
        }
        return rootView;
    }
}

