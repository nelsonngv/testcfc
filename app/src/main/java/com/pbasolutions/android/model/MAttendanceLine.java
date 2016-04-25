package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendanceLine extends PBSJson {
    public static final String TABLENAME = "";
    public static final String DEPLOYMENT_DATE_COL = "Deployment_Date";

    public static String C_BPARTNER_ID = "C_BPartner_ID";
    public static String ISABSENT = "IsAbsent";
    public static String HR_LEAVETYPE_ID = "HR_LeaveType_ID";
    public static String COMMENTS = "Comments";

    public static String ISNOSHOW = "IsNoShow";
    public static String CHECKIN = "CheckIn";
    public static String CHECKOUT = "CheckOut";

    private String     employeeId; // C_BPartner_ID
    private String checkInDate;
    private String checkOutDate;
    public boolean isAbsent;
    private boolean isPresent;
    private String comment;
    private int     leaveType;

    public boolean isAbsent() {
        return isAbsent;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public int getLeaveType() {
        return leaveType;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getComment() {
        return comment;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setIsAbsent(boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }

    public void setLeaveType(int leaveType) {
        this.leaveType = leaveType;
    }
}
