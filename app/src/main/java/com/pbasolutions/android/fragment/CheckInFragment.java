package com.pbasolutions.android.fragment;

import android.accounts.Account;
import android.app.Activity;

import android.content.ContentResolver;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.CheckInRVA;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MCheckIn;
import com.pbasolutions.android.controller.PBSCheckpointController;
import com.pbasolutions.android.R;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;

/**
 * Created by pbadell on 7/21/15.
 */
public class CheckInFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "CheckpointsFragment";

    private static final int ACTION_SYNC = 100;
    private static final int ACTION_ADD = 101;

    RecyclerView recyclerView;
    public CheckInRVA adapter;
    PBSCheckpointController checkpointController;
    ObservableArrayList<MCheckIn> checkIns;
    public CheckInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpointController = new PBSCheckpointController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkpoints, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        checkIns = getCheckIns();
        ObservableArrayList<IModel> modelList = (ObservableArrayList)checkIns;
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        new FragmentListOnItemClickListener(modelList,
                                new CheckInDetailsFragment(), getActivity(), getString(R.string.title_guardtour_checkin))
                ));
        try {
            adapter = new CheckInRVA(getActivity(), checkIns);
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Get checkin rows data from database.
     * @return
     * @throws Exception
     */
    public ObservableArrayList<MCheckIn> getCheckIns() {
        Bundle resultBundle = new Bundle();
        resultBundle = checkpointController.triggerEvent(PBSCheckpointController.CHECKIN_ROWS_EVENT,null, resultBundle, null);
        ObservableArrayList<MCheckIn> checkInList = (ObservableArrayList<MCheckIn>)
                resultBundle.getSerializable(checkpointController.ROW_ITEMS);
        return checkInList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sync;
        sync = menu.add(0, ACTION_SYNC, 0, "Sync");
        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        sync.setIcon(R.drawable.refresh);

        MenuItem add;
        sync = menu.add(0, ACTION_ADD, 0, "Add");
        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        sync.setIcon(R.drawable.add);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ACTION_ADD:
                ((PandoraMain)getActivity()).displayView(PandoraMain.FRAGMENT_NEW_CHECK_IN, false);
                return true;
        }

        return false;
    }
            @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }
    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
    }
    /**
     *
     */
    public void invalidateView() {
        getView().post(new Runnable() {
            @Override
            public void run() {
                getView().invalidate();
            }
        });
    }
}
