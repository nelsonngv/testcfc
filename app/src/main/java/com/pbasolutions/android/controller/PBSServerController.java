package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.json.PBSColumnsJSON;
import com.pbasolutions.android.json.PBSSyncJSON;
import com.pbasolutions.android.json.PBSTableJSON;
import com.pbasolutions.android.model.ModelConst;
import com.pbasolutions.android.syncAdapter.PBSIServerAccessor;
import com.pbasolutions.android.syncAdapter.PBSServerAccessor;
import com.pbasolutions.android.utils.CameraUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbadell on 7/1/15.
 */
public class PBSServerController extends PBSController {
    /**
     * Class tag name.
     */
    private static final String TAG = "PBSServerController";
    /**
     * Static string that hold event string for sync local tables.
     */
    public static final String SYNC_LOCAL_TABLES = "SYNC_LOCAL_TABLES";
    /**
     * Static string that hold event string for update local tables.
     */
    public static final String UPDATE_LOCAL_TABLES = "UPDATE_LOCAL_TABLES";
    /**
     * Static string that hold event string for delete outdated retention local tables
     */
    public static final String DELETE_RETENTION_RECORD = "DELETE_RETENTION_RECORD";
    /**
     * Static string that hold event string for checking server unsync count
     */
    public static final String GET_UNSYNC_COUNT = "GET_UNSYNC_COUNT";

    public static final String SYNCED_COUNT = "SYNCED_COUNT";
    /**
     * Constructor.
     * @param base
     */
    public PBSServerController(Context base) {
        super(base);
        task = new ServerTask(getContentResolver());
    }
}
