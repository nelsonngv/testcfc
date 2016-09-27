package com.pbasolutions.android.fragment;


import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.ApplicantRVA;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MApplicant;

/**
 * Created by pbadell on 10/5/15.
 */
public class ApplicantFragment extends Fragment {
    /**
     * Class tag name.
     */
    SwipeRefreshLayout mSwipeRefreshLayout;


    private static final String TAG = "ApplicantFragment";

    /**
     * Recruitment controller.
     */
    PBSRecruitController recCont;
    /**
     *
     */
    ObservableArrayList<MApplicant> applicantList;

    protected String applicantDetailTitle;
    /**
     * Constructor method.
     */
    public ApplicantFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recCont = new PBSRecruitController(getActivity());

        applicantDetailTitle = getString(R.string.title_applicantdetails);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.applicant, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.applicant_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        applicantList = getApplicantList();
        ApplicantRVA viewAdapter = new ApplicantRVA(getActivity(), applicantList, inflater);
        addRecyclerViewListener(recyclerView);
        recyclerView.setAdapter(viewAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items

                applicantList = getApplicantList();
                ApplicantRVA viewAdapter = new ApplicantRVA(getActivity(), applicantList, inflater);
                addRecyclerViewListener(recyclerView);
                recyclerView.setAdapter(viewAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;


    }



    /**
     * Add click listener on the recycler view. when ever user clicks the list item. it will navigate them to the item details.
     * @param rv
     */

    protected void addRecyclerViewListener(RecyclerView rv) {
        ObservableArrayList<IModel> modelList = (ObservableArrayList)applicantList;
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new FragmentListOnItemClickListener(modelList, new ApplicantDetailsFragment(),
                        getActivity(), applicantDetailTitle)));
    }

    /**
     * Get list of applicants based on project location selection.
     *
     * @return
     */
    private ObservableArrayList<MApplicant> getApplicantList() {
        PandoraContext globalVar = ((PandoraMain) getActivity()).globalVariable;
        if (globalVar != null) {
            String projectLocationUUID = globalVar.getC_projectlocation_uuid();
            if (projectLocationUUID != null) {
                Bundle input = new Bundle();
                input.putString(recCont.ARG_PROJECT_LOCATION_UUID, projectLocationUUID);
                Bundle result = recCont.triggerEvent(recCont.GET_APPLICANTS_EVENT, input, new Bundle(), null);
                return (ObservableArrayList<MApplicant>) result.getSerializable(recCont.APPLICANT_LIST);

            } else {
                PandoraHelper.showErrorMessage((PandoraMain) getActivity(), getString(R.string.text_projectloc_na));
            }
        }
        return null;
    }
}
