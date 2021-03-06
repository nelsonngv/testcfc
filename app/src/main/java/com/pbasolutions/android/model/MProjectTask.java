package com.pbasolutions.android.model;

import android.graphics.Color;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 10/13/15.
 */
public class MProjectTask extends PBSJson {
    transient private String projLocUUID;
    private String Name;
    private int SeqNo;
    private String isDone;
    private String Description;
    private String comments;
    transient private String projLocName;
    private String ATTACHMENT_BEFORETASKPICTURE_1;
    private String ATTACHMENT_TASKPICTURE_1;
    private String ATTACHMENT_TASKPICTURE_2;
    private String ATTACHMENT_TASKPICTURE_3;
    private String ATTACHMENT_TASKPICTURE_4;
    private String ATTACHMENT_TASKPICTURE_5;
    private String created;
    private String createdBy;
    private String Status;
    private String DueDate;
    transient private String assignedToName;
    private int AssignedTo;
    private int C_ProjectLocation_ID;
    private int StatusColor;

    public static final String C_PROJECTTASK_UUID_COL = "C_ProjectTask_UUID";
    public static final String C_PROJECTTASK_ID_COL = "C_ProjectTask_ID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String NAME_COL = "Name";
    public static final String ISDONE_COL = "IsDone";
    public static final String ASSIGNEDTO_COL = "AssignedTo";
    public static final String PRIORITY_COL = "Priority";
    public static final String DESCRIPTION_COL = "Description";
    public static final String COMMENTS_COL = "Comments";
    public static final String CREATED_COL = "Created";
    public static final String CREATEDBY_COL = "CreatedBy";
    public static final String DUEDATE_COL = "DueDate";
    public static final String TABLENAME = "C_ProjectTask";


    public static final String ATTACHMENT_BEFORETASKPICTURE_1_COL = "ATTACHMENT_BEFORETASKPICTURE_1";
    public static final String ATTACHMENT_TASKPICTURE_1_COL = "ATTACHMENT_TASKPICTURE_1";
    public static final String ATTACHMENT_TASKPICTURE_2_COL = "ATTACHMENT_TASKPICTURE_2";
    public static final String ATTACHMENT_TASKPICTURE_3_COL = "ATTACHMENT_TASKPICTURE_3";
    public static final String ATTACHMENT_TASKPICTURE_4_COL = "ATTACHMENT_TASKPICTURE_4";
    public static final String ATTACHMENT_TASKPICTURE_5_COL = "ATTACHMENT_TASKPICTURE_5";


    public String getProjLocUUID() {
        return projLocUUID;
    }

    public void setProjLocUUID(String projLocUUID) {
        this.projLocUUID = projLocUUID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getPriority() {
        return SeqNo;
    }

    public void setPriority(int nSeqNo) {
        this.SeqNo = nSeqNo;
    }

    public String isDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getATTACHMENT_BEFORETASKPICTURE_1() { return ATTACHMENT_BEFORETASKPICTURE_1; }

    public void setATTACHMENT_BEFORETASKPICTURE_1(String ATTACHMENT_BEFORETASKPICTURE_1) {
        this.ATTACHMENT_BEFORETASKPICTURE_1 = ATTACHMENT_BEFORETASKPICTURE_1;
    }

    public String getATTACHMENT_TASKPICTURE_1() {
        return ATTACHMENT_TASKPICTURE_1;
    }

    public void setATTACHMENT_TASKPICTURE_1(String ATTACHMENT_TASKPICTURE_1) {
        this.ATTACHMENT_TASKPICTURE_1 = ATTACHMENT_TASKPICTURE_1;
    }

    public String getATTACHMENT_TASKPICTURE_2() {
        return ATTACHMENT_TASKPICTURE_2;
    }

    public void setATTACHMENT_TASKPICTURE_2(String ATTACHMENT_TASKPICTURE_2) {
        this.ATTACHMENT_TASKPICTURE_2 = ATTACHMENT_TASKPICTURE_2;
    }

    public String getATTACHMENT_TASKPICTURE_3() {
        return ATTACHMENT_TASKPICTURE_3;
    }

    public void setATTACHMENT_TASKPICTURE_3(String ATTACHMENT_TASKPICTURE_3) {
        this.ATTACHMENT_TASKPICTURE_3 = ATTACHMENT_TASKPICTURE_3;
    }

    public String getATTACHMENT_TASKPICTURE_4() {
        return ATTACHMENT_TASKPICTURE_4;
    }

    public void setATTACHMENT_TASKPICTURE_4(String ATTACHMENT_TASKPICTURE_4) {
        this.ATTACHMENT_TASKPICTURE_4 = ATTACHMENT_TASKPICTURE_4;
    }

    public String getATTACHMENT_TASKPICTURE_5() {
        return ATTACHMENT_TASKPICTURE_5;
    }

    public void setATTACHMENT_TASKPICTURE_5(String ATTACHMENT_TASKPICTURE_5) {
        this.ATTACHMENT_TASKPICTURE_5 = ATTACHMENT_TASKPICTURE_5;
    }

    public String getProjLocName() {
        return projLocName;
    }

    public void setProjLocName(String projLocName) {
        this.projLocName = projLocName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return Status;
    }


    public void setStatus(String status) {
        Status = status;
        switch(Status){
            case "Incomplete": {
                setStatusColor(Color.RED);
                break;
            }

            case "Completed": {
                setStatusColor(Color.GREEN);
                break;
            }
            default:
        }
    }

    public int getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(int statusColor) {
        StatusColor = statusColor;
    }



    public int getAssignedTo() {
        return AssignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        AssignedTo = assignedTo;
    }

    public int getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(int c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        this.DueDate = dueDate;
    }
}
