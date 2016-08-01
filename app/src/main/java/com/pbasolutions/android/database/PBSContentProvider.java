package com.pbasolutions.android.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pbadell on 6/29/15.
 */
public class PBSContentProvider extends ContentProvider {
    /**
     * Class tag name.
     */
    private static final String TAG = "PBSContentProvider";
    /**
     * Db helper. to execute CRUD.
     */
    private SQLiteOpenHelper dbHelper;

    /**
     *
     */
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    /**
     *
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //we can pre-created the URI matches, but when user want to insert/update/delete they
        //wouldn't know nor tell which one should be used.
        try {
            //AD_USER
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.AD_USER_TABLE, ModelConst.AD_USER_TOKEN);

            //AD_CLIENT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.AD_CLIENT_TABLE, ModelConst.AD_CLIENT_TOKEN);

            //AD_ORG
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.AD_ORG_TABLE, ModelConst.AD_ORG_TOKEN);

            //AD_ROLE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.AD_ROLE_TABLE, ModelConst.AD_ROLE_TOKEN);

            //M_CHECKPOINT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_TOKEN);

            //M_CHECKPOINT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_CHECKPOINT_TABLE, ModelConst.M_CHECKPOINT_TOKEN);

            //M_CHECKIN
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_CHECKIN_TABLE, ModelConst.M_CHECKIN_TOKEN);

            //HR_SETUP_JOB
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_SETUP_JOB_TABLE, ModelConst.HR_SETUP_JOB_TOKEN);

            //HR_JOBPOSITION
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_JOBPOSITION_TABLE, ModelConst.HR_JOBPOSITION_TOKEN);

            //HR_NATIONALITY
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_NATIONALITY_TABLE, ModelConst.HR_NATIONALITY_TOKEN);

            //HR_SHIFT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_SHIFT_TABLE, ModelConst.HR_SHIFT_TOKEN);

            //HR_PROJLOCATION_SHIFT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_PROJLOCATION_SHIFT_TABLE, ModelConst.HR_PROJLOCATION_SHIFT_TOKEN);

            //HR_JOBAPPLICATION
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_JOBAPPLICATION_TABLE, ModelConst.HR_JOBAPPLICATION_TOKEN);

            //C_BPARTNER
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_BPARTNER_TABLE, ModelConst.C_BPARTNER_TOKEN);

            //C_BPARTNER_LOCATION
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_BPARTNER_LOCATION_TABLE, ModelConst.C_BPARTNER_LOCATION_TOKEN);

            //M_PRODUCT_CATEGORY
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_PRODUCT_CATEGORY_TABLE, ModelConst.M_PRODUCT_CATEGORY_TOKEN);

            //M_PRODUCT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_PRODUCT_TABLE, ModelConst. M_PRODUCT_TOKEN);

            //C_UOM
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_UOM_TABLE, ModelConst.C_UOM_TOKEN);

            //M_ATTRIBUTESETINSTANCE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_ATTRIBUTESETINSTANCE_TABLE, ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN);

            //M_REQUISITION
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_PURCHASEREQUEST_TABLE, ModelConst.M_PURCHASEREQUEST_TOKEN);

            //M_REQUISITIONLINE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_PURCHASEREQUESTLINE_TABLE, ModelConst.M_PURCHASEREQUESTLINE_TOKEN);

            //C_PROJECTTASK
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_PROJECTTASK_TABLE, ModelConst.C_PROJECTTASK_TOKEN);

            //A_ASSET
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.A_ASSET_TABLE, ModelConst.A_ASSET_TOKEN);

            //AD_NOTE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.AD_NOTE_TABLE, ModelConst.AD_NOTE_TOKEN);

            //M_MOVEMENT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_MOVEMENT_TABLE, ModelConst.M_MOVEMENT_TOKEN);

            //M_MOVEMENTLINE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_MOVEMENTLINE_TABLE, ModelConst.M_MOVEMENTLINE_TOKEN);

            //HR_PROJECTASSIGNMENT
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_PROJECTASSIGNMENT_TABLE, ModelConst.HR_PROJECTASSIGNMENT_TOKEN);

            //HR_IDENTITY
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_IDENTITY_TABLE, ModelConst.HR_IDENTITY_TOKEN);

            //HR_LEAVETYPE_TABLE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.HR_LEAVETYPE_TABLE, ModelConst.HR_LEAVETYPE_TOKEN);

            //PBS_SYNC_TABLE
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.PBS_SYNCTABLE_TABLE, ModelConst.PBS_SYNCTABLE_TOKEN);

            //JOBAPP_SHIFTS_VIEW
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.JOBAPP_SHIFTS_VIEW, ModelConst.JOBAPP_SHIFTS_TOKEN);

            //JOBAPP_LIST_VIEW
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.JOBAPP_LIST_VIEW, ModelConst.JOBAPP_LIST_TOKEN);

            //JOIN_TOKEN
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.JOIN_TABLE, ModelConst.JOIN_TOKEN);

            //C_BPARTNER_VIEW_TOKEN
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_BPARTNER_VIEW, ModelConst.C_BPARTNER_VIEW_TOKEN);

            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TABLE, ModelConst.C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TOKEN);

            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.CHECKIN_JOIN_CHECKPOINT_TABLE, ModelConst.CHECKIN_JOIN_CHECKPOINT_TOKEN);

            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.CHECKIN_JOIN_CHECKPOINT_DETAILS_TABLE, ModelConst.CHECKIN_JOIN_CHECKPOINT_DETAILS_TOKEN);

            // M_Attendance
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_ATTENDANCE_TABLE, ModelConst.M_ATTENDANCE_TOKEN);

            // M_AttendanceLine
            matcher.addURI(PBSAccountInfo.ACCOUNT_AUTHORITY, ModelConst.M_ATTENDANCELINE_TABLE, ModelConst.M_ATTENDANCELINE_TOKEN);

        } catch(Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        return matcher;
    }
    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     * <p/>
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     * <p/>
     * <p>If you use SQLite, {@link SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link SQLiteOpenHelper#getReadableDatabase} or
     * {@link SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        dbHelper = new PBSDBHelper(ctx);
        return true;
    }

    /**
     * Implement this to handle query requests from clients.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     * <p/>
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     * <p/>
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     * <p/>
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final int match = URI_MATCHER.match(uri);
        switch (match) {

            // retrieve all checkpoint list
            case ModelConst.AD_USER_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.AD_USER_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.AD_ORG_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.AD_ORG_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.AD_CLIENT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.AD_CLIENT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.AD_ROLE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.AD_ROLE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.M_CHECKIN_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_CHECKIN_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_CHECKPOINT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_CHECKPOINT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.PBS_SYNCTABLE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.PBS_SYNCTABLE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.HR_IDENTITY_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_IDENTITY_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.HR_LEAVETYPE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_LEAVETYPE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.C_PROJECTLOCATION_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_PROJECT_LOCATION_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.CHECKIN_JOIN_CHECKPOINT_TOKEN:{
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_CHECKIN_TABLE + " inner join " + ModelConst.M_CHECKPOINT_TABLE + " on (" + ModelConst.M_CHECKIN_TABLE + "." + ModelConst.M_CHECKPOINT_TABLE + "_uuid = "
                        + ModelConst.M_CHECKPOINT_TABLE + "." + ModelConst.M_CHECKPOINT_TABLE + "_uuid)");
                Map<String,String> columnMap = new HashMap<>();
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".description", ModelConst.M_CHECKIN_TABLE + ".description as checkin_desc");
                columnMap.put(ModelConst.M_CHECKPOINT_TABLE + ".name", ModelConst.M_CHECKPOINT_TABLE + ".name as name");
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".datetrx", ModelConst.M_CHECKIN_TABLE  +".datetrx as datetrx");
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".m_checkin_uuid", ModelConst.M_CHECKIN_TABLE + ".m_checkin_uuid as m_checkin_uuid");
                builder.setProjectionMap(columnMap);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.CHECKIN_JOIN_CHECKPOINT_DETAILS_TOKEN :{
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_CHECKIN_TABLE + " inner join " + ModelConst.M_CHECKPOINT_TABLE + " on (" + ModelConst.M_CHECKIN_TABLE + "." + ModelConst.M_CHECKPOINT_TABLE + "_uuid = "
                                + ModelConst.M_CHECKPOINT_TABLE + "." + ModelConst.M_CHECKPOINT_TABLE + "_uuid)  "
                                + "inner join " + ModelConst.AD_USER_TABLE + " on (" + ModelConst.M_CHECKIN_TABLE + ".ad_user_uuid = " + ModelConst.AD_USER_TABLE + ".ad_user_uuid)"
                                + "inner join " + ModelConst.C_PROJECT_LOCATION_TABLE + " on (" + ModelConst.M_CHECKPOINT_TABLE + ".c_projectlocation_uuid = " + ModelConst.C_PROJECT_LOCATION_TABLE + ".c_projectlocation_uuid)"
                                + "where " + ModelConst.M_CHECKIN_TABLE + "." + ModelConst.M_CHECKIN_TABLE + "_uuid= ?"
                );
                Map<String,String> columnMap = new HashMap<>();
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".description", ModelConst.M_CHECKIN_TABLE + ".description as desc");
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".datetrx", ModelConst.M_CHECKIN_TABLE + ".datetrx as dateTrx");
                columnMap.put(ModelConst.AD_USER_TABLE + ".name", ModelConst.AD_USER_TABLE +".name as username");
                columnMap.put(ModelConst.M_CHECKPOINT_TABLE + ".name", ModelConst.M_CHECKPOINT_TABLE + ".name as checkPoint");
                columnMap.put(ModelConst.M_CHECKIN_TABLE + ".m_checkin_uuid", ModelConst.M_CHECKIN_TABLE + ".m_checkin_uuid as m_checkin_uuid");
                columnMap.put(ModelConst.C_PROJECT_LOCATION_TABLE + ".name",  ModelConst.C_PROJECT_LOCATION_TABLE + ".name as projectLocation");
                builder.setProjectionMap(columnMap);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.C_BPARTNER_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_BPARTNER_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.C_BPARTNER_LOCATION_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_BPARTNER_LOCATION_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.HR_SETUP_JOB_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_SETUP_JOB_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.HR_JOBPOSITION_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_JOBPOSITION_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.HR_NATIONALITY_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_NATIONALITY_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.HR_SHIFT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_SHIFT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.HR_PROJLOCATION_SHIFT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_PROJLOCATION_SHIFT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.M_PRODUCT_CATEGORY_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_PRODUCT_CATEGORY_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.C_UOM_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_UOM_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_ATTRIBUTESETINSTANCE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.M_PRODUCT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_PRODUCT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.A_ASSET_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.A_ASSET_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.AD_NOTE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.AD_NOTE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.C_PROJECTTASK_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_PROJECTTASK_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.HR_PROJECTASSIGNMENT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_PROJECTASSIGNMENT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.JOBAPP_SHIFTS_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.JOBAPP_SHIFTS_VIEW);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.HR_JOBAPPLICATION_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.HR_JOBAPPLICATION_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.JOBAPP_LIST_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.JOBAPP_LIST_VIEW);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.C_BPARTNER_VIEW_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_BPARTNER_VIEW);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ModelConst.C_BPARTNER_VIEW_JOIN_HR_HR_PROJECTASSIGNMENT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.C_BPARTNER_VIEW + " left join " + ModelConst.HR_PROJECTASSIGNMENT_TABLE +
                        " on (" + ModelConst.C_BPARTNER_VIEW + "." + ModelConst.C_BPARTNER_TABLE + "_uuid = "
                        + ModelConst.HR_PROJECTASSIGNMENT_TABLE + "." + ModelConst.C_BPARTNER_TABLE + "_uuid)");
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            }
            case ModelConst.M_PURCHASEREQUEST_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_PURCHASEREQUEST_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_PURCHASEREQUESTLINE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_PURCHASEREQUESTLINE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_MOVEMENT_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_MOVEMENT_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_MOVEMENTLINE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_MOVEMENTLINE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_ATTENDANCE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_ATTENDANCE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            case ModelConst.M_ATTENDANCELINE_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ModelConst.M_ATTENDANCELINE_TABLE);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }

            default:
                return null;
        }
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case ModelConst.M_CHECKPOINT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.AD_USER_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.AD_ORG_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.AD_CLIENT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.AD_ROLE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_CHECKIN_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_PROJECTLOCATION_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_BPARTNER_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.A_ASSET_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.AD_NOTE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_BPARTNER_LOCATION_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_PROJECTTASK_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_UOM_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_JOBAPPLICATION_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_JOBPOSITION_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_NATIONALITY_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_SETUP_JOB_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_SHIFT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_PROJLOCATION_SHIFT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_MOVEMENT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_MOVEMENTLINE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_PROJECTASSIGNMENT_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.JOBAPP_SHIFTS_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.JOBAPP_LIST_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.C_BPARTNER_VIEW_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_IDENTITY_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.HR_LEAVETYPE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_ATTENDANCE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            case ModelConst.M_ATTENDANCELINE_TOKEN:
                return ModelConst.CONTENT_TYPE_DIR;
            default: {
                Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + "URI " + uri + " is not supported.");
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
            }
        }
    }

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case ModelConst.M_CHECKPOINT_TOKEN: {
                long id = db.insert(ModelConst.M_CHECKPOINT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_CHECKIN_TOKEN: {
                long id = db.insert(ModelConst.M_CHECKIN_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.AD_USER_TOKEN: {
                long id = db.insert(ModelConst.AD_USER_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.AD_ORG_TOKEN: {
                long id = db.insert(ModelConst.AD_ORG_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.AD_CLIENT_TOKEN: {
                long id = db.insert(ModelConst.AD_CLIENT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.AD_ROLE_TOKEN: {
                long id = db.insert(ModelConst.AD_ROLE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }

            case ModelConst.C_PROJECTLOCATION_TOKEN: {
                long id = db.insert(ModelConst.C_PROJECT_LOCATION_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }

            case ModelConst.PBS_SYNCTABLE_TOKEN: {
                long id = db.insert(ModelConst.PBS_SYNCTABLE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }

            case ModelConst.HR_JOBPOSITION_TOKEN: {
                long id = db.insert(ModelConst.HR_JOBPOSITION_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_NATIONALITY_TOKEN: {
                long id = db.insert(ModelConst.HR_NATIONALITY_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_PROJLOCATION_SHIFT_TOKEN: {
                long id = db.insert(ModelConst.HR_PROJLOCATION_SHIFT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_JOBAPPLICATION_TOKEN: {
                long id = db.insert(ModelConst.HR_JOBAPPLICATION_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_SHIFT_TOKEN: {
                long id = db.insert(ModelConst.HR_SHIFT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.C_BPARTNER_TOKEN: {
                long id = db.insert(ModelConst.C_BPARTNER_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.C_BPARTNER_LOCATION_TOKEN: {
                long id = db.insert(ModelConst.C_BPARTNER_LOCATION_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_PRODUCT_CATEGORY_TOKEN: {
                long id = db.insert(ModelConst.M_PRODUCT_CATEGORY_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_PRODUCT_TOKEN: {
                long id = db.insert(ModelConst.M_PRODUCT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.C_UOM_TOKEN: {
                long id = db.insert(ModelConst.C_UOM_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN: {
                long id = db.insert(ModelConst.M_ATTRIBUTESETINSTANCE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_PURCHASEREQUEST_TOKEN: {
                long id = db.insert(ModelConst.M_PURCHASEREQUEST_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_PURCHASEREQUESTLINE_TOKEN: {
                long id = db.insert(ModelConst.M_PURCHASEREQUESTLINE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.C_PROJECTTASK_TOKEN: {
                long id = db.insert(ModelConst.C_PROJECTTASK_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.A_ASSET_TOKEN: {
                long id = db.insert(ModelConst.A_ASSET_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.AD_NOTE_TOKEN: {
                long id = db.insert(ModelConst.AD_NOTE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_MOVEMENT_TOKEN: {
                long id = db.insert(ModelConst.M_MOVEMENT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_MOVEMENTLINE_TOKEN: {
                long id = db.insert(ModelConst.M_MOVEMENTLINE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_PROJECTASSIGNMENT_TOKEN: {
                long id = db.insert(ModelConst.HR_PROJECTASSIGNMENT_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_SETUP_JOB_TOKEN: {
                long id = db.insert(ModelConst.HR_SETUP_JOB_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_IDENTITY_TOKEN: {
                long id = db.insert(ModelConst.HR_IDENTITY_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.HR_LEAVETYPE_TOKEN: {
                long id = db.insert(ModelConst.HR_LEAVETYPE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_ATTENDANCE_TOKEN: {
                long id = db.insert(ModelConst.M_ATTENDANCE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case ModelConst.M_ATTENDANCELINE_TOKEN: {
                long id = db.insert(ModelConst.M_ATTENDANCELINE_TABLE, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return ModelConst.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            default: {
                Log.e(TAG, PandoraConstant.ERROR +
                        PandoraConstant.SPACE + "URI " + uri + " is not supported.");
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs
     * @return The number of rows affected.
     * @throws SQLException
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        switch (token) {
            case (ModelConst.M_CHECKPOINT_TOKEN):
                rowsDeleted = db.delete(ModelConst.M_CHECKPOINT_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.AD_USER_TOKEN):
                rowsDeleted = db.delete(ModelConst.AD_USER_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.AD_ROLE_TOKEN):
                rowsDeleted = db.delete(ModelConst.AD_ROLE_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.AD_ORG_TOKEN):
                rowsDeleted = db.delete(ModelConst.AD_ORG_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.AD_CLIENT_TOKEN):
                rowsDeleted = db.delete(ModelConst.AD_CLIENT_TABLE, selection, selectionArgs);
                break;

            case (ModelConst.PBS_SYNCTABLE_TOKEN):
                rowsDeleted = db.delete(ModelConst.PBS_SYNCTABLE_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.M_CHECKIN_TOKEN):
                rowsDeleted = db.delete(ModelConst.M_CHECKIN_TABLE, selection, selectionArgs);
                break;
            case (ModelConst.C_PROJECTLOCATION_TOKEN):
                rowsDeleted = db.delete(ModelConst.C_PROJECT_LOCATION_TABLE, selection, selectionArgs);
                break;
            case ModelConst.HR_JOBPOSITION_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_JOBPOSITION_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_NATIONALITY_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_NATIONALITY_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_PROJLOCATION_SHIFT_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_SHIFT_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_JOBAPPLICATION_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_JOBAPPLICATION_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_SHIFT_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_SHIFT_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.C_BPARTNER_TOKEN: {
                rowsDeleted = db.delete(ModelConst.C_BPARTNER_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.C_BPARTNER_LOCATION_TOKEN: {
                rowsDeleted = db.delete(ModelConst.C_BPARTNER_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PRODUCT_CATEGORY_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_PRODUCT_CATEGORY_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PRODUCT_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_PRODUCT_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.C_UOM_TOKEN: {
                rowsDeleted = db.delete(ModelConst.C_UOM_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_ATTRIBUTESETINSTANCE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PURCHASEREQUEST_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_PURCHASEREQUEST_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PURCHASEREQUESTLINE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_PURCHASEREQUESTLINE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.C_PROJECTTASK_TOKEN: {
                rowsDeleted = db.delete(ModelConst.C_PROJECTTASK_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.A_ASSET_TOKEN: {
                rowsDeleted = db.delete(ModelConst.A_ASSET_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.AD_NOTE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.AD_NOTE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_MOVEMENT_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_MOVEMENT_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_MOVEMENTLINE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_MOVEMENTLINE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_PROJECTASSIGNMENT_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_PROJECTASSIGNMENT_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_SETUP_JOB_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_SETUP_JOB_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_IDENTITY_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_IDENTITY_TABLE, selection, selectionArgs);
                break;
            }

            case ModelConst.HR_LEAVETYPE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.HR_LEAVETYPE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTENDANCE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_ATTENDANCE_TABLE, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTENDANCELINE_TOKEN: {
                rowsDeleted = db.delete(ModelConst.M_ATTENDANCELINE_TABLE, selection, selectionArgs);
                break;
            }

            default:{
                Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + "URI " + uri + " is not supported.");
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }

        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        int token = URI_MATCHER.match(uri);
        int rowsUpdated = -1;
        switch (token) {
            case (ModelConst.M_CHECKPOINT_TOKEN):
                rowsUpdated = db.update(ModelConst.M_CHECKPOINT_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.M_CHECKIN_TOKEN):
                rowsUpdated = db.update(ModelConst.M_CHECKIN_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.AD_USER_TOKEN):
                rowsUpdated = db.update(ModelConst.AD_USER_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.AD_ROLE_TOKEN):
                rowsUpdated = db.update(ModelConst.AD_ROLE_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.AD_ORG_TOKEN):
                rowsUpdated = db.update(ModelConst.AD_ORG_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.AD_CLIENT_TOKEN):
                rowsUpdated = db.update(ModelConst.AD_CLIENT_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.C_PROJECTLOCATION_TOKEN):
                rowsUpdated = db.update(ModelConst.C_PROJECT_LOCATION_TABLE, values, selection, selectionArgs);
                break;
            case (ModelConst.PBS_SYNCTABLE_TOKEN):
                rowsUpdated = db.update(ModelConst.PBS_SYNCTABLE_TABLE, values, selection, selectionArgs);
                break;
            case ModelConst.HR_JOBPOSITION_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_JOBPOSITION_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_NATIONALITY_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_NATIONALITY_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_PROJLOCATION_SHIFT_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_SHIFT_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_JOBAPPLICATION_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_JOBAPPLICATION_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_SHIFT_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_SHIFT_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.C_BPARTNER_TOKEN: {
                rowsUpdated = db.update(ModelConst.C_BPARTNER_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.C_BPARTNER_LOCATION_TOKEN: {
                rowsUpdated = db.update(ModelConst.C_BPARTNER_LOCATION_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PRODUCT_CATEGORY_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_PRODUCT_CATEGORY_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PRODUCT_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_PRODUCT_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.C_UOM_TOKEN: {
                rowsUpdated = db.update(ModelConst.C_UOM_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTRIBUTESETINSTANCE_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_ATTRIBUTESETINSTANCE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PURCHASEREQUEST_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_PURCHASEREQUEST_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_PURCHASEREQUESTLINE_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_PURCHASEREQUESTLINE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.C_PROJECTTASK_TOKEN: {
                rowsUpdated = db.update(ModelConst.C_PROJECTTASK_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.A_ASSET_TOKEN: {
                rowsUpdated = db.update(ModelConst.A_ASSET_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.AD_NOTE_TOKEN: {
                rowsUpdated = db.update(ModelConst.AD_NOTE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_MOVEMENT_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_MOVEMENT_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_MOVEMENTLINE_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_MOVEMENTLINE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_PROJECTASSIGNMENT_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_PROJECTASSIGNMENT_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_SETUP_JOB_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_SETUP_JOB_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_IDENTITY_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_IDENTITY_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.HR_LEAVETYPE_TOKEN: {
                rowsUpdated = db.update(ModelConst.HR_LEAVETYPE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTENDANCE_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_ATTENDANCE_TABLE, values, selection, selectionArgs);
                break;
            }
            case ModelConst.M_ATTENDANCELINE_TOKEN: {
                rowsUpdated = db.update(ModelConst.M_ATTENDANCELINE_TABLE, values, selection, selectionArgs);
                break;
            }
            default:{
                Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + "Unsupported URI: " + uri);
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }
        // Notifying the changes, if there are any
        if (rowsUpdated != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentProviderResult[] result = new ContentProviderResult[
                operations.size()];
        try {
            database.beginTransaction();
            for (int i = 0; i < operations.size(); i++) {
                ContentProviderOperation operation = operations.get(i);
                //apply operation one by one.
                result[i] = operation.apply(this, result, i);
                //if both return false throw exception to prevent the transaction from set to
                //successful.
                if (!getContentProviderResult(result[i])) {
                    throw new OperationApplicationException();
                }
            }
            database.setTransactionSuccessful();
        } catch (OperationApplicationException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + "Batch transaction failed ");
        } finally {
            database.endTransaction();
        }
        return result;
    }

    /**
     * Extract content provider result from uri(return for insertion operation) or
     * count (return for update/delete operation)
     * @param result
     * @return
     */
    public static boolean getContentProviderResult (ContentProviderResult result){
        boolean resultFlag = false;
        //try to get result from uri if operation is an insert.
        Uri resultUri = result.uri;
        if (resultUri != null) {
            resultFlag = ModelConst.getURIResult(resultUri);
        } else {
            //try to get result from count if operation is an update/delete
            Integer resultInt = result.count;
            if (resultInt != null && resultInt != 0) {
                resultFlag = true;
            }
        }
        return resultFlag;
    }
}
