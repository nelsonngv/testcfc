package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.control.SearchableSpinner;
import com.pbasolutions.android.controller.PBSTaskController;
import com.pbasolutions.android.databinding.TaskDetailsBinding;
import com.pbasolutions.android.json.PBSProjTaskJSON;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MProjectTask;
import com.pbasolutions.android.utils.CameraUtil;

import java.io.File;
import java.util.ArrayList;
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

    public static final int EDIT_ID = 900;
    public static final int DISCARD_ID = 901;
    public static final int SAVE_ID = 902;
    public static final int COMPLETE_PROJTASK_ID = 903;

    private MProjectTask projTask;

    TextView seqNo, taskName, taskProjLoc, taskStatus, taskDateAssigned, taskDueDate;
    EditText taskDesc, taskEquipment, taskContact, taskContactNo, taskComments;
    ImageView pretaskPicture;
    SearchableSpinner taskAssignedTo, taskSecAssignedTo;
    ArrayAdapter assignToAdapter, secAssignToAdapter;
//    SpinnerOnItemSelected assignToItem, secAssignToItem;
    ImageView[] taskPictures = new ImageView[25];
    String[] picPath = new String[25];
    TableLayout tlPics, tlMorePic;
    Button btnMorePic, btnCall;
    int picCount = 0;
    PandoraMain context;
    View m_rootView;
    MenuItem edit, discard, save, complete;
    List<SpinnerPair> oriList, assignToList, secAssignToList;
    SpinnerPair assignToPair, secAssignToPair;

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
        context.fragment = this;
        projTask = getProjTask();

        setUI(rootView);
        setUIListener();
        setValues();
