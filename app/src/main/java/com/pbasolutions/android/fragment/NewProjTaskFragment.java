package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.ModelConst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by pbadell on 1/12/16.
 */
public class NewProjTaskFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewProjTaskFragment";

    /**
     * PBSRequisitionController.
     */
    private PBSTaskController taskCont ;

    /**
     *
     */
    private EditText taskName;

    /**
     *
     */
    private EditText description;

    /**
     *
     */
    private EditText sequenceNo;

    /**
     *
     */
    private Spinner assignToSpinner;

    /**
     *
     */
    private Spinner projLocSpinner;

    /**
     *
     */
    private SpinnerOnItemSelected projLocNameItem;

    /**
     *
     */
    private ArrayAdapter projLocNameAdapter;

    /**
     *
     */
    private SpinnerOnItemSelected assignToItem;

    /**
     *
     */
    private ArrayAdapter assignToAdapter;
    private List<SpinnerPair> projLocList;

    private static final int ASSIGN_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskCont = new PBSTaskController(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_projecttask, container, false);
        try {
            setUI(rootView);
            setUIListener();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return rootView;
    }

    private void setUIListener() {
        setOnItemSelectedListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ASSIGN_ID, 0, getString(R.string.text_icon_assign));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.ic_assignment);
    }

    protected void setUI(View rootView) {
        Activity act = getActivity();
        assignToSpinner = (Spinner) rootView.findViewById(R.id.newTaskAssignTo);
        assignToAdapter = PandoraHelper.addListToSpinner(act, assignToSpinner, getUserList());
        taskName = (EditText)rootView.findViewById(R.id.newTaskName);
        description = (EditText)rootView.findViewById(R.id.newTaskDescription);
        sequenceNo = (EditText)rootView.findViewById(R.id.newTaskSeqNo);
        projLocSpinner = (Spinner) rootView.findViewById(R.id.newTaskProjLoc);
        projLocNameAdapter = PandoraHelper.addListToSpinner(act, projLocSpinner, getProjLocList());
    }
    /**
     *
     */
    private void setOnItemSelectedListener() {
        projLocNameItem = new SpinnerOnItemSelected(projLocSpinner,
                new SpinnerPair());
        projLocSpinner.setOnItemSelectedListener(projLocNameItem);

        assignToItem = new SpinnerOnItemSelected(assignToSpinner,
                new SpinnerPair());
        assignToSpinner.setOnItemSelectedListener(assignToItem);
    }

    private List<SpinnerPair> getUserList() {
        PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;
        if (globalVar != null) {
            Bundle input = new Bundle();
//            String projectLocationUUID = globalVar.getC_projectlocation_uuid();
            String adUserID = globalVar.getAd_user_id();
            if (adUserID != null) {
                input.putString(PBSTaskController.ARG_AD_USER_ID, adUserID);
                Bundle result = taskCont.triggerEvent(taskCont.GET_USERS_EVENT,
                        input, new Bundle(), null);
                return result.getParcelableArrayList(taskCont.ARG_USERS);
            } else {
                PandoraHelper.showErrorMessage((PandoraMain) getActivity(), getString(R.string.text_projectloc_na));
            }
        }
        return null;
    }

    public List<SpinnerPair> getProjLocList() {
        Bundle input = new Bundle();
        Bundle result = taskCont
                .triggerEvent(taskCont.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result
                .getParcelableArrayList(taskCont.ARG_PROJECTLOCATIONS);
    }

    public void createProjTask(){
        //check all value is not null.
        String name = taskName.getText().toString();
        String locID = projLocNameItem.getPair().getKey();
        String desc = description.getText().toString();
        String assignedTo  = assignToItem.getPair().getKey();
        String seqNo  = sequenceNo.getText().toString();
        if (locID.isEmpty()
                || name.isEmpty()
                || desc.isEmpty()
                || assignedTo.isEmpty()
                || seqNo.isEmpty())
        {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), "Please fill up all fields");
            return;
        }

        int nSeqNo = -1;
        try {
            nSeqNo = Integer.parseInt(seqNo);
        } catch (Exception e) {
        }

        if (nSeqNo < 0) {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), "Please fill up valid Priority.");
            return;
        }

        MProjectTask pt = new MProjectTask();
        pt.setName(name);
        pt.setC_ProjectLocation_ID(Integer.parseInt(locID));
        pt.setDescription(desc);
        pt.setAssignedTo(Integer.parseInt(assignedTo));
        pt.setPriority(nSeqNo);
        // added by danny 1/29/2016
        pt.setIsDone("N");

        PandoraMain context = (PandoraMain) getActivity();
        String ad_user_id = context.globalVariable.getAd_user_id();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_user_id.isEmpty()) {
            ad_user_id = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_ID_COL, context.globalVariable.getAd_user_uuid(),
                    ModelConst.AD_USER_TABLE + ModelConst._UUID, getActivity().getContentResolver());
            //set the uuid.
            context.globalVariable.setAd_user_id(ad_user_id);
        }

        String ad_user_uuid = context.globalVariable.getAd_user_uuid();
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_UUID_COL, context.globalVariable.getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            context.globalVariable.setAd_user_uuid(ad_user_uuid);
        }

        pt.setCreatedBy(ad_user_uuid);

        pt.set_UUID(UUID.randomUUID().toString());

        String projLocUUID = ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL, locID,
                ModelConst.C_PROJECTLOCATION_ID_COL, getActivity().getContentResolver());

        pt.setProjLocUUID(projLocUUID);

        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String now = dt.format(date);
        pt.setCreated(now);

        Bundle input = new Bundle();
        PandoraContext cont = ((PandoraMain) getActivity()).globalVariable;
        input.putSerializable(PBSServerConst.PARAM_URL, cont.getServer_url());
        input.putSerializable(taskCont.ARG_PROJTASK, pt);

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle output = new Bundle();
                taskCont.triggerEvent(taskCont.CREATE_TASK_EVENT, params[0], output, null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
//                if (output.getBoolean(PandoraConstant.RESULT)){
//
//                } else {
                    Fragment fragment = new ProjTaskFragment();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragment.setRetainInstance(true);
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        fragmentTransaction.commit();
                        ((PandoraMain) getActivity()).getSupportActionBar()
                                .setTitle(getString(R.string.title_task));
                    }
//                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ASSIGN_ID: {
                createProjTask();
                return  true;
            }
            default:return false;
        }
    }
}
