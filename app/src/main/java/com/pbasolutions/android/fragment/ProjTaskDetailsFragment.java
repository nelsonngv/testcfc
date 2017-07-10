package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.databinding.TaskDetailsBinding;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.utils.CameraUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by pbadell on 10/13/15.
 */
public class ProjTaskDetailsFragment extends PBSDetailsFragment implements PBABackKeyListener {
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
    TextView taskDueDate;
    ImageView pretaskPicture;
//    ImageView taskPicture1;
//    ImageView taskPicture2;
//    ImageView taskPicture3;
//    ImageView taskPicture4;
//    ImageView taskPicture5;
    ImageView[] taskPictures = new ImageView[25];
    String[] picPath = new String[25];
    EditText taskComments;
    TableLayout tlPics, tlMorePic;
    Button btnMorePic;
    int picCount = 0;
   // Button taskIsDoneButton;
    PandoraMain context;
    View m_rootView;

    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_COMPLETEPROJ = "EVENT_COMPLETEPROJ";
    protected static final String EVENT_PREPIC = "EVENT_PREPIC";
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
        m_rootView = rootView;
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
            taskDueDate.setText(projTask.getDueDate());

            if (projTask.getATTACHMENT_BEFORETASKPICTURE_1() != null && !new File(projTask.getATTACHMENT_BEFORETASKPICTURE_1()).exists()) projTask.setATTACHMENT_BEFORETASKPICTURE_1(null);
//            if (projTask.getATTACHMENT_TASKPICTURE_1() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_1()).exists()) projTask.setATTACHMENT_TASKPICTURE_1(null);
//            if (projTask.getATTACHMENT_TASKPICTURE_2() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_2()).exists()) projTask.setATTACHMENT_TASKPICTURE_2(null);
//            if (projTask.getATTACHMENT_TASKPICTURE_3() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_3()).exists()) projTask.setATTACHMENT_TASKPICTURE_3(null);
//            if (projTask.getATTACHMENT_TASKPICTURE_4() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_4()).exists()) projTask.setATTACHMENT_TASKPICTURE_4(null);
//            if (projTask.getATTACHMENT_TASKPICTURE_5() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_5()).exists()) projTask.setATTACHMENT_TASKPICTURE_5(null);
            CameraUtil.loadPicture(projTask.getATTACHMENT_BEFORETASKPICTURE_1(), pretaskPicture);
//            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_1(), taskPicture1);
//            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_2(), taskPicture2);
//            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_3(), taskPicture3);
//            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_4(), taskPicture4);
//            CameraUtil.loadPicture(projTask.getATTACHMENT_TASKPICTURE_5(), taskPicture5);
            taskComments.setText(projTask.getComments());

            picPath[0] = projTask.getATTACHMENT_TASKPICTURE_1() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_1()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_1();
            picPath[1] = projTask.getATTACHMENT_TASKPICTURE_2() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_2()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_2();
            picPath[2] = projTask.getATTACHMENT_TASKPICTURE_3() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_3()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_3();
            picPath[3] = projTask.getATTACHMENT_TASKPICTURE_4() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_4()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_4();
            picPath[4] = projTask.getATTACHMENT_TASKPICTURE_5() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_5()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_5();
            picPath[5] = projTask.getATTACHMENT_TASKPICTURE_6() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_6()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_6();
            picPath[6] = projTask.getATTACHMENT_TASKPICTURE_7() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_7()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_7();
            picPath[7] = projTask.getATTACHMENT_TASKPICTURE_8() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_8()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_8();
            picPath[8] = projTask.getATTACHMENT_TASKPICTURE_9() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_9()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_9();
            picPath[9] = projTask.getATTACHMENT_TASKPICTURE_10() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_10()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_10();
            picPath[10] = projTask.getATTACHMENT_TASKPICTURE_11() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_11()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_11();
            picPath[11] = projTask.getATTACHMENT_TASKPICTURE_12() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_12()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_12();
            picPath[12] = projTask.getATTACHMENT_TASKPICTURE_13() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_13()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_13();
            picPath[13] = projTask.getATTACHMENT_TASKPICTURE_14() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_14()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_14();
            picPath[14] = projTask.getATTACHMENT_TASKPICTURE_15() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_15()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_15();
            picPath[15] = projTask.getATTACHMENT_TASKPICTURE_16() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_16()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_16();
            picPath[16] = projTask.getATTACHMENT_TASKPICTURE_17() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_17()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_17();
            picPath[17] = projTask.getATTACHMENT_TASKPICTURE_18() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_18()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_18();
            picPath[18] = projTask.getATTACHMENT_TASKPICTURE_19() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_19()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_19();
            picPath[19] = projTask.getATTACHMENT_TASKPICTURE_20() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_20()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_20();
            picPath[20] = projTask.getATTACHMENT_TASKPICTURE_21() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_21()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_21();
            picPath[21] = projTask.getATTACHMENT_TASKPICTURE_22() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_22()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_22();
            picPath[22] = projTask.getATTACHMENT_TASKPICTURE_23() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_23()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_23();
            picPath[23] = projTask.getATTACHMENT_TASKPICTURE_24() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_24()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_24();
            picPath[24] = projTask.getATTACHMENT_TASKPICTURE_25() != null && !new File(projTask.getATTACHMENT_TASKPICTURE_25()).exists() ? null : projTask.getATTACHMENT_TASKPICTURE_25();