//        binding =  TaskDetailsBinding.bind(rootView);
//        binding.setTask(getProjTask());
        return rootView;
    }

    /**
     *
     * @return
     */
    public MProjectTask getTask() {
        Bundle inputBundle = new Bundle();
        inputBundle.putString(PBSTaskController.ARG_C_PROJECTTASK_UUID, _UUID);
        Bundle resultBundle = new Bundle();
        resultBundle = taskCont.triggerEvent(PBSTaskController.TASK_DETAILS_EVENT, inputBundle, resultBundle, null);
        return (MProjectTask)resultBundle.getSerializable(PBSTaskController.ARG_TASK);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        edit = menu.add(0, EDIT_ID, 1, "Edit");
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        edit.setIcon(R.drawable.ic_pencil);

        discard = menu.add(0, DISCARD_ID, 2, "Discard");
        discard.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        discard.setIcon(R.drawable.ic_close);
        discard.setVisible(false);

        save = menu.add(0, SAVE_ID, 3, "Save");
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        save.setIcon(R.drawable.ic_save);
        save.setVisible(false);

        complete = menu.add(0, COMPLETE_PROJTASK_ID, 4, "Complete");
        complete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        complete.setIcon(R.drawable.ic_done);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case EDIT_ID: {
                changeToolbarButtons(true);
                return true;
            }
            case DISCARD_ID: {
                if (taskDesc.getText().toString().equals(projTask.getDescription() == null ? "" : PandoraHelper.parseNewLine(projTask.getDescription()))
                        && taskEquipment.getText().toString().equals(projTask.getEquipment() == null ? "" : PandoraHelper.parseNewLine(projTask.getEquipment()))
                        && taskContact.getText().toString().equals(projTask.getContact() == null ? "" : projTask.getContact())
                        && taskContactNo.getText().toString().equals(projTask.getContactNo() == null ? "" : projTask.getContactNo())
                        && assignToPair.getKey().equals(String.valueOf(projTask.getAssignedTo()))
                        && (projTask.getSecAssignedTo() == 0 || secAssignToPair.getKey().equals(String.valueOf(projTask.getSecAssignedTo())))) {
                    changeToolbarButtons(false);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Changes will be lost. continue to discard?")
                            .setTitle(PandoraConstant.WARNING)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    taskDesc.setText(PandoraHelper.parseNewLine(projTask.getDescription()));
                                    taskEquipment.setText(PandoraHelper.parseNewLine(projTask.getEquipment()));
                                    taskContact.setText(projTask.getContact());
                                    taskContactNo.setText(projTask.getContactNo());
                                    taskAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getAssignedTo())));
                                    if (projTask.getSecAssignedTo() != 0)
                                        taskSecAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getSecAssignedTo())));
                                    changeToolbarButtons(false);
                                }
                            })
                            .setNegativeButton("No", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
            case SAVE_ID: {
                updateProj();
                return true;
            }
            case COMPLETE_PROJTASK_ID: {
                completeProj();
                return true;
            }
            default : return false;
        }
    }

    private void changeToolbarButtons(boolean isEdit) {
        edit.setVisible(!isEdit);
        complete.setVisible(!isEdit);
        discard.setVisible(isEdit);
        save.setVisible(isEdit);
        taskDesc.setEnabled(isEdit);
        taskEquipment.setEnabled(isEdit);
        taskContact.setEnabled(isEdit);
        taskContactNo.setEnabled(isEdit);
        taskAssignedTo.setEnabled(isEdit);
        taskSecAssignedTo.setEnabled(isEdit);
        if (!isEdit) {
            taskDesc.setMaxLines(Integer.MAX_VALUE);
            taskEquipment.setMaxLines(Integer.MAX_VALUE);
        } else {
            taskDesc.setMaxLines(5);
            taskEquipment.setMaxLines(5);
        }
        if (!isEdit && !taskContactNo.getText().toString().trim().isEmpty())
            btnCall.setVisibility(View.VISIBLE);
        else btnCall.setVisibility(View.GONE);
    }

    private MProjectTask getProjTask() {
        Bundle input = new Bundle();
//        input.putSerializable(taskCont.ARG_TASK_LIST, modelList);
        input.putString(PBSTaskController.ARG_C_PROJECTTASK_UUID, _UUID);
        Bundle result = taskCont.triggerEvent(PBSTaskController.GET_PROJTASK_EVENT, input, new Bundle(), null);
        return (MProjectTask)result.getSerializable(PBSTaskController.ARG_PROJTASK);
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
        taskAssignedTo = (SearchableSpinner) rootView.findViewById(R.id.taskAssignTo);
        taskSecAssignedTo = (SearchableSpinner) rootView.findViewById(R.id.taskSecAssignTo);
        taskDesc = (EditText) rootView.findViewById(R.id.taskDesc);
        taskEquipment = (EditText) rootView.findViewById(R.id.taskEquipment);
        taskContact = (EditText) rootView.findViewById(R.id.taskContact);
        taskContactNo = (EditText) rootView.findViewById(R.id.taskContactNo);
        taskDateAssigned = (TextView) rootView.findViewById(R.id.taskDateAssigned);
        taskDueDate = (TextView) rootView.findViewById(R.id.taskDueDate);
        pretaskPicture = (ImageView) rootView.findViewById(R.id.pretaskPicture);
        taskPictures[picCount] = (ImageView) rootView.findViewById(R.id.taskPicture1);
        taskComments = (EditText) rootView.findViewById(R.id.taskComments);
        tlPics = (TableLayout) rootView.findViewById(R.id.tlPics);
        tlMorePic = (TableLayout) rootView.findViewById(R.id.tlMorePic);
        btnMorePic = (Button) rootView.findViewById(R.id.btnMorePic);
        btnCall = (Button) rootView.findViewById(R.id.btnCall);
        taskPictures[picCount].setTag(R.string.tag_imageview_count, picCount);
        TextView textViewTaskComments = (TextView) rootView.findViewById(R.id.textViewTaskComments);
        PandoraHelper.setAsterisk(textViewTaskComments);

        oriList = getUserList();
        assignToList = new ArrayList<>();
        secAssignToList = new ArrayList<>();
        assignToList.addAll(oriList);
        secAssignToList.addAll(oriList);
        assignToAdapter = PandoraHelper.addListToSpinner(act, taskAssignedTo, assignToList);
        secAssignToAdapter = PandoraHelper.addListToSpinner(act, taskSecAssignedTo, secAssignToList);
        taskAssignedTo.setEnabled(false);
        taskSecAssignedTo.setEnabled(false);
    }

    protected void setUIListener() {
        setOnClickListener(pretaskPicture, EVENT_PREPIC);
        setOnClickListener(taskPictures[0], EVENT_PIC1);

        assignToPair = ((SpinAdapter) assignToAdapter).getItem(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getAssignedTo())));
        int itemLoc = ((SpinAdapter) secAssignToAdapter).getPosition(String.valueOf(projTask.getSecAssignedTo()));
        secAssignToPair = ((SpinAdapter) secAssignToAdapter).getItem(itemLoc > 0 ? itemLoc : 0);
        AdapterView.OnItemSelectedListener assignToItem = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assignToPair = (SpinnerPair) parent.getItemAtPosition(position);
                secAssignToList.clear();
                secAssignToList.addAll(oriList);
                secAssignToList.remove(assignToPair);
                secAssignToAdapter.notifyDataSetChanged();
                taskSecAssignedTo.setSelection(((SpinAdapter) secAssignToAdapter).getPosition(secAssignToPair.getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        taskAssignedTo.setOnItemSelectedListener(assignToItem);

        AdapterView.OnItemSelectedListener secAssignToItem = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secAssignToPair = (SpinnerPair) parent.getItemAtPosition(position);
                assignToList.clear();
                assignToList.addAll(oriList);
                assignToList.remove(secAssignToPair);
                assignToAdapter.notifyDataSetChanged();
                taskAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(assignToPair.getKey()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        taskSecAssignedTo.setOnItemSelectedListener(secAssignToItem);

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

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + taskContactNo.getText()));
                startActivity(callIntent);
            }
        });

        taskDesc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (taskDesc.getLineCount() > 5)
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        taskEquipment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (taskEquipment.getLineCount() > 5)
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        taskComments.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (taskComments.getLineCount() > 5)
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public boolean onBackKeyPressed() {
        if ("Y".equalsIgnoreCase(projTask.isDone()))
            return true;

        boolean hasChanged = !taskComments.getText().toString().isEmpty();
        hasChanged |= !(taskPictures[0].getTag(R.string.tag_imageview_path) == null || ((String)taskPictures[0].getTag(R.string.tag_imageview_path)).isEmpty());

        if (discard.isVisible()) {
            if (taskDesc.getText().toString().equals(projTask.getDescription() == null ? "" : PandoraHelper.parseNewLine(projTask.getDescription()))
                    && taskEquipment.getText().toString().equals(projTask.getEquipment() == null ? "" : PandoraHelper.parseNewLine(projTask.getEquipment()))
                    && taskContact.getText().toString().equals(projTask.getContact() == null ? "" : projTask.getContact())
                    && taskContactNo.getText().toString().equals(projTask.getContactNo() == null ? "" : projTask.getContactNo())
                    && assignToPair.getKey().equals(String.valueOf(projTask.getAssignedTo()))
                    && (projTask.getSecAssignedTo() == 0 || secAssignToPair.getKey().equals(String.valueOf(projTask.getSecAssignedTo())))) {
                changeToolbarButtons(false);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Changes will be lost. continue to discard?")
                        .setTitle(PandoraConstant.WARNING)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                taskDesc.setText(PandoraHelper.parseNewLine(projTask.getDescription()));
                                taskEquipment.setText(PandoraHelper.parseNewLine(projTask.getEquipment()));
                                taskContact.setText(projTask.getContact());
                                taskContactNo.setText(projTask.getContactNo());
                                taskAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getAssignedTo())));
                                if (projTask.getSecAssignedTo() != 0)
                                    taskSecAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getSecAssignedTo())));
                                changeToolbarButtons(false);
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else if (hasChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        } else return true;

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
                    case EVENT_PREPIC: {
                        if (projTask.getATTACHMENT_BEFORETASKPICTURE_1() != null && !projTask.getATTACHMENT_BEFORETASKPICTURE_1().equals("")) {
                            intent.setDataAndType(Uri.fromFile(new File(projTask.getATTACHMENT_BEFORETASKPICTURE_1())), "image/*");
                            getActivity().startActivity(intent);
                        }
                        break;
                    }
                    case EVENT_PIC1: {
                        if ("Y".equalsIgnoreCase(projTask.isDone())) {
                            if (object != null && object.getTag(R.string.tag_imageview_path) != null) {
                                String path = object.getTag(R.string.tag_imageview_path).toString();
                                if (path != null && !path.equals("")) {
                                    intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
                                    getActivity().startActivity(intent);
                                }
                            }
                        }
                        else takePicture(Integer.parseInt(object.getTag(R.string.tag_imageview_count).toString()), object);
//                            takePicture(CameraUtil.CAPTURE_ATTACH_1, object);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    protected void setValues() {
        if (projTask != null) {
            seqNo.setText(String.valueOf(projTask.getPriority()));
            taskName.setText(projTask.getName());
            taskProjLoc.setText(projTask.getProjLocName());
            taskStatus.setText(projTask.getStatus());
            taskDesc.setText(PandoraHelper.parseNewLine(projTask.getDescription()));
            taskEquipment.setText(PandoraHelper.parseNewLine(projTask.getEquipment()));
            taskContact.setText(projTask.getContact());
            taskContactNo.setText(projTask.getContactNo());
            taskDateAssigned.setText(projTask.getDateAssigned());
            taskDueDate.setText(projTask.getDueDate());
            taskAssignedTo.setSelection(((SpinAdapter) assignToAdapter).getPosition(String.valueOf(projTask.getAssignedTo())));
            if (projTask.getSecAssignedTo() != 0)
                taskSecAssignedTo.setSelection(((SpinAdapter) secAssignToAdapter).getPosition(String.valueOf(projTask.getSecAssignedTo())));

            if (projTask.getATTACHMENT_BEFORETASKPICTURE_1() != null && !new File(projTask.getATTACHMENT_BEFORETASKPICTURE_1()).exists()) projTask.setATTACHMENT_BEFORETASKPICTURE_1(null);
            CameraUtil.loadPicture(projTask.getATTACHMENT_BEFORETASKPICTURE_1(), pretaskPicture);
            taskComments.setText(PandoraHelper.parseNewLine(projTask.getComments()));

            taskDesc.setMaxLines(Integer.MAX_VALUE);
            taskEquipment.setMaxLines(Integer.MAX_VALUE);
            taskComments.setMaxLines(Integer.MAX_VALUE);

            if (taskContactNo.getText().toString().trim().isEmpty())
                btnCall.setVisibility(View.GONE);

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

            if ("N".equalsIgnoreCase(projTask.isDone())) {
                setHasOptionsMenu(true);
                getActivity().invalidateOptionsMenu();
            } else {
                taskComments.setEnabled(false);
                tlMorePic.setVisibility(View.GONE);
            }
        }
    }

    private void addPicField() {
        picCount += 1;
        try {
            TableRow tr = new TableRow(getActivity());
            TableLayout.LayoutParams tlParams = new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            tlParams.setMargins(0, 20, 0, 20);
            tr.setLayoutParams(tlParams);

            TextView tv = new TextView(new ContextThemeWrapper(getActivity(), R.style.TableLabel));
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins((int) getResources().getDimension(R.dimen.margin_left), 0,
                    (int) getResources().getDimension(R.dimen.margin_right), 0);
            trParams.setMargins(0, 15, 10, 15);
            tv.setLayoutParams(trParams);
            tv.setText(getString(R.string.label_picture, String.valueOf(picCount + 1)));

            taskPictures[picCount] = new ImageView(getActivity());
            TableRow.LayoutParams params = new TableRow.LayoutParams((int) getResources().getDimension(R.dimen.dimen_small_pic), (int) getResources().getDimension(R.dimen.dimen_small_pic));
            params.setMargins(20, 15, 0, 15);
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

    private List<SpinnerPair> getUserList() {
        PandoraContext globalVar = ((PandoraMain)getActivity()).getGlobalVariable();
        if (globalVar != null) {
            Bundle input = new Bundle();
            String adUserID = globalVar.getAd_user_id();
            String adClientID = globalVar.getAd_client_id();
            if (adUserID != null) {
                input.putString(PBSTaskController.ARG_AD_USER_ID, adUserID);
                input.putString(PBSTaskController.ARG_AD_CLIENT_ID, adClientID);
                Bundle result = taskCont.triggerEvent(PBSTaskController.GET_USERS_EVENT,
                        input, new Bundle(), null);
                return result.getParcelableArrayList(PBSTaskController.ARG_USERS);
            } else {
                PandoraHelper.showErrorMessage(getActivity(), getString(R.string.text_projectloc_na));
            }
        }
        return null;
    }

    private void updateProj() {
        if (taskDesc.getText().toString().trim().isEmpty()
                || taskEquipment.getText().toString().trim().isEmpty()
                || taskContact.getText().toString().trim().isEmpty()
                || taskContactNo.getText().toString().trim().isEmpty()) {
            String fields = "";
            if (taskDesc.getText().toString().trim().isEmpty())
                fields += getString(R.string.label_projdesc) + ", ";
            if (taskEquipment.getText().toString().trim().isEmpty())
                fields += getString(R.string.label_taskequip) + ", ";
            if (taskContact.getText().toString().trim().isEmpty())
                fields += getString(R.string.label_taskcontact) + ", ";
            if (taskContactNo.getText().toString().trim().isEmpty())
                fields += getString(R.string.label_taskcontactno) + ", ";
            PandoraHelper.showWarningMessage(getActivity(),
                    getString(R.string.please_fill_in_fields, fields.substring(0, fields.length() - 2)));
            return;
        }

        if (taskDesc.getText().toString().equals(projTask.getDescription() == null ? "" : PandoraHelper.parseNewLine(projTask.getDescription()))
                && taskEquipment.getText().toString().equals(projTask.getEquipment() == null ? "" : PandoraHelper.parseNewLine(projTask.getEquipment()))
                && taskContact.getText().toString().equals(projTask.getContact() == null ? "" : projTask.getContact())
                && taskContactNo.getText().toString().equals(projTask.getContactNo() == null ? "" : projTask.getContactNo())
                && assignToPair.getKey().equals(String.valueOf(projTask.getAssignedTo()))
                && (projTask.getSecAssignedTo() == 0 || secAssignToPair.getKey().equals(String.valueOf(projTask.getSecAssignedTo())))) {
            changeToolbarButtons(false);
            return;
        }

        PandoraHelper.hideSoftKeyboard(getActivity());
        MProjectTask task = new MProjectTask();
        task.set_ID(projTask.get_ID());

        if (!assignToPair.getKey().equals(String.valueOf(projTask.getAssignedTo())))
            task.setAssignedTo(Integer.parseInt(assignToPair.getKey()));

        if (!secAssignToPair.getKey().equals(String.valueOf(projTask.getSecAssignedTo())))
            task.setSecAssignedTo(Integer.parseInt(secAssignToPair.getKey()));

        if (!taskDesc.getText().toString().equals(PandoraHelper.parseNewLine(projTask.getDescription())))
            task.setDescription(taskDesc.getText().toString());

        if (!taskEquipment.getText().toString().equals(PandoraHelper.parseNewLine(projTask.getEquipment())))
            task.setEquipment(taskEquipment.getText().toString());

        if (!taskContact.getText().toString().equals(projTask.getContact()))
            task.setContact(taskContact.getText().toString());

        if (!taskContactNo.getText().toString().equals(projTask.getContactNo()))
            task.setContactNo(taskContactNo.getText().toString());

        PandoraContext globalVar = ((PandoraMain) getActivity()).getGlobalVariable();
        Bundle input = new Bundle();
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
        input.putSerializable(PBSTaskController.ARG_PROJTASK, task);

        new AsyncTask<Bundle, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle output = new Bundle();
                output = taskCont.triggerEvent(PBSTaskController.UPDATE_TASK_EVENT, params[0], output, null);
                return output;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (PandoraConstant.RESULT.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    changeToolbarButtons(false);
                    if (!assignToPair.getKey().equals(String.valueOf(projTask.getAssignedTo())))
                        projTask.setAssignedTo(Integer.parseInt(assignToPair.getKey()));

                    if (!secAssignToPair.getKey().equals(String.valueOf(projTask.getSecAssignedTo())))
                        projTask.setSecAssignedTo(Integer.parseInt(secAssignToPair.getKey()));

                    if (!taskDesc.getText().toString().equals(PandoraHelper.parseNewLine(projTask.getDescription())))
                        projTask.setDescription(taskDesc.getText().toString());

                    if (!taskEquipment.getText().toString().equals(PandoraHelper.parseNewLine(projTask.getEquipment())))
                        projTask.setEquipment(taskEquipment.getText().toString());

                    if (!taskContact.getText().toString().equals(projTask.getContact()))
                        projTask.setContact(taskContact.getText().toString());

                    if (!taskContactNo.getText().toString().equals(projTask.getContactNo()))
                        projTask.setContactNo(taskContactNo.getText().toString());

                    PandoraHelper.showMessage(getActivity(),
                            result.getString(PandoraConstant.RESULT));
                } else {
                    PandoraHelper.showMessage(getActivity(),
                            result.getString(PandoraConstant.ERROR));
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    private void completeProj() {
        if (taskComments.getText() == null || taskComments.getText().toString().isEmpty()){
            PandoraHelper.showWarningMessage(getActivity(),
                    getString(R.string.please_fill_in_fields, getString(R.string.label_comment)));
            return;
        }

        PandoraContext globalVar = ((PandoraMain) getActivity()).getGlobalVariable();
        Bundle input = new Bundle();
        input.putString(PBSServerConst.PARAM_URL, globalVar.getServer_url());
//        input.putString(taskCont.ARG_PROJLOC_ID, globalVar.getC_projectlocation_id());

        Bundle locResult = taskCont.triggerEvent(PBSTaskController.GET_PROJECTLOCATIONS_EVENT,
                input, new Bundle(), null);
        List<SpinnerPair> projLoc = locResult.getParcelableArrayList(PBSTaskController.ARG_PROJECTLOCATIONS);
        for(int i = 0; i < projLoc.size(); i++) {
            if(projLoc.get(i).getValue().equalsIgnoreCase(projTask.getProjLocName()))
                input.putString(PBSTaskController.ARG_PROJLOC_ID, projLoc.get(i).getKey());
        }

        input.putString(PBSTaskController.ARG_TASK_ID,String.valueOf(projTask.get_ID()));
        input.putString(PBSTaskController.ARG_TASK_UUID, _UUID);
        input.putString(PBSTaskController.ARG_COMMENTS, taskComments.getText().toString());
        input.putString(PBSTaskController.ARG_DUEDATE, taskDueDate.getText().toString());
        for (int i = 0; i < taskPictures.length; i++) {
            if (taskPictures[i] == null || taskPictures[i].getTag(R.string.tag_imageview_path) == null || ((String) taskPictures[i].getTag(R.string.tag_imageview_path)).isEmpty())
                break;
            input.putString(PBSTaskController.ARG_TASKPIC + (i + 1), (String) taskPictures[i].getTag(R.string.tag_imageview_path));
        }

        PandoraHelper.hideSoftKeyboard(getActivity());
        Fragment fragment = new ProjTaskSignFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.setArguments(input);
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String picturePath = CameraUtil.getPicPath(context, data);

            if (picturePath != null) {
//                if (!picturePath.endsWith(".jpg") && !picturePath.endsWith(".jpg"))
//                    picturePath += ".jpg";

                CameraUtil.handleBigCameraPhoto(taskPictures[requestCode], picturePath, context);
                context.mCurrentPhotoPath = null;
            }
        }
    }
}
