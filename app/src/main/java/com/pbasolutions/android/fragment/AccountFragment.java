package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.AccountTabAdapter;
import com.pbasolutions.android.controller.PBSAuthenticatorController;

/**
 * Created by pbadell on 8/6/15.
 */
public class AccountFragment extends Fragment {
    public  ViewPager viewPager;
    private int index;
    private PandoraMain context;

    public AccountFragment() {
    }


    public PBSAuthenticatorController getAuthenticatorController() {
        return authenticatorController;
    }

    public void setAuthenticatorController(PBSAuthenticatorController authenticatorController) {
        this.authenticatorController = authenticatorController;
    }

    private PBSAuthenticatorController authenticatorController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (PandoraMain) getActivity();
        authenticatorController = new PBSAuthenticatorController(getActivity());
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.account, container, false);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Connection"));
        Bundle inputBundle = new Bundle();
        inputBundle.putString(authenticatorController.ARG_AUTH_TYPE, PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
        if (context.globalVariable != null){
            if (!context.globalVariable.getAd_user_name().isEmpty()) {
                inputBundle.putString(authenticatorController.USER_NAME_ARG, context.globalVariable.getAd_user_name());
            }

        }
        // Bundle authenticateToken = authenticatorController.triggerEvent(PBSAuthenticatorController.AUTHENTICATE_TOKEN, inputBundle, new Bundle(), getActivity());
        boolean isAuthToken = false;
        if (context.globalVariable != null)
        isAuthToken = (!context.globalVariable.getAuth_token().isEmpty());
        //|| (authenticateToken !=null);

        if (!isAuthToken) {
            context.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            context.getSupportActionBar().hide();
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            context.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            context.getSupportActionBar().setDisplayShowHomeEnabled(true);
            context.getSupportActionBar().show();
            tabLayout.addTab(tabLayout.newTab().setText("Defaults"));
            tabLayout.addTab(tabLayout.newTab().setText("Preferences"));
        }

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        final AccountTabAdapter adapter;

        adapter = new AccountTabAdapter(getChildFragmentManager(), tabLayout.getTabCount());

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

        if(context.globalVariable != null){
            if (context.globalVariable.isAuth()){
                viewPager.setCurrentItem(1);
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sync;
        sync = menu.add(0, 200, 0, "Log Out");
        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        sync.setIcon(R.drawable.log_out_icon);
    }
}
