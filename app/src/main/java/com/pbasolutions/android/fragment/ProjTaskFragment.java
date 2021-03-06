package com.pbasolutions.android.fragment;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    private ObservableArrayList<MProjectTask> taskList;
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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.task_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new AsyncTask<Object, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];
                Bundle result = syncProjTasks();
                populateProjTask();

                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                PandoraMain pandoraMain = PandoraMain.instance;
                pandoraMain.dismissProgressDialog();

                viewAdapter = new TaskRVA(pandoraMain, taskList, inflater);
                recyclerView.setAdapter(viewAdapter);
                addRecyclerViewListener(recyclerView);
            }
        }.execute(inflater, recyclerView);

        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sync;
        sync = menu.add(0, SYNC_PROJTASK_ID, 0, "Sync Task");
        sync.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        sync.setIcon(R.drawable.refresh);

        MenuItem add;
        add = menu.add(0, ADD_ID, 1, getString(R.string.text_icon_add));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SYNC_PROJTASK_ID: {
                new AsyncTask<Object, Void, Bundle>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        ((PandoraMain)getActivity()).showProgressDialog("Loading...");
                    }

                    @Override
                    protected Bundle doInBackground(Object... params) {
                        Bundle result = syncProjTasks();
                        return result;
                    }

                    @Override
                    protected void onPostExecute(Bundle result) {
                        super.onPostExecute(result);
                        PandoraMain pandoraMain = PandoraMain.instance;
                        pandoraMain.dismissProgressDialog();

                        String resultTitle = result.getString(PandoraConstant.TITLE);
                        String text;
                        if (resultTitle != null && !result.isEmpty()) {
                            populateProjTask();
                            viewAdapter = new TaskRVA(pandoraMain, taskList, LayoutInflater.from(PandoraMain.instance));
                            recyclerView.setAdapter(viewAdapter);
                            addRecyclerViewListener(recyclerView);
                            text = result.getString(resultTitle);
                        } else {
                            text = "Project Task is up to date";
                        }
                        PandoraHelper.showMessage(PandoraMain.instance, text);
                    }
                }.execute();
                return true;
            }
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
        Bundle result = taskCont.triggerEvent(taskCont.GET_PROJTASKS_EVENT, input, new Bundle(), null);
        taskList = (ObservableArrayList<MProjectTask>) result.getSerializable(taskCont.ARG_TASK_LIST);
    }

    /**
     * Sync project task from server.
     */
    private Bundle syncProjTasks() {
        if (PBSServerConst.cookieStore !=null){
            Bundle input = new Bundle();
            input.putString(PBSTaskController.ARG_PROJLOC_UUID, globalVar.getC_projectlocation_uuid());
            input.putString(PBSTaskController.ARG_AD_USER_ID, globalVar.getAd_user_id());
            input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
            Bundle result = taskCont.triggerEvent(PBSTaskController.SYNC_PROJTASKS_EVENT, input, new Bundle(), null);

            return result;
        }

        return null;
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

    /**
     * Add click listener on the recycler view. when ever user clicks the list item.
     * it will navigate them to the item details.
     * @param rv
     */
    protected void addRecyclerViewListener(RecyclerView rv) {
//        if (isrecyclerviewAdded) return;
        isrecyclerviewAdded = true;

        ObservableArrayList<IModel> modelList = (ObservableArrayList) taskList;
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new FragmentListOnItemClickListener(modelList, new ProjTaskDetailsFragment(),
                        getActivity(), taskDetailTitle)));
    }
}
