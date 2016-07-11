package com.pbasolutions.android.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.pbasolutions.android.json.IPBSJson;
import com.pbasolutions.android.json.PBSJson;

import java.io.Serializable;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendanceLine  implements IPBSJson, IModel, Serializable {
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

    private String C_BPartner_ID; // C_BPartner_ID
    private transient String C_BPartner_Name;
    private String CheckInDate;
    private String CheckOutDate;
    private String IsAbsent;
    private String Comments;
    private String HR_LeaveType_ID;
    private transient String HR_LeaveType_Name;

    private transient boolean isSelected = false;
    transient String _UUID;
    transient int _ID;

    public String getC_BPartner_ID() {
        return C_BPartner_ID;
    }

    public void setC_BPartner_ID(String c_BPartner_ID) {
        C_BPartner_ID = c_BPartner_ID;
    }

    public String getIsAbsent() {
        return IsAbsent;
    }

    public void setIsAbsent(String isAbsent) {
        IsAbsent = isAbsent;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comment) {
        Comments = comment;
    }

    public String getHR_LeaveType_ID() {
        return HR_LeaveType_ID;
    }

    public void setHR_LeaveType_ID(String HR_LeaveType_ID) {
        this.HR_LeaveType_ID = HR_LeaveType_ID;
    }

    public String getCheckInDate() {
        return CheckInDate;
    }

    public void setCheckInDate(String checkInDate) {
        CheckInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return CheckOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        CheckOutDate = checkOutDate;
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

    public void prepareForGson() {
        if (IsAbsent.equalsIgnoreCase("Y")) {
            CheckInDate = null;
            CheckOutDate = null;
        }
        else {
            IsAbsent = null;
            HR_LeaveType_ID = null;
        }
    }
}
