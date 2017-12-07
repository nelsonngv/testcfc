package com.pbasolutions.android.controller;

import android.content.Context;

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
        task = new ServerTask(base, getContentResolver());
    }
}