            for (int i = 0; i < picPath.length; i++) {
                if (picPath[i] == null) break;

                if (i > 0) addPicField();
                CameraUtil.loadPicture(picPath[i], taskPictures[i]);
                CameraUtil.addPathToPic(taskPictures[i], picPath[i]);
            }

            String buttonText;
            if ("N".equalsIgnoreCase(projTask.isDone())) {
                //buttonText = "Complete";
                setHasOptionsMenu(true);
                getActivity().invalidateOptionsMenu();
            } else {
                taskComments.setEnabled(false);
                tlMorePic.setVisibility(View.GONE);
            }
            /*else {
               // buttonText = "Completed";

               // taskIsDoneButton.setEnabled(false);
                taskComments.setEnabled(false);
                taskPicture1.setEnabled(false);
                taskPicture2.setEnabled(false);
                taskPicture3.setEnabled(false);
                taskPicture4.setEnabled(false);
                taskPicture5.setEnabled(false);
            }*/
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
        menu.clear();
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
//        input.putSerializable(taskCont.ARG_TASK_LIST, modelList);
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
        taskDueDate = (TextView) rootView.findViewById(R.id.taskDueDate);
        pretaskPicture = (ImageView) rootView.findViewById(R.id.pretaskPicture);
        taskPictures[picCount] = (ImageView) rootView.findViewById(R.id.taskPicture1);
//        taskPicture2 = (ImageView) rootView.findViewById(R.id.taskPicture2);
//        taskPicture3 = (ImageView) rootView.findViewById(R.id.taskPicture3);
//        taskPicture4 = (ImageView) rootView.findViewById(R.id.taskPicture4);
//        taskPicture5 = (ImageView) rootView.findViewById(R.id.taskPicture5);
        taskComments = (EditText) rootView.findViewById(R.id.taskComments);
        tlPics = (TableLayout) rootView.findViewById(R.id.tlPics);
        tlMorePic = (TableLayout) rootView.findViewById(R.id.tlMorePic);
        btnMorePic = (Button) rootView.findViewById(R.id.btnMorePic);
        taskPictures[picCount].setTag(R.string.tag_imageview_count, picCount);
     //   taskIsDoneButton = (Button) rootView.findViewById(R.id.buttonTaskStatus);
        TextView textViewTaskComments = (TextView) rootView.findViewById(R.id.textViewTaskComments);
        PandoraHelper.setAsterisk(textViewTaskComments);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        taskDueDate.setText(sdf.format(date));

        btnMorePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskPictures[picCount].getTag(R.string.tag_imageview_path) == null || ((String)taskPictures[picCount].getTag(R.string.tag_imageview_path)).isEmpty()) {
                    PandoraHelper.showWarningMessage(getActivity(), getString(R.string.warning_add_pic_first));
                    return;
                }
                addPicField();

                if (picCount == 24)
                    tlMorePic.setVisibility(View.GONE);
            }
        });
    }

    protected void setUIListener() {
       // setOnClickListener(taskIsDoneButton, EVENT_COMPLETEPROJ);
        setOnClickListener(pretaskPicture, EVENT_PREPIC);
        setOnClickListener(taskPictures[0], EVENT_PIC1);
//        setOnClickListener(taskPicture2, EVENT_PIC2);
//        setOnClickListener(taskPicture3, EVENT_PIC3);
//        setOnClickListener(taskPicture4, EVENT_PIC4);
//        setOnClickListener(taskPicture5, EVENT_PIC5);
    }

    @Override
    public boolean onBackKeyPressed() {
        if ("Y".equalsIgnoreCase(projTask.isDone()))
            return true;

        boolean hasChanged = !taskComments.getText().toString().isEmpty();
        hasChanged |= !(taskPictures[0].getTag(R.string.tag_imageview_path) == null || ((String)taskPictures[0].getTag(R.string.tag_imageview_path)).isEmpty());
//        hasChanged |= !(taskPicture2.getTag(R.string.tag_imageview_path) == null || ((String)taskPicture2.getTag(R.string.tag_imageview_path)).isEmpty());
//        hasChanged |= !(taskPicture3.getTag(R.string.tag_imageview_path) == null || ((String)taskPicture3.getTag(R.string.tag_imageview_path)).isEmpty());
//        hasChanged |= !(taskPicture4.getTag(R.string.tag_imageview_path) == null || ((String)taskPicture4.getTag(R.string.tag_imageview_path)).isEmpty());
//        hasChanged |= !(taskPicture5.getTag(R.string.tag_imageview_path) == null || ((String)taskPicture5.getTag(R.string.tag_imageview_path)).isEmpty());

        if (hasChanged == false)
            return true;

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

        return false;
    }

    protected void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptFutureDatePicker((TextView) object, getActivity());
                        break;
                    }
