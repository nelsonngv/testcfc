package com.pbasolutions.android.controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.os.Bundle;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.R;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.model.MCheckIn;
import com.pbasolutions.android.model.MCheckPoint;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by pbadell on 7/23/15.
 */
public class PBSCheckpointController extends ContextWrapper implements PBSIController {
    /**
     * Event name : get checkpoint rows.
     */
    public static final String CHECKIN_ROWS_EVENT = "CHECKIN_ROWS_EVENT";
    /**
     * Event name : get checkpoint details event.
     */
    public static final String CHECKIN_DETAILS_EVENT = "CHECKIN_DETAILS_EVENT";
    /**
     * Event name : get checkpoint seq rows.
     */
    public static final String CHECKPOINT_SEQ_ROWS_EVENT = "CHECKPOINT_SEQ_ROWS_EVENT";
    /**
     * Param for row items object.
     */
    public static final String ROW_ITEMS = "ROW_ITEMS";
    /**
     * Param for row items object.
     */
    public static final String ARG_CHECKPOINT_DETAILS = "ARG_CHECKPOINT_DETAILS";
    /**
     * Param for checkin uuid.
     */
    public static final String ARG_CHECKIN_UUID = "ARG_CHECKIN_UUID";
    /**
     * Param for checkin uuid.
     */
    public static final String ARG_CHECKPOINT_SEQ = "ARG_CHECKPOINT_SEQ";
    /**
     * Param project location
     */
    public static final String ARG_PROJECT_LOCATION_JSON = "ARG_PROJECT_LOCATION_JSON";

    /**
     *
     */
    public static final String ARG_PROJECT_LOCATION_UUID = "ARG_PROJECT_LOCATION_UUID";

    /**
     *
     */
    public static final String GET_PROJECT_LOCATION_DATA = "SET_PROJECT_LOCATION_DATA";
    /**
     * Class name tag.
     */
    private static final String TAG = "PBSCheckpointController";

    private Context context;
    /**
     * Constructor.
     * @param base
     */
    public PBSCheckpointController(Context base) {
        super(base);
        context = base;
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle bundle, Bundle resultBundle, Object object) {
        switch (eventName) {
            case CHECKIN_ROWS_EVENT: {
                getCheckinRows(bundle, resultBundle, object);
                break;
            }
            case CHECKIN_DETAILS_EVENT: {
                getCheckinDetails(bundle, resultBundle, object);
                break;
            }
            case CHECKPOINT_SEQ_ROWS_EVENT: {
                getCheckPointSeqRows(bundle, resultBundle, object);
                break;
            }
            case GET_PROJECT_LOCATION_DATA: {
                getCheckPointSeqs(bundle, resultBundle, object);
                break;
            }
            default:
                break;
        }
        return resultBundle;
    }

