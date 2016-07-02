package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.databinding.TaskDetailsBinding;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.utils.CameraUtil;

/**
 * Created by pbadell on 10/13/15.
 */
public class ProjTaskDetailsFragment extends PBSDetailsFragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "ProjTaskDetailsFragment";
    /**
     * PBSTaskController controller.
     */
    private PBSTaskController taskCont;
    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private TaskDetailsBinding binding;

    public static final int COMPLETE_PROJTASK_ID = 900;

    private MProjectTask projTask;

    TextView seqNo;
    TextView taskName;
    TextView taskProjLoc;
    TextView taskStatus;
    TextView taskDesc;
    ImageView taskPicture1;
    ImageView taskPicture2;
    ImageView taskPicture3;
    ImageView taskPicture4;
    ImageView taskPicture5;
    EditText taskComments;
   // Button taskIsDoneButton;
    PandoraMain context;


    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_COMPLETEPROJ = "EVENT_COMPLETEPROJ";
    protected static final String EVENT_PIC1 = "EVENT_PIC1";
    protected static final String EVENT_PIC2 = "EVENT_PIC2";
    protected static final String EVENT_PIC3 = "EVENT_PIC3";
    protected static final String EVENT_PIC4 = "EVENT_PIC4";
    protected static final String EVENT_PIC5 = "EVENT_PIC5";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskCont = new PBSTaskController(getActivity());
        context = (PandoraMain)getActivity();
        context.fragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_details, container, false);
        setUI(rootView);
        setUIListener();
        context.fragment = this;
        projTask = getProjTask();
        setValues();
        return rootView;
    }

    /**
     *
     */
    protected void setValues() {
        if (projTask != null) {
            seqNo.setText(String.valueOf(projTask.getPriority()));
            taskName.setText(projTask.getName());
            taskProjLoc.setText(projTask.getProjLocName());
            taskStatus.setText(projTask.getStatus());
            taskDesc.setText(projTask.getDescription());

            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_1(), taskPicture1);
            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_2(), taskPicture2);
            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_3(), taskPicture3);
            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_4(), taskPicture4);
            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_5(), taskPicture5);
            taskComments.setText(projTask.getComments());

            String buttonText;
            if ("N".equalsIgnoreCase(projTask.isDone())){
                //buttonText = "Complete";
                setHasOptionsMenu(true);
                getActivity().invalidateOptionsMenu();
            } else {
               // buttonText = "Completed";

               // taskIsDoneButton.setEnabled(false);
                taskComments.setEnabled(false);
                taskPicture1.setEnabled(false);
                taskPicture2.setEnabled(false);
                taskPicture3.setEnabled(false);
                taskPicture4.setEnabled(false);
                taskPicture5.setEnabled(false);
            }
           // taskIsDoneButton.setText(buttonText);
        }
    }

    /**
     *
     * @return
     */
    public MProjectTask getTask() {
        Bundle inputBundle = new Bundle();
        inputBundle.putString(taskCont.ARG_C_PROJECTTASK_UUID, _UUID);
        Bundle resultBundle = new Bundle();
        resultBundle = taskCont.triggerEvent(taskCont.TASK_DETAILS_EVENT, inputBundle, resultBundle, null);
        return (MProjectTask)resultBundle.getSerializable(taskCont.ARG_TASK);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item;
        item = menu.add(0, COMPLETE_PROJTASK_ID, 1, "Complete Project Task");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_done);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case COMPLETE_PROJTASK_ID: {
                completeProj();
                return true;
            }
            default : return false;
        }
    }

    private boolean saveTask(){
        Bundle input = new Bundle();
        input.putString(taskCont.ARG_C_PROJECTTASK_UUID, _UUID);
        return true;
    }

    private MProjectTask getProjTask() {
        Bundle input = new Bundle();
        input.putSerializable(taskCont.ARG_TASK_LIST, modelList);
        input.putString(taskCont.ARG_C_PROJECTTASK_UUID, _UUID);
        Bundle result = taskCont.triggerEvent(taskCont.GET_PROJTASK_EVENT, input, new Bundle(), null);
        return (MProjectTask)result.getSerializable(taskCont.ARG_PROJTASK);
    }

    /**
     *
     * @param rootView
     */
    protected void setUI(View rootView) {
        Activity act = getActivity();
        seqNo = (TextView) rootView.findViewById(R.id.taskSeqNo);
        taskName = (TextView) rootView.findViewById(R.id.taskName);
        taskProjLoc = (TextView) rootView.findViewById(R.id.taskProjLoc);
        taskStatus = (TextView) rootView.findViewById(R.id.taskStatus);
        taskDesc = (TextView) rootView.findViewById(R.id.taskDesc);
        taskPicture1 = (ImageView) rootView.findViewById(R.id.taskPicture1);
        taskPicture2 = (ImageView) rootView.findViewById(R.id.taskPicture2);
        taskPicture3 = (ImageView) rootView.findViewById(R.id.taskPicture3);
        taskPicture4 = (ImageView) rootView.findViewById(R.id.taskPicture4);
        taskPicture5 = (ImageView) rootView.findViewById(R.id.taskPicture5);
        taskComments = (EditText) rootView.findViewById(R.id.taskComments);
     //   taskIsDoneButton = (Button) rootView.findViewById(R.id.buttonTaskStatus);
    }

    protected void setUIListener(){
       // setOnClickListener(taskIsDoneButton, EVENT_COMPLETEPROJ);
        setOnClickListener(taskPicture1, EVENT_PIC1);
        setOnClickListener(taskPicture2, EVENT_PIC2);
        setOnClickListener(taskPicture3, EVENT_PIC3);
        setOnClickListener(taskPicture4, EVENT_PIC4);
        setOnClickListener(taskPicture5, EVENT_PIC5);
    }

    protected void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptDatePicker((TextView) object, getActivity());
                        break;
                    }
