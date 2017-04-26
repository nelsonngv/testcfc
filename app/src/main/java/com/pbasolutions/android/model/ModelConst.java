package com.pbasolutions.android.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.json.PBSTableJSON;
import com.pbasolutions.android.json.PBSColumnsJSON;

import java.util.UUID;

/**
 * Created by pbadell on 6/30/15.
 */
public class ModelConst {

    public static final String ARG_CONTENT_VALUES = "Content_Values";
    public static final String _UUID = "_UUID";
    public static final String _ID = "_ID";
    public static final String AD_USER_UUID_COL = "AD_User_UUID";
    public static final String AD_USER_ID_COL = "AD_User_ID";
    public static final String AD_ORG_UUID_COL = "AD_Org_UUID";
    public static final String AD_CLIENT_UUID_COL = "AD_Client_UUID";
    public static final String NAME_COL = "Name";
    public static final String DESC_COL = "Description";
    public static final String SEQNO_COL = "SeqNo";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATIONTO_ID_COL = "C_ProjectLocationTo_ID";
    public static final String M_CHECKIN_UUID_COL = "M_Checkin_UUID";
    public static final String CREATEDBY_COL = "CreatedBy";
    public static final String UPDATEDBY_COL = "UpdatedBy";
    public static final String IS_UPDATED_COL = "IsUpdated";
    public static final String IS_SYNCED_COL = "IsSynced";
    public static final String IS_DELETED_COL = "IsDeleted";
    public static final String HR_SHIFT_ID_COL = "HR_Shift_ID";
    public static final String HR_SHIFT_UUID_COL = "HR_Shift_UUID";
    public static final String HR_PROJLOCATION_SHIFT_UUID_COL = "HR_ProjLocation_Shift_UUID";
    public static final String HR_SETUP_JOB_UUID_COL = "HR_SETUP_JOB_UUID";
    public static final String HR_NATIONALITY_UUID_COL = "HR_NATIONALITY_UUID";
    public static final String IDNUMBER_COL = "IDNumber";
    public static final String ICNO_COL = "ICNo";
    public static final String WORKPERMIT_COL = "WorkPermit";
    public static final String PHONE_COL = "Phone" ;
    public static final String STATUS_COL = "Status";
    public static final String CREATED_COL = "Created";
    public static final String HR_LEAVETYPE_UUID_COL = "HR_LEAVETYPE_UUID";
    public static final String HR_LEAVETYPE_ID_COL = "HR_LEAVETYPE_ID";
    public static final String INTERVIEWER_NOTES_COL = "INTERVIEWER_NOTES";
    public static final String C_BPARTNER_ID_COL = "C_BPartner_ID";
    public static final String C_BPARTNER_UUID_COL = "C_BPartner_UUID";

    /**
     * table names.
     */
    public static final String AD_USER_TABLE = "AD_User";
    public static final String AD_ORG_TABLE = "AD_Org";
    public static final String AD_CLIENT_TABLE = "AD_Client";
    public static final String AD_ROLE_TABLE = "AD_Role";
    public static final String M_CHECKPOINT_TABLE = "M_CheckPoint";
    public static final String M_CHECKIN_TABLE = "M_CheckIn";
    public static final String PBS_SYNCTABLE_TABLE = "PBS_SyncTable";
    public static final String C_PROJECT_LOCATION_TABLE = "C_ProjectLocation";
    public static final String HR_JOBPOSITION_TABLE = "HR_JobPosition";
    public static final String HR_NATIONALITY_TABLE = "HR_Nationality";
    public static final String HR_PROJLOCATION_SHIFT_TABLE = "HR_ProjLocation_Shift";
    public static final String HR_JOBAPPLICATION_TABLE = "HR_JobApplication";
    public static final String HR_SHIFT_TABLE = "HR_Shift";
    public static final String C_BPARTNER_TABLE = "C_BPartner";
    public static final String C_BPARTNER_LOCATION_TABLE = "C_BPartner_Location";
    public static final String M_PRODUCT_CATEGORY_TABLE = "M_Product_Category";
    public static final String M_PRODUCT_TABLE = "M_Product";
    public static final String C_UOM_TABLE = "C_UOM";
    public static final String M_ATTRIBUTESETINSTANCE_TABLE = "M_AttributeSetInstance";
    public static final String M_PURCHASEREQUEST_TABLE = "M_PurchaseRequest";
    public static final String M_PURCHASEREQUESTLINE_TABLE = "M_PurchaseRequestLine";
    public static final String C_PROJECTTASK_TABLE = "C_ProjectTask";
    public static final String A_ASSET_TABLE = "A_Asset";
    public static final String AD_NOTE_TABLE = "AD_Note";
    public static final String M_MOVEMENT_TABLE = "M_Movement";
    public static final String M_MOVEMENTLINE_TABLE = "M_MovementLine";
    public static final String JOIN_TABLE = "join_table";
    public static final String CHECKIN_JOIN_CHECKPOINT_TABLE= "checkin_join_checkpoint_table";
    public static final String CHECKIN_JOIN_CHECKPOINT_DETAILS_TABLE= "checkin_join_checkpoint_details_table";
    public static final String HR_PROJECTASSIGNMENT_TABLE = "HR_ProjectAssignment";
    public static final String JOBAPP_SHIFTS_VIEW = "JobApp_Shifts_View";
    public static final String JOBAPP_LIST_VIEW = "JobApp_List_View";
    public static final String HR_SETUP_JOB_TABLE = "HR_Setup_Job" ;
    public static final String C_BPARTNER_VIEW = "C_BPartner_View" ;
    public static final String C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TABLE = "c_bpartner_view_join_hr_hr_projectassignment_table" ;
    public static final String HR_IDENTITY_TABLE = "HR_Identity";

