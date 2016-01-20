package com.pbasolutions.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.app.FragmentTransaction;

import com.pbasolutions.android.fragment.ApplicantFragment;
import com.pbasolutions.android.fragment.EmployeeFragment;

/**
 * Created by pbadell on 10/5/15.
 * Recruitment tab adapter.
 */
public class RecruitmentTA extends FragmentStatePagerAdapter {
    /**
     * Number of tabs.
     */
    private int mNumOfTabs;
    /**
     * Login Fragment.
     */
    private EmployeeFragment empFrag;
    /**
     * Role Fragment
     */
    private ApplicantFragment appFrag;
    /**
     * Fragment Manager.
     */
    private FragmentManager fm;
    /**
     * Employees tab index.
     */
    private static final int EMP_INDEX = 0;
    /**
     * Applicants tab index.
     */
    private static final int APP_INDEX = 1;
    /**
     * Adapter for tab.
     * @param fm
     * @param mNumOfTabs
     */
    public RecruitmentTA(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.fm = fm;
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case EMP_INDEX: {
                empFrag = new EmployeeFragment();
                addToStack();
                return empFrag;
            }
            case APP_INDEX: {
                appFrag = new ApplicantFragment();
                addToStack();
                return appFrag;
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
