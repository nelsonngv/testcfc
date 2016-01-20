package com.pbasolutions.android.json;

import java.io.Serializable;

/**
 * Created by pbadell on 6/25/15.
 */
public class PBSOrgJSON implements Serializable{
    /**
     * Org ID.
     */
    private String AD_Org_ID;
    /**
     * Client ID.
     */
    private String AD_Client_ID;
    /**
     * Org Name.
     */
    private String OrgName;
    /**
     * Client Name.
     */
    private String ClientName;
    /**
     *
     * @return
     */
    private PBSProjLocJSON ProjectLocation[];

    public PBSProjLocJSON[] getProjectLocation() {
        return ProjectLocation;
    }

    public void setProjectLocation(PBSProjLocJSON[] projectLocation) {
        ProjectLocation = projectLocation;
    }

    public String getAD_Client_ID() {
        return AD_Client_ID;
    }

    public void setAD_Client_ID(String AD_Client_ID) {
        this.AD_Client_ID = AD_Client_ID;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    /**
     * Get Org ID
     * @return
     */
    public String getAD_Org_ID() {
        return AD_Org_ID;
    }
    /**
     * Set Org ID
     * @param iD
     */
    public void setAD_Org_ID(String iD) {
        AD_Org_ID = iD;
    }
    /**
     * Get Org Name.
     * @return
     */
    public String getOrgName() {
        return OrgName;
    }
    /**
     * Set Org Name.
     * @param name
     */
    public void setOrgName(String name) {
        OrgName = name;
    }

}