    public static final String HR_LEAVETYPE_TABLE = "HR_LeaveType";
    public static final String M_ATTENDANCE_TABLE = "M_Attendance";
    public static final String M_ATTENDANCELINE_TABLE = "M_AttendanceLine";
    public static final String HR_CLUSTERMANAGEMENT_TABLE = "HR_ClusterManagement";
    public static final String C_SURVEY_TABLE = "C_Survey";
    public static final String C_SURVEYRESPONSE_TABLE = "C_SurveyResponse";
    public static final String C_SURVEYTEMPLATE_TABLE = "C_SurveyTemplate";
    public static final String C_SURVEYTEMPLATEQUESTION_TABLE = "C_SurveyTemplateQuestion";
    public static final String C_SURVEY_JOIN_TEMPLATE_TABLE = "C_Survey_Join_Template";
    public static final String C_SURVEY_JOIN_TEMPLATE_JOIN_QUESTION_JOIN_RESPONSE_TABLE = "C_Survey_Join_Template_Join_Question_Join_Response";



    /**
     * Table token.
     */
    public static final int JOIN_TOKEN = 10;
    public static final int AD_USER_TOKEN = 100;
    public static final int AD_ORG_TOKEN = 200;
    public static final int AD_CLIENT_TOKEN = 300;
    public static final int AD_ROLE_TOKEN = 400;
    public static final int M_CHECKPOINT_TOKEN = 500;
    public static final int M_CHECKIN_TOKEN = 600;
    public static final int C_PROJECTLOCATION_TOKEN = 700;
    public static final int HR_SETUP_JOB_TOKEN = 800;
    public static final int HR_JOBPOSITION_TOKEN = 900;
    public static final int HR_NATIONALITY_TOKEN = 1000;
    public static final int HR_SHIFT_TOKEN = 1100;
    public static final int HR_PROJLOCATION_SHIFT_TOKEN = 1200;
    public static final int HR_JOBAPPLICATION_TOKEN = 1300;
    public static final int C_BPARTNER_TOKEN = 1400;
    public static final int C_BPARTNER_LOCATION_TOKEN = 1500;
    public static final int M_PRODUCT_CATEGORY_TOKEN = 1600;
    public static final int M_PRODUCT_TOKEN = 1700;
    public static final int C_UOM_TOKEN = 1800;
    public static final int M_ATTRIBUTESETINSTANCE_TOKEN = 1900;
    public static final int M_PURCHASEREQUEST_TOKEN = 2000;
    public static final int M_PURCHASEREQUESTLINE_TOKEN = 2100;
    public static final int C_PROJECTTASK_TOKEN = 2200;
    public static final int A_ASSET_TOKEN = 2300;
    public static final int AD_NOTE_TOKEN = 2400;
    public static final int M_MOVEMENT_TOKEN = 2500;
    public static final int M_MOVEMENTLINE_TOKEN = 2600;
    public static final int HR_PROJECTASSIGNMENT_TOKEN = 2700;
    public static final int JOBAPP_SHIFTS_TOKEN = 2800;
    public static final int JOBAPP_LIST_TOKEN = 2900;
    public static final int PBS_SYNCTABLE_TOKEN = 3000;
    public static final int C_BPARTNER_VIEW_TOKEN = 3100;

