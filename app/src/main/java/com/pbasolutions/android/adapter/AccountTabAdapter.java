package com.pbasolutions.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.fragment.LoginFragment;
import com.pbasolutions.android.fragment.PreferencesFragment;
import com.pbasolutions.android.fragment.RoleFragment;

/**
 * Adapter for tab.
 * Created by pbadell on 8/6/15.
 */

public class AccountTabAdapter extends FragmentStatePagerAdapter {
    /**
     * Number of tabs.
     */
    private int mNumOfTabs;
    /**
     * Login Fragment.
     */
    private LoginFragment fragmentLogin;
    /**
     * Role Fragment
     */
    private RoleFragment fragmentRole;
    /**
     * Preference Fragment.
     */
    private PreferencesFragment fragmentPreference;
    /**
     * Fragment Manager.
     */
    private FragmentManager fm;
    /**
     * Login index.
     */
    private static final int LOGIN_INDEX = 0;
    /**
     * Role index.
     */
    private static final int ROLE_INDEX = 1;
    /**
     * Pref index.
     */
    private static final int PREF_INDEX = 2;
    /**
     * Adapter for tab.
     * @param fm
     * @param mNumOfTabs
     */
    public AccountTabAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.fm = fm;
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case LOGIN_INDEX: {
                fragmentLogin = new LoginFragment();
                PandoraHelper.addToStack(fm);
                return fragmentLogin;
            }
            case ROLE_INDEX: {
                fragmentRole = new RoleFragment();
                PandoraHelper.addToStack(fm);
                return fragmentRole;
            }
            case PREF_INDEX: {
                fragmentPreference = new PreferencesFragment();
                PandoraHelper.addToStack(fm);
                return fragmentPreference;
            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}