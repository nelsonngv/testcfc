package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.database.PBSContentProvider;
import com.pbasolutions.android.json.PBSNoteJSON;
import com.pbasolutions.android.json.PBSNotesJSON;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MNote;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by pbadell on 10/30/15.
 */
public class BroadcastTask implements Callable<Bundle> {

    private static final String TAG = "BroadcastTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;

    public BroadcastTask(Bundle input, Bundle result, ContentResolver cr) {
        this.input = input;
        this.output = result;
        this.cr = cr;
        event = input.getString(PBSIController.ARG_TASK_EVENT);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event){
            case PBSBroadcastController.GET_NOTES_EVENT: {
                return getNotes();
            }

            case PBSBroadcastController.GET_NOTE_EVENT: {
                return getNote();
            }

            case PBSBroadcastController.SYNC_NOTES_EVENT: {
                return syncNotes();
            }

            case PBSBroadcastController.DELETE_NOTES_EVENT: {
                return deleteNotes();
            }
            default:return null;
        }
    }

    private Bundle getNote() {
        String selection = ModelConst.AD_NOTE_TABLE + ModelConst._UUID + "=?";
        String selectionArgs[] = {input.getString(PBSBroadcastController.ARG_NOTE_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.AD_NOTE_TABLE),
                null, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                output.putSerializable(PBSBroadcastController.ARG_NOTE, populateNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return output;
    }

    /**
     * Delete selected note.
     * @return
     */
    private Bundle deleteNotes() {
        ObservableArrayList<IModel> noteList =
                (ObservableArrayList)input.getSerializable(PBSBroadcastController.NOTE_LIST);
        String selection = input.getString(PBSBroadcastController.ARG_SELECTION);
        return PandoraHelper.deleteFromList(noteList, selection, cr, output,
                ModelConst.AD_NOTE_TABLE, MNote.itemName);
    }

    /**
     * Sync note from server then insert into local database.
     * @return
     */
    private Bundle syncNotes() {
        String projLocId = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL, input.getString(PBSBroadcastController.ARG_PROJLOC_UUID),
                ModelConst.C_PROJECT_LOCATION_TABLE + ModelConst._ID, cr);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        PBSNotesJSON notesJSON = serverAPI.getNoteByUser
                (input.getString(PBSBroadcastController.ARG_USER_ID),
                        projLocId,
                        input.getString(PBSServerConst.PARAM_URL));

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();
        if (notesJSON != null) {
            for (PBSNoteJSON noteJSON : notesJSON.getNotes()){
                ContentValues cv = new ContentValues();
                cv.put(MNote.AD_NOTE_UUID_COL, UUID.randomUUID().toString());
                cv.put(MNote.AD_NOTE_ID_COL, noteJSON.getAD_Note_ID());
                String AD_USER_UUID = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, MNote.AD_USER_UUID_COL, noteJSON.getAD_User_ID(), "AD_USER_ID", cr);
                cv.put(MNote.AD_USER_UUID_COL, AD_USER_UUID);
                cv.put(MNote.DATE_COL, noteJSON.getDate());
                cv.put(MNote.MESSAGE_COL, noteJSON.getMessage());
                String sender = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, MNote.AD_USER_UUID_COL, noteJSON.getSender(), "AD_USER_ID", cr);
                cv.put(MNote.SENDER_COL, sender);

                String selection = MNote.AD_NOTE_ID_COL;
                String[] arg = {cv.getAsString(selection)};
                String tableName = ModelConst.AD_NOTE_TABLE;
                if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                    cv.put(MNote.AD_NOTE_UUID_COL, UUID.randomUUID().toString());
                    ops.add(ContentProviderOperation
                            .newInsert(ModelConst.uriCustomBuilder(ModelConst.AD_NOTE_TABLE))
                            .withValues(cv)
                            .build());
                } else {
                    selection = selection + "=?";
                    ops.add(ContentProviderOperation
                            .newUpdate(ModelConst.uriCustomBuilder(ModelConst.AD_NOTE_TABLE))
                            .withValues(cv)
                            .withSelection(selection, arg)
                            .build());
                }
            }
            try {
                ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
                for(ContentProviderResult result : results) {
                    boolean resultFlag = ModelConst.getURIResult(result.uri);
                    if (!PBSContentProvider.getContentProviderResult(result)) {
                        output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                        output.putString(PandoraConstant.ERROR, "Fail to sync notes.");
                        return output;
                    }
                }
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully synced notes");
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            } catch (OperationApplicationException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return output;
    }

    /**
     * Get all notes inserted.
     * @return
     */
    private Bundle getNotes(){
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.AD_NOTE_TABLE),
                null, null, null, null);
        ObservableArrayList<MNote> noteList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                noteList.add(populateNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSBroadcastController.NOTE_LIST, noteList);
        return output;
    }

    /**
     * Populate note.
     * @param cursor
     * @return
     */
    private MNote populateNote(Cursor cursor) {
        MNote note = new MNote();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue =  cursor.getString(x);
            if (MNote.AD_NOTE_UUID_COL.equalsIgnoreCase(columnName)){
                note.set_UUID(rowValue);
            } else if (ModelConst.AD_USER_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                note.setAD_USER_UUID(rowValue);
            } else if (MNote.DATE_COL
                    .equalsIgnoreCase(columnName)) {
                note.setDate(PandoraHelper.parseToDisplaySDate(rowValue, "dd-MM-yyyy",
                        TimeZone.getDefault()));
                note.setTime(PandoraHelper.parseToDisplaySDate(rowValue, "HH:mm:ss",
                        TimeZone.getDefault()));
            } else if (MNote.SENDER_COL
                    .equalsIgnoreCase(columnName)) {
                note.setSender(getSenderName(rowValue));
            } else if (MNote.MESSAGE_COL
                    .equalsIgnoreCase(columnName)) {
                String message = PandoraHelper.parseNewLine(rowValue);
                if (message != null)
                  note.setTextMsgs(message);
                else
                  note.setTextMsgs(rowValue);
            }
        }
        return note;
    }

    /**
     * Get sender name.
     * @param rowValue
     * @return
     */
    private String getSenderName(String rowValue) {
        return ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_UUID_COL,
                rowValue, ModelConst.NAME_COL, cr);
    }
}
