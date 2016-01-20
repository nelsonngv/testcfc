package com.pbasolutions.android.model;

import android.databinding.BaseObservable;

import com.pbasolutions.android.json.PBSJson;

import java.io.Serializable;

/**
 * Created by pbadell on 10/8/15.
 */
public class MNote extends PBSJson {
    private String _UUID = "";
    private String date;
    private String Time;
    private String textMsgs;
    private String sender;
    private String AD_NOTE_ID;
    private String AD_USER_ID;
    private String AD_USER_UUID;


    public static String AD_NOTE_ID_COL = "AD_Note_ID";
    public static String AD_NOTE_UUID_COL = "AD_Note_UUID";
    public static String AD_USER_UUID_COL = "AD_User_UUID";
    public static String DATE_COL = "Date";
    public static String SENDER_COL = "Sender";
    public static String MESSAGE_COL = "Message";
    public static final String itemName = "Note";

    public String getTextMsgs() {
        return textMsgs;
    }

    public void setTextMsgs(String textMsgs) {
        this.textMsgs = textMsgs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    }

    @Override
    public int get_ID() {
        return 0;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAD_NOTE_ID() {
        return AD_NOTE_ID;
    }

    public void setAD_NOTE_ID(String AD_NOTE_ID) {
        this.AD_NOTE_ID = AD_NOTE_ID;
    }

    public String getAD_USER_UUID() {
        return AD_USER_UUID;
    }

    public void setAD_USER_UUID(String AD_USER_UUID) {
        this.AD_USER_UUID = AD_USER_UUID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setIsSelected(boolean isSelected) {
//        this.isSelected = isSelected;
//    }
}
