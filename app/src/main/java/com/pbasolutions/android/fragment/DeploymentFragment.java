package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.DeployRVA;
import com.pbasolutions.android.controller.PBSDeployController;
import com.pbasolutions.android.model.MDeploy;

/**
 * Created by pbadell on 10/8/15.
 */
public class DeploymentFragment extends Fragment {

    /**
     * Class tag name.
     */
    private static final String TAG = "DeploymentFragment";

    PBSDeployController deployCont;
        private ObservableArrayList<MDeploy> deployList;

    public DeploymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deployCont = new PBSDeployController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.deployment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.deployment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deployList  = getDeploys();
        DeployRVA viewAdapter = new DeployRVA(getActivity(), deployList, inflater);
        recyclerView.setAdapter(viewAdapter);
        PandoraHelper.addRecyclerViewListener(recyclerView, deployList, getActivity(),
                new RequisitionDetailFragment(), getString(R.string.title_deployment_details));
        return rootView;
    }

    private ObservableArrayList<MDeploy> getDeploys() {
        Bundle input = new Bundle();
       // input.putString();
        Bundle result = deployCont.triggerEvent(PBSDeployController.GET_DEPLOYS_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MDeploy>)result.get(PBSDeployController.ARG_DEPLOYS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem sync;
        sync = menu.add(0, PandoraMain.SYNC_DEPLOY_ID, 0, "Sync Deploy");
        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        sync.setIcon(R.drawable.refresh);
    }
}
