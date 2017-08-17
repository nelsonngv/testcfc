package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSDeployController;
import com.pbasolutions.android.databinding.DeploymentDetailsBinding;
import com.pbasolutions.android.model.MResourceAlloc;

/**
 * Created by pbadell on 10/8/15.
 */
public class DeploymentDetailsFragment extends Fragment {
    /**
     * PBSDeployController controller.
     */
    private PBSDeployController deployCont ;
    /**
     * Resource Allocation uuid value.
     */
    private String resourcealloc_uuid;
    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private DeploymentDetailsBinding binding;

    public static final int SAVE_DEPLOY_ID = 700;

    /**
     * Set selected note.
     * @param resourcealloc_uuid
     */
    public void setResourceAlloc_uuid(String resourcealloc_uuid) {
        this.resourcealloc_uuid = resourcealloc_uuid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deployCont = new PBSDeployController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.deployment_details, container, false);
        binding =  DeploymentDetailsBinding.bind(rootView);
        binding.setDeploy(getDeploy());
        return rootView;
    }

    /**
     *
     * @return
     */
    public MResourceAlloc getDeploy() {
        Bundle inputBundle = new Bundle();
        inputBundle.putString(PBSDeployController.ARG_RESOURCEALLOC_UUID, resourcealloc_uuid);
        Bundle resultBundle = new Bundle();
        resultBundle = deployCont.triggerEvent(PBSDeployController.NOTE_DETAILS_EVENT, inputBundle, resultBundle, null);
        return (MResourceAlloc)resultBundle.getSerializable(PBSDeployController.ARG_NOTE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem delete;
        delete = menu.add(0, PandoraMain.SAVE_DEPLOY_ID, 1, "Save Deploy");
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        delete.setIcon(R.drawable.x);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SAVE_DEPLOY_ID: {
                return saveDeploys();
            }
            default : return false;
        }
    }


    private boolean saveDeploys(){
        return true;
    }
}
