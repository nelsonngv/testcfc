package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendance extends PBSJson {
    public static final String TABLENAME = "M_Attendance";
    public static final String M_ATTENDANCE_ID_COL = "M_Attendance_ID";
    public static final String M_ATTENDANCE_UUID_COL = "M_Attendance_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String HR_SHIFT_ID_COL = "HR_Shift_ID";
    public static final String HR_SHIFT_UUID_COL = "HR_Shift_UUID";
    public static final String DEPLOYMENT_DATE_COL = "DeploymentDate";

    private String DeploymentDate;
    private String M_Attendance_ID;
    private String M_Attendance_UUID;
    private String C_ProjectLocation_ID;
    private String C_ProjectLocation_UUID;
    private String HR_Shift_ID;
    private String HR_Shift_UUID;

    MAttendanceLine[] Lines;

    public MAttendanceLine[] getLines() {
        return Lines;
    }

    public void setLines(MAttendanceLine[] lines) {
        Lines = lines;
    }


    public String getM_Attendance_ID() {
        return M_Attendance_ID;
    }

    public void setM_Attendance_ID(String m_Attendance_ID) {
        M_Attendance_ID = m_Attendance_ID;
    }

    public String getM_Attendance_UUID() {
        return M_Attendance_UUID;
    }

    public void setM_Attendance_UUID(String m_Attendance_UUID) {
        M_Attendance_UUID = m_Attendance_UUID;
    }

    public String getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(String c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getC_ProjectLocation_UUID() {
        return C_ProjectLocation_UUID;
    }

    public void setC_ProjectLocation_UUID(String c_ProjectLocation_UUID) {
        C_ProjectLocation_UUID = c_ProjectLocation_UUID;
    }

    public String getHR_Shift_ID() {
        return HR_Shift_ID;
    }

    public void setHR_Shift_ID(String HR_Shift_ID) {
        this.HR_Shift_ID = HR_Shift_ID;
    }

    public String getHR_Shift_UUID() {
        return HR_Shift_UUID;
    }

    public void setHR_Shift_UUID(String HR_Shift_UUID) {
        this.HR_Shift_UUID = HR_Shift_UUID;
    }

    public String getDeploymentDate() {
        return DeploymentDate;
    }

    public void setDeploymentDate(String deploymentDate) {
        DeploymentDate = deploymentDate;
    }
}
