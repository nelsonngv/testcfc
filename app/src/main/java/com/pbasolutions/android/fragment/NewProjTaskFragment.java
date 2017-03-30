package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.utils.CameraUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by pbadell on 1/12/16.
 */
public class NewProjTaskFragment extends PBSDetailsFragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewProjTaskFragment";

    /**
     * PBSRequisitionController.
     */
    private PBSTaskController taskCont ;
    private EditText taskName;
    private EditText description;
    private EditText sequenceNo;
    private ImageView taskPicture1;
    private Spinner assignToSpinner;
    private Spinner projLocSpinner;
    private SpinnerOnItemSelected projLocNameItem;
    private ArrayAdapter projLocNameAdapter;
    private SpinnerOnItemSelected assignToItem;
    private ArrayAdapter assignToAdapter;
    private List<SpinnerPair> projLocList;
    private TextView taskDueDate;
    PandoraMain context;

    private static final int ASSIGN_ID = 1;
    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_PIC1 = "EVENT_PIC1";

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
            context = (PandoraMain)getActivity();
            context.fragment = this;
            setUI(rootView);
            setUIListener();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return rootView;
    }

    protected void setUIListener() {
        setOnItemSelectedListener();
        setOnClickListener(taskDueDate, EVENT_DATE);
        setOnClickListener(taskPicture1, EVENT_PIC1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
        taskPicture1 = (ImageView) rootView.findViewById(R.id.taskPicture1);
        taskDueDate = (TextView) rootView.findViewById(R.id.taskDueDate);
        TextView textViewTaskName = (TextView)rootView.findViewById(R.id.textViewTaskName);
        TextView textViewTaskDescription = (TextView)rootView.findViewById(R.id.textViewTaskDescription);
        TextView textViewTaskSeqNo = (TextView)rootView.findViewById(R.id.textViewTaskSeqNo);
        PandoraHelper.setAsterisk(textViewTaskName);
        PandoraHelper.setAsterisk(textViewTaskDescription);
        PandoraHelper.setAsterisk(textViewTaskSeqNo);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        taskDueDate.setText(sdf.format(date));
    }

    protected void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptFutureDatePicker((TextView) object, getActivity());
                        break;
                    }
                    case EVENT_PIC1: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_1, object);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    protected void setValues() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == context.RESULT_OK) {
            String picturePath = CameraUtil.getPicPath(context, data);

            if (picturePath != null) {
//            if (!(picturePath.endsWith(".jpg") || picturePath.endsWith(".jpeg")))
                if (!picturePath.endsWith(".jpg") && !picturePath.endsWith(".jpg"))
                    picturePath += ".jpg";

                switch (requestCode) {
                    case CameraUtil.CAPTURE_ATTACH_1: {
                        CameraUtil.handleBigCameraPhoto(taskPicture1, picturePath, context);
                        context.mCurrentPhotoPath = null;
                        break;
                    }
                    default:
                        break;
                }
            }
        }
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

    @Override
    public boolean onBackKeyPressed() {
        boolean hasChanged = !(taskPicture1.getTag() == null || ((String)taskPicture1.getTag()).isEmpty());

        if (hasChanged == false)
            return false;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Changes will be lost. continue to leave?")
                .setTitle(PandoraConstant.WARNING)
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .setNegativeButton("Close", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private List<SpinnerPair> getUserList() {
        PandoraContext globalVar = ((PandoraMain)getActivity()).getGlobalVariable();
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
        if (projLocNameItem.getPair().getKey() == null || assignToItem.getPair().getKey() == null)
        {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), "Incomplete info");
            return;
        }
        String name = taskName.getText().toString();
        String locID = projLocNameItem.getPair().getKey();
        String desc = description.getText().toString();
        String assignedTo  = assignToItem.getPair().getKey();
        String seqNo  = sequenceNo.getText().toString();
        String dueDate  = taskDueDate.getText().toString();
        if (locID == null
                || locID.isEmpty()
                || name.isEmpty()
                || desc.isEmpty()
                || assignedTo == null
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
        pt.setIsDone("N");
        pt.setDueDate(dueDate);
        if (taskPicture1.getTag() != null && !((String)taskPicture1.getTag()).isEmpty()) {
//            String pic1 = CameraUtil
//                    .imageToBase64(taskPicture1.getTag().toString());
//            pt.setATTACHMENT_BEFORETASKPICTURE_1(pic1);
            pt.setATTACHMENT_BEFORETASKPICTURE_1(taskPicture1.getTag().toString());
        }

        PandoraMain context = (PandoraMain) getActivity();
        String ad_user_id = context.getGlobalVariable().getAd_user_id();
        //as we dont know when does the initial sync completed. we try check if the uuid isEmpty.
        // if it is .. we have to search the uuid in database
        if (ad_user_id.isEmpty()) {
            ad_user_id = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_ID_COL, context.getGlobalVariable().getAd_user_uuid(),
                    ModelConst.AD_USER_TABLE + ModelConst._UUID, getActivity().getContentResolver());
            //set the uuid.
            context.getGlobalVariable().setAd_user_id(ad_user_id);
        }

        String ad_user_uuid = context.getGlobalVariable().getAd_user_uuid();
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                    ModelConst.AD_USER_UUID_COL, context.getGlobalVariable().getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
            //set the uuid.
            context.getGlobalVariable().setAd_user_uuid(ad_user_uuid);
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
        PandoraContext cont = ((PandoraMain) getActivity()).getGlobalVariable();
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
                output = taskCont.triggerEvent(taskCont.CREATE_TASK_EVENT, params[0], output, null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (result.getString(PandoraConstant.TITLE).equals(PandoraConstant.RESULT)) {
                    PandoraHelper.showMessage(getActivity(), result.getString(PandoraConstant.RESULT));
                } else {
                    PandoraHelper.showMessage(getActivity(), result.getString(PandoraConstant.ERROR));
//                    Fragment fragment = new ProjTaskFragment();
//                    if (fragment != null) {
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragment.setRetainInstance(true);
//                        fragmentTransaction.replace(R.id.container_body, fragment);
//                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                        fragmentTransaction.commit();
//                        ((PandoraMain) getActivity()).getSupportActionBar()
//                                .setTitle(getString(R.string.title_task));
//                    }
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
                PandoraHelper.hideSoftKeyboard();
                PandoraMain.instance.getSupportFragmentManager().popBackStack();
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
