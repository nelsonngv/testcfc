package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 12/15/15.
 */
public class MDeploy extends PBSJson {
    public static final String TABLENAME = "";
    public static final String DEPLOYMENT_DATE_COL = "Deployment_Date";

    private String Deployment_Date;
    private int HR_Shift_ID;
    private int C_ProjectLocation_ID;
    private int C_BPartner_IDs[];
    private String EmployeesName;
    private String ProjectLocationName;
    private String HRShiftName;
    private String HRShiftTimeFrom;
    private String HRShiftTimeTo;

    public String getDeployment_Date() {
        return Deployment_Date;
    }

    public void setDeployment_Date(String deployment_Date) {
        Deployment_Date = deployment_Date;
    }

    public int getHR_Shift_ID() {
        return HR_Shift_ID;
    }

    public void setHR_Shift_ID(int HR_Shift_ID) {
        this.HR_Shift_ID = HR_Shift_ID;
    }

    public int getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(int c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public int[] getC_BPartner_IDs() {
        return C_BPartner_IDs;
    }

    public void setC_BPartner_IDs(int[] c_BPartner_IDs) {
        C_BPartner_IDs = c_BPartner_IDs;
    }

    public String getEmployeesName() {
        return EmployeesName;
    }

    public void setEmployeesName(String employeesName) {
        EmployeesName = employeesName;
    }

    public String getProjectLocationName() {
        return ProjectLocationName;
    }

    public void setProjectLocationName(String projectLocationName) {
        ProjectLocationName = projectLocationName;
    }

    public String getHRShiftName() {
        return HRShiftName;
    }

    public void setHRShiftName(String HRShiftName) {
        this.HRShiftName = HRShiftName;
    }

    public String getHRShiftTimeFrom() {
        return HRShiftTimeFrom;
    }

    public void setHRShiftTimeFrom(String HRShiftTimeFrom) {
        this.HRShiftTimeFrom = HRShiftTimeFrom;
    }

    public String getHRShiftTimeTo() {
        return HRShiftTimeTo;
    }

    public void setHRShiftTimeTo(String HRShiftTimeTo) {
        this.HRShiftTimeTo = HRShiftTimeTo;
    }
}
