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
import com.pbasolutions.android.adapter.RecruitmentTA;
import com.pbasolutions.android.controller.PBSAuthenticatorController;

/**
 * Created by pbadell on 10/5/15.
 */
public class RecruitFragment extends Fragment {
    public ViewPager viewPager;
    public static final int ADD_APPLICANT_ID = 300;

    private Object syncHandle;
    private SyncStatusObserver observer;
    private PBSAuthenticatorController authCont;
    private String userName;
    private boolean isAddApplicant=false;

    public RecruitFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userName = ((PandoraMain)getActivity()).getGlobalVariable().getAd_user_name();
        authCont = new PBSAuthenticatorController(getActivity());
        PandoraHelper.setAutoSync(getActivity(), userName, PBSAccountInfo.ACCOUNT_TYPE);
        PandoraContext context = ((PandoraMain) getActivity()).getGlobalVariable();
        if (context.getC_projectlocation_uuid() == null
                || context.getC_projectlocation_uuid().isEmpty()) {
            PandoraHelper.getProjLocAvailable(getActivity(), true);
        }
//        syncListener();
    }

    private void syncListener() {
        observer = new SyncStatusObserver()
        {
            @Override
            public void onStatusChanged(int which)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle input = new Bundle();
                        input.putString(authCont.USER_NAME_ARG,
                                ((PandoraMain)getActivity()).getGlobalVariable().getAd_user_name());
                        input.putString(authCont.ARG_ACCOUNT_TYPE,
                                PBSAccountInfo.ACCOUNT_TYPE);
                        Bundle accountBundle = authCont.triggerEvent(authCont.GET_USER_ACCOUNT_EVENT, input, new Bundle(), null);
                        Account acc;
                        if (accountBundle != null) {
                            acc = accountBundle.getParcelable(authCont.USER_ACC_ARG);
                            boolean isSynchronizing =
                                    PandoraHelper.isSyncActive(acc, PBSAccountInfo.ACCOUNT_AUTHORITY);
                            if (isSynchronizing)
                            Toast.makeText(PandoraMain.instance, "Syncing data",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.recruit, container, false);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.recruit_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_employees)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_applicants)));

        viewPager = (ViewPager) rootView.findViewById(R.id.recruit_pager);
        final RecruitmentTA adapter = new RecruitmentTA(getChildFragmentManager(), tabLayout.getTabCount());
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

        if(isAddApplicant){
                viewPager.setCurrentItem(1);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem add;
        add = menu.add(0, ADD_APPLICANT_ID, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_APPLICANT_ID: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_ADD_APPLICANT, false);
                return  true;
            }
            default:return false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Refresh synchronization status
//        observer.onStatusChanged(0);

        // Watch for synchronization status changes
//        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
//                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
//        syncHandle = ContentResolver.addStatusChangeListener(mask, observer);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Remove our synchronization listener if registered
        if (syncHandle != null)
        {
            ContentResolver.removeStatusChangeListener(syncHandle);
            syncHandle = null;
        }
    }

    public boolean isAddApplicant() {
        return isAddApplicant;
    }

    public void setIsAddApplicant(boolean isAddApplicant) {
        this.isAddApplicant = isAddApplicant;
    }
}
