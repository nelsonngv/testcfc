package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.TaskRVA;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MProjectTask;

/**
 * Created by pbadell on 10/13/15.
 */
public class ProjTaskFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "TaskFragment";
    private static final int ADD_ID = 2;
    /**
     * Task controller.
     */
    PBSTaskController taskCont;
    /**
     * Project Task list.
     */
    private ObservableArrayList<MProjectTask> taskList = new ObservableArrayList<>();
    /**
     * Global Variable.
     */
    private PandoraContext globalVar;
    /**
     * Sync menu id.
     */
    public static final int SYNC_PROJTASK_ID = 800;

    private boolean isrecyclerviewAdded;

    protected String taskDetailTitle;
    private TaskRVA viewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AsyncTask asyncTask;
    /**
     * Contructor.
     */
    public ProjTaskFragment() {
        isrecyclerviewAdded = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskCont = new PBSTaskController(getActivity());
        globalVar = ((PandoraMain) getActivity()).getGlobalVariable();
        setHasOptionsMenu(true);
        taskDetailTitle = getString(R.string.title_projtaskdetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.task_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        populateProjTask();
        viewAdapter = new TaskRVA(getActivity(), taskList);
        recyclerView.setAdapter(viewAdapter);
        PandoraHelper.addRecyclerViewListener(recyclerView, taskList, getActivity(),
                new ProjTaskDetailsFragment(), taskDetailTitle);
        refreshProjtask(false);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshProjtask(true);
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
        Fragment fragment = new NewProjTaskFragment();
            if (fragment != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();
                ((PandoraMain) getActivity()).getSupportActionBar().setTitle(
                        getString(R.string.title_newprojecttask));
            }
    }

    /**
     * Populate project task list from db.
     */
    private void populateProjTask() {
        Bundle input = new Bundle();
        input.putString(PBSTaskController.ARG_PROJLOC_UUID, globalVar.getC_projectlocation_uuid());
        input.putString(PBSTaskController.ARG_AD_USER_ID, globalVar.getAd_user_id());
        Bundle result = taskCont.triggerEvent(PBSTaskController.GET_PROJTASKS_EVENT, input, new Bundle(), null);
        taskList.clear();
        taskList.addAll((ObservableArrayList<MProjectTask>) result.getSerializable(PBSTaskController.ARG_TASK_LIST));
    }

    /**
     * Sync project task from server.
     */
    private Bundle syncProjTasks() {
        Bundle input = new Bundle();
        if (PBSServerConst.cookieStore != null) {
            input.putString(PBSTaskController.ARG_PROJLOC_UUID, globalVar.getC_projectlocation_uuid());
            input.putString(PBSTaskController.ARG_AD_USER_ID, globalVar.getAd_user_id());
            input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());

            return taskCont.triggerEvent(PBSTaskController.SYNC_PROJTASKS_EVENT, input, new Bundle(), null);
        }

        return input;
    }

    /**
     * Project task list.
     * @return
     */
    public ObservableArrayList<MProjectTask> getTaskList() {
        return taskList;
    }

    /**
     * Project task setter.
     * @param taskList
     */
    public void setTaskList(ObservableArrayList<MProjectTask> taskList) {
        this.taskList = taskList;
    }

    protected void refreshProjtask(final boolean showMsg) {
        if (asyncTask != null && !asyncTask.getStatus().name().equalsIgnoreCase("FINISHED"))
            asyncTask.cancel(true);

        asyncTask = new AsyncTask<Object, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(true);
//                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Object... params) {
                Bundle bundle = syncProjTasks();
                populateProjTask();
                return bundle;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                PandoraMain pandoraMain = PandoraMain.instance;
//                pandoraMain.dismissProgressDialog();

                String resultTitle = result.getString(PandoraConstant.TITLE);
                String text;
                viewAdapter.notifyDataSetChanged();
                if (resultTitle != null && !result.isEmpty()) {
                    text = result.getString(resultTitle);
                } else {
                    text = result.getString(PandoraConstant.ERROR);
                }
                if (showMsg)
                    PandoraHelper.showMessage(PandoraMain.instance, text);
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    @Override
    public void onPause() {
        asyncTask.cancel(true);
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        super.onPause();
    }
}
