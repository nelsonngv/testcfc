package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MAttendance extends PBSJson {
    public static final String TABLENAME = "";
    public static final String DEPLOYMENT_DATE_COL = "Deployment_Date";

    private String Deployment_Date;
    private int HR_Shift_ID;
    private int C_ProjectLocation_ID;
}
