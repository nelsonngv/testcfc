package com.pbasolutions.android.model;

import android.content.ContentResolver;
import android.database.Cursor;

import com.pbasolutions.android.json.PBSJson;

import java.util.Date;

/**
 * Created by pbadell on 12/15/15.
 */
public class MShift extends PBSJson {

    public static final String TABLENAME = "HR_Shift";
    public static final String TIMEFROM_COL = "TimeFrom";
    public static final String TIMETO_COL = "TimeTo";
    public static final String NAME_COL = "Name";
    public static final String DESCRIPTION_COL = "Description";
    public static final String HR_SHIFT_ID_COL = "HR_Shift_ID";
    public static final String HR_SHIFT_UUID_COL = "HR_Shift_UUID";

    public static final String[] projection = {
            HR_SHIFT_UUID_COL,
            HR_SHIFT_ID_COL,
            DESCRIPTION_COL,
            NAME_COL,
            TIMEFROM_COL,
            TIMETO_COL

    };

    private Date TimeFrom;
    private Date TimeTo;
    private String Name;
    private String Description;
    private int HR_Shift_ID;
    private String HR_Shift_UUID;

    public MShift() {
    }

    public MShift(int HR_Shift_ID) {
        this.HR_Shift_ID = HR_Shift_ID;
    }

    public MShift(String HR_Shift_UUID) {
        this.HR_Shift_UUID = HR_Shift_UUID;
    }

    public String getHR_Shift_UUID() {
        return HR_Shift_UUID;
    }

    public void setHR_Shift_UUID(String HR_Shift_UUID) {
        this.HR_Shift_UUID = HR_Shift_UUID;
    }

    public int getHR_Shift_ID() {
        return HR_Shift_ID;
    }

    public void setHR_Shift_ID(int HR_Shift_ID) {
        this.HR_Shift_ID = HR_Shift_ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getTimeTo() {
        return TimeTo;
    }

    public void setTimeTo(Date timeTo) {
        TimeTo = timeTo;
    }

    public Date getTimeFrom() {
        return TimeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        TimeFrom = timeFrom;
    }

    private MShift populateShift(Cursor cursor) {
        MShift shift = new MShift();
        for (int x=0; x<cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue= cursor.getString(x);
                if (HR_SHIFT_ID_COL.equalsIgnoreCase(columnName)){
                    shift.set_ID(cursor.getInt(x));
                    shift.setHR_Shift_ID(cursor.getInt(x));
                } else if (HR_SHIFT_UUID_COL.equalsIgnoreCase(columnName)){
                    shift.setHR_Shift_UUID(rowValue);
                } else if (NAME_COL.equalsIgnoreCase(columnName)){
                    shift.setName(rowValue);
                } else if (DESCRIPTION_COL.equalsIgnoreCase(columnName)){
                    shift.setDescription(rowValue);
                } else if (TIMEFROM_COL.equalsIgnoreCase(columnName)) {
                    shift.getTimeFrom();
                } else if (TIMETO_COL.equalsIgnoreCase(columnName)) {
                    shift.getTimeTo();
                }
        }
        return shift;
    }

    public MShift getShift(ContentResolver cr, boolean isUUID){
        String selection;
        String selectionArgs[] ={};
        if (isUUID) {
            selection = HR_SHIFT_UUID_COL + "=?";
            selectionArgs[0]= HR_Shift_UUID;
        } else {
            selection = HR_SHIFT_ID_COL + "=?";
            selectionArgs[0]= String.valueOf(HR_Shift_ID);
        }
        return queryShift(selection, selectionArgs, cr);
    }

    private MShift queryShift(String selection, String[] selectionArgs, ContentResolver cr){
        MShift shift = null;
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(TABLENAME), projection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                shift =  populateShift(cursor);
            } while (cursor.moveToNext());
        }
        return shift;
    }
}