//                    case EVENT_COMPLETEPROJ: {
//                        completeProj();
//                        break;
//                    }
                    case EVENT_PREPIC: {
                        if (projTask.getATTACHMENT_BEFORETASKPICTURE_1() != null && !projTask.getATTACHMENT_BEFORETASKPICTURE_1().equals("")) {
                            intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_BEFORETASKPICTURE_1())), "image/*");
                            PandoraMain.instance.startActivity(intent);
                        }
                        break;
                    }
                    case EVENT_PIC1: {
                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
                            if (object != null && object.getTag(R.string.tag_imageview_path) != null) {
                                String path = object.getTag(R.string.tag_imageview_path).toString();
                                if (path != null && !path.equals("")) {
                                    intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
                                    PandoraMain.instance.startActivity(intent);
                                }
                            }
                        }
                        else takePicture(Integer.parseInt(object.getTag(R.string.tag_imageview_count).toString()), object);
//                            takePicture(CameraUtil.CAPTURE_ATTACH_1, object);
                        break;
                    }
//                    case EVENT_PIC2: {
//                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
//                            if (projTask.getATTACHMENT_TASKPICTURE_2() != null && !projTask.getATTACHMENT_TASKPICTURE_2().equals("")) {
//                                intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_TASKPICTURE_2())), "image/*");
//                                PandoraMain.instance.startActivity(intent);
//                            }
//                        }
//                        else takePicture(CameraUtil.CAPTURE_ATTACH_2, object);
//                        break;
//                    }
//                    case EVENT_PIC3: {
//                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
//                            if (projTask.getATTACHMENT_TASKPICTURE_3() != null && !projTask.getATTACHMENT_TASKPICTURE_3().equals("")) {
//                                intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_TASKPICTURE_3())), "image/*");
//                                PandoraMain.instance.startActivity(intent);
//                            }
//                        }
//                        else takePicture(CameraUtil.CAPTURE_ATTACH_3, object);
//                        break;
//                    }
//                    case EVENT_PIC4: {
//                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
//                            if (projTask.getATTACHMENT_TASKPICTURE_4() != null && !projTask.getATTACHMENT_TASKPICTURE_4().equals("")) {
//                                intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_TASKPICTURE_4())), "image/*");
//                                PandoraMain.instance.startActivity(intent);
//                            }
//                        }
//                        else takePicture(CameraUtil.CAPTURE_ATTACH_4, object);
//                        break;
//                    }
//                    case EVENT_PIC5: {
//                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
//                            if (projTask.getATTACHMENT_TASKPICTURE_5() != null && !projTask.getATTACHMENT_TASKPICTURE_5().equals("")) {
//                                intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_TASKPICTURE_5())), "image/*");
//                                PandoraMain.instance.startActivity(intent);
//                            }
//                        }
//                        else takePicture(CameraUtil.CAPTURE_ATTACH_5, object);
//                        break;
//                    }
                    default:
                        break;
                }
            }
        });
    }

    private void addPicField() {
        picCount += 1;
        try {
            TableRow tr = new TableRow(getActivity());
            TableLayout.LayoutParams tlParams = new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tlParams.setMargins(0, 20, 0, 20);
            tr.setLayoutParams(tlParams);

            TextView tv = new TextView(new ContextThemeWrapper(getActivity(), R.style.TableLabel));
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins((int) getResources().getDimension(R.dimen.margin_left), 0,
                    (int) getResources().getDimension(R.dimen.margin_right), 0);
            tv.setLayoutParams(trParams);
            tv.setText(getString(R.string.label_picture, String.valueOf(picCount + 1)));

            taskPictures[picCount] = new ImageView(getActivity());
            TableRow.LayoutParams params = new TableRow.LayoutParams((int) getResources().getDimension(R.dimen.dimen_small_pic), (int) getResources().getDimension(R.dimen.dimen_small_pic));
            taskPictures[picCount].setLayoutParams(params);
            taskPictures[picCount].requestLayout();
            taskPictures[picCount].setBackgroundResource(R.drawable.camera);
            taskPictures[picCount].setScaleType(ImageView.ScaleType.FIT_XY);
            taskPictures[picCount].setTag(R.string.tag_imageview_count, picCount);

            tr.addView(tv);
            tr.addView(taskPictures[picCount]);
            tlPics.addView(tr);
            setOnClickListener(taskPictures[picCount], EVENT_PIC1);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void completeProj() {

        if (taskComments.getText() == null || taskComments.getText().toString().isEmpty()){
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(),
                    getString(R.string.please_fill_in_fields, getString(R.string.label_comment)));
            return;
        }

        PandoraContext globalVar = ((PandoraMain) getActivity()).getGlobalVariable();
        Bundle input = new Bundle();
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
//        input.putString(taskCont.ARG_PROJLOC_ID, globalVar.getC_projectlocation_id());

        Bundle locResult = taskCont.triggerEvent(taskCont.GET_PROJECTLOCATIONS_EVENT,
                input, new Bundle(), null);
        List<SpinnerPair> projLoc = locResult.getParcelableArrayList(taskCont.ARG_PROJECTLOCATIONS);
        for(int i = 0; i < projLoc.size(); i++) {
            if(projLoc.get(i).getValue().equalsIgnoreCase(projTask.getProjLocName()))
                input.putString(taskCont.ARG_PROJLOC_ID, projLoc.get(i).getKey());
        }

        input.putString(taskCont.ARG_TASK_ID,String.valueOf(projTask.get_ID()));
        input.putString(taskCont.ARG_TASK_UUID, _UUID);
        input.putString(taskCont.ARG_COMMENTS, taskComments.getText().toString());
        input.putString(taskCont.ARG_DUEDATE, taskDueDate.getText().toString());
        for (int i = 0; i < taskPictures.length; i++) {
            if (taskPictures[i] == null || taskPictures[i].getTag(R.string.tag_imageview_path) == null || ((String) taskPictures[i].getTag(R.string.tag_imageview_path)).isEmpty())
                break;
            input.putString(taskCont.ARG_TASKPIC + (i + 1), (String) taskPictures[i].getTag(R.string.tag_imageview_path));
        }
//        input.putString(taskCont.ARG_TASKPIC_1, (String)taskPicture1.getTag(R.string.tag_imageview_path));
//        input.putString(taskCont.ARG_TASKPIC_2, (String)taskPicture2.getTag(R.string.tag_imageview_path));
//        input.putString(taskCont.ARG_TASKPIC_3, (String)taskPicture3.getTag(R.string.tag_imageview_path));
//        input.putString(taskCont.ARG_TASKPIC_4, (String)taskPicture4.getTag(R.string.tag_imageview_path));
//        input.putString(taskCont.ARG_TASKPIC_5, (String)taskPicture5.getTag(R.string.tag_imageview_path));

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle output = new Bundle();
                output = taskCont.triggerEvent(taskCont.COMPLETE_PROJTASK_EVENT, params[0], output, null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (PandoraConstant.RESULT.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    PandoraMain.instance.getSupportFragmentManager().popBackStack();
                } else {
                    PandoraHelper.showMessage((PandoraMain)getActivity(),
                            result.getString(result.getString(PandoraConstant.TITLE)));
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == context.RESULT_OK) {
            String picturePath = CameraUtil.getPicPath(context, data);

            if (picturePath != null) {
//                if (!picturePath.endsWith(".jpg") && !picturePath.endsWith(".jpg"))
//                    picturePath += ".jpg";

                CameraUtil.handleBigCameraPhoto(taskPictures[requestCode], picturePath, context);
                context.mCurrentPhotoPath = null;
//                switch (requestCode) {
//                    case CameraUtil.CAPTURE_ATTACH_1: {
//                        CameraUtil.handleBigCameraPhoto(taskPicture1, picturePath, context);
//                        context.mCurrentPhotoPath = null;
//                        break;
//                    }
//                    case CameraUtil.CAPTURE_ATTACH_2: {
//                        CameraUtil.handleBigCameraPhoto(taskPicture2, picturePath, context);
//                        context.mCurrentPhotoPath = null;
//                        break;
//                    }
//                    case CameraUtil.CAPTURE_ATTACH_3: {
//                        CameraUtil.handleBigCameraPhoto(taskPicture3, picturePath, context);
//                        context.mCurrentPhotoPath = null;
//                        break;
//                    }
//                    case CameraUtil.CAPTURE_ATTACH_4: {
//                        CameraUtil.handleBigCameraPhoto(taskPicture4, picturePath, context);
//                        context.mCurrentPhotoPath = null;
//                        break;
//                    }
//                    case CameraUtil.CAPTURE_ATTACH_5: {
//                        CameraUtil.handleBigCameraPhoto(taskPicture5, picturePath, context);
//                        context.mCurrentPhotoPath = null;
//                        break;
//                    }
//
//                    default:
//                        break;
//                }
            }
        }
    }
}
