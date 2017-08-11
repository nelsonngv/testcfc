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
    private String ATTACHMENT_TASKPICTURE_6;
    private String ATTACHMENT_TASKPICTURE_7;
    private String ATTACHMENT_TASKPICTURE_8;
    private String ATTACHMENT_TASKPICTURE_9;
    private String ATTACHMENT_TASKPICTURE_10;
    private String ATTACHMENT_TASKPICTURE_11;
    private String ATTACHMENT_TASKPICTURE_12;
    private String ATTACHMENT_TASKPICTURE_13;
    private String ATTACHMENT_TASKPICTURE_14;
    private String ATTACHMENT_TASKPICTURE_15;
    private String ATTACHMENT_TASKPICTURE_16;
    private String ATTACHMENT_TASKPICTURE_17;
    private String ATTACHMENT_TASKPICTURE_18;
    private String ATTACHMENT_TASKPICTURE_19;
    private String ATTACHMENT_TASKPICTURE_20;
    private String ATTACHMENT_TASKPICTURE_21;
    private String ATTACHMENT_TASKPICTURE_22;
    private String ATTACHMENT_TASKPICTURE_23;
    private String ATTACHMENT_TASKPICTURE_24;
    private String ATTACHMENT_TASKPICTURE_25;
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
    public static final String ATTACHMENT_TASKPICTURE_6_COL = "ATTACHMENT_TASKPICTURE_6";
    public static final String ATTACHMENT_TASKPICTURE_7_COL = "ATTACHMENT_TASKPICTURE_7";
    public static final String ATTACHMENT_TASKPICTURE_8_COL = "ATTACHMENT_TASKPICTURE_8";
    public static final String ATTACHMENT_TASKPICTURE_9_COL = "ATTACHMENT_TASKPICTURE_9";
    public static final String ATTACHMENT_TASKPICTURE_10_COL = "ATTACHMENT_TASKPICTURE_10";
    public static final String ATTACHMENT_TASKPICTURE_11_COL = "ATTACHMENT_TASKPICTURE_11";
    public static final String ATTACHMENT_TASKPICTURE_12_COL = "ATTACHMENT_TASKPICTURE_12";
    public static final String ATTACHMENT_TASKPICTURE_13_COL = "ATTACHMENT_TASKPICTURE_13";
    public static final String ATTACHMENT_TASKPICTURE_14_COL = "ATTACHMENT_TASKPICTURE_14";
    public static final String ATTACHMENT_TASKPICTURE_15_COL = "ATTACHMENT_TASKPICTURE_15";
    public static final String ATTACHMENT_TASKPICTURE_16_COL = "ATTACHMENT_TASKPICTURE_16";
    public static final String ATTACHMENT_TASKPICTURE_17_COL = "ATTACHMENT_TASKPICTURE_17";
    public static final String ATTACHMENT_TASKPICTURE_18_COL = "ATTACHMENT_TASKPICTURE_18";
    public static final String ATTACHMENT_TASKPICTURE_19_COL = "ATTACHMENT_TASKPICTURE_19";
    public static final String ATTACHMENT_TASKPICTURE_20_COL = "ATTACHMENT_TASKPICTURE_20";
    public static final String ATTACHMENT_TASKPICTURE_21_COL = "ATTACHMENT_TASKPICTURE_21";
    public static final String ATTACHMENT_TASKPICTURE_22_COL = "ATTACHMENT_TASKPICTURE_22";
    public static final String ATTACHMENT_TASKPICTURE_23_COL = "ATTACHMENT_TASKPICTURE_23";
    public static final String ATTACHMENT_TASKPICTURE_24_COL = "ATTACHMENT_TASKPICTURE_24";
    public static final String ATTACHMENT_TASKPICTURE_25_COL = "ATTACHMENT_TASKPICTURE_25";
    public static final String ATTACHMENT_TASKPICTURE_COL = "ATTACHMENT_TASKPICTURE_";
    public static final String ATTACHMENT_PIC = "Attachment_Pic";


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

    public String getATTACHMENT_TASKPICTURE_6() {
        return ATTACHMENT_TASKPICTURE_6;
    }

    public void setATTACHMENT_TASKPICTURE_6(String ATTACHMENT_TASKPICTURE_6) {
        this.ATTACHMENT_TASKPICTURE_6 = ATTACHMENT_TASKPICTURE_6;
    }

    public String getATTACHMENT_TASKPICTURE_7() {
        return ATTACHMENT_TASKPICTURE_7;
    }

    public void setATTACHMENT_TASKPICTURE_7(String ATTACHMENT_TASKPICTURE_7) {
        this.ATTACHMENT_TASKPICTURE_7 = ATTACHMENT_TASKPICTURE_7;
    }

    public String getATTACHMENT_TASKPICTURE_8() {
        return ATTACHMENT_TASKPICTURE_8;
    }

    public void setATTACHMENT_TASKPICTURE_8(String ATTACHMENT_TASKPICTURE_8) {
        this.ATTACHMENT_TASKPICTURE_8 = ATTACHMENT_TASKPICTURE_8;
    }

    public String getATTACHMENT_TASKPICTURE_9() {
        return ATTACHMENT_TASKPICTURE_9;
    }

    public void setATTACHMENT_TASKPICTURE_9(String ATTACHMENT_TASKPICTURE_9) {
        this.ATTACHMENT_TASKPICTURE_9 = ATTACHMENT_TASKPICTURE_9;
    }

    public String getATTACHMENT_TASKPICTURE_10() {
        return ATTACHMENT_TASKPICTURE_10;
    }

    public void setATTACHMENT_TASKPICTURE_10(String ATTACHMENT_TASKPICTURE_10) {
        this.ATTACHMENT_TASKPICTURE_10 = ATTACHMENT_TASKPICTURE_10;
    }

    public String getATTACHMENT_TASKPICTURE_11() {
        return ATTACHMENT_TASKPICTURE_11;
    }

    public void setATTACHMENT_TASKPICTURE_11(String ATTACHMENT_TASKPICTURE_11) {
        this.ATTACHMENT_TASKPICTURE_11 = ATTACHMENT_TASKPICTURE_11;
    }

    public String getATTACHMENT_TASKPICTURE_12() {
        return ATTACHMENT_TASKPICTURE_12;
    }

    public void setATTACHMENT_TASKPICTURE_12(String ATTACHMENT_TASKPICTURE_12) {
        this.ATTACHMENT_TASKPICTURE_12 = ATTACHMENT_TASKPICTURE_12;
    }

    public String getATTACHMENT_TASKPICTURE_13() {
        return ATTACHMENT_TASKPICTURE_13;
    }

    public void setATTACHMENT_TASKPICTURE_13(String ATTACHMENT_TASKPICTURE_13) {
        this.ATTACHMENT_TASKPICTURE_13 = ATTACHMENT_TASKPICTURE_13;
    }

    public String getATTACHMENT_TASKPICTURE_14() {
        return ATTACHMENT_TASKPICTURE_14;
    }

    public void setATTACHMENT_TASKPICTURE_14(String ATTACHMENT_TASKPICTURE_14) {
        this.ATTACHMENT_TASKPICTURE_14 = ATTACHMENT_TASKPICTURE_14;
    }

    public String getATTACHMENT_TASKPICTURE_15() {
        return ATTACHMENT_TASKPICTURE_15;
    }

    public void setATTACHMENT_TASKPICTURE_15(String ATTACHMENT_TASKPICTURE_15) {
        this.ATTACHMENT_TASKPICTURE_15 = ATTACHMENT_TASKPICTURE_15;
    }

    public String getATTACHMENT_TASKPICTURE_16() {
        return ATTACHMENT_TASKPICTURE_16;
    }

    public void setATTACHMENT_TASKPICTURE_16(String ATTACHMENT_TASKPICTURE_16) {
        this.ATTACHMENT_TASKPICTURE_16 = ATTACHMENT_TASKPICTURE_16;
    }

    public String getATTACHMENT_TASKPICTURE_17() {
        return ATTACHMENT_TASKPICTURE_17;
    }

    public void setATTACHMENT_TASKPICTURE_17(String ATTACHMENT_TASKPICTURE_17) {
        this.ATTACHMENT_TASKPICTURE_17 = ATTACHMENT_TASKPICTURE_17;
    }

    public String getATTACHMENT_TASKPICTURE_18() {
        return ATTACHMENT_TASKPICTURE_18;
    }

    public void setATTACHMENT_TASKPICTURE_18(String ATTACHMENT_TASKPICTURE_18) {
        this.ATTACHMENT_TASKPICTURE_18 = ATTACHMENT_TASKPICTURE_18;
    }

    public String getATTACHMENT_TASKPICTURE_19() {
        return ATTACHMENT_TASKPICTURE_19;
    }

    public void setATTACHMENT_TASKPICTURE_19(String ATTACHMENT_TASKPICTURE_19) {
        this.ATTACHMENT_TASKPICTURE_19 = ATTACHMENT_TASKPICTURE_19;
    }

    public String getATTACHMENT_TASKPICTURE_20() {
        return ATTACHMENT_TASKPICTURE_20;
    }

    public void setATTACHMENT_TASKPICTURE_20(String ATTACHMENT_TASKPICTURE_20) {
        this.ATTACHMENT_TASKPICTURE_20 = ATTACHMENT_TASKPICTURE_20;
    }

    public String getATTACHMENT_TASKPICTURE_21() {
        return ATTACHMENT_TASKPICTURE_21;
    }

    public void setATTACHMENT_TASKPICTURE_21(String ATTACHMENT_TASKPICTURE_21) {
        this.ATTACHMENT_TASKPICTURE_21 = ATTACHMENT_TASKPICTURE_21;
    }

    public String getATTACHMENT_TASKPICTURE_22() {
        return ATTACHMENT_TASKPICTURE_22;
    }

    public void setATTACHMENT_TASKPICTURE_22(String ATTACHMENT_TASKPICTURE_22) {
        this.ATTACHMENT_TASKPICTURE_22 = ATTACHMENT_TASKPICTURE_22;
    }

    public String getATTACHMENT_TASKPICTURE_23() {
        return ATTACHMENT_TASKPICTURE_23;
    }

    public void setATTACHMENT_TASKPICTURE_23(String ATTACHMENT_TASKPICTURE_23) {
        this.ATTACHMENT_TASKPICTURE_23 = ATTACHMENT_TASKPICTURE_23;
    }

    public String getATTACHMENT_TASKPICTURE_24() {
        return ATTACHMENT_TASKPICTURE_24;
    }

    public void setATTACHMENT_TASKPICTURE_24(String ATTACHMENT_TASKPICTURE_24) {
        this.ATTACHMENT_TASKPICTURE_24 = ATTACHMENT_TASKPICTURE_24;
    }

    public String getATTACHMENT_TASKPICTURE_25() {
        return ATTACHMENT_TASKPICTURE_25;
    }

    public void setATTACHMENT_TASKPICTURE_25(String ATTACHMENT_TASKPICTURE_25) {
        this.ATTACHMENT_TASKPICTURE_25 = ATTACHMENT_TASKPICTURE_25;
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
