package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MMovementLine;
import com.pbasolutions.android.model.MStorage;
import com.pbasolutions.android.model.MUOM;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by pbadell on 12/4/15.
 */
public class NewMovementLineFragment extends AbstractMovementLineFragment {

    ObservableArrayList<MStorage> storages;
   // private String movementUUID;
    @Override
    public void save() {
        //check the movement qty value first
        if (checkMovementQty()) {
            ContentValues cv = new ContentValues();

            cv.put(MMovementLine.M_MOVEMENTLINE_UUID_COL, UUID.randomUUID().toString());
            cv.put(MMovementLine.M_MOVEMENT_UUID_COL, (_UUID));
            MUOM muom = ((MUOM)uom.getTag());
            cv.put(MMovementLine.C_UOM_UUID_COL, muom.get_UUID());
            cv.put(MMovementLine.ASI_DESCRIPTION_COL, asiItem.getPair().getValue());
            cv.put(MMovementLine.M_ATTRIBUTESETINSTANCE_ID_COL, asiItem.getPair().getKey());
            cv.put(MMovementLine.M_PRODUCT_UUID_COL, prodNameItem.getPair().getKey());
            cv.put(MMovementLine.MOVEMENTQTY_COL, movementQty.getText().toString());

            Bundle input = new Bundle();
            input.putParcelable(assetCont.ARG_CONTENTVALUES, cv);
            input.putString(assetCont.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
            input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
            Bundle output = assetCont.triggerEvent(assetCont.SAVE_MOVEMENTLINE_EVENT, input, new Bundle(), null);
            if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                Fragment fragment = new AssetNewMovement();
                ((AssetNewMovement) fragment).set_UUID(get_UUID());
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragment.setRetainInstance(true);
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    fragmentTransaction.commit();
                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newmovementline));
                }
            } else  {
                PandoraHelper.showAlertMessage(context,
                        output.getString(output.getString(PandoraConstant.TITLE)),
                        output.getString(PandoraConstant.TITLE), "Ok", null);
            }
        }
    }

    @Override
    protected MMovementLine getMovementLine() {
        return null;
    }

    /**
     *
     * @return
     */
    private boolean checkMovementQty() {
        boolean result = false;
        String qtyOnHd = qtyOnHand.getText().toString();
        String movementQuantity = movementQty.getText().toString();
        if (!qtyOnHd.isEmpty() && !movementQuantity.isEmpty()) {
            BigDecimal quantityOnHand =  new BigDecimal(qtyOnHd);
            BigDecimal moveQty = new BigDecimal(movementQuantity);
            if (moveQty.compareTo(quantityOnHand) == 1) {
                PandoraHelper.showAlertMessage((PandoraMain)getActivity(),
                        getString(R.string.movement_greater_than), PandoraConstant.ERROR, PandoraConstant.OK_BUTTON, null);
            } else {
                result = true;
            }
        } else {
            PandoraHelper.showAlertMessage((PandoraMain) getActivity(),
                    getString(R.string.no_value_movement_qty), PandoraConstant.ERROR, PandoraConstant.OK_BUTTON, null);
        }
        return result;
    }

    @Override
    protected List<SpinnerPair> getAsi() {
        Bundle input = new Bundle();
        input.putString(assetCont.ARG_M_PRODUCT_UUID, ((String) (prodName.getTag())));
        input.putBoolean(assetCont.ARG_IS_GET_ASI, true);
        Bundle result = assetCont.triggerEvent(assetCont.GET_ASI_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(assetCont.ARG_ASI);
    }

    @Override
    protected List<SpinnerPair> getProdList(boolean isName) {
        Bundle input = new Bundle();
        input.putBoolean(assetCont.ARG_ISNAME, isName);
        Bundle result = assetCont.triggerEvent(assetCont.GET_ASSET_PRODUCT_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(assetCont.ARG_ASSET_PRODUCTS);
    }

    @Override
    public void setValues() {
    }

    public void updateValues(SpinnerPair pair, int id) {
        if (R.id.prodName == id) {
            //get values from uom table.
            Bundle input = new Bundle();
            input.putString(assetCont.ARG_M_PRODUCT_UUID, pair.getKey());
            Bundle result = assetCont.triggerEvent(assetCont.GET_UOM_EVENT, input, new Bundle(), null);
            MUOM mUOM = (MUOM)result.getSerializable(assetCont.ARG_UOM);
            uom.setText(mUOM.getName());
            uom.setTag(mUOM);

            input.putString(assetCont.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
            input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
            result = assetCont.triggerEvent(assetCont.GET_ASI_EVENT, input, new Bundle(), null);
            storages = (ObservableArrayList<MStorage>)result.getSerializable(PBSAssetController.ARG_STORAGE);
            List<SpinnerPair> asiPair = result.getParcelableArrayList(assetCont.ARG_ASI);
            asiAdapter.clear();
            //asiAdapter.addAll(asiPair);
            asiAdapter = PandoraHelper.addListToSpinner(getActivity(), asi, asiPair);
            asiAdapter.notifyDataSetChanged();
        } else if (R.id.asi == id){
            //get the QtyOnHand value
            Bundle input = new Bundle();
            input.putSerializable(PBSAssetController.ARG_STORAGE, storages);
            input.putString(PBSAssetController.ARG_M_ATTRIBUTESETINSTANCE_ID, pair.getKey());
            Bundle result = assetCont.triggerEvent(assetCont.GET_QTYONHAND_EVENT, input, new Bundle(), null);
            Number quantityOnHand = result.getDouble(assetCont.ARG_QTYONHAND);
            qtyOnHand.setText(String.valueOf(quantityOnHand));
        }
    }

    public ObservableArrayList<MStorage> getStorages() {
        return storages;
    }

    public void setStorages(ObservableArrayList<MStorage> storages) {
        this.storages = storages;
    }
}
