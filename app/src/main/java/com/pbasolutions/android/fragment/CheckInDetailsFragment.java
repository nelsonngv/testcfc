package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.controller.PBSCheckpointController;
import com.pbasolutions.android.R;

import  com.pbasolutions.android.databinding.CheckpointDetailsBinding;
import com.pbasolutions.android.model.MCheckIn;


/**
 * Created by pbadell on 7/24/15.
 */
public class CheckInDetailsFragment extends PBSDetailsFragment {
    /**
     * Checkpoint controller.
     */
    private PBSCheckpointController checkpointController ;

    /**
     * Checkpoint details binding. **auto generated class based on the xml name.
     */
    private CheckpointDetailsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpointController = new PBSCheckpointController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkpoint_details, container, false);
        Bundle inputBundle = new Bundle();
        inputBundle.putString(checkpointController.ARG_CHECKIN_UUID, _UUID);
        Bundle resultBundle = new Bundle();
        resultBundle = checkpointController.triggerEvent(checkpointController.CHECKIN_DETAILS_EVENT, inputBundle, resultBundle, null);
        final MCheckIn checkpointdetails = (MCheckIn)resultBundle.getSerializable(checkpointController.ARG_CHECKPOINT_DETAILS);
        binding =  CheckpointDetailsBinding.bind(rootView);
        binding.setCheckpointdetails(checkpointdetails);
        return rootView;
    }

    @Override
    protected void setUI(View view) {

    }

    @Override
    protected void setUIListener() {

    }

    @Override
    protected void setValues() {

    }
}
