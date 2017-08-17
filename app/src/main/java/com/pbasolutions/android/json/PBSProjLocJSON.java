package com.pbasolutions.android.json;

/**
 * Created by pbadell on 9/15/15.
 */
public class PBSProjLocJSON extends PBSJson {

    /**
     * Client C_ProjectLocation_ID.
     */
    private String C_ProjectLocation_ID;
    /**
     * Name.
     */
    String Name;
    /**
     *
     */
    String C_ProjectLocation_UUID;
    /**
     *
     * @return
     */
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(String ID) {
        this.C_ProjectLocation_ID = ID;
    }

    public String getC_ProjectLocation_UUID() {
        return C_ProjectLocation_UUID;
    }

    public void setC_ProjectLocation_UUID(String c_ProjectLocation_UUID) {
        C_ProjectLocation_UUID = c_ProjectLocation_UUID;
    }


}
