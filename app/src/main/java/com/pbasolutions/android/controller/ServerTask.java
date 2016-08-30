package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.json.PBSColumnsJSON;
import com.pbasolutions.android.json.PBSSyncJSON;
import com.pbasolutions.android.json.PBSTableJSON;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.syncAdapter.PBSIServerAccessor;
import com.pbasolutions.android.syncAdapter.PBSServerAccessor;
import com.pbasolutions.android.utils.CameraUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pbadell on 1/5/16.
 */
public class ServerTask extends Task {

    private static final String TAG = "ServerTask";

    private ContentResolver cr;

    public ServerTask(ContentResolver cr) {
        this.cr = cr;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event) {
            case PBSServerController.SYNC_LOCAL_TABLES: {
                return syncLocalTables();
            }
            case PBSServerController.UPDATE_LOCAL_TABLES: {
                return updateLocalTables();
            }

            case PBSServerController.DELETE_RETENTION_RECORD: {
                return deleteRetentionRecord();
            }
            default:
                return null;
        }
    }

    private Bundle deleteRetentionRecord() {
        //select * from pbs_synctable where !masterdata for tables name.
      //  List<String> retentionTables = new ArrayList();
        String projection[] = {"NAME", "RETENTIONPERIOD"};
        String selection = "ISMASTERDATA" + "=? and 'RETENTIONPERIOD' > ? and ISRETAIN=?";
        String selectionArgs[] = {"false","0", "true"};
        List <Pair<String, String>> retentionTables = new ArrayList<>();
        Cursor cursor =
                cr.query(ModelConst.uriCustomBuilder(ModelConst.PBS_SYNCTABLE_TABLE),
                projection, selection,
                        selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                //add table name and retention perion in list.
                //name, retentionperiod.
                retentionTables.add(new Pair(cursor.getString(0), cursor.getString(1)));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //check the retention period against updated timestamp
        for(Pair table : retentionTables) {

            String tableName= (String)table.first;
            String tableProjection[] = {"UPDATED", tableName + "_UUID"};

            //check record
            Cursor tableCursor =
                    cr.query(ModelConst.uriCustomBuilder((String)table.first),
                           tableProjection, null, null, null);
            if (tableCursor != null && tableCursor.getCount() >0){
                tableCursor.moveToFirst();
                do {
                    //check the retention period
                    String updated = tableCursor.getString(0);
                    for (String format : PandoraHelper.formats){
                        Date date = PandoraHelper.stringToDate(format, updated);
                        if (date != null){
                            //get date + retention period
                            int addDays = Integer.parseInt((String)table.second);
                            Date addedDate = new Date(date.getTime() + addDays * 1000 * 3600 * 24);
                            Date today = new Date();

                            if (today.compareTo(addedDate) > 0) {
                                //today is after addedDate
                                //delete record directly.
                                String _UUID = tableCursor.getString(1);
                                String deleteArg[] = {_UUID};
                                boolean isDeleted = ModelConst.deleteTableRow(cr, tableName,
                                        tableName + "_UUID",
                                        deleteArg);
                                if (isDeleted) {
                                    output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                                    output.putString(PandoraConstant.RESULT, "Successfully delete " +
                                            tableName+ " " +_UUID+"record!");

                                } else {
                                    output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                                    output.putString(PandoraConstant.RESULT, "Successfully delete " +
                                            "record!");
                                }

                            }
                            break;
                        }

                    }

                } while (tableCursor.moveToNext());
            }

            if (tableCursor != null)
                tableCursor.close();
        }
        return output;
    }

    private Bundle updateLocalTables() {
        //check whether the tables contains not sync row
        if (!isLocalTableSync(cr, ModelConst.localUpdateTables)) {
            //loop through all table and check isSync column.
            String tableNames[] = ModelConst.localUpdateTables;
            for (int i = 0; i < tableNames.length; i++) {
                String tableName = tableNames[i];
                //current TABLE
                Cursor tableCursor = cr.query(ModelConst.uriCustomBuilder(tableName),
                        null, ModelConst.IS_SYNCED_COL + " = 'N'", null, null);
                if (tableCursor != null) {
                    //if the table contains row(s) that isSynced == 'N'
                    if (tableCursor.getCount() > 0) {
                        for (tableCursor.moveToFirst(); !tableCursor.isAfterLast();
                             tableCursor.moveToNext()) {
                            // check if contains parent & update parent.
                            if (isContainsParentTable(tableCursor, tableName)) {
                                output = updateParentTable(tableCursor, tableName,
                                        cr, output, input);
                            }
                            //then only update table.
                            output = updateTable(tableCursor, tableName, cr,
                                    output, input);
                        }
                    }
                    tableCursor.close();
                }
            }
        } else {
            output.putBoolean(PandoraConstant.RESULT, true);
        }
        return output;
    }

    private Bundle syncLocalTables() {
        //first check the local tables for isSync = Y, if Y then only trigger sync.
        boolean isSuccessSync = true;
//        if (isLocalTableSync(cr, ModelConst.tableNames))
        {
            PBSIServerAccessor serverAccessor = new PBSServerAccessor();
            PBSSyncJSON syncJSON = serverAccessor.syncTables(
                    input.getString(PBSAuthenticatorController.USER_NAME_ARG),
                    input.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG),
                    input.getString(PBSAuthenticatorController.SERVER_URL_ARG));
            int syncedCount = 0;
            if (syncJSON != null) {
                if (syncJSON.getNew() != null) {
                    //as this data coming from server side, the checking will be against
                    // pbs_syncdata id if the pbs_syncdata_id
                    //is not exist, this means the record is new.
                    for (PBSTableJSON insertTable : syncJSON.getNew()) {
                        syncedCount++;
                        ContentValues cv = ModelConst.mapDataToContentValues(insertTable,
                                cr);
                        String tableName = insertTable.getTableName();

                        String selection = ModelConst.getTableColumnIdName(tableName, cv);
                        String[] arg = {cv.getAsString(selection)};
                        if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                            if (!ModelConst.insertTableRow(cr, tableName, cv,
                                    selection, arg)) {
                                isSuccessSync = false;
                            }
                        }
                        Log.i(TAG, PandoraConstant.INFO + PandoraConstant.SPACE + tableName
                                + PandoraConstant.SPACE + selection + arg[0]);
                    }
                }


                if (syncJSON.getUpdate() != null) {
                    for (PBSTableJSON updateTable : syncJSON.getUpdate()) {
                        syncedCount++;
                        ContentValues cv = ModelConst.mapDataToContentValues(updateTable,
                                cr);
                        String selection = ModelConst.getTableColumnIdName(
                                updateTable.getTableName(), cv);
                        String tableName = updateTable.getTableName();
                        String[] arg = {cv.getAsString(selection)};
                        //try to update
                        if (!ModelConst.updateTableRow(cr, tableName, cv,
                                selection, arg)) {
                            //if its not update-able due to row is not inserted yet then insert data
                            if (!ModelConst.insertTableRow(cr, tableName, cv,
                                    selection, arg)) {
                                isSuccessSync = false;
                            }
                        }
                    }
                }

                if (syncJSON.getDelete() != null) {
                    for (PBSTableJSON deleteTable : syncJSON.getDelete()) {
                        syncedCount++;
                        ContentValues cv = ModelConst.mapDataToContentValues(deleteTable,
                                cr);
                        String selection = ModelConst.getTableColumnIdName(
                                deleteTable.getTableName(), cv);
                        String[] arg = {cv.getAsString(selection)};
                        if (ModelConst.isInsertedRow(cr,
                                deleteTable.getTableName(), selection, arg))
                        //checking if deleted before
                        {
                            if (!ModelConst.deleteTableRow(cr,
                                    deleteTable.getTableName(), selection, arg)) {
                                isSuccessSync = false;
                            }
                        }
                    }
                }
                if (isSuccessSync) {
                    output.putBoolean(PandoraConstant.RESULT, true);
                } else {
                    output.putBoolean(PandoraConstant.RESULT, false);
                }

                output.putInt(PandoraConstant.SYNC_COUNT, syncedCount);
            } else {
                output.putBoolean(PandoraConstant.RESULT, false);
                output.putInt(PandoraConstant.SYNC_COUNT, -1);
            }
        }
        return output;
    }



    /**
     * Check whether that table is sync or not by checking all rows has value of isSynced column is boolean Y.
     * @param cr
     * @return
     */
    private boolean isLocalTableSync(ContentResolver cr, String[] tableNames) {
        //loop through all table and check isSync column.
        for (int i = 0; i < tableNames.length; i++) {
            Cursor cursorBP = cr.query(ModelConst.uriCustomBuilder(tableNames[i]), null,
                    ModelConst.IS_SYNCED_COL + " = 'N'", null, null);
            if (cursorBP != null) {
                if (cursorBP.getCount() > 0) {
                    //update first before sync.
                    return false;
                }
                cursorBP.close();
            }
        }
        return true;
    }

    /**
     * Check whether current table referring to other tables.
     * @param tableCursor
     * @param tableName
     * @return
     */
    private boolean isContainsParentTable(Cursor tableCursor, String tableName) {
        //loop through all columns to find parent column
        for (int j = 0; j < tableCursor.getColumnCount(); j++) {
            String col = tableCursor.getColumnName(j);
            if (col.contains(ModelConst._UUID) && !col.equals(tableName + ModelConst._UUID)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Update parent table to server first.
     * @param tableCursor
     * @param tableName
     * @param cr
     * @param resultBundle
     * @param inputBundle
     * @return
     */
    private Bundle updateParentTable(Cursor tableCursor, String tableName, ContentResolver cr,
                                     Bundle resultBundle, Bundle inputBundle) {
        //loop through all columns
        for (int j = 0; j < tableCursor.getColumnCount(); j++) {
            String col = tableCursor.getColumnName(j);
            if (col.contains(ModelConst._UUID) && !col.equals(tableName + ModelConst._UUID)) {
                //get the parent table name & id
                String parentTableName = col.replace(ModelConst._UUID, "");
                int columnIndex = tableCursor.getColumnIndex(col);
                String parentUUID = tableCursor.getString(columnIndex);
                //find parent table with value of parent id
                //check if isSynced == 'N'
                Cursor parentTableCursor = cr.query(ModelConst.uriCustomBuilder(parentTableName),
                        null, ModelConst.IS_SYNCED_COL + " = 'N' and " + col + "=" + parentUUID, null, null);
                if (parentTableCursor != null){
                    if (parentTableCursor.getCount() > 0) {
                        for (parentTableCursor.moveToFirst(); !parentTableCursor.isAfterLast();
                             parentTableCursor.moveToNext()) {
                            resultBundle = updateTable(parentTableCursor, parentTableName, cr,
                                    resultBundle, inputBundle);
                        }
                    }
                    parentTableCursor.close();
                }

            }
        }

        return resultBundle;
    }


    /**
     * Update tables in database with new values from server.
     * @param tableCursor
     * @param tableName
     * @param contentResolver
     * @param resultBundle
     * @param inputBundle
     * @return
     */
    private Bundle updateTable(Cursor tableCursor, String tableName,
                               ContentResolver contentResolver, Bundle resultBundle,
                               Bundle inputBundle) {
        //set all values to be updated by table.
        PBSTableJSON data = new PBSTableJSON();
        data.setTableName(tableName);
        String uuid = "";
        List<PBSColumnsJSON> columns = new ArrayList<>();
        try {
            //loop through all columns
            for (int x = 0; x < tableCursor.getColumnCount(); x++) {
                //if is column _UUID skip
                if (tableCursor.getColumnName(x).equalsIgnoreCase(tableName + ModelConst._UUID)){
                    uuid = tableCursor.getString(x);
                    continue;
                }

                boolean isNonUpdateCol = false;
                for (String col : ModelConst.NON_UPDATE_COLUMNS) {
                    if (tableCursor.getColumnName(x).equalsIgnoreCase(col)) {
                        isNonUpdateCol = true;
                        break;
                    }
                }

                if (isNonUpdateCol) {
                    continue;
                }
                //check if the foreign key column
                if (!tableCursor.getColumnName(x).equalsIgnoreCase(tableName + ModelConst._UUID)
                        && tableCursor.getColumnName(x).contains(ModelConst._UUID)) {
                    String columnID = tableCursor.getColumnName(x).replace(ModelConst._UUID,
                            ModelConst._ID);
                    String columnIDTableName = columnID.replace(ModelConst._ID, "");
                    String _ID = ModelConst.mapUUIDtoColumn(ModelConst.mapTableName(columnIDTableName),
                            tableCursor.getColumnName(x), tableCursor.getString(x),
                            columnID, contentResolver);
                    PBSColumnsJSON column = new PBSColumnsJSON(columnID,
                            Integer.parseInt(_ID));
                    columns.add(column);
                } else if (tableCursor.getColumnName(x).equalsIgnoreCase("CreatedBy") ||
                        tableCursor.getColumnName(x).equalsIgnoreCase("UpdatedBy")){
                    String columnName = tableCursor.getColumnName(x);
                    String columnID = ModelConst.AD_USER_TABLE + ModelConst._ID;
                    String columnIDTableName = ModelConst.AD_USER_TABLE;
                    String _ID =   ModelConst.mapUUIDtoColumn(ModelConst.mapTableName(columnIDTableName),
                            "AD_USER_UUID", tableCursor.getString(x),
                            columnID, contentResolver);
                    PBSColumnsJSON column = new PBSColumnsJSON(columnName,
                            Integer.parseInt(_ID));
                    columns.add(column);
                } else if (tableCursor.getColumnName(x).equalsIgnoreCase("Latitude") ||
                        tableCursor.getColumnName(x).equalsIgnoreCase("Longitude")) {
                    String value = String.valueOf(tableCursor.getDouble(x));
                    if (value == null || value.isEmpty())
                        value = "null";
                    columns.add(new PBSColumnsJSON(tableCursor.getColumnName(x), value));
                } else if (tableCursor.getColumnName(x).contains("ATTACHMENT_")){
                    String value = CameraUtil.imageToBase64(tableCursor.getString(x));
                    if (value == null || value.isEmpty())
                        value = "null";
                    columns.add(new PBSColumnsJSON(tableCursor.getColumnName(x), value));
                } else if (tableCursor.getColumnName(x).equalsIgnoreCase(tableName + "_ID")){
                    columns.add(new PBSColumnsJSON(tableCursor.getColumnName(x),
                            tableCursor.getInt(x)));
                }
                else {
                    String value = unescapedString(tableCursor.getString(x));
                    if(value == null)
                    {
                        value = "null";
                    }
                    columns.add(new PBSColumnsJSON(tableCursor.getColumnName(x), value));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        PBSColumnsJSON[] array = columns.toArray(new PBSColumnsJSON[columns.size()]);
        data.setColumns(array);
        return updateResult(data, resultBundle, contentResolver, tableName,
                uuid, inputBundle.getString(PBSAuthenticatorController.USER_PASS_ARG),
                inputBundle.getString(PBSAuthenticatorController.AUTH_TOKEN_ARG),
                inputBundle.getString(PBSAuthenticatorController.SERVER_URL_ARG));
    }


    /**
     * Update the local table to server.
     *
     * @param tableData
     * @param resultBundle
     * @param contentResolver
     * @param tableName
     * @param tableUUID
     * @return
     */
    private Bundle updateResult(PBSTableJSON tableData, Bundle resultBundle,
                                ContentResolver contentResolver
            , String tableName, String tableUUID, String username, String authToken, String server) {

        try {
            PBSIServerAccessor serverAccessor = new PBSServerAccessor();
            resultBundle = serverAccessor.updateTables(tableData, username, authToken, server);
            if (!resultBundle.getBoolean(PandoraConstant.RESULT)) {
//                resultBundle.putString("Result" + tableData.getTableName(), tableData.getTableName());
//                resultBundle.putBoolean(PandoraConstant.RESULT, false);
            } else {
                ContentValues cv = new ContentValues();
                cv.put(ModelConst.IS_SYNCED_COL, "Y");
                //TODO check if the update is not success.
                //TODO refactor code to be safer user ? param.
                String where = tableName + ModelConst._UUID + PandoraConstant.EQUAL
                        + PandoraConstant.QUESTION_MARK;
                String[] selectionArg = {tableUUID};
                contentResolver.update(ModelConst.uriCustomBuilder(tableName),
                        cv, where, selectionArg);
                resultBundle.putString("Result" + tableData.getTableName(),
                        tableData.getTableName() + "\n\n");
                //+ updateResult.getSuccess()
                //       + "\n\n" + updateResult.getRecord()
                //      + "\n\n" + updateResult.getDocStatus());
                //only return true/false for update process.
                //resultBundle.putBoolean(PandoraConstant.RESULT, true);

            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        return resultBundle;
    }

    String unescapedString(String inputStr) {
        if (inputStr == null) return null;
        return inputStr.replace("\\", "");
    }

}
