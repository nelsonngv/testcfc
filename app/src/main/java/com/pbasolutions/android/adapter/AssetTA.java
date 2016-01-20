package com.pbasolutions.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.pbasolutions.android.fragment.AssetListFragment;
import com.pbasolutions.android.fragment.MovementListFragment;

/**
 * Asset tab adapter.
 * Created by pbadell on 10/12/15.
 */
public class AssetTA extends FragmentStatePagerAdapter {
    /**
     * Fragment manager.
     */
    private final FragmentManager fm;
    /**
     * Number of tabs.
     */
    private int mNumOfTabs;
    /**
     * AssetListFragment.
     */
    private AssetListFragment assListFrag;
    /**
     * AssetMovement
     */
    private MovementListFragment moveFrag;
    /**
     * Assets tab index.
     */
    private static final int ASS_INDEX = 0;
    /**
     * Movements tab index.
     */
    private static final int MOV_INDEX = 1;
    /**
     * Adapter for tab.
     * @param fm
     * @param mNumOfTabs
     */
    public AssetTA(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.fm = fm;
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ASS_INDEX: {
                assListFrag = new AssetListFragment();
                addToStack();
                return assListFrag;
            }
            case MOV_INDEX: {
                moveFrag = new MovementListFragment();
                addToStack();
                return moveFrag;
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
