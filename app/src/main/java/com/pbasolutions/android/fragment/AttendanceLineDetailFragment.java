package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.databinding.RequisitionlineDetailsBinding;
import com.pbasolutions.android.model.MPurchaseRequestLine;

/**
 * Created by tinker on 4/22/16.
 */
public class AttendanceLineDetailFragment extends AbstractRequisitionLineFragment {


    /**
     * PBSRequisitionController.
     */
    private PBSRequisitionController reqCont;

    private boolean isRequested;

    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private RequisitionlineDetailsBinding binding;

    private MPurchaseRequestLine prl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqCont = new PBSRequisitionController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.requisitionline_details, container, false);
        binding = RequisitionlineDetailsBinding.bind(rootView);
        prl = getRequisitionLine();
        binding.setPrLine(prl);
        setUI(rootView);
        setUIListener();
        setValues();
        isRequested();
        return rootView;
    }

    private void isRequested() {
        Bundle input = new Bundle();
        input.putString(reqCont.ARG_PURCHASEREQUEST_UUID, prl.getM_PurchaseRequest_UUID());
        Bundle result = reqCont.triggerEvent(reqCont.CHECK_PR_IS_REQUESTED_EVENT,
                input, new Bundle(), null);
        isRequested = result.getBoolean(reqCont.ARG_IS_PR_REQUESTED);
        //disable save button:
        if (isRequested)
            add.setEnabled(false);
    }

    protected void setValues() {
        prodNameSpinner.setSelection(((SpinAdapter) prodNameAdapter)
                .getPosition(prl.getM_Product_UUID()));
    }

    private MPurchaseRequestLine getRequisitionLine() {
        Bundle input = new Bundle();
        input.putString(reqCont.ARG_PURCHASEREQUESTLINE_UUID, _UUID);
        Bundle result = reqCont.triggerEvent(reqCont.GET_REQUISITIONLINE_EVENT, input, new Bundle(), null);
        return (MPurchaseRequestLine) result.getSerializable(reqCont.ARG_PURCHASEREQUESTLINE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        MenuItem delete;
//        delete = menu.add(0, PandoraMain.DELETE_NOTE_ID, 1, "Delete Note");
//        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        delete.setIcon(R.drawable.x);
    }

    @Override
    public void savePRLine() {
        //check first whether the PR has been requested.

        Bundle input = new Bundle();
        //only allow update on not yet request PR.
        if (!isRequested) {
            MPurchaseRequestLine tempPRLine = new MPurchaseRequestLine();
            tempPRLine.setProductName(prodNameItem.getPair().getValue());
            tempPRLine.setQtyRequestedString(qtyRequested.getText().toString());
            tempPRLine.setM_Product_UUID(prodNameItem.getPair().getKey());
            prodNameItem.getPair().getKey();

            //insertion values.
            ContentValues cv = new ContentValues();

            cv.put(MPurchaseRequestLine.M_PRODUCT_UUID_COL, prodNameItem.getPair().getKey());
            cv.put(MPurchaseRequestLine.DATEREQUIRED_COL, requiredDate.getText().toString());
            cv.put(MPurchaseRequestLine.QTYREQUESTED_COL, Integer.parseInt(qtyRequested.getText().toString()));
            input.putString(reqCont.ARG_PURCHASEREQUESTLINE_UUID, prl.getM_PurchaseRequestLine_UUID());
            input.putParcelable(reqCont.ARG_CONTENTVALUES, cv);
            Bundle output = reqCont.triggerEvent(reqCont.UPDATE_PR_LINE_EVENT, input, new Bundle(), null);
            if (!PandoraConstant.ERROR.equalsIgnoreCase(output.getString(PandoraConstant.TITLE))) {

                Fragment fragment = new NewRequisitionFragment();
                ((NewRequisitionFragment) fragment).set_UUID(prl.getM_PurchaseRequest_UUID());
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragment.setRetainInstance(true);
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    fragmentTransaction.commit();
                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newrequisition));
                }
            } else {
                PandoraHelper.showMessage(context, output.getString(output.getString(PandoraConstant.TITLE)));
            }
        }
    }
}