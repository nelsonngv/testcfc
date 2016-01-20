package com.pbasolutions.android.json;

/**
 * Created by pbadell on 10/30/15.
 */
public class PBSNoteJSON extends PBSJson {
    private String AD_Note_ID;
    private String AD_User_ID;
    private String Created;
    private String TxtMsg;
    private String CreatedBy;

    public String getAD_Note_ID() {
        return AD_Note_ID;
    }

    public void setAD_Note_ID(String AD_Note_ID) {
        this.AD_Note_ID = AD_Note_ID;
    }

    public String getAD_User_ID() {
        return AD_User_ID;
    }

    public void setAD_User_ID(String AD_User_ID) {
        this.AD_User_ID = AD_User_ID;
    }

    public String getDate() {
        return Created;
    }

    public void setDate(String date) {
        Created = date;
    }

    public String getMessage() {
        return TxtMsg;
    }

    public void setMessage(String message) {
        TxtMsg = message;
    }

    public String getSender() {
        return CreatedBy;
    }

    public void setSender(String sender) {
        CreatedBy = sender;
    }
}