    private Bundle getCheckPointSeqs(Bundle bundle, Bundle resultBundle, Object object) {
        String projection [] = {ModelConst.C_PROJECTLOCATION_UUID_COL, ModelConst.NAME_COL};
        Cursor cursor = getContentResolver().query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE), projection, null, null, null);
        int numberOfRows = cursor.getCount();
        if (numberOfRows > 0) {
            PBSProjLocJSON[] projLocJSONs = new PBSProjLocJSON[numberOfRows];
            cursor.moveToFirst();
            for (int x = 0; x < numberOfRows; x++) {
                projLocJSONs[x] = new PBSProjLocJSON();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.NAME_COL))
                        projLocJSONs[x].setName(cursor.getString(i));
                    else if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.C_PROJECTLOCATION_UUID_COL))
                        projLocJSONs[x].setC_ProjectLocation_UUID(cursor.getString(i));

                }
                cursor.moveToNext();
            }
            resultBundle.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            resultBundle.putString(PandoraConstant.ERROR, "Please select the project location in defaults setting tab.");
            resultBundle.putSerializable(ARG_PROJECT_LOCATION_JSON, projLocJSONs);

        } else {
            resultBundle.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            resultBundle.putString(PandoraConstant.ERROR, "This app has not run initial sync yet. Please sync now.");
        }
        return resultBundle;
    }

    /**
     * Get all the checkin rows.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    public Bundle getCheckinRows(Bundle bundle, Bundle resultBundle, Object object) {
        Cursor cursor = getContentResolver().query(ModelConst.uriCustomBuilder(ModelConst.CHECKIN_JOIN_CHECKPOINT_TABLE), null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ObservableArrayList<MCheckIn> checkInList = new ObservableArrayList<>();
            //multiple rows. iterate the cursor.
            for (int j = 0; j < cursor.getCount(); j++) {
                MCheckIn checkIn = new MCheckIn();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equalsIgnoreCase("name")) {
                        checkIn.setProjectLocation(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("datetrx")) {
                        checkIn.setDate(PandoraHelper.parseToDisplaySDate(cursor.getString(i), "dd-MM-yyyy HH:mm:ss", TimeZone.getDefault()));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("checkin_desc")) {
                        checkIn.setComment(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("m_checkin_uuid")) {
                        checkIn.setUuid(cursor.getString(i));
                    }
                }
                checkIn.setStatusIcon(R.drawable.checkedin);
                checkInList.add(checkIn);
                cursor.moveToNext();
            }
            cursor.close();
            resultBundle.putSerializable(ROW_ITEMS, checkInList);
        }
        return resultBundle;
    }

    /**
     * Get all the checkin rows.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    public Bundle getCheckPointSeqRows(Bundle bundle, Bundle resultBundle, Object object) {
        String projection[] = {ModelConst.NAME_COL, ModelConst.DESC_COL, ModelConst.SEQNO_COL};
        String selectionArg [] = {bundle.getString(ARG_PROJECT_LOCATION_UUID)};

        Cursor cursor = getContentResolver().query(ModelConst.uriCustomBuilder(ModelConst.M_CHECKPOINT_TABLE), projection, ModelConst.C_PROJECTLOCATION_UUID_COL + "=?", selectionArg, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ObservableArrayList<MCheckPoint> checkPointSeqList = new ObservableArrayList<MCheckPoint>();
            //multiple rows. iterate the cursor.
            for (int j = 0; j < cursor.getCount(); j++) {
                MCheckPoint checkPointSeq = new MCheckPoint();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.NAME_COL)){
                        checkPointSeq.setName(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.SEQNO_COL)){
                        checkPointSeq.setSeqNo(String.valueOf(cursor.getInt(i)));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.DESC_COL)){
                        checkPointSeq.setDescription(cursor.getString(i));
                    }
                }
                checkPointSeqList.add(checkPointSeq);
                cursor.moveToNext();
            }
            cursor.close();
            resultBundle.putSerializable(ARG_CHECKPOINT_SEQ, checkPointSeqList);
        }
        return resultBundle;
    }

    /**
     * Get checkin details based on the checkin id.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    public Bundle getCheckinDetails(Bundle bundle, Bundle resultBundle, Object object) {
        String selectionArgs [] = {bundle.getString(ARG_CHECKIN_UUID)};
        Cursor cursor = getContentResolver().query(ModelConst.uriCustomBuilder(ModelConst.CHECKIN_JOIN_CHECKPOINT_DETAILS_TABLE), null, null, selectionArgs, null);
        cursor.moveToFirst();
        MCheckIn checkpointdetails = new MCheckIn();
        if (cursor.getCount() > 0){
            for (int j = 0; j < cursor.getCount(); j++) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equalsIgnoreCase("dateTrx")){
                        String dateTime = cursor.getString(i);
                        checkpointdetails.setDate(PandoraHelper.parseToDisplaySDate(dateTime, "dd-MM-yyyy", TimeZone.getDefault()));
                        checkpointdetails.setTime(PandoraHelper.parseToDisplaySDate(dateTime, "HH:mm:ss", TimeZone.getDefault()));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("desc")){
                        checkpointdetails.setComment(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("m_checkin_uuid")){
                        checkpointdetails.setUuid(cursor.getString(i));
                    }
                    else if (cursor.getColumnName(i).equalsIgnoreCase("username")) {
                        checkpointdetails.setUser(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("checkPoint")) {
                        checkpointdetails.setCheckpoint(cursor.getString(i));
                    } else if (cursor.getColumnName(i).equalsIgnoreCase("projectLocation")) {
                        checkpointdetails.setProjectLocation(cursor.getString(i));
                    }
                }
            }
            resultBundle.putSerializable(ARG_CHECKPOINT_DETAILS, checkpointdetails);
            cursor.close();
        }
        return resultBundle;
    }


}
