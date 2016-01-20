//package com.pbasolutions.android.fragment;
//
//import android.databinding.ObservableArrayList;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.pbasolutions.android.PandoraHelper;
//import com.pbasolutions.android.R;
//import com.pbasolutions.android.adapter.RequisitionLineRVA;
//import com.pbasolutions.android.controller.PBSRequisitionController;
//import com.pbasolutions.android.databinding.RequisitionDetailsBinding;
//import com.pbasolutions.android.model.MPurchaseRequest;
//import com.pbasolutions.android.model.MPurchaseRequestLine;
//
///**
// * Created by pbadell on 11/26/15.
// */
//public class AbstractRequisitionFragment extends Fragment{
//    /**
//     * Class tag name.
//     */
//    private static final String TAG = "AbstractRequisitionFragment";
//    /**
//     * PBSRequisitionController.
//     */
//    private PBSRequisitionController reqCont ;
//    /**
//     * Message details binding. **auto generated class based on the xml name.
//     */
//    private RequisitionDetailsBinding binding;
//    private ObservableArrayList<MPurchaseRequestLine> purchaseRequestLineList;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        reqCont = new PBSRequisitionController(getActivity());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.requisition_details, container, false);
//        binding =  RequisitionDetailsBinding.bind(rootView);
//        binding.setPr(getRequisition());
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.requisitionline_rv);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        purchaseRequestLineList = getRequisitionLines();
//        RequisitionLineRVA viewAdapter = new RequisitionLineRVA(getActivity(),purchaseRequestLineList, inflater);
//        PandoraHelper.addRecyclerViewListener(recyclerView, purchaseRequestLineList, getActivity(),
//                new RequisitionLineDetailsFragment(), getString(R.string.title_requisitiondetails));
//        recyclerView.setAdapter(viewAdapter);
//        return rootView;
//    }
//
//    private ObservableArrayList<MPurchaseRequestLine> getRequisitionLines() {
//        Bundle input = new Bundle();
//        input.putString(reqCont.ARG_PURCHASEREQUEST_UUID, _UUID);
//        Bundle result = reqCont.triggerEvent(reqCont.GET_REQUISITIONLINES_EVENT, input, new Bundle(), null);
//        return (ObservableArrayList<MPurchaseRequestLine>)result.getSerializable(reqCont.ARG_PURCHASEREQUESTLINE_LIST);
//    }
//
//    private MPurchaseRequest getRequisition() {
//        Bundle input = new Bundle();
//        input.putString(reqCont.ARG_PURCHASEREQUEST_UUID, _UUID);
//        Bundle result = reqCont.triggerEvent(reqCont.GET_REQUISITION_EVENT, input, new Bundle(), null);
//        return (MPurchaseRequest)result.getSerializable(reqCont.ARG_PURCHASEREQUEST);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuItem delete;
//        //        delete = menu.add(0, PandoraMain.DELETE_NOTE_ID, 1, "Delete Note");
//        //        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        //        delete.setIcon(R.drawable.x);
//    }
//
//}