    public static final int HR_LEAVETYPE_TOKEN = 3200;
    public static final int M_ATTENDANCE_TOKEN = 3300;
    public static final int M_ATTENDANCELINE_TOKEN = 3400;
    public static final int HR_CLUSTERMANAGEMENT_TOKEN = 3500;
    public static final int C_SURVEY_TOKEN = 3600;
    public static final int C_SURVEYRESPONSE_TOKEN = 3700;
    public static final int C_SURVEYTEMPLATE_TOKEN = 3800;
    public static final int C_SURVEYTEMPLATEQUESTION_TOKEN = 3900;
    public static final int HR_IDENTITY_TOKEN = 4000;

    /**
     * TODO : Evaluate which table to be joined and provide table tokens for that.
     */
    public static final int CHECKIN_JOIN_CHECKPOINT_TOKEN = 20;
    public static final int CHECKIN_JOIN_CHECKPOINT_DETAILS_TOKEN = 30;
    public static final int C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TOKEN = 40;
    public static final int C_SURVEY_JOIN_TEMPLATE_TOKEN = 50;
    public static final int C_SURVEY_JOIN_TEMPLATE_JOIN_QUESTION_JOIN_RESPONSE_TOKEN = 60;
    /**
     *
     */
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.pbasolutions.android";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ PBSAccountInfo.ACCOUNT_AUTHORITY+"/");
    public static final String CONTENT_URI_STRING = "content://"+ PBSAccountInfo.ACCOUNT_AUTHORITY+"/";
    public static final String VALUE_COL = "Value";


    ////////////////////////////////////////////////////////////////////////////////
    public static Uri uriCustomBuilder(String tableName) {
        return Uri.parse(CONTENT_URI_STRING + tableName);
    }

    /**
     * all table names.
     * to be sync or updated.L
     */
    public static String tableNames[] =
            {
                    ModelConst.AD_CLIENT_TABLE,
                    ModelConst.AD_ORG_TABLE,
                    ModelConst.AD_ROLE_TABLE,
                    ModelConst.AD_USER_TABLE,
                    ModelConst.C_BPARTNER_TABLE,
                    ModelConst.C_PROJECT_LOCATION_TABLE,
                    ModelConst.M_CHECKPOINT_TABLE,
                    ModelConst.M_CHECKIN_TABLE,
                    ModelConst.HR_SETUP_JOB_TABLE,
                    ModelConst.HR_JOBPOSITION_TABLE,
                    ModelConst.HR_NATIONALITY_TABLE,
                    ModelConst.HR_SHIFT_TABLE,
                    ModelConst.HR_PROJLOCATION_SHIFT_TABLE,
                    ModelConst.C_BPARTNER_LOCATION_TABLE,
                    ModelConst.M_PRODUCT_CATEGORY_TABLE,
                    ModelConst.C_UOM_TABLE,
                    ModelConst.M_ATTRIBUTESETINSTANCE_TABLE,
                    ModelConst.M_PRODUCT_TABLE,
                    ModelConst.A_ASSET_TABLE,
                    ModelConst.HR_PROJECTASSIGNMENT_TABLE,
                    ModelConst.HR_JOBAPPLICATION_TABLE,
                    ModelConst.HR_LEAVETYPE_TABLE,
                    ModelConst.HR_CLUSTERMANAGEMENT_TABLE,
                    ModelConst.C_SURVEY_TABLE,
                    ModelConst.C_SURVEYRESPONSE_TABLE,
                    ModelConst.C_SURVEYTEMPLATE_TABLE,
                    ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE
            };

    /**
     * other tables than master data is updateable by phone.
     */
    public static String localUpdateTables[] =
            {
                    ModelConst.M_CHECKIN_TABLE,
                    ModelConst.HR_JOBAPPLICATION_TABLE,
                    ModelConst.C_SURVEY_TABLE,
                    ModelConst.C_SURVEYRESPONSE_TABLE
            };

    /**
     * tables that need to update their id in local tables after updated to server.
     */
    public static String localUpdateIDTables[] =
            {
                    ModelConst.C_SURVEY_TABLE
            };

    /**
     * all table names.
     */
    public static String NON_UPDATE_COLUMNS[] =
            {
                    ModelConst.IS_SYNCED_COL,
                    ModelConst.IS_DELETED_COL,
                    ModelConst.IS_UPDATED_COL
            };

    /**
     * String for insertion process fails.
     */
    public static final String URI_INSERT_RESULT = "/-1";


    /**
     *
     */
    public static final String LOCAL_DATABASE_TIMEZONE = "UTC";

