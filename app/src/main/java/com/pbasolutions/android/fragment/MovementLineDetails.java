package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MMovementLine;
import com.pbasolutions.android.model.MUOM;

import java.util.List;

/**
 * Created by pbadell on 12/9/15.
 */
public class MovementLineDetails extends AbstractMovementLineFragment {
    @Override
    protected MMovementLine getMovementLine() {
        Bundle input = new Bundle();
        input.putInt(assetCont.ARG_M_MOVEMENTLINE_ID, get_ID());
        input.putSerializable(assetCont.ARG_MOVEMENTLINES, getModelList());
        Bundle result = assetCont.triggerEvent(assetCont.GET_MOVEMENTLINE_EVENT, input,
                new Bundle(), null);
        return (MMovementLine)result.getSerializable(assetCont.ARG_MOVEMENTLINE);
    }


    @Override
    protected List<SpinnerPair> getAsi() {
        Bundle input = new Bundle();
        input.putString(assetCont.ARG_M_PRODUCT_UUID, ((line.getM_Product_UUID())));
        input.putString(assetCont.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
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
    public void setValues(){
        //populate uuids value
        if (prodNameAdapter.getCount()>0){
            prodName.setSelection(((SpinAdapter) prodNameAdapter).getPosition(line.getM_Product_UUID()));
            prodValue.setSelection(((SpinAdapter) prodValueAdapter).getPosition(line.getM_Product_UUID()));
            asi.setSelection(((SpinAdapter) asiAdapter)
                    .getPosition(String.valueOf(line.getM_AttributeSetInstance_ID())));
        } else {
            PandoraHelper.showErrorMessage((PandoraMain)getActivity(), "You have not full sync Asset" +
                    " table, product name,value and asi wont be able to display, please sync.");
        }
        uom.setText(line.getUOMName());
        movementQty.setText(String.valueOf(line.getMovementQty()));

        int lineId = 0;
        if (line != null && line.getM_MovementLine_ID() != null)
            lineId = line.getM_MovementLine_ID().intValue();

        setHasOptionsMenu(lineId == 0);

        getActivity().invalidateOptionsMenu();
        qtyOnHand.setText(String.valueOf(line.getQtyOnHand()));
    }

    @Override
    public void save() {
        if (line == null) return;

        ContentValues cv = new ContentValues();

        cv.put(MMovementLine.M_MOVEMENTLINE_UUID_COL, line.getM_MovementLine_UUID());

        MUOM muom = ((MUOM)uom.getTag());
        cv.put(MMovementLine.C_UOM_UUID_COL, muom.get_UUID());
        cv.put(MMovementLine.ASI_DESCRIPTION_COL, asiItem.getPair().getValue());
        cv.put(MMovementLine.M_ATTRIBUTESETINSTANCE_ID_COL, asiItem.getPair().getKey());
        cv.put(MMovementLine.M_PRODUCT_UUID_COL, prodNameItem.getPair().getKey());
        cv.put(MMovementLine.MOVEMENTQTY_COL, movementQty.getText().toString());

        Bundle input = new Bundle();
        input.putParcelable(PBSAssetController.ARG_CONTENTVALUES, cv);
        Bundle output = assetCont.triggerEvent(assetCont.UPDATE_MOVEMENTLINE_EVENT, input, new Bundle(), null);
        if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        } else  {
            PandoraHelper.showMessage(context,
                    output.getString(output.getString(PandoraConstant.TITLE)));
        }
    }
}
