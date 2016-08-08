package com.pbasolutions.android.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.pbasolutions.android.json.IPBSJson;
import com.pbasolutions.android.json.PBSJson;

import java.io.Serializable;

/**
 * Created by tinker on 8/7/16.
 */
public class MAttendanceSearchItem  extends PBSJson {
    public static final String TABLENAME = "M_AttendanceLine";

    public static final String M_ATTENDANCELINE_UUID_COL = "M_ATTENDANCELINE_UUID";

    public static final String C_BPARTNER_ID_COL = "C_BPARTNER_ID";
    public static final String C_BPARTNER_UUID_COL = "C_BPARTNER_UUID";
    public static final String ISABSENT_COL = "ISABSENT";
    public static final String HR_LEAVETYPE_ID_COL = "HR_LEAVETYPE_ID";
    public static final String COMMENT_COL = "COMMENT";

//    public static final String ISNOSHOW_COL = "ISNOSHOW";
    public static final String CHECKIN_COL = "CHECKINDATE";
    public static final String CHECKOUT_COL = "CHECKOUTDATE";

    private String Status;
    private int HR_LeaveType_ID;
    private String CheckOut;
    private int C_BPartner_ID;
    private String CheckIn;
    private String HR_ResourceAllocation_ID;

    private transient String HR_LeaveType_Name;
    private transient String C_BPartner_Name;

    private transient boolean isSelected = false;
    transient String _UUID;
    transient int _ID;



    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public int get_ID() {
        return _ID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getHR_LeaveType_ID() {
        return HR_LeaveType_ID;
    }

    public void setHR_LeaveType_ID(int HR_LeaveType_ID) {
        this.HR_LeaveType_ID = HR_LeaveType_ID;
    }

    public String getCheckOut() {
        return CheckOut;
    }

    public void setCheckOut(String checkOut) {
        CheckOut = checkOut;
    }

    public int getC_BPartner_ID() {
        return C_BPartner_ID;
    }

    public void setC_BPartner_ID(int c_BPartner_ID) {
        C_BPartner_ID = c_BPartner_ID;
    }

    public String getCheckIn() {
        return CheckIn;
    }

    public void setCheckIn(String checkIn) {
        CheckIn = checkIn;
    }

    public String getHR_ResourceAllocation_ID() {
        return HR_ResourceAllocation_ID;
    }

    public void setHR_ResourceAllocation_ID(String HR_ResourceAllocation_ID) {
        this.HR_ResourceAllocation_ID = HR_ResourceAllocation_ID;
    }

    public String getHR_LeaveType_Name() {
        return HR_LeaveType_Name;
    }

    public void setHR_LeaveType_Name(String HR_LeaveType_Name) {
        this.HR_LeaveType_Name = HR_LeaveType_Name;
    }

    public String getC_BPartner_Name() {
        return C_BPartner_Name;
    }

    public void setC_BPartner_Name(String c_BPartner_Name) {
        C_BPartner_Name = c_BPartner_Name;
    }
}
