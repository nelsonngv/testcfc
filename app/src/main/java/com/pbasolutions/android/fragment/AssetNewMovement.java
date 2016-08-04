package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSController;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MMovementLine;
import com.pbasolutions.android.model.ModelConst;

import java.util.List;
import java.util.UUID;

/**
 * Created by pbadell on 10/8/15.
 */
public class AssetNewMovement extends AbstractMovementFragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "AssetNewMovement";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, REMOVE_LINE_ID, 3, getString(R.string.text_icon_remove));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.minus_white);

        add = menu.add(0, ADD_LINE_ID, 2, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);

        add = menu.add(0, MOVE_LINE_ID, 1, getString(R.string.text_icon_move));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.rightarrow);
    }

    @Override
    public void saveMovement() {
        Bundle input = new Bundle();
        ContentValues cv = new ContentValues();
        if (get_UUID() == null || get_UUID().isEmpty()) {
            set_UUID(UUID.randomUUID().toString());
        }
        cv.put(MMovement.M_MOVEMENT_UUID_COL, get_UUID());
        cv.put(MMovement.C_PROJECTLOCATION_UUID_COL, (String) projectLocation.getTag());
        cv.put(MMovement.C_PROJECTLOCATIONTO_UUID_COL, toProjectLocationItem.getPair().getKey());
        cv.put(MMovement.MOVEMENTDATE_COL, movementDate.getText().toString());

        input.putString(assetCont.ARG_AD_USER_ID, appContext.getAd_user_id());
        input.putParcelable(PBSController.ARG_CONTENTVALUES, cv);
        Bundle result = assetCont.triggerEvent(assetCont.SAVE_MOVEMENT_EVENT,
                input, new Bundle(), null);
        if (PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
            PandoraHelper.showErrorMessage(context,
                    result.getString(result.getString(PandoraConstant.TITLE)));
        }

    }

    @Override
    protected void move() {
        if (movement == null && !get_UUID().isEmpty()){
            populateMovement();
        }

        //validate movementlines at least on
        if (movement.getLines() == null){
            PandoraHelper.showWarningMessage(context, "Please add at least one line to execute move.");
            return;
        }
        Bundle input = new Bundle();
        input.putSerializable(PBSAssetController.ARG_MOVEMENT, movement);
        input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
        Bundle result = assetCont.triggerEvent(assetCont.MOVE_EVENT,
                input, new Bundle(), null);
        if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE)) &&
                result.getString(PandoraConstant.TITLE) != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack();
            fragmentManager.popBackStack();
            Fragment frag = new AssetFragment();
            ((AssetFragment)frag).setIsMovementFrag(true);
            frag.setRetainInstance(true);
            fragmentTransaction.replace(R.id.container_body, frag);
            fragmentTransaction.addToBackStack(frag.getClass().getName());
            fragmentTransaction.commit();
        } else {
            PandoraHelper.showErrorMessage(context,
                    result.getString(result.getString(PandoraConstant.TITLE)));
        }
    }

    @Override
    public void addLine() {
        Fragment fragment = new NewMovementLineFragment();
        if (get_UUID() != null) {
            ((NewMovementLineFragment) fragment).set_UUID(_UUID);
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment.setRetainInstance(true);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();
            ((PandoraMain) getActivity()).getSupportActionBar()
                    .setTitle(getString(R.string.title_newrequisitionline));
        }
    }

    @Override
    protected void populateLines() {
        if (_UUID != null && !_UUID.isEmpty()) {
            Bundle input = new Bundle();
            input.putString(assetCont.ARG_M_MOVEMENT_UUID, get_UUID());
            Bundle result = assetCont.triggerEvent(assetCont.GET_LINES_EVENT,
                    input, new Bundle(), null);
            lines = (ObservableArrayList<MMovementLine>)result.getSerializable(assetCont.ARG_LINES);
            if (lines != null && movement != null)
                movement.setLines(lines.toArray(new MMovementLine[lines.size()]));
        }
    }

    @Override
    protected void populateMovement(){
        if (_UUID != null && !_UUID.isEmpty()) {
            Bundle input = new Bundle();
            input.putString(assetCont.ARG_M_MOVEMENT_UUID, get_UUID());
            Bundle result = assetCont.callEventDirectly(assetCont.GET_MOVEMENT_EVENT,
                    input, new Bundle(), null);
            movement = (MMovement) result.getSerializable(assetCont.ARG_MOVEMENT);
        }
    }

    @Override
    protected void setValues() {
        String locName = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL,
                appContext.getC_projectlocation_uuid(),
                ModelConst.NAME_COL, getActivity().getContentResolver());
        projectLocation.setText(locName);
//        projectLocation.setText(appContext.getC_projectlocation_name());
        projectLocation.
                setTag(appContext.getC_projectlocation_uuid().toString());
        if (movement != null && ! movement.getMovementDate().isEmpty()) {
            movementDate.setText(movement.getMovementDate());
        } else {
            movementDate.setText(PandoraHelper.getTodayDate("dd-MM-yyyy"));
        }
    }

    @Override
    protected void removeLine() {
        Bundle input = new Bundle();
        input.putSerializable(assetCont.ARG_MOVEMENTLINES, viewAdapter.getLines());
        Bundle result = assetCont.triggerEvent(assetCont.REMOVE_LINES_EVENT,
                input, new Bundle(), null);
        if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected List<SpinnerPair> getProjectLocationsTo() {
        Bundle input = new Bundle();
        input.putString(assetCont.ARG_EXCLUDE_PROJECTLOCATION_UUID, appContext.getC_projectlocation_uuid());
        Bundle result = assetCont
                .triggerEvent(assetCont.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result
                .getParcelableArrayList(assetCont.ARG_PROJECTLOCATIONS);
    }

}
