package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendanceLog extends PBSJson {
    public static final String TABLENAME = "HR_EntryLog";
    public static final String HR_ENTRYLOG_ID_COL = "HR_EntryLog_ID";
    public static final String HR_ENTRYLOG_UUID_COL = "HR_EntryLog_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String C_BPARTNER_UUID_COL = "C_BPartner_UUID";
    public static final String DATETRX_COL = "DateTrx";
    public static final String LONGITUDE_COL = "Longitude";
    public static final String LATITUDE_COL = "Latitude";
    public static final String ATTACHMENT_IMAGE_COL = "Attachment_Image";
    public static final String NFCTAG_COL = "NfcTag";
    public static final String DEVICENO_COL = "DeviceNo";

    private String HR_EntryLog_ID;
    private String HR_EntryLog_UUID;
    private int C_ProjectLocation_ID;
    private String C_ProjectLocation_UUID;
    private String C_BPartner_UUID;
    private String DateTrx;
    private String Longitude;
    private String Latitude;
    private String Attachment_Image;


    public String getHR_EntryLog_ID() {
        return HR_EntryLog_ID;
    }

    public void setHR_EntryLog_ID(String HR_EntryLog_ID) {
        this.HR_EntryLog_ID = HR_EntryLog_ID;
    }

    public String getHR_EntryLog_UUID() {
        return HR_EntryLog_UUID;
    }

    public void setHR_EntryLog_UUID(String HR_EntryLog_UUID) {
        this.HR_EntryLog_UUID = HR_EntryLog_UUID;
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

    public String getDateTrx() {
        return DateTrx;
    }

    public void setDateTrx(String dateTrx) {
        DateTrx = dateTrx;
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
