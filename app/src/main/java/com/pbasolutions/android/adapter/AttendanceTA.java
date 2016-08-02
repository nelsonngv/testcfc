package com.pbasolutions.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.app.FragmentTransaction;

import com.pbasolutions.android.fragment.AttendanceSearchFragment;
import com.pbasolutions.android.fragment.NewAttendanceFragment;

/**
 * Created by pbadell on 10/5/15.
 * Recruitment tab adapter.
 */
public class AttendanceTA extends FragmentStatePagerAdapter {
    /**
     * Number of tabs.
     */
    private int mNumOfTabs;
    /**
     * Login Fragment.
     */
    private NewAttendanceFragment newAttFrag;
    /**
     * Role Fragment
     */
    private AttendanceSearchFragment attFrag;
    /**
     * Fragment Manager.
     */
    private FragmentManager fm;
    /**
     * Employees tab index.
     */
    private static final int ATT_NEW_INDEX = 0;
    /**
     * Applicants tab index.
     */
    private static final int ATT_SRCH_INDEX = 1;
    /**
     * Adapter for tab.
     * @param fm
     * @param mNumOfTabs
     */
    public AttendanceTA(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.fm = fm;
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ATT_NEW_INDEX: {
                newAttFrag = new NewAttendanceFragment();
                addToStack();
                return newAttFrag;
            }
            case ATT_SRCH_INDEX: {
                attFrag = new AttendanceSearchFragment();
                addToStack();
                return attFrag;
            }
            default:
                return null;
        }
    }

    /**
     * Add the fragment to undo stack.
     */
    private void addToStack() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(fm.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
