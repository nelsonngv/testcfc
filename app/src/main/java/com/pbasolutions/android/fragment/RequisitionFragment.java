package com.pbasolutions.android.fragment;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.RequisitionRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pbadell on 10/12/15.
 */
public class RequisitionFragment extends Fragment{
    /**
     * Class tag name.
     */
    private static final String TAG = "RequisitionFragment";
    private static final int ADD_REQUISITION_ID = 1;

    PBSRequisitionController requisCont;

    private Spinner statusSpinner;
    private ArrayAdapter statusAdapter;
    private SpinnerOnItemSelected statusItem;
    private TextView sortDocDesc;
    private TextView sortDocAsc;
    private TextView sortDateDesc;
    private TextView sortDateAsc;
    private TextView sortStatusDesc;
    private TextView sortStatusAsc;

    protected String requisitionDetailTitle;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RequisitionRVA linesAdapter;

    Context context;

    private ObservableArrayList<MPurchaseRequest> requisitionList;

    public RequisitionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requisCont = new PBSRequisitionController(getActivity());
        context =  getActivity();
        statusItem = new SpinnerOnItemSelected(statusSpinner, new SpinnerPair());

        requisitionDetailTitle = getString(R.string.title_requisitiondetails);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.requisition_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.requisition_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setIsApprovedSpinner(rootView);
        setOnItemSelectedListener();

        requisitionList = getRequisitionList();
        RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(), requisitionList, inflater);
        recyclerView.setAdapter(viewAdapter);
        PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                new RequisitionDetailFragment(), requisitionDetailTitle);

        new AsyncTask<Object, Void, Void>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Void doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];

                if (PBSServerConst.cookieStore != null) {
                    refreshRequsitions();
                }

                requisitionList = getRequisitionList();

                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);

                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute(inflater, recyclerView);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Object, Void, Void>() {
                    protected LayoutInflater inflater;
                    protected RecyclerView recyclerView;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Object... params) {
                        inflater = (LayoutInflater) params[0];
                        recyclerView = (RecyclerView) params[1];

                        if (PBSServerConst.cookieStore != null) {
                            refreshRequsitions();
                        }

                        requisitionList = getRequisitionList();

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void avoid) {
                        super.onPostExecute(avoid);

                        RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                        recyclerView.setAdapter(viewAdapter);
                        PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                                new RequisitionDetailFragment(), requisitionDetailTitle);

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }.execute(inflater, recyclerView);
            }
        });

        sortDocDesc = (TextView) rootView.findViewById(R.id.DocSortDesc);
        sortDocAsc = (TextView) rootView.findViewById(R.id.DocSortAsc);
        sortDateDesc = (TextView) rootView.findViewById(R.id.DateSortDesc);
        sortDateAsc = (TextView) rootView.findViewById(R.id.DateSortAsc);
        sortStatusDesc = (TextView) rootView.findViewById(R.id.StatusSortDesc);
        sortStatusAsc = (TextView) rootView.findViewById(R.id.StatusSortAsc);
        sortDocDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortDocAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);
        sortDateDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortDateAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);
        sortStatusDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortStatusAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);

        sortDocDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {
                        return lhs.getDocumentNo().compareTo(rhs.getDocumentNo());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortDocDesc.setVisibility(View.GONE);
                sortDocAsc.setVisibility(View.VISIBLE);
            }
        });

        sortDocAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {
                        return rhs.getDocumentNo().compareTo(lhs.getDocumentNo());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortDocAsc.setVisibility(View.GONE);
                sortDocDesc.setVisibility(View.VISIBLE);
            }
        });

        sortDateDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for (int i = 0; i< requisitionList.size(); i++){
//                    requisitionList.get(i).getRequestDate();
//                    Log.i(TAG, "onTouch: " + requisitionList.get(i).getRequestDate());
//                }
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {

                        return lhs.getRequestDateFormat().compareTo(rhs.getRequestDateFormat());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortDateDesc.setVisibility(View.GONE);
                sortDateAsc.setVisibility(View.VISIBLE);
            }
        });

        sortDateAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {
                        return rhs.getRequestDateFormat().compareTo(lhs.getRequestDateFormat());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortDateDesc.setVisibility(View.VISIBLE);
                sortDateAsc.setVisibility(View.GONE);
            }
        });

        sortStatusDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {
                        return rhs.getStatus().compareTo(lhs.getStatus());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortStatusDesc.setVisibility(View.GONE);
                sortStatusAsc.setVisibility(View.VISIBLE);
            }
        });

        sortStatusAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(requisitionList, new Comparator<MPurchaseRequest>(){
                    @Override
                    public int compare(MPurchaseRequest lhs, MPurchaseRequest rhs) {
                        return lhs.getStatus().compareTo(rhs.getStatus());
                    }
                });
                RequisitionRVA viewAdapter = new RequisitionRVA(getActivity(),requisitionList, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, requisitionList, getActivity(),
                        new RequisitionDetailFragment(), requisitionDetailTitle);
                sortStatusDesc.setVisibility(View.VISIBLE);
                sortStatusAsc.setVisibility(View.GONE);
            }
        });



        return rootView;
    }

    private void setIsApprovedSpinner(View rootView) {
      statusSpinner = (Spinner) rootView.findViewById(R.id.filterByStatus);
      statusAdapter = PandoraHelper
              .addListToSpinner(getActivity(),
                      statusSpinner, PandoraHelper.getStatusList());
    }

    private void setOnItemSelectedListener() {
        statusItem.setFragment(this);
        statusItem.setFragmentManager(getFragmentManager());
       statusSpinner.setOnItemSelectedListener(statusItem);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem add;
        add = menu.add(0, ADD_REQUISITION_ID, 0, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_REQUISITION_ID: {
                ((PandoraMain)getActivity()).
                        displayView(PandoraMain.FRAGMENT_CREATE_REQUISITION, false);
                return true;
            }
            default:return false;
        }
    }

    public void refreshRequsitions(){
        PandoraContext globalVar = ((PandoraMain)context).getGlobalVariable();
        Bundle input = new Bundle();
        input.putString(PBSRequisitionController.ARG_USER_ID, globalVar.getAd_user_id());
        input.putString(PBSRequisitionController.ARG_PROJECT_LOCATION_ID, globalVar.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
        Bundle result = requisCont.triggerEvent(PBSRequisitionController.SYNC_REQUISITIONS_EVENT, input, new Bundle(), null);
    }

    public ObservableArrayList<MPurchaseRequest> getRequisitionList() {
        Bundle input = new Bundle();
        if (statusItem != null) {
              String orderBy = statusItem.getPair() == null ? null : statusItem.getPair().getValue();
              input.putString(PBSRequisitionController.ARG_ORDERBY, orderBy);
        }

        input.putString(PBSRequisitionController.ARG_PROJECT_LOCATION_UUID, ((PandoraMain)context).getGlobalVariable().getC_projectlocation_uuid());
        Bundle result = requisCont.triggerEvent(PBSRequisitionController.GET_REQUISITIONS_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MPurchaseRequest>)result.get(PBSRequisitionController.ARG_REQUISITION_LIST);
    }

}