    /**
     *
     * @param contentResolver
     * @param tableName
     * @param cv
     * @param arg
     * @return
     */
    public static boolean updateTableRow(ContentResolver contentResolver, String tableName, ContentValues cv, String selectionColumn, String[] arg) {
        if (arg.length != 0) {
            //If update, update all other column except primary key
            cv.remove(tableName + ModelConst._UUID);
            int resultReturned = contentResolver.update(ModelConst.uriCustomBuilder(tableName),
                    cv, selectionColumn + " = ? ", arg);
            if ( resultReturned == -1 || resultReturned == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check table is inserted based on where clause.
     * @param contentResolver
     * @param tableName
     * @return
     */
    public static boolean isInsertedRow(ContentResolver contentResolver, String tableName, String selectionCol, String[] selectionArg) {
        boolean result = false;
        Cursor cursor = contentResolver.query(ModelConst.uriCustomBuilder(tableName), null,
                selectionCol + " = ?", selectionArg, null);
        if (cursor != null && cursor.getCount() > 0) {
            result =  true;
        }
        if (cursor != null)
            cursor.close();

        return result;
    }

    /**
     * Check table is updated before based on where clause with more parameters.
     * @param contentResolver
     * @param tableName
     * @return
     */
    public static boolean isUpdatedRow(ContentResolver contentResolver, String tableName, String selection, String[] selectionArg) {
        Cursor cursor = contentResolver.query(ModelConst.uriCustomBuilder(tableName), null,
                selection, selectionArg, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * Insert tables into local database.
     * @param contentResolver
     * @return
     */
    public static boolean insertTableRow(ContentResolver contentResolver, String tableName, ContentValues cv, String selection, String[] arg) {
        //add uuid into the cv
        cv.put(tableName + ModelConst._UUID, UUID.randomUUID().toString());
        Uri result = contentResolver.insert(ModelConst.uriCustomBuilder(tableName), cv);
        return getURIResult(result);
    }

    /**
     *
     * @param uri
     * @return
     */
    public static boolean getURIResult (Uri uri){
        if (uri != null) {
            if (uri.getPath().equalsIgnoreCase(URI_INSERT_RESULT)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Map value from json table data to content value.
     * @param data
     * @return
     */
    public static ContentValues mapDataToContentValues(PBSTableJSON data, ContentResolver contentResolver) {
        ContentValues cv = new ContentValues();
        for (PBSColumnsJSON column : data.getColumns()){
            if ((column.getName().contains(ModelConst._ID) && !column.getName().equalsIgnoreCase(data.getTableName() + ModelConst._ID))) {
                //parent table name.
                String parentTableName = column.getName().replace(ModelConst._ID, "");
                String[] projection = {(parentTableName + ModelConst._UUID)};
                String[] argument = {String.valueOf(column.getValue())};
                Cursor dependencyCursor = contentResolver.query(ModelConst.uriCustomBuilder(parentTableName),
                        projection, column.getName() + " = ?", argument, null);
                String idValue;
                if (dependencyCursor != null)  {
                    dependencyCursor.moveToFirst();
                    if (dependencyCursor.getCount() > 0) {
                        idValue = dependencyCursor.getString(0);
                    } else {
                        idValue = String.valueOf(column.getValue());
                    }
                    dependencyCursor.close();
                }
                else idValue = String.valueOf(column.getValue());
                cv.put(parentTableName + ModelConst._UUID, idValue);
            } else if (column.getName().equalsIgnoreCase("CreatedBy") || column.getName().equalsIgnoreCase("UpdatedBy")){
                //aduser table name.
                String parentTableName = ModelConst.AD_USER_TABLE;
                String[] projection = {(parentTableName + ModelConst._UUID)};
                String[] argument = {String.valueOf(column.getValue())};
                Cursor dependencyCursor = contentResolver.query(ModelConst.uriCustomBuilder(parentTableName),
                        projection, ModelConst.AD_USER_TABLE + ModelConst._ID + " = ?", argument, null);
                if (dependencyCursor != null)  {
                    dependencyCursor.moveToFirst();
                    String idValue;
                    if (dependencyCursor.getCount() >0) {
                        idValue = dependencyCursor.getString(0);
                    } else {
                        idValue = String.valueOf(column.getValue());
                    }
                    cv.put(column.getName(), idValue);
                    dependencyCursor.close();
                } else {
                    cv.put(column.getName(), String.valueOf(column.getValue()));
                }
            } else {
                cv.put(column.getName(), String.valueOf(column.getValue()));
            }
        }
        return cv;
    }

    /**
     * Map uuid column to targeted column eg : AD_User_UUID to Name.
     *                                    eg : CreatedBy to Name , createdby contains ad_user_uuid, and to map with same row name.
     * @param tableName
     * @param columnNameUUID
     * @param columnValueUUID
     * @param targetColumn
     * @param contentResolver content resolver.
     * @return
     */
    public static String mapUUIDtoColumn(String tableName, String columnNameUUID, String columnValueUUID, String targetColumn, ContentResolver contentResolver) {
        String projection[] = {targetColumn};
        String arg[] = {columnValueUUID};
        Cursor cursor = contentResolver.query(ModelConst.uriCustomBuilder(tableName), projection, columnNameUUID + "=?", arg, null);
        //if _UUID has targetColumn
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String value = cursor.getString(0);
            cursor.close();
            if (value == null)
                return "null";

            return value;
        } else {
            cursor.close();
            return "null";
        }
    }

    /**
     * Map uuid column to id.
     * @param tableName
     * @param columnName
     * @param columnNameID
     * @param contentResolver content resolver.
     * @return
     */
    public static String mapIDtoColumn(String tableName, String columnName, String columnValueID, String columnNameID, ContentResolver contentResolver) {
        String projection[] = {columnName};
        String arg[] = {columnValueID};
        Cursor cursor = contentResolver.query(ModelConst.uriCustomBuilder(tableName), projection, columnNameID + "=?", arg, null);
        //if table has ID
        if (cursor !=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String value = cursor.getString(0);
            cursor.close();
            if (value == null)
                return "null";

            return value;
        } else {
            if (cursor != null)
                cursor.close();
            return "null";
        }
    }

    public static String mapTableName(String fromName)
    {
        for (String name : tableNames){
            if (fromName.equalsIgnoreCase(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * Check whether the table has no data/rows.
     * @param contentResolver
     * @param tableName
     * @return
     */
    public static boolean isEmptyTable(ContentResolver contentResolver, String tableName) {
        Cursor cursorBP = contentResolver.query(ModelConst.uriCustomBuilder(tableName), null, null, null, null);
        if (cursorBP.getCount() > 0) {
            cursorBP.close();
            return false;
        }
        cursorBP.close();
        return true;
    }

    /**
     * Delete row.
     * @param contentResolver
     * @param tableName
     * @param selectionCol
     * @param arg
     * @return
     */
    public static boolean deleteTableRow(ContentResolver contentResolver, String tableName, String selectionCol, String[] arg) {
        if (arg.length > 0) {
            if (contentResolver.delete(ModelConst.uriCustomBuilder(tableName), selectionCol + " = ? ", arg) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the table column id name from a table.
     * @param tableName
     * @return
     */
    public static String getTableColumnIdName(String tableName, ContentValues cv)
    {

        String columnIdName;
        //first check whether the table_id == null ?
        tableName = tableName;
        if (cv.getAsString(tableName + ModelConst._ID) != null){
            columnIdName = tableName + ModelConst._ID;
        }
        //if table_id == null then only get the table name from uuid, but currently is only being used in sync/update table from server,
        //means all slightly will want to refer on the table_id.
        else if (cv.getAsString(tableName + ModelConst._UUID) != null) {
            columnIdName = tableName + ModelConst._UUID;
        }
        else {
            columnIdName = "";
        }
        return columnIdName;
    }

    public static String getADOrgUUID(String AD_Org_ID,ContentResolver cr){
        return ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE, ModelConst.AD_ORG_UUID_COL, AD_Org_ID,
                ModelConst.AD_ORG_TABLE + ModelConst._ID, cr);
    }

    public static String getProjLocationUUID(String projLocationID, ContentResolver cr){
        return ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL, projLocationID,
                ModelConst.C_PROJECTLOCATION_ID_COL, cr);
    }

    //TODO: review back inserdataRow code.
    public static  Bundle insertData(ContentValues cv, ContentResolver cr, String tableName, Bundle output) {
        Uri uri = cr.insert(uriCustomBuilder(tableName), cv);
        boolean result = ModelConst.getURIResult(uri);

        if (result) {
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully inserted.");
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to insert.");
        }
        return output;
    }

    public static SpinnerPair getProductPair(Cursor cursor, boolean isName) {
        SpinnerPair pair = new SpinnerPair();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MProduct.M_PRODUCT_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setKey(rowValue);
            } else if (MProduct.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                if (isName)
                pair.setValue(rowValue);
            } else if (MProduct.VALUE_COL
                    .equalsIgnoreCase(columnName)) {
                if (!isName)
                pair.setValue(rowValue);
            }
        }
        return pair;
    }



}
