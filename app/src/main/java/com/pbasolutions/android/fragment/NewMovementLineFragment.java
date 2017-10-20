package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSController;
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
            input.putParcelable(PBSController.ARG_CONTENTVALUES, cv);
            input.putString(PBSAssetController.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
            input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
            Bundle output = assetCont.triggerEvent(PBSAssetController.SAVE_MOVEMENTLINE_EVENT, input, new Bundle(), null);
            if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {
                PandoraHelper.hideSoftKeyboard(getActivity());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
//                Fragment fragment = new AssetNewMovement();
//                ((AssetNewMovement) fragment).set_UUID(get_UUID());
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragment.setRetainInstance(true);
//                    fragmentTransaction.replace(R.id.container_body, fragment);
//                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                    fragmentTransaction.commit();
//                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newmovementline));
//                }
            } else  {
                PandoraHelper.showMessage(context,
                        output.getString(output.getString(PandoraConstant.TITLE)));
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
        if (prodNameItem.getPair().getKey() != null) {
            if (!qtyOnHd.isEmpty() && !movementQuantity.isEmpty()) {
                BigDecimal quantityOnHand = new BigDecimal(qtyOnHd);
                BigDecimal moveQty = new BigDecimal(movementQuantity);
                if (moveQty.compareTo(quantityOnHand) == 1) {
                    PandoraHelper.showWarningMessage(getActivity(),
                            getString(R.string.movement_greater_than));
                } else {
                    result = true;
                }
            } else {
                PandoraHelper.showWarningMessage(getActivity(),
                        getString(R.string.no_value_movement_qty));
            }
        } else {
            PandoraHelper.showWarningMessage(getActivity(), getString(
                    R.string.no_list_error, "Product"));
        }
        return result;
    }

    @Override
    protected List<SpinnerPair> getAsi() {
        Bundle input = new Bundle();
        input.putString(PBSAssetController.ARG_M_PRODUCT_UUID, ((String) (prodName.getTag())));
        input.putBoolean(PBSAssetController.ARG_IS_GET_ASI, true);
        input.putString(PBSAssetController.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());
        Bundle result = assetCont.triggerEvent(PBSAssetController.GET_ASI_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(PBSAssetController.ARG_ASI);
    }

    @Override
    protected List<SpinnerPair> getProdList(boolean isName) {
        Bundle input = new Bundle();
        input.putBoolean(PBSAssetController.ARG_ISNAME, isName);
        Bundle result = assetCont.triggerEvent(PBSAssetController.GET_ASSET_PRODUCT_EVENT,
                input, new Bundle(), null);
        return result.getParcelableArrayList(PBSAssetController.ARG_ASSET_PRODUCTS);
    }

    @Override
    public void setValues() {
    }

    public void updateValues(SpinnerPair pair, int id) {
        if (R.id.prodName == id) {
            //get values from uom table.
            Bundle input = new Bundle();
            input.putString(PBSAssetController.ARG_M_PRODUCT_UUID, pair.getKey());
            Bundle result = assetCont.triggerEvent(PBSAssetController.GET_UOM_EVENT, input, new Bundle(), null);
            MUOM mUOM = (MUOM)result.getSerializable(PBSAssetController.ARG_UOM);
            uom.setText(mUOM.getName());
            uom.setTag(mUOM);

            input.putString(PBSAssetController.ARG_PROJECTLOCATION_ID, appContext.getC_projectlocation_id());
            input.putString(PBSServerConst.PARAM_URL, appContext.getServer_url());

            new AsyncTask<Bundle, Void, Bundle>() {
                protected void onPreExecute() {
                    super.onPreExecute();
                    ((PandoraMain)getActivity()).showProgressDialog("Loading...");
                }

                @Override
                protected Bundle doInBackground(Bundle... params) {
                    return assetCont.triggerEvent(assetCont.GET_ASI_EVENT, params[0], new Bundle(), null);
                }

                @Override
                protected void onPostExecute(Bundle result) {
                    super.onPostExecute(result);
                    storages = (ObservableArrayList<MStorage>)result.getSerializable(PBSAssetController.ARG_STORAGE);
                    List<SpinnerPair> asiPair = result.getParcelableArrayList(PBSAssetController.ARG_ASI);
                    asiAdapter.clear();
                    //asiAdapter.addAll(asiPair);
                    asiAdapter = PandoraHelper.addListToSpinner(getActivity(), asi, asiPair);
                    asiAdapter.notifyDataSetChanged();

                    ((PandoraMain)getActivity()).dismissProgressDialog();
                }
            }.execute(input);
        } else if (R.id.asi == id && storages != null){

            //get the QtyOnHand value
            Bundle input = new Bundle();
            input.putSerializable(PBSAssetController.ARG_STORAGE, storages);
            input.putString(PBSAssetController.ARG_M_ATTRIBUTESETINSTANCE_ID, pair.getKey());
            new AsyncTask<Bundle, Void, Bundle>() {
                protected void onPreExecute() {
                    super.onPreExecute();
                    ((PandoraMain)getActivity()).showProgressDialog("Loading...");
                }

                @Override
                protected Bundle doInBackground(Bundle... params) {
                    return assetCont.triggerEvent(assetCont.GET_QTYONHAND_EVENT, params[0], new Bundle(), null);
                }

                @Override
                protected void onPostExecute(Bundle result) {
                    super.onPostExecute(result);
                    Number quantityOnHand = result.getDouble(PBSAssetController.ARG_QTYONHAND);
                    qtyOnHand.setText(String.valueOf(quantityOnHand));

                    ((PandoraMain)getActivity()).dismissProgressDialog();
                }
            }.execute(input);
        }
    }

    public ObservableArrayList<MStorage> getStorages() {
        return storages;
    }

    public void setStorages(ObservableArrayList<MStorage> storages) {
        this.storages = storages;
    }
}
