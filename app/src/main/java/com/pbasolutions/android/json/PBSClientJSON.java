package com.pbasolutions.android.json;

import java.io.Serializable;

/**
 * Created by pbadell on 7/31/15.
 */
public class PBSClientJSON implements Serializable {
    /**
     * Client AD_Client_ID.
     */
    private String AD_Client_ID;
    /**
     * Client Name.
     */
    private String Name;
    /**
     *
     * @return
     */
    public String getAD_Client_ID() {
        return AD_Client_ID;
    }
    /**
     *
     * @param AD_Client_ID
     */
    public void setAD_Client_ID(String AD_Client_ID) {
        this.AD_Client_ID = AD_Client_ID;
    }
    /**
     *
     * @return
     */
    public String getName() {
        return Name;
    }
    /**
     *
     * @param name
     */
    public void setName(String name) {
        Name = name;
    }
}
