package com.pbasolutions.android.fragment;

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

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AssetTA;

/**
 * Created by pbadell on 10/8/15.
 */
public class AssetFragment extends Fragment{
    public ViewPager viewPager;
    private int index;
    private PandoraMain context;
    private boolean isMovementFrag =false;

    private static final int ADD_MOVEMENT_ID = 1;

    public AssetFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (PandoraMain) getActivity();
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.asset, container, false);
        //tablayout settings
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.asset_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_assets)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_movement)));
        //set view pager
        viewPager = (ViewPager) rootView.findViewById(R.id.asset_pager);
        final AssetTA adapter = new AssetTA(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if (!PandoraHelper.isInternetOn(getActivity())) {
            PandoraHelper.showMessage((PandoraMain)getActivity(),"Please turn on internet" +
                    " connection to view latest assets and movements");
//            PandoraHelper.showAlertMessage((PandoraMain)getActivity(),"Please turn on internet" +
//                            " connection to view latest assets and movements",
//                    "Error", "Ok", null);
        }
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

        if(isMovementFrag){
                viewPager.setCurrentItem(1);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ADD_MOVEMENT_ID, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_MOVEMENT_ID: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_CREATE_MOVEMENT, false);
                return  true;
            }
            default:return false;
        }
    }

    public boolean isMovementFrag() {
        return isMovementFrag;
    }

    public void setIsMovementFrag(boolean isAddMovement) {
        this.isMovementFrag = isAddMovement;
    }
}
