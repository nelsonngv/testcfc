package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
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

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SurveyRVA;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MSurvey;

public class AuditFragment extends Fragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = "AuditFragment";
    private static final int ADD_ID = 2;
    /**
     * Task controller.
     */
    PBSSurveyController auditCont;
    /**
     * Project Task list.
     */
    private ObservableArrayList<MSurvey> auditList;
    /**
     * Global Variable.
     */
    private PandoraContext globalVar;

    protected String auditDetailTitle;
    private SurveyRVA viewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * Contructor.
     */
    public AuditFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auditCont = new PBSSurveyController(getActivity());
        globalVar = ((PandoraMain) getActivity()).getGlobalVariable();
        setHasOptionsMenu(true);
        auditDetailTitle = getString(R.string.title_auditdetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.survey_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshAudit();

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshAudit();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem add;
        add = menu.add(0, ADD_ID, 1, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_ID: {
                add();
                return true;
            }
            default:return false;
        }
    }

    private void add() {
        Fragment fragment = new NewAuditStartFragment();
            if (fragment != null) {
                ((PandoraMain) getActivity()).
                        displayView(PandoraMain.FRAGMENT_START_AUDIT, false);
            }
    }

    /**
     * Populate audit list from db.
     */
    private void populateAudit() {
        Bundle input = new Bundle();
        input.putString(PBSTaskController.ARG_PROJLOC_UUID, globalVar.getC_projectlocation_uuid());
        input.putString(PBSTaskController.ARG_AD_USER_ID, globalVar.getAd_user_id());
        Bundle result = auditCont.triggerEvent(PBSSurveyController.GET_SURVEYS_EVENT, input, new Bundle(), null);
        auditList = (ObservableArrayList<MSurvey>) result.getSerializable(PBSSurveyController.ARG_SURVEYS);
    }

    /**
     * Add click listener on the recycler view. when ever user clicks the list item.
     * it will navigate them to the item details.
     * @param rv
     */
    protected void addRecyclerViewListener(RecyclerView rv) {
        ObservableArrayList<IModel> modelList = (ObservableArrayList) auditList;
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new FragmentListOnItemClickListener(modelList, new NewAuditPagerFragment(),
                        getActivity(), auditDetailTitle)));
    }

    protected void refreshAudit() {
        PandoraMain pandoraMain = ((PandoraMain)getActivity());
        if (pandoraMain == null || globalVar == null)
            return;
        populateAudit();
        viewAdapter = new SurveyRVA(pandoraMain, auditList);
        recyclerView.setAdapter(viewAdapter);
        addRecyclerViewListener(recyclerView);
    }

    @Override
    public boolean onBackKeyPressed() {
        return false;
    }
}