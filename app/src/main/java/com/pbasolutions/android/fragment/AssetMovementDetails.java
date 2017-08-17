package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MMovementLine;

import java.util.Collections;
import java.util.List;

/**
 * Created by pbadell on 10/8/15.
 */
public class AssetMovementDetails extends AbstractMovementFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (movement != null){
            String currentProjLocId = ((PandoraMain) getActivity())
                    .getGlobalVariable().getC_projectlocation_id();
            int nProjLocId = Integer.parseInt(currentProjLocId);
            if (!"DR".equalsIgnoreCase(movement.getDocStatus())
                    && !"RE".equalsIgnoreCase(movement.getDocStatus())
                    ) {

                //  move.setEnabled(false);
            } else if (movement.getC_ProjectLocation_ID() != null
                    && movement.getC_ProjectLocation_ID().intValue() == nProjLocId) {
                //  move.setEnabled(false);
            } else {
                Toast.makeText(PandoraMain.instance, "Please complete the movement.",
                        Toast.LENGTH_SHORT).show();
                MenuItem item;
                item = menu.add(0, COMPLETE_MOVE_ID, 1, getString(R.string.text_icon_complete));
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                item.setIcon(R.drawable.ic_done);
            }
        }


    }

    /**
     * On details user can only complete movement. not delete.
     */
    @Override
    protected void move() {
        Bundle input = new Bundle();
        input.putInt(PBSAssetController.ARG_M_MOVEMENT_ID, get_ID());
        input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
        input.putString(PBSAssetController.ARG_M_MOVEMENT_UUID, movement.getM_Movement_UUID());

        //complete movement
        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle output = new Bundle();
                output = assetCont.triggerEvent(PBSAssetController.COMPLETE_MOVEMENT_EVENT, params[0], output, null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if(PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))){
                    PandoraHelper.showErrorMessage(context,
                            result.getString(result.getString(PandoraConstant.TITLE)));
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    /**
     *
     */
    @Override
    protected void saveMovement() {

    }

    @Override
    protected void addLine() {
     //on details not able to add lines.
    }

    @Override
    protected void removeLine() {

    }

    @Override
    protected void populateLines() {
    }

    @Override
    protected void populateMovement() {
        ObservableArrayList<MMovement> movements = (ObservableArrayList)getModelList();
        for (MMovement mmovement : movements) {
            if (_ID == mmovement.getM_Movement_ID().intValue()) {
                movement = mmovement;
            }
        }
    }

    @Override
    protected void setValues() {
        Bundle input = new Bundle();
        input.putSerializable(PBSAssetController.ARG_MOVEMENT, movement);
        Bundle result = assetCont.triggerEvent(PBSAssetController.UPDATE_MOVEMENT_INFO_EVENT, input,
                new Bundle(), null);

        if (result != null) {
            movement = (MMovement) result.getSerializable(PBSAssetController.ARG_MOVEMENT);
            getActivity().invalidateOptionsMenu();
            projectLocation.setText(movement.getProjectLocation());
            projectLocation.setTag(movement.getProjectLocation_UUID());
            toProjectLocation.setSelection(((SpinAdapter) toProjectLocationAdapter)
                    .getPosition(movement.getProjectLocationTo_UUID()));
            movementDate.setText(movement.getMovementDate());
            documentNo.setText(movement.getDocumentNo());
            ObservableArrayList<MMovementLine> movementLines = new ObservableArrayList();
            Collections.addAll(movementLines, movement.getLines());
            lines = movementLines;
        }
        toProjectLocation.setEnabled(false);
        movementDate.setEnabled(false);
    }

    protected List<SpinnerPair> getProjectLocationsTo() {
        Bundle input = new Bundle();
        Bundle result = assetCont
                .triggerEvent(PBSAssetController.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result
                .getParcelableArrayList(PBSAssetController.ARG_PROJECTLOCATIONS);
    }
}
