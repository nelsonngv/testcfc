package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.model.MPurchaseRequestLine;

import java.util.UUID;

/**
 * Created by pbadell on 11/27/15.
 */
public class NewRequisitionLineFragment extends AbstractRequisitionLineFragment {

    /**
     * Class tag name.
     */
    private static final String TAG = "NewRLFragment";

    MPurchaseRequestLine tempPRLine;
    String prUUID;
    Fragment requisitionFragment;

    Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.requisitionline_new, container, false);
        try {
            setUI(rootView);
            setUIListener();
        } catch (Exception e) {
          //  Log.e(TAG, e.getMessage());
        }
        return rootView;
    }
    @Override
    protected void setUI(View rootView) {
        super.setUI(rootView);
    }

    @Override
    protected void setValues() {

    }

    public Fragment getRequisitionFragment() {
        return requisitionFragment;
    }

    public void setRequisitionFragment(Fragment requisitionFragment) {
        this.requisitionFragment = requisitionFragment;
    }

    @Override
    public void savePRLine(){
        //check all value is not null.
        if (prodName == null || prodName.isEmpty()
                || qtyRequested.getText().toString().isEmpty()
                || requiredDate.getText().toString().isEmpty())
        {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), "Please fill up all fields");
            return;
        }

        int nQty = -1;
        try {
            nQty = Integer.parseInt(qtyRequested.getText().toString());
        } catch (Exception e) {
        }

        if (nQty < 0) {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), "Please fill up valid Qty.");
            return;
        }
        tempPRLine = new MPurchaseRequestLine();
        tempPRLine.setProductName(prodName);
        //TODO: make choice available. tempPRLine.setProductValue();
        tempPRLine.setQtyRequestedString(qtyRequested.getText().toString());
        tempPRLine.setM_Product_UUID(prodUUID);
        tempPRLine.setM_PurchaseRequestLine_UUID(UUID.randomUUID().toString());
        tempPRLine.setM_PurchaseRequest_UUID(prUUID);

        //insertion values.
        ContentValues cv = new ContentValues();

        cv.put(MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL, UUID.randomUUID().toString());
        cv.put(MPurchaseRequestLine.M_PRODUCT_UUID_COL, prodUUID);
        cv.put(MPurchaseRequestLine.DATEREQUIRED_COL, requiredDate.getText().toString());
        cv.put(MPurchaseRequestLine.M_PURCHASEREQUEST_UUID_COL, getPrUUID());
        cv.put(MPurchaseRequestLine.QTYREQUESTED_COL, nQty);

        Bundle input = new Bundle();
        input.putParcelable(reqCont.ARG_CONTENTVALUES, cv);
        Bundle output = reqCont.triggerEvent(reqCont.INSERT_REQLINE_EVENT, input, new Bundle(), null);
        if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {

            PandoraHelper.hideSoftKeyboard();
            PandoraMain.instance.getSupportFragmentManager().popBackStack();
//            Fragment fragment = new NewRequisitionFragment();
         //   if (get_UUID() != null){
//                ((NewRequisitionFragment) fragment).set_UUID(getPrUUID());
//                if (fragment != null) {
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragment.setRetainInstance(true);
//                    fragmentTransaction.replace(R.id.container_body, fragment);
//                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                    fragmentTransaction.commit();
//                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newrequisitionline));
//                }
           // }

        } else {
            PandoraHelper.showMessage(context, output.getString(output.getString(PandoraConstant.TITLE)));
        }
    }

    public String getPrUUID() {
        return prUUID;
    }

    public void setPrUUID(String prUUID) {
        this.prUUID = prUUID;
    }
}