//                    case EVENT_COMPLETEPROJ: {
//                        completeProj();
//                        break;
//                    }
                    case EVENT_PIC1: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_1, object);
                        break;
                    }
                    case EVENT_PIC2: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_2, object);
                        break;
                    }
                    case EVENT_PIC3: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_3, object);
                        break;
                    }
                    case EVENT_PIC4: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_4, object);
                        break;
                    }
                    case EVENT_PIC5: {
                        takePicture(CameraUtil.CAPTURE_ATTACH_5, object);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    private void completeProj() {

        if (taskComments.getText() == null || taskComments.getText().toString().isEmpty()){
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(),
                    getString(R.string.please_fill_in_fields, getString(R.string.label_comment)));
            return;
        }

        PandoraContext globalVar = ((PandoraMain) getActivity()).globalVariable;
        Bundle input = new Bundle();
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
        input.putString(taskCont.ARG_PROJLOC_ID, globalVar.getC_projectlocation_id());
        input.putString(taskCont.ARG_TASK_ID,String.valueOf(projTask.get_ID()));
        input.putString(taskCont.ARG_TASK_UUID, _UUID);
        input.putString(taskCont.ARG_COMMENTS, taskComments.getText().toString());
        input.putString(taskCont.ARG_TASKPIC_1, (String)taskPicture1.getTag());
        input.putString(taskCont.ARG_TASKPIC_2, (String)taskPicture2.getTag());
        input.putString(taskCont.ARG_TASKPIC_3, (String)taskPicture3.getTag());
        input.putString(taskCont.ARG_TASKPIC_4, (String)taskPicture4.getTag());
        input.putString(taskCont.ARG_TASKPIC_5, (String)taskPicture5.getTag());

        Bundle result = taskCont.triggerEvent(taskCont.COMPLETE_PROJTASK_EVENT, input, new Bundle(), null);

        if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
            PandoraHelper.redirectToFragment(new ProjTaskFragment(), getActivity());
        } else {
            PandoraHelper.showMessage((PandoraMain)getActivity(),
                    result.getString(result.getString(PandoraConstant.TITLE)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == context.RESULT_OK) {
            String picturePath = null;
            if (data != null) {
                Uri curImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(curImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                picturePath = context.getmCurrentPhotoPath();
            }

            if (!picturePath.endsWith(".jpg") && !picturePath.endsWith(".jpg"))
                picturePath += ".jpg";

            switch (requestCode) {
                case CameraUtil.CAPTURE_ATTACH_1: {
                    CameraUtil.handleBigCameraPhoto(taskPicture1, picturePath, context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_2: {
                    CameraUtil.handleBigCameraPhoto(taskPicture2, picturePath, context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_3: {
                    CameraUtil.handleBigCameraPhoto(taskPicture3, picturePath, context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_4: {
                    CameraUtil.handleBigCameraPhoto(taskPicture4, picturePath, context);
                    context.mCurrentPhotoPath = null;
                    break;
                }
                case CameraUtil.CAPTURE_ATTACH_5: {
                    CameraUtil.handleBigCameraPhoto(taskPicture5, picturePath, context);
                    context.mCurrentPhotoPath = null;
                    break;
                }

                default:
                    break;
            }
        }
    }
}
