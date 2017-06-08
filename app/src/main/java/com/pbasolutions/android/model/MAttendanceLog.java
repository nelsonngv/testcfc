package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendanceLog extends PBSJson {
    public static final String TABLENAME = "HR_AttendanceLog";
    public static final String HR_ATTENDANCELOG_ID_COL = "HR_AttendanceLog_ID";
    public static final String HR_ATTENDANCELOG_UUID_COL = "HR_AttendanceLog_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String C_BPARTNER_UUID_COL = "C_BPartner_UUID";
    public static final String CHECKINOUTDATE_COL = "CheckInOutDate";
    public static final String CHECKINOUTTYPE_COL = "CheckInOutType";
    public static final String LONGITUDE_COL = "Longitude";
    public static final String LATITUDE_COL = "Latitude";
    public static final String ATTACHMENT_IMAGE_COL = "Attachment_Image";

    private String HR_AttendanceLog_ID;
    private String HR_AttendanceLog_UUID;
    private int C_ProjectLocation_ID;
    private String C_ProjectLocation_UUID;
    private String C_BPartner_UUID;
    private String CheckInOutDate;
    private String CheckInOutType;
    private String Longitude;
    private String Latitude;
    private String Attachment_Image;


    public String getHR_AttendanceLog_ID() {
        return HR_AttendanceLog_ID;
    }

    public void setHR_AttendanceLog_ID(String HR_AttendanceLog_ID) {
        this.HR_AttendanceLog_ID = HR_AttendanceLog_ID;
    }

    public String getHR_AttendanceLog_UUID() {
        return HR_AttendanceLog_UUID;
    }

    public void setHR_AttendanceLog_UUID(String HR_AttendanceLog_UUID) {
        this.HR_AttendanceLog_UUID = HR_AttendanceLog_UUID;
    }

    public int getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(int c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getC_ProjectLocation_UUID() {
        return C_ProjectLocation_UUID;
    }

    public void setC_ProjectLocation_UUID(String c_ProjectLocation_UUID) {
        C_ProjectLocation_UUID = c_ProjectLocation_UUID;
    }

    public String getC_BPartner_UUID() {
        return C_BPartner_UUID;
    }

    public void setC_BPartner_UUID(String c_BPartner_UUID) {
        C_BPartner_UUID = c_BPartner_UUID;
    }

    public String getCheckInOutDate() {
        return CheckInOutDate;
    }

    public void setCheckInOutDate(String checkInOutDate) {
        CheckInOutDate = checkInOutDate;
    }

    public String getCheckInOutType() {
        return CheckInOutType;
    }

    public void setCheckInOutType(String checkInOutType) {
        CheckInOutType = checkInOutType;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAttachment_Image() {
        return Attachment_Image;
    }

    public void setAttachment_Image(String attachment_Image) {
        Attachment_Image = attachment_Image;
    }
}
